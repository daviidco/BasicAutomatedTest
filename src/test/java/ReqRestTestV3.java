import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.path.json.JsonPath.from;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

// Class improved. global method setup
// Class improved. using databind robot pojo plugin - library jackson to des-serialize
public class ReqRestTestV3 {

    @BeforeEach
    public void setup(){
        // Main global routes
        RestAssured.baseURI = "https://reqres.in";
        RestAssured.basePath = "/api";
        // Instruction to logging request -  Not necessary (.log(), .all())
        // Before do request and after do request
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .build();
    }

    @Test
    public void loginTest(){
        given()
            .contentType(ContentType.JSON)
            .body("{\n" +
                    "    \"email\": \"eve.holt@reqres.in\",\n" +
                    "    \"password\": \"cityslicka\"\n" +
                    "}")
            .post("login")
            .then()
            // Begin asserts
            .statusCode(200)
            .body("token", notNullValue());
    }

    @Test
    public void getSingleUserTest(){
        given()
            .contentType(ContentType.JSON)
            .get("users/2")
            .then()
            // Begin asserts
            .statusCode(HttpStatus.SC_OK)
            .body("data.id", equalTo(2));


    }

    @Test
    public void deleteUserTest(){
        given()
                .contentType(ContentType.JSON)
                .delete("users/2")
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT);
    }

    @Test
    public void patchUserTest(){
        String nameUpdated = given()
                .when()
                .body("{\n" +
                        "    \"name\": \"morpheus\",\n" +
                        "    \"job\": \"zion resident\"\n" +
                        "}")
                .contentType(ContentType.JSON)
                .patch("users/2")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract().jsonPath().getString("name");
        assertThat(nameUpdated, equalTo("morpheus"));
    }

    @Test
    public void putUserTest(){
        String jobUpdated = given()
                .when()
                .body("{\n" +
                        "    \"name\": \"morpheus\",\n" +
                        "    \"job\": \"zion resident\"\n" +
                        "}")
                .contentType(ContentType.JSON)
                .put("users/2")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract().jsonPath().getString("job");
        assertThat(jobUpdated, equalTo("zion resident"));
    }

    @Test
    public void getAllUsersTest(){
        Response response = given()
                .get("users?page=2");
        Headers headers = response.getHeaders();
        int statusCode = response.getStatusCode();
        String body = response.getBody().asString();
        String contenType = response.getContentType();

        assertThat(statusCode, equalTo(HttpStatus.SC_OK));
        System.out.println("body: " + body);
        System.out.println("content type: "  + contenType);
        System.out.println("Headers: " + headers);
        System.out.println("***************************");
        System.out.println("***************************");
        System.out.println(headers.get("Content-Type"));
        System.out.println(headers.get("Transfer-Encoding"));
    }

    @Test
    public void getAllUsersTest2(){
        String response = given()
                .when()
                .get("users?page=2")
                .then()
                .extract()
                .body()
                .asString();
        int page = from(response).get("page");
        int totalPages = from(response).get("total_pages");
        int idFirstUser = from(response).get("data[0].id");
        System.out.println("page: " + page);
        System.out.println("total Pages: " + totalPages);
        System.out.println("id First User: " + idFirstUser);

        List<Map> usersWithIdGreaterThan10 = from(response).get("data.findAll { user -> user.id > 10}");
        String email = usersWithIdGreaterThan10.get(0).get("email").toString();
        System.out.println("email: " + email);

        List<Map> user = from(response).get("data.findAll { user -> user.id > 10 && user.last_name == 'Howell'}");
        String id = user.get(0).get("id").toString();
        System.out.println("id: " + id);
    }

    @Test
    public void createUserTest(){
        String response = given()
                .when()
                .body("{\n" +
                        "    \"name\": \"morpheus\",\n" +
                        "    \"job\": \"zion resident\"\n" +
                        "}")
                .post("users")
                .then().extract().body().asString();

        User user = from(response).getObject("", User.class);
        System.out.println("Created at: " + user.getCreatedAt());
        System.out.println("id: " + user.getId());
        System.out.println("Name: " + user.getName());
        System.out.println("Job: " + user.getJob());

    }
    @Test
    public void registerUserTest(){

        // Model
        CreateUserRequest user = new CreateUserRequest();
        user.setEmail("eve.holt@reqres.in");
        user.setPassword("pistol");


        CreateUserResponse userResponse = given()
                .when()
                .body(user)
                .post("register")
                .then()
                // previous validation
                .statusCode(HttpStatus.SC_OK)
                .contentType(equalTo("application/json; charset=utf-8"))
                .extract()
                .body()
                .as(CreateUserResponse.class);

       assertThat(userResponse.getId(), equalTo(4));
       assertThat(userResponse.getToken(), equalTo("QpwL5tke4Pnpja7X4"));
    }
}
