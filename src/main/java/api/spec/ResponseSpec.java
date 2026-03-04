package api.spec;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;

public class ResponseSpec {

    public static ResponseSpecification ok200() {
        return new ResponseSpecBuilder()
                .expectStatusCode(200)
                .build();
    }

    public static ResponseSpecification notFound404() {
        return new ResponseSpecBuilder()
                .expectStatusCode(404)
                .build();
    }
}
