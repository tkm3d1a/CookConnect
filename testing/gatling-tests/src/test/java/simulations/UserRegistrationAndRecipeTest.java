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

public class UserRegistrationAndRecipeTest extends Simulation{
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
                    .check(status().is(201))
                    .check(jsonPath("$.id").saveAs("recipeId"))
                    .check(jsonPath("$.createdBy").isEL("#{userId}"))
                    .check(jsonPath("$.createdByUsername").isEL("#{username}"))
                    .check(jsonPath("$.title").isEL("First Recipe by #{username}"))
    );

    // Create a second recipe to test multiple creations
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
                    .check(status().is(201))
                    .check(jsonPath("$.id").saveAs("recipeId2"))
    );

    // Get all recipes to verify both recipes show up
    ChainBuilder getAllRecipes = exec(
            http("Get All Recipes")
                    .get(SimulationConfig.RECIPE_GET_ALL_PATH)
                    .header("Authorization", "Bearer #{accessToken}")
                    .check(status().is(200))
                    .check(jsonPath("$[*].id").findAll().exists())
    );

    // Complete end-to-end scenario
    ScenarioBuilder completeUserJourney = scenario("Complete User Journey - Register to Multiple Recipes")
            // Generate unique user data
            .exec(UserRegistrationHelper.generateUserData())
            // Register the user and get token
            .exec(UserRegistrationHelper.registerAndAuthenticate())
            .pause(Duration.ofMillis(200))
            // Create first recipe
            .exec(createRecipeAsNewUser)
            .pause(Duration.ofMillis(200))
            // Create second recipe
            .exec(createSecondRecipe)
            .pause(Duration.ofMillis(200))
            // Verify by getting all recipes
            .exec(getAllRecipes)
            .exec(session -> {
                // Log success
                System.out.println("✓ User " + session.getString("username") +
                        " (ID: " + session.getString("userId") + ") " +
                        "created recipes: " + session.getString("recipeId") +
                        ", " + session.getString("recipeId2"));
                return session;
            });

    // Load pattern: Create multiple users concurrently
    {
        setUp(
                completeUserJourney.injectOpen(
                        rampUsers(10).during(Duration.ofSeconds(10)) // 10 users over 10 seconds
                )
        )
                .protocols(httpProtocol)
                .maxDuration(Duration.ofMinutes(2))
                .assertions(
                        global().responseTime().max().lt(5000),
                        global().successfulRequests().percent().gte(95.0),
                        details("Register New User").responseTime().percentile3().lt(2000),
                        details("Create Recipe as New User").responseTime().percentile3().lt(1000)
                );
    }
}
