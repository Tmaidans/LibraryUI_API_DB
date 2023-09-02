package com.library.steps_defs;

import com.github.javafaker.Faker;
import com.library.pages.BasePage;
import com.library.pages.BookPage;
import com.library.pages.LoginPage;
import com.library.utility.BrowserUtil;
import com.library.utility.DB_Util;
import com.library.utility.Driver;
import com.library.utility.LibraryAPI_Util;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.jsoup.Connection;
import org.junit.Assert;
import org.openqa.selenium.By;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class us01_StepDefs extends BasePage {

    String token;
    String acceptHeader;
    Response response;
    JsonPath jsonPath;
    String pathParamValue;
    String randomBook;
    String requestHeader;
    String isbn;
    String bookName;
    String year;
    String author;

    String userFullName;
    String userEmail;
    String userPassword;
    int userGroupId;
    String userStatus;
    String userStartDate;
    String userEndDate;
    String userAddress;
    String randomUser;
    String createdUserID;
    List<String> expectedUserInformation;

    LoginPage loginPage = new LoginPage();
    BookPage bookPage = new BookPage();

    @Given("I logged Library api as a {string}")
    public void i_logged_library_api_as_a(String string) {
        token = LibraryAPI_Util.getToken(string);
    }

    @Given("Accept header is {string}")
    public void accept_header_is(String string) {
        acceptHeader=string;
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
    public void status_code_should_be(int expectedResponseCode) {
        jsonPath = response.jsonPath();
        Assert.assertEquals(expectedResponseCode, response.statusCode());
            }

    @Then("Response Content type is {string}")
    public void response_content_type_is(String expectedResponseContentType) {
        String responseContentType = response.header("Content-Type");
        Assert.assertEquals(expectedResponseContentType,responseContentType);
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
        System.out.println("requestHeader = " + string);
    }

    @Given("I create a random {string} as request body")
    public void i_create_a_random_as_request_body(String string) {
        Faker faker = new Faker();

        if (string.equals("book")) {
            bookName = "Tim and " + faker.book().title();
            isbn = "" + faker.number().randomNumber(6, false);
            year = "" + faker.number().numberBetween(1900, 2023);
            author = "" + faker.book().author();
            String book_category_id = "book_category_id=4";
            String description = "" + faker.lorem().paragraph();

            randomBook = "name=" + bookName + "&isbn=" + isbn + "&year=" + year + "&author=" + author + "&book_category_id=4&description=" + description;
        } else if (string.equals("user")) {

            userFullName = ""+faker.name().fullName();
            userEmail = ""+faker.number().randomNumber(3,true)+"tim@tim.com";
            userPassword=""+faker.internet().password();
            System.out.println("userPassword1 = " + userPassword);
            userGroupId=2;
            userStatus="ACTIVE";
            userStartDate="2020-02-03";
            userEndDate="2023-02-03";
            userAddress="180 NE 29th street";

            randomUser = "full_name="+userFullName+"&email="+userEmail+"&password="+userPassword+"&user_group_id="+userGroupId+
                    "&status="+userStatus+"&start_date="+userStartDate+"&end_date="+userEndDate+"&address="+userAddress;

            expectedUserInformation = Arrays.asList(userFullName,userEmail,userAddress);
            System.out.println("User information ---> " + expectedUserInformation);
            System.out.println("userPassword2 = " + userPassword);
        }


    }

    @When("I send POST request to {string} endpoint")
        public void i_send_post_request_to_endpoint (String string) {

        if (string.equals("/add_book")){
            response = given().accept(acceptHeader).log().uri()
                    .contentType("application/x-www-form-urlencoded")
                    .header("x-library-token", token)
                    .body(randomBook)
                    .when()
                    .post("https://library2.cydeo.com/rest/v1" + string).prettyPeek();

        System.out.println(response);
            System.out.println("userPassword3 = " + userPassword);

    } else if (string.equals("/add_user")) {
            response = given().accept(acceptHeader).log().uri()
                    .contentType("application/x-www-form-urlencoded")
                    .header("x-library-token", token)
                    .body(randomUser)
                    .when()
                    .post("https://library2.cydeo.com/rest/v1" + string).prettyPeek();

            createdUserID=response.body().path("user_id");
            System.out.println("createdUserID = " + createdUserID);
            System.out.println(response);

        } else if (string.equals("/decode")) {
            response = given().accept(acceptHeader).log().uri()
                    .contentType("application/x-www-form-urlencoded")
                    .header("x-library-token", token)
                    .body("token="+token)
                    .when()
                    .post("https://library2.cydeo.com/rest/v1" + string).prettyPeek();

        }
    }

    @Then("the field value for {string} path should be equal to {string}")
    public void the_field_value_for_path_should_be_equal_to(String string, String string2) {
        Assert.assertEquals(response.body().path(string),string2);

    }
//---------------------------->  US03 - Scenario 2 <-------------------------------------


    @Given("I logged in Library UI as {string}")
    public void i_logged_in_library_ui_as(String string) {
        loginPage.login(string);
        BrowserUtil.waitFor(3);
    }
    @Given("I navigate to {string} page")
    public void i_navigate_to_page(String string) {
        navigateModule(string);
    }
    @Then("UI, Database and API created book information must match")
    public void ui_database_and_api_created_book_information_must_match() {
        bookPage.search.click();
        bookPage.search.sendKeys(bookName);
        BrowserUtil.waitFor(2);

        String expectedBookName = bookName;
        String expectedYear = year;
        String expectedAuthor = author;
        String expectedIsbn = isbn;

        System.out.println("expectedBookName = " + expectedBookName);
        System.out.println("expectedYear = " + expectedYear);
        System.out.println("expectedAuthor = " + expectedAuthor);
        System.out.println("expectedIsbn = " + expectedIsbn);

        String actualBookName = bookPage.nameInTable.getText();
        String actualYear = bookPage.yearInTable.getText();
        String actualAuthor = bookPage.authorInTable.getText();
        String actualIsbn = bookPage.isbnInTable.getText();

        System.out.println("actualBookName = " + actualBookName);
        System.out.println("actualYear = " + actualYear);
        System.out.println("actualAuthor = " + actualAuthor);
        System.out.println("actualIsbn = " + actualIsbn);

        Assert.assertEquals(expectedAuthor,actualAuthor);
        Assert.assertEquals(expectedIsbn,actualIsbn);
        Assert.assertEquals(expectedYear,actualYear);
        Assert.assertEquals(expectedBookName,actualBookName);
    }

//---------------------------->  US04 - Scenario 2 <-------------------------------------

    @Then("created user information should match with Database")
    public void created_user_information_should_match_with_database() {
        System.out.println("userPassword4 = " + userPassword);
        DB_Util.runQuery("select full_name,email,address from users\n" +
                "where id="+createdUserID);
        List<String>actualUserInformation = DB_Util.getRowDataAsList(1);
        System.out.println("actualUserInformation = " + actualUserInformation);
        System.out.println("expectedUserInformation = " + expectedUserInformation);

        Assert.assertEquals(expectedUserInformation,actualUserInformation);

    }
    @Then("created user should be able to login Library UI")
    public void created_user_should_be_able_to_login_library_ui() {
        BrowserUtil.waitFor(2);
        loginPage.login(userEmail,userPassword);
        BrowserUtil.waitFor(2);
    }
    @Then("created user name should appear in Dashboard Page")
    public void created_user_name_should_appear_in_dashboard_page() {
        String nameOnDashBoard = Driver.getDriver().findElement(By.xpath("//a[@id=\"navbarDropdown\"]//span")).getText();
        Assert.assertEquals(userFullName,nameOnDashBoard);
    }

    //---------------------------->  US05 <-------------------------------------

    @Given("I logged Library api with credentials {string} and {string}")
    public void i_logged_library_api_with_credentials_and(String email, String password) {
        token = LibraryAPI_Util.getToken(email,password);
    }

}