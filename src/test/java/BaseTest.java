import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.Filter;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseTest {

    private static final Logger logger = LogManager.getLogger(ReqRestTestV4.class);

    @BeforeAll
    public static void setup(){
        // Logger
        logger.info("Iniciando la configuración");
        RestAssured.requestSpecification = defaultRequestSpecification();
        logger.info("Configuración Exitosa.");

    }

    public static RequestSpecification defaultRequestSpecification(){
        List<Filter> filters = new ArrayList<>();
        filters.add(new RequestLoggingFilter());
        filters.add(new ResponseLoggingFilter());

        return new RequestSpecBuilder().setBaseUri(ConfigVariables.getHost())
                .setBasePath(ConfigVariables.getPath())
                .addFilters(filters)
                .setContentType(ContentType.JSON).build();
    }

    public RequestSpecification prodRequestSpecification(){

        return new RequestSpecBuilder().setBaseUri("https://prd.reqres.in")
                .setBasePath("/api")
                .setContentType(ContentType.JSON)
                .build();
    }

    public ResponseSpecification defaultResponseSpecification(){
        return new ResponseSpecBuilder()
                .expectStatusCode(HttpStatus.SC_OK)
                .expectContentType(ContentType.JSON)
                .build();
    }
}
