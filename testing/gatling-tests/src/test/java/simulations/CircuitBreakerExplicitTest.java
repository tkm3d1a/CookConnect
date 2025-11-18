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

public class CircuitBreakerExplicitTest extends Simulation {

    HttpProtocolBuilder httpProtocol = SimulationConfig.httpProtocol();

    ChainBuilder createRecipe = exec(
            http("Create Recipe (Circuit Breaker Test)")
                    .post(SimulationConfig.RECIPE_SIMPLE_PATH)
                    .header("Authorization", "Bearer #{accessToken}")
                    .body(StringBody("""
                {
                    "title": "Recipe #{requestNum}",
                    "description": "Testing circuit breaker",
                    "createdBy": "#{userId}"
                }
                """))
                    .asJson()
                    .check(status().in(200, 201, 429, 503, 504))
                    .check(status().saveAs("status"))
    )
            .exec(session -> {
                int status = session.getInt("status");
                int requestNum = session.getInt("requestNum");
                String message = switch (status) {
                    case 200, 201 -> "+++ Success";
                    case 429 -> "!!! Rate Limited";
                    case 503 -> "( ) Circuit Breaker OPEN (Service Unavailable)";
                    case 504 -> "(T) Gateway Timeout";
                    default -> "? Unknown status: " + status;
                };
                System.out.println("Request " + requestNum + ": " + message);
                return session.set("requestNum", requestNum + 1);
            });

    ScenarioBuilder circuitBreakerScenario = scenario("Explicit Circuit Breaker Test")
            .exec(UserRegistrationHelper.generateUserData())
            .exec(UserRegistrationHelper.registerAndAuthenticate())
            .exec(session -> session.set("requestNum", 1))
            .pause(Duration.ofMillis(200))

            // Phase 1: Normal requests (circuit closed)
            .exec(session -> {
                System.out.println("\n=== PHASE 1: Normal Load (Circuit should be CLOSED) ===");
                return session;
            })
            .repeat(10).on(
                    exec(createRecipe)
                            .pause(Duration.ofMillis(200))
            )

            // Phase 2: Sudden massive spike (trigger circuit breaker)
            .exec(session -> {
                System.out.println("\n=== PHASE 2: MASSIVE SPIKE (Trying to OPEN circuit) ===");
                return session;
            })
            .repeat(50).on(
                    exec(createRecipe)
                            .pause(Duration.ofMillis(10)) // Very rapid requests
            )

            // Phase 3: Wait for circuit to open
            .exec(session -> {
                System.out.println("\n=== PHASE 3: Waiting for circuit breaker to open... ===");
                return session;
            })
            .pause(Duration.ofSeconds(2))

            // Phase 4: Test while circuit is open (should get fast failures)
            .exec(session -> {
                System.out.println("\n=== PHASE 4: Testing with OPEN circuit (should fail fast) ===");
                return session;
            })
            .repeat(10).on(
                    exec(createRecipe)
                            .pause(Duration.ofMillis(100))
            )

            // Phase 5: Wait for half-open state
            .exec(session -> {
                System.out.println("\n=== PHASE 5: Waiting for HALF-OPEN state... ===");
                return session;
            })
            .pause(Duration.ofSeconds(15)) // Wait for waitDurationInOpenState

            // Phase 6: Test recovery (circuit tries to close)
            .exec(session -> {
                System.out.println("\n=== PHASE 6: Testing HALF-OPEN -> CLOSED recovery ===");
                return session;
            })
            .repeat(10).on(
                    exec(createRecipe)
                            .pause(Duration.ofMillis(500))
            );

    {
        setUp(
                circuitBreakerScenario.injectOpen(
                        atOnceUsers(1), // Start with single user
                        nothingFor(Duration.ofSeconds(30)),
                        atOnceUsers(10) // Then add concurrent users to amplify the spike
                )
        )
                .protocols(httpProtocol)
                .maxDuration(Duration.ofMinutes(3));
    }
}
