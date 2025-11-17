package simulations;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import simulations.config.SimulationConfig;
import simulations.helpers.UserRegistrationHelper;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class UserRegistrationAndRecipeStressTest extends Simulation{
    HttpProtocolBuilder httpProtocol = SimulationConfig.httpProtocol();

    // Create recipe with the newly registered user
    ChainBuilder createRecipeAsNewUser = exec(
            http("Create Recipe as New User")
                    .post(SimulationConfig.RECIPE_SIMPLE_PATH)
                    .header("Authorization", "Bearer #{accessToken}")
                    .body(StringBody("""
                {
                    "title": "First Recipe by #{username}",
                    "description": "Created by newly registered user via Gatling test",
                    "createdBy": "#{userId}"
                }
                """))
                    .asJson()
                    .check(status().in(200, 201, 429, 503)) // Accept rate limit and circuit breaker responses
                    .check(status().saveAs("firstRecipeStatus"))
    );

    ChainBuilder createSecondRecipe = exec(
            http("Create Second Recipe")
                    .post(SimulationConfig.RECIPE_SIMPLE_PATH)
                    .header("Authorization", "Bearer #{accessToken}")
                    .body(StringBody("""
                {
                    "title": "Second Recipe by #{username}",
                    "description": "Another recipe by the same user",
                    "createdBy": "#{userId}"
                }
                """))
                    .asJson()
                    .check(status().in(200, 201, 429, 503))
                    .check(status().saveAs("secondRecipeStatus"))
    );

    ChainBuilder getAllRecipes = exec(
            http("Get All Recipes")
                    .get(SimulationConfig.RECIPE_GET_ALL_PATH + "?page=0&size=20")
                    .header("Authorization", "Bearer #{accessToken}")
                    .check(status().in(200, 429, 503))
                    .check(status().saveAs("getAllStatus"))
    );

    // Log what happened
    ChainBuilder logResults = exec(session -> {
        String username = session.getString("username");
        String userId = session.getString("userId");

        // Use getInt with default values
        Integer firstStatus = session.contains("firstRecipeStatus")
                ? session.getInt("firstRecipeStatus")
                : null;
        Integer secondStatus = session.contains("secondRecipeStatus")
                ? session.getInt("secondRecipeStatus")
                : null;
        Integer getAllStatus = session.contains("getAllStatus")
                ? session.getInt("getAllStatus")
                : null;

        String statusMessage = "";
        if (firstStatus == null || secondStatus == null || getAllStatus == null) {
            statusMessage = "XXX FAILED - Request error";
        } else if (firstStatus == 429 || secondStatus == 429 || getAllStatus == 429) {
            statusMessage = "!!! RATE LIMITED";
        } else if (firstStatus == 503 || secondStatus == 503 || getAllStatus == 503) {
            statusMessage = "( ) CIRCUIT BREAKER OPEN";
        } else {
            statusMessage = "+++ SUCCESS";
        }

        System.out.println(statusMessage + " - User: " + username +
                " (ID: " + userId + ") " +
                "Statuses: [" + firstStatus + ", " + secondStatus + ", " + getAllStatus + "]");
        return session;
    });

    ScenarioBuilder completeUserJourney = scenario("Complete User Journey - Stress Test")
            .exec(UserRegistrationHelper.generateUserData())
            .exec(UserRegistrationHelper.registerAndAuthenticate())
            .exitHereIf(session -> !session.contains("userId") || session.getString("userId") == null)
            .pause(Duration.ofMillis(100))
            .exec(createRecipeAsNewUser)
            .pause(Duration.ofMillis(100))
            .exec(createSecondRecipe)
            .pause(Duration.ofMillis(100))
            .exec(getAllRecipes)
            .exec(logResults);


    {
        setUp(
                completeUserJourney.injectOpen(
                        rampUsers(20).during(Duration.ofSeconds(20)),
                        constantUsersPerSec(10).during(Duration.ofMinutes(2)),
                        atOnceUsers(50),
                        constantUsersPerSec(5).during(Duration.ofMinutes(1)),
                        rampUsers(100).during(Duration.ofSeconds(30))
                )
        )
//        setUp(
//                completeUserJourney.injectOpen(
//                        rampUsers(10).during(Duration.ofSeconds(30)),      // Slower ramp
//                        constantUsersPerSec(5).during(Duration.ofMinutes(2)) // Lower rate
//                )
//        )
                .protocols(httpProtocol)
                .maxDuration(Duration.ofMinutes(10)) // Total test duration
                .assertions(
                        global().responseTime().max().lt(15000),
                        global().responseTime().mean().lt(10000),
                        global().successfulRequests().percent().gte(90.0) // Lower threshold since we expect some failures
                );
    }
}
