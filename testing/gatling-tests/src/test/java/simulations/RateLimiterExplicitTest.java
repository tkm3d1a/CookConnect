package simulations;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import simulations.config.SimulationConfig;
import simulations.helpers.UserRegistrationHelper;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class RateLimiterExplicitTest extends Simulation{
    HttpProtocolBuilder httpProtocol = SimulationConfig.httpProtocol();

    // Counters for analysis
    AtomicInteger successCount = new AtomicInteger(0);
    AtomicInteger rateLimitCount = new AtomicInteger(0);

    ChainBuilder hammertGetAll = exec(
            http("Get All Recipes (Rate Limit Test)")
                    .get(SimulationConfig.RECIPE_GET_ALL_PATH)
                    .header("Authorization", "Bearer #{accessToken}")
                    .check(status().in(200, 429))
                    .check(status().saveAs("responseStatus"))
    )
            .exec(session -> {
                int status = session.getInt("responseStatus");
                if (status == 200) {
                    int count = successCount.incrementAndGet();
                    if (count % 10 == 0) {
                        System.out.println("✓ Successful requests: " + count);
                    }
                } else if (status == 429) {
                    int count = rateLimitCount.incrementAndGet();
                    System.out.println("⚠️ RATE LIMITED! Total rate limits: " + count);
                }
                return session;
            });

    ScenarioBuilder rateLimitScenario = scenario("Explicit Rate Limiter Test")
            // Register once, then hammer the endpoint
            .exec(UserRegistrationHelper.generateUserData())
            .exec(UserRegistrationHelper.registerAndAuthenticate())
            .pause(Duration.ofMillis(500))
            // Make 100 rapid requests
            .repeat(100).on(
                    exec(hammertGetAll)
                            .pause(Duration.ofMillis(10)) // Very short pause - 100 req/sec
            )
            .exec(session -> {
                System.out.println("\n=== FINAL STATS ===");
                System.out.println("Total successful: " + successCount.get());
                System.out.println("Total rate limited: " + rateLimitCount.get());
                System.out.println("Rate limit percentage: " +
                        (rateLimitCount.get() * 100.0 / (successCount.get() + rateLimitCount.get())) + "%");
                return session;
            });

    {
        setUp(
                rateLimitScenario.injectOpen(
                        // Start with 1 user
                        atOnceUsers(1),
                        // Wait, then add more users
                        nothingFor(Duration.ofSeconds(5)),
                        atOnceUsers(5),
                        // Wait again
                        nothingFor(Duration.ofSeconds(5)),
                        atOnceUsers(10)
                )
        )
                .protocols(httpProtocol)
                .maxDuration(Duration.ofMinutes(3));
    }
}
