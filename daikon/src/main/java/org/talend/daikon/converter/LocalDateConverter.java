package org.talend.daikon.converter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Collection;

import javax.servlet.http.HttpServletResponse;

import org.talend.daikon.exception.ExceptionContext;
import org.talend.daikon.exception.TalendRuntimeException;
import org.talend.daikon.exception.error.ErrorCode;

public class LocalDateConverter extends Converter<LocalDate> {

    public static final String FORMATTER = "formatter";

    @Override
    public LocalDate convert(Object value) {
        if (properties.containsKey(LocalDateConverter.FORMATTER)) {
            try {
                return LocalDate.parse(value.toString(), getDateTimeFormatter());
            } catch (DateTimeParseException dtpe) {
                throw LocalDateConverterErrorCode.createCannotParse(dtpe, value.toString(), getDateTimeFormatter().toString());
            }
        }
        return LocalDate.parse(value.toString());
    }

    public LocalDateConverter withDateTimeFormatter(DateTimeFormatter formatter) {
        properties.put(LocalDateConverter.FORMATTER, formatter);
        return this;
    }

    public DateTimeFormatter getDateTimeFormatter() {
        return (DateTimeFormatter) properties.get(LocalDateTimeConverter.FORMATTER);
    }

    /**
     * Error codes for the {@link LocalDateConverter}.
     */
    public enum LocalDateConverterErrorCode implements ErrorCode {

        /** The user is attempting to create an output on a path that already exists, but is not set to overwrite. */
        CANNOT_PARSE("CANNOT_PARSE", HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "value", "format");

        private final String code;

        private final int httpStatus;

        private final Collection<String> contextEntries;

        LocalDateConverterErrorCode(String code, int httpStatus, String... contextEntries) {
            this.httpStatus = httpStatus;
            this.code = code;
            this.contextEntries = Arrays.asList(contextEntries);
        }

        @Override
        public String getProduct() {
            return "Talend";
        }

        @Override
        public String getGroup() {
            return "ALL";
        }

        @Override
        public int getHttpStatus() {
            return httpStatus;
        }

        @Override
        public Collection<String> getExpectedContextEntries() {
            return contextEntries;
        }

        @Override
        public String getCode() {
            return code;
        }

        /**
         * Create an exception with the error code and context for {@link #CANNOT_PARSE}.
         *
         * @param cause The technical exception that was caught when the error occurred.
         * @param value The value that was being parsed.
         * @param format The format that the value could not be parsed with.
         * @return An exception corresponding to the error code.
         */
        public static TalendRuntimeException createCannotParse(Throwable cause, String value, String format) {
            // TODO: The format looks like Value(DayOfMonth,2)'/'Value(MonthOfYear,2)'/'Value(YearOfEra,4,19,EXCEEDS_PAD)
            return new TalendMsgRuntimeException(cause, CANNOT_PARSE,
                    ExceptionContext.withBuilder().put("value", value).put("format", format).build(),
                    "Cannot parse '" + value + "' using the specified format.");
        }

        /**
         * {@link TalendRuntimeException} with a reasonable user-friendly message in English.
         */
        private static class TalendMsgRuntimeException extends TalendRuntimeException {

            private final String localizedMessage;

            public TalendMsgRuntimeException(Throwable cause, ErrorCode code, ExceptionContext context, String localizedMessage) {
                super(code, cause, context);
                this.localizedMessage = localizedMessage;
            }

            @Override
            public String getMessage() {
                return getLocalizedMessage();
            }

            @Override
            public String getLocalizedMessage() {
                return localizedMessage;
            }
        }
    }
}
