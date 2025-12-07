package simulations.helpers;

import io.gatling.javaapi.core.ChainBuilder;
import simulations.config.SimulationConfig;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class UserServiceHelper {

    public static ChainBuilder getAllUsers() {
        return exec(
                http("Get All Users")
                        .get(SimulationConfig.USER_BASE_PATH + "/")
                        .header("Authorization", session -> AuthenticationHelper.authHeader(session))
                        .check(status().is(200))
                        .check(jsonPath("$[0].id").optional().saveAs("firstUserId"))
                        .check(jsonPath("$[1].id").optional().saveAs("secondUserId"))
        )
                .exec(session -> {
                    if (session.contains("firstUserId")) {
                        System.out.println("[USER] Retrieved users list, first user ID: " +
                                session.getString("firstUserId"));
                    }
                    return session;
                });
    }

    public static ChainBuilder closeAccount() {
        return exec(
                http("Close User Account")
                        .post(session -> SimulationConfig.USER_BASE_PATH + "/" +
                                session.getString("userId") + "/close")  // Lambda for dynamic URL
                        .header("Authorization", session -> AuthenticationHelper.authHeader(session))
                        .check(status().is(200))
                        .check(bodyString().is("Account closed"))
        )
                .exec(session -> {
                    System.out.println("*** [KAFKA EVENT] ACCOUNT_CLOSED triggered for user: " +
                            session.getString("username") + " (ID: " + session.getString("userId") + ")");
                    System.out.println("    Expecting Recipe service to update createdBy to null");
                    System.out.println("    Expecting Social service to update description with '[ACCOUNT CLOSED]'");
                    return session;
                });
    }

    public static ChainBuilder deleteAccount() {
        return exec(
                http("Delete User Account")
                        .delete(session -> SimulationConfig.USER_BASE_PATH + "/" +
                                session.getString("userId"))  // Lambda for dynamic URL
                        .header("Authorization", session -> AuthenticationHelper.authHeader(session))
                        .check(status().is(200))
        )
                .exec(session -> {
                    System.out.println("*** [KAFKA EVENT] ACCOUNT_DELETED triggered for user: " +
                            session.getString("username") + " (ID: " + session.getString("userId") + ")");
                    System.out.println("    Expecting Social service to delete profile");
                    System.out.println("    User removed from database and Keycloak");
                    return session;
                });
    }
}