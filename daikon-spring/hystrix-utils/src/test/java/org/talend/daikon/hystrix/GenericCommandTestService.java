// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// https://github.com/Talend/data-prep/blob/master/LICENSE
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package org.talend.daikon.hystrix;

import static org.apache.http.HttpHeaders.AUTHORIZATION;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.talend.daikon.exception.TalendRuntimeException;
import org.talend.daikon.exception.error.ErrorCode;
import org.talend.daikon.hystrix.processors.InternalErrorCodes;

import com.netflix.hystrix.exception.HystrixRuntimeException;

@RestController
public class GenericCommandTestService {

    private static void setHttpStatus(int status) {
        final RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes != null && attributes instanceof ServletRequestAttributes) {
            ((ServletRequestAttributes) attributes).getResponse().setStatus(status);
        }
    }

    @RequestMapping(value = "/command/test/success", method = RequestMethod.GET, consumes = MediaType.ALL_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public String success() throws IOException {
        return "success";
    }

    @RequestMapping(value = "/command/test/authentication/token", method = RequestMethod.GET, consumes = MediaType.ALL_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public String testToken(@RequestHeader(value = AUTHORIZATION) String token) throws IOException {
        if (StringUtils.isBlank(token)) {
            throw new TalendRuntimeException(InternalErrorCodes.UNEXPECTED_EXCEPTION);
        }
        return token;
    }

    @RequestMapping(value = "/command/test/success_with_unknown", method = RequestMethod.GET, consumes = MediaType.ALL_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public String success_with_unknown() throws IOException {
        setHttpStatus(HttpStatus.ACCEPTED.value());
        return "success";
    }

    @RequestMapping(value = "/command/test/fail_with_400", method = RequestMethod.GET, consumes = MediaType.ALL_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public String fail_with_400() throws IOException {
        throw new TalendRuntimeException(TestErrorCodes.A_400_ERROR);
    }

    @RequestMapping(value = "/command/test/fail_with_500", method = RequestMethod.GET, consumes = MediaType.ALL_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public String fail_with_500() throws IOException {
        throw new TalendRuntimeException(TestErrorCodes.A_500_ERROR);
    }

    @RequestMapping(value = "/command/test/fail_with_unknown", method = RequestMethod.GET, consumes = MediaType.ALL_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public void fail_with_unknown() throws IOException {
        setHttpStatus(HttpStatus.I_AM_A_TEAPOT.value());
    }

    @RequestMapping(value = "/command/test/unexpected", method = RequestMethod.GET, consumes = MediaType.ALL_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public void fail_with_unexpected() {
        throw new IndexOutOfBoundsException();
    }

    @RequestMapping(value = "/command/test/fail_bad_request_exception", method = RequestMethod.GET, consumes = MediaType.ALL_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public void fail_with_bad_request_exception() {
        throw new HystrixRuntimeException(HystrixRuntimeException.FailureType.BAD_REQUEST_EXCEPTION, null, "Bad request", null,
                null);
    }

    @RequestMapping(value = "/command/test/fail_command_exception", method = RequestMethod.GET, consumes = MediaType.ALL_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public void fail_with_command_exception() {
        throw new HystrixRuntimeException(HystrixRuntimeException.FailureType.COMMAND_EXCEPTION, null, "Command Exception", null,
                null);
    }

    @RequestMapping(value = "/command/test/fail_rejected_semaphore_execution", method = RequestMethod.GET, consumes = MediaType.ALL_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public void fail_with_rejected_semaphore_execution() {
        throw new HystrixRuntimeException(HystrixRuntimeException.FailureType.REJECTED_SEMAPHORE_EXECUTION, null,
                "Rejected Semaphore execution", null, null);
    }

    @RequestMapping(value = "/command/test/fail_rejected_execution", method = RequestMethod.GET, consumes = MediaType.ALL_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public void fail_with_rejected_thread_execution() {
        throw new HystrixRuntimeException(HystrixRuntimeException.FailureType.REJECTED_THREAD_EXECUTION, null,
                "Rejected thread execution", null, null);
    }

    @RequestMapping(value = "/command/test/fail_rejected_semaphore_fallback", method = RequestMethod.GET, consumes = MediaType.ALL_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public void fail_with_rejected_semaphore_fallback() {
        throw new HystrixRuntimeException(HystrixRuntimeException.FailureType.REJECTED_SEMAPHORE_FALLBACK, null,
                "Rejected semaphore fallback", null, null);
    }

    @RequestMapping(value = "/command/test/fail_short_circuit", method = RequestMethod.GET, consumes = MediaType.ALL_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public void fail_with_short_circuit() {
        throw new HystrixRuntimeException(HystrixRuntimeException.FailureType.SHORTCIRCUIT, null, "Short circuit", null, null);
    }

    @RequestMapping(value = "/command/test/fail_timeout", method = RequestMethod.GET, consumes = MediaType.ALL_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public void fail_with_timeout() {
        throw new HystrixRuntimeException(HystrixRuntimeException.FailureType.TIMEOUT, null, "Timeout", null, null);
    }

    enum TestErrorCodes implements ErrorCode {

        A_500_ERROR(500),
        A_400_ERROR(400);

        private int httpStatus;

        private List<String> expectedContextEntries = new ArrayList<>();

        TestErrorCodes(int status) {
            this.httpStatus = status;
        }

        @Override
        public String getProduct() {
            return "DAIKON";
        }

        @Override
        public String getGroup() {
            return "TEST-HYSTRIX";
        }

        @Override
        public int getHttpStatus() {
            return httpStatus;
        }

        @Override
        public Collection<String> getExpectedContextEntries() {
            return this.expectedContextEntries;
        }

        @Override
        public String getCode() {
            return this.toString();
        }
    }

}
