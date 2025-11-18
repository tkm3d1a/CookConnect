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

public class RecipeServiceBasicTest extends Simulation {
    HttpProtocolBuilder httpProtocol = SimulationConfig.httpProtocol();

    // Create Basic Auth header for Keycloak client credentials
    String basicAuth = "Basic " + Base64.getEncoder().encodeToString(
            (SimulationConfig.CLIENT_ID + ":" + SimulationConfig.CLIENT_SECRET).getBytes()
    );

    // Test 1: Unauthenticated - Create Simple Recipe
    ChainBuilder createSimpleRecipeUnauthenticated = exec(
            http("Create Simple Recipe (Unauthenticated)")
                    .post(SimulationConfig.RECIPE_SIMPLE_PATH)
                    .body(StringBody("""
                {
                    "title": "Gatling Test Recipe - #{randomId}",
                    "description": "Created by Gatling load test at #{timestamp}"
                }
                """))
                    .asJson()
                    .check(status().is(201))
                    .check(jsonPath("$.id").saveAs("recipeId"))
                    .check(jsonPath("$.createdBy").is("anonymous"))
    );

    // Test 2: Get Keycloak Token
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

    // Test 3: Authenticated - Create Simple Recipe
    ChainBuilder createSimpleRecipeAuthenticated = exec(
            http("Create Simple Recipe (Authenticated)")
                    .post(SimulationConfig.RECIPE_SIMPLE_PATH)
                    .header("Authorization", "Bearer #{accessToken}")
                    .body(StringBody("""
                {
                    "title": "Authenticated Recipe - #{randomId}",
                    "description": "Created by authenticated user via Gatling",
                    "createdBy": "9fea8746-a530-480c-9ada-b2e679535c53"
                }
                """))
                    .asJson()
                    .check(status().is(201))
                    .check(jsonPath("$.id").saveAs("recipeId"))
                    .check(jsonPath("$.createdByUsername").is(SimulationConfig.TEST_USERNAME))
    );

    // Test 4: Get All Recipes (Requires Authentication)
    ChainBuilder getAllRecipes = exec(
            http("Get All Recipes")
                    .get(SimulationConfig.RECIPE_GET_ALL_PATH)
                    .header("Authorization", "Bearer #{accessToken}")
                    .check(status().is(200))
                    .check(jsonPath("$").ofList().count().gte(0))
    );

    // Scenario 1: Unauthenticated user creating recipes
    ScenarioBuilder unauthenticatedScenario = scenario("Unauthenticated Recipe Creation")
            .exec(session -> session.set("randomId", System.currentTimeMillis()))
            .exec(session -> session.set("timestamp", java.time.Instant.now().toString()))
            .exec(createSimpleRecipeUnauthenticated)
            .pause(Duration.ofMillis(500));

    // Scenario 2: Authenticated user flow
    ScenarioBuilder authenticatedScenario = scenario("Authenticated Recipe Operations")
            .exec(session -> session.set("randomId", System.currentTimeMillis()))
            .exec(session -> session.set("timestamp", java.time.Instant.now().toString()))
            .exec(getKeycloakToken)
            .pause(Duration.ofMillis(100))
            .exec(createSimpleRecipeAuthenticated)
            .pause(Duration.ofMillis(500))
            .exec(getAllRecipes);

    // Very simple load: 5 unauthenticated users, 5 authenticated users
    {
        setUp(
                unauthenticatedScenario.injectOpen(
                        atOnceUsers(5)
                ),
                authenticatedScenario.injectOpen(
                        atOnceUsers(5)
                )
        )
                .protocols(httpProtocol)
                .maxDuration(Duration.ofSeconds(30))
                .assertions(
                        global().responseTime().max().lt(3000),
                        global().successfulRequests().percent().gte(95.0)
                );
    }
}
