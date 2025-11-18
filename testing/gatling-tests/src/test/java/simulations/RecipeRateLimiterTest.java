package simulations;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import simulations.config.SimulationConfig;

import java.time.Duration;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicInteger;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class RecipeRateLimiterTest extends Simulation{
    HttpProtocolBuilder httpProtocol = SimulationConfig.httpProtocol();

    AtomicInteger successCount = new AtomicInteger(0);
    AtomicInteger rateLimitCount = new AtomicInteger(0);

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

    ChainBuilder getAllRecipesRandomPage = exec(session -> {
        int requestNum = session.getInt("requestNum");
        return session.set("page", requestNum);
    }).exec(
            http("Get All Recipes - Page #{page}")
                    .get(SimulationConfig.RECIPE_GET_ALL_PATH + "?page=#{page}&size=20")
                    .header("Authorization", "Bearer #{accessToken}")
                    .check(status().in(200, 429))
                    .check(status().saveAs("status"))
    ).exec(session -> {
        int status = session.getInt("status");
        int requestNum = session.getInt("requestNum");

        if (status == 429) {
            int count = rateLimitCount.incrementAndGet();
            System.out.println("!!! RATE LIMITED! Request #" + requestNum + " | Total rate limits: " + count);
        } else {
            successCount.incrementAndGet();
        }

        return session.set("requestNum", requestNum + 1);
    });

    // Scenario: Get token once, then repeatedly hit the rate-limited endpoint
    ScenarioBuilder rateLimiterScenario = scenario("Trigger Rate Limiter on GET All")
            .exec(session -> session.set("requestNum", 1))
            .exec(getKeycloakToken)
            .pause(Duration.ofMillis(100))
            .repeat(500).on(
                    exec(getAllRecipesRandomPage)
            ).exec(session -> {
                System.out.println("\n=== FINAL RESULTS ===");
                System.out.println("Successful requests: " + successCount.get());
                System.out.println("Rate limited: " + rateLimitCount.get());
                System.out.println("Total requests: " + (successCount.get() + rateLimitCount.get()));
                return session;
            });

    // Adjust testing params here
    {
        setUp(
                rateLimiterScenario.injectOpen(
                        atOnceUsers(5)
                )
        )
                .protocols(httpProtocol)
                .maxDuration(Duration.ofSeconds(30));
    }
}
