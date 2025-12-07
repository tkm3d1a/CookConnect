package simulations.helpers;

import io.gatling.javaapi.core.ChainBuilder;
import simulations.config.SimulationConfig;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class RecipeServiceHelper {

    public static ChainBuilder createRecipeAsUser() {
        return exec(
                http("Create Recipe as User")
                        .post(SimulationConfig.RECIPE_SIMPLE_PATH)
                        .header("Authorization", session -> AuthenticationHelper.authHeader(session))
                        .body(StringBody(session ->
                                String.format("""
                                {
                                    "title": "%s",
                                    "description": "%s",
                                    "createdBy": "%s"
                                }
                                """,
                                        session.getString("recipeTitle"),
                                        session.getString("recipeDescription"),
                                        session.getString("userId")
                                )
                        ))
                        .asJson()
                        .check(status().saveAs("recipeCreateStatus"))
                        .check(jsonPath("$.id").optional().saveAs("recipeId"))
                        .check(jsonPath("$.createdByUsername").optional().saveAs("recipeCreatedBy"))
        )
                // Only log if successful AND recipeId exists
                .doIf(session -> session.getInt("recipeCreateStatus") == 201 && session.contains("recipeId")).then(
                        exec(session -> {
                            System.out.println("[RECIPE] Created recipe ID: " + session.getInt("recipeId") +
                                    " for user: " + session.getString("recipeCreatedBy"));
                            return session;
                        })
                )
                // Log error if failed OR recipeId missing
                .doIf(session -> session.getInt("recipeCreateStatus") != 201 || !session.contains("recipeId")).then(
                        exec(session -> {
                            System.err.println("[RECIPE ERROR] Failed to create recipe or missing recipeId - User: " +
                                    session.getString("username") +
                                    " - Status: " + session.getInt("recipeCreateStatus") +
                                    " - Has recipeId: " + session.contains("recipeId"));
                            return session;
                        })
                );
    }

    public static ChainBuilder createAnonymousRecipe() {
        return exec(
                http("Create Anonymous Recipe")
                        .post(SimulationConfig.RECIPE_SIMPLE_PATH)
                        .header("Authorization", session -> AuthenticationHelper.authHeader(session))
                        .body(StringBody(session ->
                                String.format("""
                                {
                                    "title": "%s",
                                    "description": "%s"
                                }
                                """,
                                        session.getString("recipeTitle"),
                                        session.getString("recipeDescription")
                                )
                        ))
                        .asJson()
                        .check(status().saveAs("recipeCreateStatus"))
                        .check(jsonPath("$.id").optional().saveAs("recipeId"))
        )
                // Only log if successful AND recipeId exists
                .doIf(session -> session.getInt("recipeCreateStatus") == 201 && session.contains("recipeId")).then(
                        exec(session -> {
                            System.out.println("[RECIPE] Created anonymous recipe ID: " + session.getInt("recipeId"));
                            return session;
                        })
                )
                // Log error if failed OR recipeId missing
                .doIf(session -> session.getInt("recipeCreateStatus") != 201 || !session.contains("recipeId")).then(
                        exec(session -> {
                            System.err.println("[RECIPE ERROR] Failed to create anonymous recipe or missing recipeId" +
                                    " - Status: " + session.getInt("recipeCreateStatus") +
                                    " - Has recipeId: " + session.contains("recipeId"));
                            return session;
                        })
                );
    }

    public static ChainBuilder getAllRecipes(int page, int size) {
        return exec(
                http("Get All Recipes")
                        .get(SimulationConfig.RECIPE_GET_ALL_PATH)
                        .header("Authorization", session -> AuthenticationHelper.authHeader(session))
                        .queryParam("page", String.valueOf(page))
                        .queryParam("size", String.valueOf(size))
                        .check(status().is(200))
                        .check(jsonPath("$.totalElements").saveAs("totalRecipes"))
                        .check(jsonPath("$.numberOfElements").saveAs("pageRecipes"))
        )
                .exec(session -> {
                    System.out.println("[RECIPE] Total recipes: " + session.getInt("totalRecipes") +
                            ", on this page: " + session.getInt("pageRecipes"));
                    return session;
                });
    }

    public static ChainBuilder generateRecipeData() {
        return exec(session -> {
            int random = java.util.concurrent.ThreadLocalRandom.current().nextInt(1000, 9999);
            return session
                    .set("recipeTitle", "Gatling Recipe - " + random)
                    .set("recipeDescription", "Test description for load testing - " + random);
        });
    }
}