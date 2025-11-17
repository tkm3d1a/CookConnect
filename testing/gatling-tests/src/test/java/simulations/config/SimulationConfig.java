package simulations.config;

import io.gatling.javaapi.http.HttpProtocolBuilder;

import static io.gatling.javaapi.http.HttpDsl.http;

public class SimulationConfig {
    public static final String GATEWAY_URL = "http://localhost:8073";
    public static final String KEYCLOAK_URL = "http://localhost:8074";
    public static final String KEYCLOAK_REALM = "tkforgeworks";
    public static final String CLIENT_ID = "cookconnect";
    public static final String CLIENT_SECRET = System.getenv("KEYCLOAK_CLIENT_SECRET");
    public static final String TEST_USERNAME = "tim_test";
    public static final String TEST_PASSWORD = "tim1234";

    public static final String RECIPE_BASE_PATH = "/recipes/api/v1";
    public static final String RECIPE_SIMPLE_PATH = RECIPE_BASE_PATH + "/simple";
    public static final String RECIPE_DETAILED_PATH = RECIPE_BASE_PATH + "/detailed";
    public static final String RECIPE_GET_ALL_PATH = RECIPE_BASE_PATH + "/";

    public static final String USER_BASE_PATH = "/users/api/v1"; // UPDATE THIS!
    public static final String USER_REGISTER_PATH = USER_BASE_PATH + "/register";

    public static HttpProtocolBuilder httpProtocol() {
        return http
                .baseUrl(GATEWAY_URL)
                .acceptHeader("application/json")
                .contentTypeHeader("application/json")
                .userAgentHeader("Gatling Performance Test");
    }

    public static String keycloakTokenEndpoint() {
        return KEYCLOAK_URL + "/realms/" + KEYCLOAK_REALM + "/protocol/openid-connect/token";
    }

    public static final Integer[] ACCEPTABLE_STATUSES = {200, 201, 429, 503, 504};
}
