package org.talend.daikon.logging.spring;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ResourceUtils;
import org.talend.daikon.logging.ecs.EcsFields;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.RestAssured;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertNotNull;

@SpringBootTest(classes = SimpleApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class AccessLogbackJsonLayoutTest {

    @Value("${local.server.port}")
    public int port;

    @Before
    public void setup() {
        RestAssured.port = port;
    }

    @Test
    public void testGetSimple() throws IOException {
        given().auth().preemptive().basic("test", "foobar")
                .header("user-agent", "Chrome")
                .when()
                .get("/hello")
                .then()
                .statusCode(200);
        File accessLogFile = ResourceUtils.getFile("classpath:logback-access.log");
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(accessLogFile);
        assertNotNull(jsonNode.get("ecs.version"));
        assertNotNull(jsonNode.get(EcsFields.EVENT_KIND.fieldName));
        assertNotNull(jsonNode.get(EcsFields.EVENT_CATEGORY.fieldName));
        assertNotNull(jsonNode.get(EcsFields.EVENT_TYPE.fieldName));
        assertNotNull(jsonNode.get(EcsFields.MESSAGE.fieldName));

    }
}
