package simulations;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import simulations.config.SimulationConfig;
import simulations.helpers.*;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.*;

public class KafkaEventFlowSimulation extends Simulation {

    // =========================================================================
    // SCENARIO 1: Full User Journey - Account Closure (Triggers ACCOUNT_CLOSED event)
    // =========================================================================
    ScenarioBuilder userAccountClosureScenario = scenario("User Journey - Account Closure")
            // Generate user data
            .exec(UserRegistrationHelper.generateUserData())

            // Register new user
            .exec(UserRegistrationHelper.registerUser())
            .pause(Duration.ofMillis(500))

            // Authenticate
            .exec(AuthenticationHelper.authenticate())
            .pause(Duration.ofSeconds(1))

            // Create 3 recipes as this user
            .repeat(3, "recipeCounter").on(
                    exec(RecipeServiceHelper.generateRecipeData())
                            .exec(RecipeServiceHelper.createRecipeAsUser())
                            .pause(Duration.ofMillis(500))
            )

            // Get all recipes to see current state
            .exec(RecipeServiceHelper.getAllRecipes(0, 20))
            .pause(Duration.ofSeconds(1))

            // Create social interaction
            .exec(SocialServiceHelper.createSocialInteraction())
            .pause(Duration.ofSeconds(1))

            // *** TRIGGER KAFKA EVENT - Close Account ***
            .exec(UserServiceHelper.closeAccount())

            // Wait for Kafka processing
            .pause(Duration.ofSeconds(3))

            // Verify recipes were updated
            .exec(RecipeServiceHelper.getAllRecipes(0, 20))
            .exec(session -> {
                System.out.println("[VERIFICATION] Check logs for Kafka message consumption");
                System.out.println("[VERIFICATION] Recipes should show '(CLOSED)' in createdByUsername");
                return session;
            });

    // =========================================================================
    // SCENARIO 2: User Deletion (Triggers ACCOUNT_DELETED event + cleanup)
    // =========================================================================
    ScenarioBuilder userAccountDeletionScenario = scenario("User Journey - Account Deletion")
            // Generate user data
            .exec(UserRegistrationHelper.generateUserData())

            // Register new user
            .exec(UserRegistrationHelper.registerUser())
            .pause(Duration.ofMillis(500))

            // Authenticate
            .exec(AuthenticationHelper.authenticate())
            .pause(Duration.ofSeconds(1))

            // Create 2 recipes
            .repeat(2, "recipeCounter").on(
                    exec(RecipeServiceHelper.generateRecipeData())
                            .exec(RecipeServiceHelper.createRecipeAsUser())
                            .pause(Duration.ofMillis(500))
            )

            // Create social interaction
            .exec(SocialServiceHelper.createSocialInteraction())
            .pause(Duration.ofSeconds(1))

            // *** TRIGGER KAFKA EVENT - Delete Account ***
            .exec(UserServiceHelper.deleteAccount())

            // Wait for Kafka processing and cleanup
            .pause(Duration.ofSeconds(2))

            .exec(session -> {
                System.out.println("[CLEANUP] User deleted from DB and Keycloak");
                return session;
            });

    // =========================================================================
    // SCENARIO 3: Anonymous Recipe Creation (No events)
    // =========================================================================
    ScenarioBuilder anonymousRecipeScenario = scenario("Anonymous Recipe Creation")
            // Create a new user instead of using pre-existing one
            .exec(UserRegistrationHelper.generateUserData())
            .exec(UserRegistrationHelper.registerUser())
            .pause(Duration.ofMillis(500))
            .exec(AuthenticationHelper.authenticate())
            .pause(Duration.ofSeconds(1))

            // Create 5 anonymous recipes
            .repeat(5, "anonRecipeCounter").on(
                    exec(RecipeServiceHelper.generateRecipeData())
                            .exec(RecipeServiceHelper.createAnonymousRecipe())
                            .pause(Duration.ofMillis(500))
            )

            // Clean up - delete the user
            .exec(UserServiceHelper.deleteAccount());

    // =========================================================================
    // SCENARIO 4: Social Following Flow (No user events)
    // =========================================================================
    ScenarioBuilder socialFollowingScenario = scenario("Social Following Flow")
            // Generate user data
            .exec(UserRegistrationHelper.generateUserData())

            // Register and auth
            .exec(UserRegistrationHelper.registerUser())
            .pause(Duration.ofMillis(500))
            .exec(AuthenticationHelper.authenticate())
            .pause(Duration.ofSeconds(1))

            // Create social interaction
            .exec(SocialServiceHelper.createSocialInteraction())
            .pause(Duration.ofSeconds(1))

            // Get all users to find someone to follow
            .exec(UserServiceHelper.getAllUsers())

            // Follow a user (if found)
            .doIf(session -> session.contains("firstUserId")).then(
                    exec(SocialServiceHelper.followUser("firstUserId"))
                            .pause(Duration.ofSeconds(1))

                            // Unfollow the user
                            .exec(SocialServiceHelper.unfollowUser("firstUserId"))
            );

    // =========================================================================
    // LOAD SETUP
    // =========================================================================
    {
        setUp(
                userAccountClosureScenario.injectOpen(
                        rampUsers(5).during(Duration.ofSeconds(10))
                ),
                userAccountDeletionScenario.injectOpen(
                        rampUsers(3).during(Duration.ofSeconds(10))
                ),
                anonymousRecipeScenario.injectOpen(
                        atOnceUsers(2)
                ),
                socialFollowingScenario.injectOpen(
                        rampUsers(3).during(Duration.ofSeconds(10))
                )
        ).protocols(SimulationConfig.httpProtocol())
                .assertions(
                        global().failedRequests().count().is(0L)
                );
    }
}
