// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.daikon.di;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import org.talend.daikon.exception.TalendRuntimeException;
import org.talend.daikon.exception.error.CommonErrorCodes;

/**
 * Transformer interface used in {@link DiOutgoingSchemaEnforcer} and {@link DiIncomingSchemaEnforcer}. In
 * {@link DiOutgoingSchemaEnforcer} it is used to transform value while getting it from IndexedRecord. In
 * {@link DiIncomingSchemaEnforcer} it is used to transform value before putting in to IndexedRecord.
 */
public interface Transformer {

    public Object transform(Object value);

    /**
     * Transformer used to transform date from logical avro date value.
     */
    public static class LogicalDateTransformer implements Transformer {

        @Override
        public Object transform(Object value) {
            Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
            c.setTimeInMillis(0L);
            c.add(Calendar.DATE, (Integer) value);
            return c.getTime();
        }

    }

    /**
     * Transforms avro logical time milliseconds value.
     */
    public static class TimeMillisTransformer implements Transformer {

        @Override
        public Object transform(Object value) {
            return value;
        }

    }

    /**
     * Transforms logical avro timestamp milliseconds value to date.
     */
    public static class TimestampMillisTransformer implements Transformer {

        @Override
        public Object transform(Object value) {
            return new Date((Long) value);
        }

    }

    public static class ShortTransformer implements Transformer {

        @Override
        public Object transform(Object value) {
            return value instanceof Number ? ((Number) value).shortValue() : Short.parseShort(String.valueOf(value));
        }

    }

    public static class DateTransformer implements Transformer {

        @Override
        public Object transform(Object value) {
            return value instanceof Date ? value : new Date((Long) value);
        }

    }

    public static class ByteTransformer implements Transformer {

        @Override
        public Object transform(Object value) {
            return value instanceof Number ? ((Number) value).byteValue() : Byte.parseByte(String.valueOf(value));
        }

    }

    public static class CharacterTransformer implements Transformer {

        @Override
        public Object transform(Object value) {
            return value instanceof Character ? value : ((String) value).charAt(0);
        }

    }

    public static class BigDecimalTransformer implements Transformer {

        @Override
        public Object transform(Object value) {
            return value instanceof BigDecimal ? value : new BigDecimal(String.valueOf(value));
        }

    }

    public static class EmptyTransformer implements Transformer {

        @Override
        public Object transform(Object value) {
            return value;
        }
    }

    /**
     * Transforms Date time studio value to Date object. Date time value can be represented with long, String or
     * java.util.Date object.
     */
    public static class DateTimeTransformer implements Transformer {

        private final String pattern;

        private final Map<String, SimpleDateFormat> dateFormatCache;

        public DateTimeTransformer(String pattern, Map<String, SimpleDateFormat> dateFormatCache) {
            this.pattern = pattern;
            this.dateFormatCache = dateFormatCache;
        }

        @Override
        public Object transform(Object v) {
            Object datum = null;
            if (v instanceof Date) {
                datum = v;
            } else if (v instanceof Long) {
                datum = new Date((long) v);
            } else if (v instanceof String) {
                String vs = (String) v;
                String patt = pattern;

                if (pattern == null || pattern.equals("yyyy-MM-dd'T'HH:mm:ss'000Z'")) {
                    if (!vs.endsWith("000Z")) {
                        TalendRuntimeException.build(CommonErrorCodes.UNEXPECTED_ARGUMENT).setAndThrow("Date format", vs);
                    }
                    patt = "yyyy-MM-dd'T'HH:mm:ss";
                }

                SimpleDateFormat df = dateFormatCache.get(patt);
                if (df == null) {
                    df = new SimpleDateFormat(patt);
                    df.setTimeZone(TimeZone.getTimeZone("UTC"));
                    dateFormatCache.put(pattern, df);
                }

                try {
                    datum = df.parse((String) v);
                } catch (ParseException e) {
                    TalendRuntimeException.build(CommonErrorCodes.UNEXPECTED_ARGUMENT).setAndThrow("Date format", vs);
                }
            }
            return datum;
        }

    }

    public static class BooleanTransformer implements Transformer {

        @Override
        public Object transform(Object value) {
            return (value instanceof Boolean) ? value : Boolean.valueOf(String.valueOf(value));
        }

    }

    public static class BytesTransformer implements Transformer {

        @Override
        public Object transform(Object value) {
            return (value instanceof byte[]) ? value : String.valueOf(value).getBytes();
        }

    }

    public static class DoubleTransformer implements Transformer {

        @Override
        public Object transform(Object value) {
            return (value instanceof Number) ? ((Number) value).doubleValue() : Double.valueOf(String.valueOf(value));
        }

    }

    public static class FloatTransformer implements Transformer {

        @Override
        public Object transform(Object value) {
            return (value instanceof Number) ? ((Number) value).floatValue() : Float.valueOf(String.valueOf(value));
        }

    }

    public static class IntTransformer implements Transformer {

        @Override
        public Object transform(Object value) {
            return (value instanceof Number) ? ((Number) value).intValue() : Integer.valueOf(String.valueOf(value));
        }

    }

    public static class LongTransformer implements Transformer {

        @Override
        public Object transform(Object value) {
            return (value instanceof Number) ? ((Number) value).longValue() : Long.valueOf(String.valueOf(value));
        }

    }

    public static class StringTransformer implements Transformer {

        @Override
        public Object transform(Object value) {
            return String.valueOf(value);
        }

    }

}