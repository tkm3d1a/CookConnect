package simulations;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import simulations.config.SimulationConfig;

import java.time.Duration;
import java.util.Base64;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class RecipeRateLimiterTest extends Simulation{
    HttpProtocolBuilder httpProtocol = SimulationConfig.httpProtocol();

    String basicAuth = "Basic " + Base64.getEncoder().encodeToString(
            (SimulationConfig.CLIENT_ID + ":" + SimulationConfig.CLIENT_SECRET).getBytes()
    );

    // Get token once
    ChainBuilder getKeycloakToken = exec(
            http("Get Keycloak Token")
                    .post(SimulationConfig.keycloakTokenEndpoint())
                    .header("Authorization", basicAuth)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .formParam("grant_type", "password")
                    .formParam("username", SimulationConfig.TEST_USERNAME)
                    .formParam("password", SimulationConfig.TEST_PASSWORD)
                    .check(status().is(200))
                    .check(jsonPath("$.access_token").saveAs("accessToken"))
    );

    // Hammer the GET ALL endpoint (the one with @RateLimiter)
    ChainBuilder getAllRecipes = exec(
            http("Get All Recipes")
                    .get(SimulationConfig.RECIPE_GET_ALL_PATH)
                    .header("Authorization", "Bearer #{accessToken}")
                    .check(status().in(200, 429)) // Accept both success and rate limit
    );

    // Scenario: Get token once, then repeatedly hit the rate-limited endpoint
    ScenarioBuilder rateLimiterScenario = scenario("Trigger Rate Limiter on GET All")
            .exec(getKeycloakToken)
            .pause(Duration.ofMillis(100))
            .repeat(50).on( // Each user makes 50 requests
                    exec(getAllRecipes)
            );

    // Load pattern designed to exceed your rate limit
    // Adjust the numbers based on your rate limit configuration
    {
        setUp(
                rateLimiterScenario.injectOpen(
                        // Start with 10 users
                        atOnceUsers(10),
                        // Wait a bit
                        nothingFor(Duration.ofSeconds(2)),
                        // Add 20 more users (should trigger rate limiter)
                        atOnceUsers(20)
                )
        )
                .protocols(httpProtocol)
                .maxDuration(Duration.ofMinutes(1));
    }
}
