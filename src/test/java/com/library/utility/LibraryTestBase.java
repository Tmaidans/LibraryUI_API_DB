package com.library.utility;

import io.cucumber.java.BeforeAll;
import io.restassured.RestAssured;

public abstract class LibraryTestBase {

    @BeforeAll
    public static void init(){
        RestAssured.baseURI = "https://library2.cydeo.com/rest/v1";
    }

}
