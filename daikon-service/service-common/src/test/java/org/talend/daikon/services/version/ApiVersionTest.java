package org.talend.daikon.services.version;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import com.jayway.restassured.RestAssured;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class ApiVersionTest {

    @LocalServerPort
    protected int localServerPort;

    @Before
    public void setUp() {
        // ensure any call from restassured goes to our server instance
        RestAssured.port = localServerPort;
    }

    @Test
    public void test() {
        given().when().get("v0/classVersionRoute").then().assertThat().body(is("foo"));
        given().when().get("v1/classVersionRoute").then().assertThat().body(is("foo"));
        given().when().get("v2-SNAPSHOT/methodVersionRoute").then().assertThat().body(is("bar"));
        given().when().get("vBAR/methodVersionRoute").then().assertThat().body(is("bar"));
        given().when().get("vnew/methodVersionRoute").then().assertThat().body(is("newbar"));
    }

}
