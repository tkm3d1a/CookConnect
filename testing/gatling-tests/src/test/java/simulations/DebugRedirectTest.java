package simulations;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import simulations.config.SimulationConfig;
import simulations.helpers.UserRegistrationHelper;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class DebugRedirectTest extends Simulation {

    HttpProtocolBuilder httpProtocol = http
            .baseUrl(SimulationConfig.GATEWAY_URL)
            .acceptHeader("application/json")
            .contentTypeHeader("application/json")
            // DISABLE auto-redirect to see what's happening
            .disableFollowRedirect();

    ChainBuilder testGetAll = exec(
            http("Get All Recipes (No Auto Redirect)")
                    .get(SimulationConfig.RECIPE_GET_ALL_PATH)
                    .header("Authorization", "Bearer #{accessToken}")
                    .check(status().saveAs("statusCode"))
                    .check(header("Location").optional().saveAs("redirectLocation"))
                    .check(bodyString().saveAs("responseBody"))
    )
            .exec(session -> {
                int status = session.getInt("statusCode");
                String location = session.getString("redirectLocation");
                String body = session.getString("responseBody");

                System.out.println("=== Response Debug ===");
                System.out.println("Status: " + status);
                System.out.println("Redirect Location: " + (location != null ? location : "None"));
                System.out.println("Body preview: " + (body != null ? body.substring(0, Math.min(200, body.length())) : "Empty"));
                System.out.println("====================\n");

                return session;
            });

    ScenarioBuilder debugScenario = scenario("Debug Redirect")
            .exec(UserRegistrationHelper.generateUserData())
            .exec(UserRegistrationHelper.registerAndAuthenticate())
            .exec(testGetAll);

    {
        setUp(debugScenario.injectOpen(atOnceUsers(1)))
                .protocols(httpProtocol)
                .maxDuration(Duration.ofSeconds(30));
    }
}
