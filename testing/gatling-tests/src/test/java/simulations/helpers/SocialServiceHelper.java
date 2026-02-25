package simulations.helpers;

import io.gatling.javaapi.core.ChainBuilder;

import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.core.CoreDsl.jsonPath;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class SocialServiceHelper {

    private static final String SOCIAL_BASE_PATH = "/socials/api/v1";

    public static ChainBuilder createSocialInteraction() {
        return exec(
                http("Create Social Interaction")
                        .post(session -> SOCIAL_BASE_PATH + "/" + session.getString("userId"))  // Lambda
                        .header("Authorization", session -> AuthenticationHelper.authHeader(session))
                        .check(status().is(201))
        )
                .exec(session -> {
                    System.out.println("[SOCIAL] Created social interaction for user: " +
                            session.getString("username"));
                    return session;
                });
    }

    public static ChainBuilder followUser(String targetUserIdKey) {
        return exec(
                http("Follow User")
                        .post(session -> SOCIAL_BASE_PATH + "/" +
                                session.getString("userId") + "/follow/" +
                                session.getString(targetUserIdKey))  // Lambda for dynamic URL
                        .header("Authorization", session -> AuthenticationHelper.authHeader(session))
                        .check(status().is(202))
                        .check(jsonPath("$.followingIds[0]").optional().saveAs("followedUserId"))
        )
                .exec(session -> {
                    System.out.println("[SOCIAL] User " + session.getString("username") +
                            " followed user ID: " + session.getString(targetUserIdKey));
                    return session;
                });
    }

    public static ChainBuilder unfollowUser(String targetUserIdKey) {
        return exec(
                http("Unfollow User")
                        .delete(session -> SOCIAL_BASE_PATH + "/" +
                                session.getString("userId") + "/follow/" +
                                session.getString(targetUserIdKey))  // Lambda for dynamic URL
                        .header("Authorization", session -> AuthenticationHelper.authHeader(session))
                        .check(status().is(200))
        )
                .exec(session -> {
                    System.out.println("[SOCIAL] User " + session.getString("username") +
                            " unfollowed user ID: " + session.getString(targetUserIdKey));
                    return session;
                });
    }
}