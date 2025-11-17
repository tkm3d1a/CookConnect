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

public class ProgressiveLoadTest extends Simulation {

    HttpProtocolBuilder httpProtocol = SimulationConfig.httpProtocol();

    ChainBuilder createRecipe = exec(
            http("Create Recipe")
                    .post(SimulationConfig.RECIPE_SIMPLE_PATH)
                    .header("Authorization", "Bearer #{accessToken}")
                    .body(StringBody("""
                {
                    "title": "Recipe by #{username}",
                    "description": "Progressive load test",
                    "createdBy": "#{userId}"
                }
                """))
                    .asJson()
                    .check(status().in(200, 201, 429, 503))
    );

    ScenarioBuilder progressiveScenario = scenario("Progressive Load - Find Breaking Point")
            .exec(UserRegistrationHelper.generateUserData())
            .exec(UserRegistrationHelper.registerAndAuthenticate())
            .pause(Duration.ofMillis(200))
            .exec(createRecipe);

    {
        setUp(
                progressiveScenario.injectOpen(
                        // Incrementally increase load to find breaking point
                        incrementUsersPerSec(5)  // Start at 5 users/sec
                                .times(5)             // Increase 5 times
                                .eachLevelLasting(Duration.ofSeconds(30))  // Each level lasts 30 seconds
                                .separatedByRampsLasting(Duration.ofSeconds(10)) // 10 second ramp between levels
                                .startingFrom(5)      // Start at 5 users/sec
                        // This creates: 5, 10, 15, 20, 25 users/sec in 30-second intervals
                )
        )
                .protocols(httpProtocol)
                .maxDuration(Duration.ofMinutes(5))
                .assertions(
                        global().responseTime().percentile3().lt(5000)
                );
    }
}
