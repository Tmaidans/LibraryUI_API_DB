package com.library.steps_defs;

import com.library.utility.LibraryAPI_Util;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.Assert;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class us01_StepDefs {

    String token;
    String acceptHeader;
    Response response;
    JsonPath jsonPath;
    String pathParamValue;
    String requestHeader;
    Map<String, String> randomBook = new LinkedHashMap<>();

    @Given("I logged Library api as a {string}")
    public void i_logged_library_api_as_a(String string) {
        token = LibraryAPI_Util.getToken(string);
    }

    @Given("Accept header is {string}")
    public void accept_header_is(String string) {
        acceptHeader = string;
    }

    @When("I send GET request to {string} endpoint")
    public void i_send_get_request_to_endpoint(String string) {

        if (pathParamValue == null) {

            response = given().accept(acceptHeader).log().uri()
                    .header("x-library-token", token)
                    .when()
                    .get("https://library2.cydeo.com/rest/v1" + string);

        } else {
            response = given().accept(acceptHeader).log().uri()
                    .header("x-library-token", token)
                    .pathParam("id", pathParamValue)
                    .when()
                    .get("https://library2.cydeo.com/rest/v1" + string);
        }


    }


    @Then("status code should be {int}")
    public void status_code_should_be(int int1) {
        jsonPath = response.jsonPath();
        Assert.assertEquals(int1, response.statusCode());
        System.out.println("Status Code ---> " + response.statusCode());
    }

    @Then("Response Content type is {string}")
    public void response_content_type_is(String string) {
        String responseContentType = response.header("Content-Type");
        Assert.assertEquals(string, responseContentType);
    }

    @Then("{string} field should not be null")
    public void field_should_not_be_null(String string) {
        assertThat(response.body().path(string), is(notNullValue()));
    }


    //---------------------------->  US02 <-------------------------------------


    @Given("Path param is {string}")
    public void path_param_is(String string) {
        pathParamValue = string;
    }

    @Then("{string} field should be same with path param")
    public void field_should_be_same_with_path_param(String string) {
        String fieldToCheck = response.body().path(string);
        Assert.assertEquals(fieldToCheck, pathParamValue);
    }

    @Then("following fields should not be null")
    public void following_fields_should_not_be_null(List<String> fields) {
        System.out.println("fields = " + fields);
        assertThat(fields, everyItem(notNullValue()));
    }

    //---------------------------->  US03 <-------------------------------------

    @Given("Request Content Type header is {string}")
    public void request_content_type_header_is(String string) {
        requestHeader = string;
    }

    @Given("I create a random {string} as request body")
    public void i_create_a_random_as_request_body(String string) {

        randomBook.put("name", "Crack");
        randomBook.put("isbn", "666");
        randomBook.put("year", "2020");
        randomBook.put("author", "Timmy");
        randomBook.put("book_category_id", "12");
        randomBook.put("description", "Cracky");
        System.out.println("randomBook = " + randomBook);
    }

    @When("I send POST request to {string} endpoint")
    public void i_send_post_request_to_endpoint(String string) {

        response = given().accept(acceptHeader).log().uri()
                .contentType(requestHeader)
                .header("x-library-token", token)
                .body("name=Crack&isbn=666&year=2020&author=Timmy&book_category_id=12&description=Cracky")
                .when()
                .post("https://library2.cydeo.com/rest/v1" + string).prettyPeek();
    }

    @Then("the field value for {string} path should be equal to {string}")
    public void the_field_value_for_path_should_be_equal_to(String string, String string2) {
        Assert.assertEquals(response.body().path(string), string2);
    }


}
