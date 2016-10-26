package org.talend.daikon.serialize.jsonschema;

import static org.junit.Assert.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;

import org.junit.Test;

public class JsonSchemaUtilTest {

    @Test
    public void test() throws URISyntaxException, IOException, ClassNotFoundException, NoSuchMethodException,
            InstantiationException, IllegalAccessException, InvocationTargetException {
        String jsonDataStr = readJson("FullExampleJsonData.json");

        FullExampleProperties properties = JsonSchemaUtil.fromJson(jsonDataStr,
                (FullExampleProperties) new FullExampleProperties("").init());

        String jsonStr = readJson("FullExampleProperties.json");
        String jsonResult = JsonSchemaUtil.toJson(properties, true);
        assertEquals(jsonStr, jsonResult);
    }

    public static String readJson(String path) throws URISyntaxException, IOException {
        java.net.URL url = JsonSchemaUtilTest.class.getResource(path);
        java.nio.file.Path resPath = java.nio.file.Paths.get(url.toURI());
        return new String(java.nio.file.Files.readAllBytes(resPath), "UTF8").trim();
    }
}
