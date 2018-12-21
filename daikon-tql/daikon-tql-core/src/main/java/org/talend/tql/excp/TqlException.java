package org.talend.tql.excp;

/*
 * Thrown to indicate that the Tql filter is not valid.
 */

/**
 * Created by gmzoughi on 23/06/16.
 */

public class TqlException extends RuntimeException {

    /**
     * Constructs a <code>TqlException</code> with the specified detail message.
     * 
     * @param message Exception message
     */
    public TqlException(String message) {
        super(message);
    }

    /**
     * Constructs a <code>TqlException</code> with the specified detail message
     * and the specified cause.
     * 
     * @param message Exception message
     * @param cause Exception cause
     */
    public TqlException(String message, Throwable cause) {
        super(message, cause);
    }
}
