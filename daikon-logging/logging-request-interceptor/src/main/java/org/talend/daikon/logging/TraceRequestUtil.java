package org.talend.daikon.logging;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class TraceRequestUtil {

    private TraceRequestUtil () {
        //Not instanciate
    }
    public static String getRequestBody(byte[] body) throws UnsupportedEncodingException {
        if (body != null && body.length > 0) {
            return getBodyAsJson(new String(body, "UTF-8"));
        } else {
            return null;
        }
    }

    public static String getBodyAsJson(String bodyString) {
        if (bodyString == null || bodyString.length() == 0) {
            return null;
        } else {
            if (isValidJSON(bodyString)) {
                return bodyString;
            } else {
                bodyString.replaceAll("\"", "\\\"");
                return "\"" + bodyString + "\"";
            }
        }
    }

    public static boolean isValidJSON(final String json) {
        boolean valid = false;

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.readTree(json);
        } catch (IOException e) {
            valid = false;
        }
        return valid;
    }
}
