// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.daikon.exception;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.daikon.exception.ExceptionContext.ExceptionContextBuilder;
import org.talend.daikon.exception.error.CommonErrorCodes;
import org.talend.daikon.exception.error.ErrorCode;
import org.talend.daikon.exception.json.JsonErrorCode;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

/**
 * Class for all business exception.
 */
public class TalendRuntimeException extends RuntimeException {

    private static final long serialVersionUID = -5306654994356243153L;

    private static final Logger LOGGER = LoggerFactory.getLogger(TalendRuntimeException.class);

    /** The error code for this exception. */
    private final ErrorCode code;

    /** The exception cause. */
    private final Throwable cause;

    /** Context of the error when it occurred (used to detail the user error message in frontend). */
    private final ExceptionContext context;

    /**
     * @param code the error code, uniquely describing the error condition that occurred.
     * @param cause the root cause of this error.
     * @param context the context of the error when it occurred (used to detail the user error message in frontend).
     */
    public TalendRuntimeException(ErrorCode code, Throwable cause, ExceptionContext context) {
        super(getExceptionMessage(code, context), cause); // $NON-NLS-1$ //$NON-NLS-2$
        if (code == null) {
            // Validate code but keep the possible underlying cause
            throw new IllegalArgumentException("A Talend exception needs a non-null code.", cause);
        }
        this.code = code;
        this.cause = cause;
        this.context = (context == null ? ExceptionContext.build() : context);
        checkContext();
    }

    private static String getExceptionMessage(ErrorCode code, ExceptionContext context) {
        return (code == null ? "" : code.getCode()) + (context == null ? "" : ":" + context.toString());
    }

    /**
     * Lightweight constructor without context.
     *
     * @param code the error code that holds all the .
     * @param cause the root cause of this error.
     */
    public TalendRuntimeException(ErrorCode code, Throwable cause) {
        this(code, cause, null);
    }

    /**
     * Lightweight constructor without a cause.
     *
     * @param code the error code that holds all the .
     * @param context the exception context.
     */
    public TalendRuntimeException(ErrorCode code, ExceptionContext context) {
        this(code, null, context);
    }

    /**
     * Basic constructor from a JSON error code.
     *
     * @param code an error code serialized to JSON.
     */
    public TalendRuntimeException(JsonErrorCode code) {
        this(code, ExceptionContext.build().from(code.getContext()));
    }

    /**
     * Basic constructor with the bare error code.
     *
     * @param code the error code that holds all the .
     */
    public TalendRuntimeException(ErrorCode code) {
        this(code, null, null);
    }

    /**
     * Called when something unexpected happens.
     * 
     * @param cause the unexpected exception.
     * @deprecated cause the IDEs do not know this throws an exception please use {@link #createUnexpectedException(Throwable)}
     */
    @Deprecated
    public static void unexpectedException(Throwable cause) {
        // TODO - add some logging here
        throw new TalendRuntimeException(CommonErrorCodes.UNEXPECTED_EXCEPTION, cause);
    }

    /**
     * Called when something unexpected happens.
     * 
     * @param message the unexpected exception.
     * @deprecated cause the IDEs do not know this throws an exception please use {@link #createUnexpectedException(String)}
     */
    @Deprecated
    public static void unexpectedException(String message) {
        // TODO - add some logging here
        throw new TalendRuntimeException(CommonErrorCodes.UNEXPECTED_EXCEPTION,
                ExceptionContext.build().put(ExceptionContext.KEY_MESSAGE, message));
    }

    public static RuntimeException createUnexpectedException(Throwable cause) {
        // TODO - add some logging here
        return new TalendRuntimeException(CommonErrorCodes.UNEXPECTED_EXCEPTION, cause);
    }

    public static TalendRuntimeException createUnexpectedException(String message) {
        // TODO - add some logging here
        return new TalendRuntimeException(CommonErrorCodes.UNEXPECTED_EXCEPTION,
                ExceptionContext.build().put(ExceptionContext.KEY_MESSAGE, message));
    }

    /**
     * Make sure that the context is filled with the expected context entries from the error code. If an entry is
     * missing, only a warning log is issued.
     */
    private void checkContext() {
        Collection<String> expectedContextEntries = code.getExpectedContextEntries();
        if (expectedContextEntries == null) {
            LOGGER.debug(
                    "Code {} was logged but returned null to getExpectedContextEntries(). Should be empty list if no expected entries.",
                    code);
        } else {
            List<String> missingEntries = new ArrayList<>();
            for (String expectedEntry : expectedContextEntries) {
                if (!context.contains(expectedEntry)) {
                    missingEntries.add(expectedEntry);
                }
            }

            if (missingEntries.size() > 0) {
                LOGGER.warn("TDPException context for {}, is missing the given entry(ies) \n{}. \nStacktrace for info",
                        code.getCode(), missingEntries, this);
            }
        }
    }

