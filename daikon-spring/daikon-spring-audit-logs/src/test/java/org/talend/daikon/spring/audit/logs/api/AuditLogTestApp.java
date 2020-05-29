package org.talend.daikon.spring.audit.logs.api;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.talend.daikon.spring.audit.logs.service.AuditLogContextBuilder;

@RestController
@EnableGlobalMethodSecurity(prePostEnabled = true)
@SpringBootApplication
public class AuditLogTestApp {

    // Basic audit logs properties

    public static final String APPLICATION = "Daikon";

    public static final String EVENT_TYPE = "test type";

    public static final String EVENT_CATEGORY = "test category";

    public static final String EVENT_OPERATION = "test category";

    // User properties

    public static final String USER_ID = UUID.randomUUID().toString();

    public static final String USERNAME = "user";

    public static final String USER_EMAIL = "edwin.jarvis@talend.com";

    public static final String ACCOUNT_ID = UUID.randomUUID().toString();

    // Controller endpoints

    public static final String GET_200_WITH_BODY = "/get/200/body";

    public static final String GET_200_WITHOUT_BODY = "/get/200/nobody";

    public static final String GET_200_ERROR_ONLY = "/get/200/error";

    public static final String GET_200_SUCCESS_ONLY = "/get/200/success";

    public static final String GET_302_SUCCESS_ONLY = "/get/302/success";

    public static final String GET_400_ANNOTATION = "/get/400/annotation";

    public static final String GET_400_EXCEPTION = "/get/400/exception";

    public static final String GET_400_RESPONSE_ENTITY = "/get/400/responseentity";

    public static final String GET_400_ERROR_ONLY = "/get/400/error";

    public static final String GET_400_SUCCESS_ONLY = "/get/400/success";

    public static final String GET_401 = "/get/401";

    public static final String GET_403 = "/get/403";

    public static final String GET_404 = "/get/404";

    public static final String POST_200_FILTERED = "/post/200/filtered";

    public static final String POST_204 = "/post/204";

    public static final String POST_500 = "/post/500";

    public static final String POST_500_FILTERED = "/post/500/filtered";

    // HTTP request & response properties

    public static final String BODY_RESPONSE = "Hello world !";

    public static final String BODY_RESPONSE_400 = "Sorry, bad request :(";

    public static class TestAuditContextFilter implements AuditContextFilter {

        @Override
        public AuditLogContextBuilder filter(AuditLogContextBuilder auditLogContextBuilder, Object requestBody,
                Object responseObject) {
            Object filteredRequest = Optional.ofNullable(requestBody).filter(RequestObject.class::isInstance)
                    .map(RequestObject.class::cast).map(r -> r.setUnsafeProp(null)).orElse(null);
            Object filteredResponse = Optional.ofNullable(responseObject).filter(RequestObject.class::isInstance)
                    .map(RequestObject.class::cast).map(r -> r.setUnsafeProp(null)).orElse(null);
            return auditLogContextBuilder.withRequestBody(filteredRequest).withResponseBody(filteredResponse);
        }
    }

    public static class RequestObject {

        private String safeProp;

        private String unsafeProp;

        public RequestObject() {
        }

        public RequestObject(String safeProp, String unsafeProp) {
            this.safeProp = safeProp;
            this.unsafeProp = unsafeProp;
        }

        public String getSafeProp() {
            return safeProp;
        }

        public RequestObject setSafeProp(String safeProp) {
            this.safeProp = safeProp;
            return this;
        }

        public String getUnsafeProp() {
            return unsafeProp;
        }

        public RequestObject setUnsafeProp(String unsafeProp) {
            this.unsafeProp = unsafeProp;
            return this;
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(AuditLogTestApp.class, args);
    }

    @GetMapping(GET_200_WITH_BODY)
    @GenerateAuditLog(application = APPLICATION, eventType = EVENT_TYPE, eventCategory = EVENT_CATEGORY, eventOperation = EVENT_OPERATION)
    public ResponseEntity get200WithBody() {
        return ResponseEntity.ok(BODY_RESPONSE);
    }

    @GetMapping(GET_200_WITHOUT_BODY)
    @GenerateAuditLog(application = APPLICATION, eventType = EVENT_TYPE, eventCategory = EVENT_CATEGORY, eventOperation = EVENT_OPERATION, includeBodyResponse = false)
    public ResponseEntity get200WithoutBody() {
        return ResponseEntity.ok(BODY_RESPONSE);
    }

    @GetMapping(GET_200_ERROR_ONLY)
    @GenerateAuditLog(application = APPLICATION, eventType = EVENT_TYPE, eventCategory = EVENT_CATEGORY, eventOperation = EVENT_OPERATION, scope = AuditLogScope.ERROR)
    public ResponseEntity get200ErrorOnly() {
        return ResponseEntity.ok(BODY_RESPONSE);
    }

