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

public class UserRegistrationAndRecipeConstantLoadTest extends Simulation{
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
                    .check(status().in(200, 201))
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
                    .check(status().in(200, 201))
                    .check(status().saveAs("secondRecipeStatus"))
    );

    ChainBuilder getAllRecipes = exec(
            http("Get All Recipes")
                    .get(SimulationConfig.RECIPE_GET_ALL_PATH)
                    .header("Authorization", "Bearer #{accessToken}")
                    .check(status().in(200))
                    .check(status().saveAs("getAllStatus"))
    );

    ScenarioBuilder completeUserJourney = scenario("Complete User Journey - Stress Test")
            .exec(UserRegistrationHelper.generateUserData())
            .exec(UserRegistrationHelper.registerAndAuthenticate())
            .exitHereIf(session -> !session.contains("userId") || session.getString("userId") == null)
            .pause(Duration.ofMillis(100))
            .exec(createRecipeAsNewUser)
            .pause(Duration.ofMillis(100))
            .exec(createSecondRecipe)
            .pause(Duration.ofMillis(100))
            .exec(getAllRecipes);

    {
        setUp(
                completeUserJourney.injectOpen(
                        constantUsersPerSec(10).during(Duration.ofMinutes(1)),
                        constantUsersPerSec(30).during(Duration.ofMinutes(1)),
                        constantUsersPerSec(60).during(Duration.ofMinutes(1))

                )
        )
                .protocols(httpProtocol)
                .maxDuration(Duration.ofMinutes(10)) // Total test duration
                .assertions(
                        global().responseTime().max().lt(15000),
                        global().responseTime().mean().lt(10000),
                        global().successfulRequests().percent().gte(90.0)
                );
    }
}