    /**
     * Describe this error in json into the given writer.
     * The json format can then be used to deserialize it into {@link JsonErrorCode}.
     * 
     * @param writer where to write this error.
     */
    public void writeTo(Writer writer) {
        try {
            JsonGenerator generator = (new JsonFactory()).createGenerator(writer);
            generator.writeStartObject();
            {
                generator.writeStringField("code", code.getProduct() + '_' + code.getGroup() + '_' + code.getCode()); //$NON-NLS-1$
                generator.writeStringField("message", code.getCode()); //$NON-NLS-1$
                if (cause != null) {
                    generator.writeStringField("cause", cause.getMessage()); //$NON-NLS-1$
                }
                if (context != null) {
                    generator.writeFieldName("context"); //$NON-NLS-1$
                    generator.writeStartObject();
                    for (Map.Entry<String, Object> entry : context.entries()) {
                        generator.writeStringField(entry.getKey(), entry.getValue().toString());
                    }
                    generator.writeEndObject();
                }
            }
            generator.writeEndObject();
            generator.flush();
        } catch (IOException e) {
            LOGGER.error("Unable to write exception to " + writer + ".", e);
        }
    }

    /**
     * @return the error code.
     */
    public ErrorCode getCode() {
        return code;
    }

    /**
     *
     * @return the root cause of this error
     */
    @Override
    public Throwable getCause() {
        return cause;
    }

    /**
     *
     * @return Context of the error when it occurred
     */
    public ExceptionContext getContext() {
        return context;
    }

    public static TalendRuntimeExceptionBuilder build(ErrorCode code) {
        return new TalendRuntimeExceptionBuilder(code);
    }

    public static TalendRuntimeExceptionBuilder build(ErrorCode code, Throwable exception) {
        return new TalendRuntimeExceptionBuilder(code, exception);
    }

    public static class TalendRuntimeExceptionBuilder {

        private ExceptionContextBuilder ecb = ExceptionContext.withBuilder();

        private ErrorCode errorCode;

        private Throwable exception;

        TalendRuntimeExceptionBuilder(ErrorCode errorCode) {
            this(errorCode, null);
        }

        /**
         * @param code
         * @param exception
         */
        public TalendRuntimeExceptionBuilder(ErrorCode code, Throwable exception) {
            this.exception = exception;
            this.errorCode = code;
        }

        /**
         * add new key,value to the context of the exception that will be created
         */
        public TalendRuntimeExceptionBuilder put(String key, String value) {
            if (ecb.getContextSize() != errorCode.getExpectedContextEntries().size()) {
                ecb.put(key, value);
            } // or else context is already set so ignore this
            return this;
        }

        /**
         * add values to the context of the exception that will be created in the order of the context
         * keys, shall be used only once instead of {@link #put(String, String)} if you want to set all values at once.
         * This will return a final TalendRuntimeException as no more context can be set.
         * 
         * @param values all ordered value to set. never null.
         * @throws IllegalArgumentException if the context values set do not match the expected context
         */
        public TalendRuntimeException set(String... values) {
            String[] expectedContextEntries = errorCode.getExpectedContextEntries()
                    .toArray(new String[errorCode.getExpectedContextEntries().size()]);

            for (int i = 0; (i < expectedContextEntries.length) && (i < values.length); i++) {
                put(expectedContextEntries[i], values[i]);
            }
            if (values.length != errorCode.getExpectedContextEntries().size()) {
                throw new IllegalArgumentException("values set [" + Arrays.toString(values)
                        + "] do not match the expected context keys [" + errorCode.getExpectedContextEntries() + "]");
            }
            return create();
        }

        /**
         * same as {@link #set(String...)} and throws the exception as well instead of returning it
         * 
         * @throws IllegalArgumentException if the context values set do not match the expected context
         */
        public void setAndThrow(String... values) {
            throw set(values);
        }

        /**
         * same as {@link #create()} but also throws it
         * 
         * @throws IllegalArgumentException if the context values set do not match the expected context
         */
        public void throwIt() {
            throw create();
        }

        /**
         * creates the exception with the previously set context. It enforces the context number of arguments that must match.
         * It throws an {@link IllegalArgumentException} in case the context was not set properly.
         * 
         * @throws IllegalArgumentException if the context values set do not match the expected context
         */
        public TalendRuntimeException create() {
            if (ecb.getContextSize() != errorCode.getExpectedContextEntries().size()) {
                throw new IllegalArgumentException("context values [" + ecb.toString()
                        + "] do not match the expected context keys [" + errorCode.getExpectedContextEntries() + "]");
            }
            return new TalendRuntimeException(errorCode, exception, ecb.build());
        }
    }
}
