package api.spec;

import api.util.ConfigReader;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

public class RequestSpec {

    private static final String BASE_URL = ConfigReader.get("base.url");

    public static RequestSpecification requestSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .addHeader("Content-Type", "application/json")
                .build();
    }
}