    @GetMapping(GET_200_SUCCESS_ONLY)
    @GenerateAuditLog(application = APPLICATION, eventType = EVENT_TYPE, eventCategory = EVENT_CATEGORY, eventOperation = EVENT_OPERATION, scope = AuditLogScope.SUCCESS)
    public ResponseEntity get200SuccessOnly() {
        return ResponseEntity.ok(BODY_RESPONSE);
    }

    @GetMapping(GET_302_SUCCESS_ONLY)
    @GenerateAuditLog(application = APPLICATION, eventType = EVENT_TYPE, eventCategory = EVENT_CATEGORY, eventOperation = EVENT_OPERATION, scope = AuditLogScope.SUCCESS)
    public ResponseEntity get302SuccessOnly() {
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(GET_200_SUCCESS_ONLY)).build();
    }

    @GetMapping(GET_400_ANNOTATION)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @GenerateAuditLog(application = APPLICATION, eventType = EVENT_TYPE, eventCategory = EVENT_CATEGORY, eventOperation = EVENT_OPERATION)
    public String get400Annotation() {
        return BODY_RESPONSE;
    }

    @GetMapping(GET_400_EXCEPTION)
    @GenerateAuditLog(application = APPLICATION, eventType = EVENT_TYPE, eventCategory = EVENT_CATEGORY, eventOperation = EVENT_OPERATION)
    public ResponseEntity get400Exception() {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    @GetMapping(GET_400_RESPONSE_ENTITY)
    @GenerateAuditLog(application = APPLICATION, eventType = EVENT_TYPE, eventCategory = EVENT_CATEGORY, eventOperation = EVENT_OPERATION)
    public ResponseEntity get400ResponseEntity() {
        return ResponseEntity.badRequest().body(BODY_RESPONSE_400);
    }

    @GetMapping(GET_400_ERROR_ONLY)
    @GenerateAuditLog(application = APPLICATION, eventType = EVENT_TYPE, eventCategory = EVENT_CATEGORY, eventOperation = EVENT_OPERATION, scope = AuditLogScope.ERROR)
    public ResponseEntity get400ErrorOnly() {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    @GetMapping(GET_400_SUCCESS_ONLY)
    @GenerateAuditLog(application = APPLICATION, eventType = EVENT_TYPE, eventCategory = EVENT_CATEGORY, eventOperation = EVENT_OPERATION, scope = AuditLogScope.SUCCESS)
    public ResponseEntity get400SuccessOnly() {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    @GetMapping(GET_401)
    @PreAuthorize("isAuthenticated()")
    @GenerateAuditLog(application = APPLICATION, eventType = EVENT_TYPE, eventCategory = EVENT_CATEGORY, eventOperation = EVENT_OPERATION)
    public ResponseEntity get401() {
        return ResponseEntity.ok(BODY_RESPONSE);
    }

    @GetMapping(GET_403)
    @PreAuthorize("hasRole('GOD')")
    @GenerateAuditLog(application = APPLICATION, eventType = EVENT_TYPE, eventCategory = EVENT_CATEGORY, eventOperation = EVENT_OPERATION)
    public ResponseEntity get403() {
        return ResponseEntity.ok(BODY_RESPONSE);
    }

    @GetMapping(GET_404)
    @GenerateAuditLog(application = APPLICATION, eventType = EVENT_TYPE, eventCategory = EVENT_CATEGORY, eventOperation = EVENT_OPERATION)
    public ResponseEntity get404() {
        return ResponseEntity.notFound().build();
    }

    @PostMapping(path = POST_200_FILTERED, produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = {
            MediaType.APPLICATION_JSON_VALUE })
    @GenerateAuditLog(application = APPLICATION, eventType = EVENT_TYPE, eventCategory = EVENT_CATEGORY, eventOperation = EVENT_OPERATION, filter = TestAuditContextFilter.class)
    public ResponseEntity post200Filtered(@RequestBody RequestObject requestBody) {
        return ResponseEntity.ok(requestBody);
    }

    @PostMapping(POST_204)
    @GenerateAuditLog(application = APPLICATION, eventType = EVENT_TYPE, eventCategory = EVENT_CATEGORY, eventOperation = EVENT_OPERATION)
    public ResponseEntity post204(@RequestBody String requestBody) {
        return ResponseEntity.noContent().build();
    }

    @PostMapping(POST_500)
    @GenerateAuditLog(application = APPLICATION, eventType = EVENT_TYPE, eventCategory = EVENT_CATEGORY, eventOperation = EVENT_OPERATION)
    public ResponseEntity post500(@RequestBody String requestBody) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @PostMapping(path = POST_500_FILTERED, produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = {
            MediaType.APPLICATION_JSON_VALUE })
    @GenerateAuditLog(application = APPLICATION, eventType = EVENT_TYPE, eventCategory = EVENT_CATEGORY, eventOperation = EVENT_OPERATION, filter = TestAuditContextFilter.class)
    public ResponseEntity post500Filtered(@RequestBody RequestObject requestBody) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
