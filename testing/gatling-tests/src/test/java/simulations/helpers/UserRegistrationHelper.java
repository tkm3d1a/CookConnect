package simulations.helpers;

import io.gatling.javaapi.core.ChainBuilder;
import simulations.config.SimulationConfig;

import java.util.Base64;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class UserRegistrationHelper {

    public static ChainBuilder registerUser() {
        return exec(
                http("Register New User")
                        .post(SimulationConfig.USER_REGISTER_PATH)
                        .body(StringBody(session ->
                                String.format("""
                        {
                            "username": "%s",
                            "password": "%s",
                            "email": "%s",
                            "firstName": "%s",
                            "lastName": "%s"
                        }
                        """,
                                        session.getString("username"),
                                        session.getString("password"),
                                        session.getString("email"),
                                        session.getString("firstName"),
                                        session.getString("lastName")
                                )
                        ))
                        .asJson()
                        .check(status().in(201, 400, 429, 503)) // Accept errors
                        .check(status().saveAs("registrationStatus"))
                        // Only save userId if successful
                        .check(
                                jsonPath("$.id").optional().saveAs("userId")
                        )
                        .check(
                                jsonPath("$.username").optional().saveAs("registeredUsername")
                        )
        )
                .doIf(session -> session.getInt("registrationStatus") != 201).then(
                        exec(session -> {
                            System.err.println("!!! Registration failed with status: " +
                                    session.getInt("registrationStatus") +
                                    " for user: " + session.getString("username"));
                            return session;
                        })
                );
    }

    public static ChainBuilder getUserToken() {
        String basicAuth = "Basic " + Base64.getEncoder().encodeToString(
                (SimulationConfig.CLIENT_ID + ":" + SimulationConfig.CLIENT_SECRET).getBytes()
        );

        return doIf(session -> session.contains("userId") && session.getString("userId") != null).then(
                exec(
                        http("Get User Token")
                                .post(SimulationConfig.keycloakTokenEndpoint())
                                .header("Authorization", basicAuth)
                                .header("Content-Type", "application/x-www-form-urlencoded")
                                .formParam("grant_type", "password")
                                .formParam("username", session -> session.getString("username"))
                                .formParam("password", session -> session.getString("password"))
                                .check(status().in(200, 400, 401))
                                .check(status().saveAs("tokenStatus"))
                                .check(jsonPath("$.access_token").optional().saveAs("accessToken"))
                )
        );
    }

    public static ChainBuilder registerAndAuthenticate() {
        return exec(registerUser())
                .pause(java.time.Duration.ofMillis(500))
                .exec(getUserToken());
    }

    public static ChainBuilder generateUserData() {
        return exec(session -> {
            int random = java.util.concurrent.ThreadLocalRandom.current().nextInt(10000, 99999);
            long timestamp = System.currentTimeMillis();
            return session
                    .set("username", "gatling_user_" + random)
                    .set("password", "GatlingTest123!")
                    .set("email", "gatling" + random + "@test.com")
                    .set("firstName", "Gatling")
                    .set("lastName", "User" + random)
                    .set("timestamp", timestamp);
        });
    }
}
