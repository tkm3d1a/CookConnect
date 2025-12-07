package simulations.helpers;

import io.gatling.javaapi.core.ChainBuilder;
import simulations.config.SimulationConfig;

import java.util.Base64;

import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.core.CoreDsl.jsonPath;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class AuthenticationHelper {

    private static final String BASIC_AUTH = "Basic " + Base64.getEncoder().encodeToString(
            (SimulationConfig.CLIENT_ID + ":" + SimulationConfig.CLIENT_SECRET).getBytes()
    );

    /**
     * Authenticate with password grant using username/password from session
     */
    public static ChainBuilder authenticate() {
        return exec(
                http("Keycloak - Get Token")
                        .post(SimulationConfig.keycloakTokenEndpoint())
                        .header("Authorization", BASIC_AUTH)
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .formParam("grant_type", "password")
                        .formParam("username", session -> session.getString("username"))
                        .formParam("password", session -> session.getString("password"))
                        .check(status().in(200, 400, 401))
                        .check(status().saveAs("authStatus"))
                        .check(jsonPath("$.access_token").optional().saveAs("accessToken"))
                        .check(jsonPath("$.refresh_token").optional().saveAs("refreshToken"))
        )
                .doIf(session -> session.getInt("authStatus") != 200).then(
                        exec(session -> {
                            System.err.println("!!! Authentication failed with status: " +
                                    session.getInt("authStatus") +
                                    " for user: " + session.getString("username"));
                            return session;
                        })
                )
                .doIf(session -> session.getInt("authStatus") == 200).then(
                        exec(session -> {
                            System.out.println("[AUTH] Successfully authenticated user: " +
                                    session.getString("username"));
                            return session;
                        })
                );
    }

    /**
     * Refresh the access token using refresh token
     */
    public static ChainBuilder refreshToken() {
        return exec(
                http("Keycloak - Refresh Token")
                        .post(SimulationConfig.keycloakTokenEndpoint())
                        .header("Authorization", BASIC_AUTH)
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .formParam("grant_type", "refresh_token")
                        .formParam("refresh_token", session -> session.getString("refreshToken"))
                        .check(status().is(200))
                        .check(jsonPath("$.access_token").saveAs("accessToken"))
                        .check(jsonPath("$.refresh_token").saveAs("refreshToken"))
        );
    }

    /**
     * Helper to add Authorization header to requests
     */
    public static String authHeader(io.gatling.javaapi.core.Session session) {
        return "Bearer " + session.getString("accessToken");
    }
}