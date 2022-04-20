import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;



import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;


public class ReqRestTest {

    @Test
    public void loginTest(){

        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body("{\n" +
                        "    \"email\": \"eve.holt@reqres.in\",\n" +
                        "    \"password\": \"cityslicka\"\n" +
                        "}")
                .post("https://reqres.in/api/login")
                .then()
                .log()
                .all()
                // Begin asserts
                .statusCode(200)
                .body("token", notNullValue());
                //.extract()
                //.asString();
    }

    @Test
    public void getSingleUserTest(){
        RestAssured
                .given()
                //.log().all()
                .contentType(ContentType.JSON)
                .get("https://reqres.in/api/users/2")
                .then()
                .log().all()
                // Begin asserts
                .statusCode(200)
                .body("data.id", equalTo(2));


    }
}
