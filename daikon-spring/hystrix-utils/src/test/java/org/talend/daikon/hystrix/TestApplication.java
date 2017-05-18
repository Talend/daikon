package org.talend.daikon.hystrix;

import static org.apache.http.HttpHeaders.AUTHORIZATION;

import java.io.StringWriter;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.talend.daikon.exception.TalendRuntimeException;
import org.talend.daikon.hystrix.processors.DefaultSecurityProcessor;
import org.talend.daikon.hystrix.processors.ErrorProcessor;
import org.talend.daikon.hystrix.processors.SecurityProcessor;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
public class TestApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }

    @Bean
    public CloseableHttpClient httpClient() {
        return HttpClientBuilder.create().build();
    }

    @Bean
    public ErrorProcessor errorProcessor(ObjectMapper objectMapper) {
        return new DefaultSecurityProcessor(objectMapper);
    }

    @Bean
    public SecurityProcessor securityProcessor() {
        return request -> request.addHeader(AUTHORIZATION, "#1234");
    }

    @ControllerAdvice
    public static class TalendExceptionController {

        @ExceptionHandler(TalendRuntimeException.class)
        @ResponseBody
        public String handleError(TalendRuntimeException e) {
            final RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
            if (attributes != null && attributes instanceof ServletRequestAttributes) {
                HttpServletResponse response = ((ServletRequestAttributes) attributes).getResponse();
                response.setStatus(e.getCode().getHttpStatus());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            }
            final StringWriter message = new StringWriter();
            e.writeTo(message);
            return message.toString();
        }
    }

}
