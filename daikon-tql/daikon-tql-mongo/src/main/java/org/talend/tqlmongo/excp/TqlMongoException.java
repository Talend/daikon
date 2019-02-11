package org.talend.tqlmongo.excp;

/*
 * Thrown to indicate that the Tql filter could not be
 * converted to a MongoDB criteria.
 */

import org.talend.tql.excp.TqlException;

/**
 * Created by gmzoughi on 11/07/16.
 */
public class TqlMongoException extends TqlException {

    /**
     * Constructs a <code>TqlMongoException</code> with the specified detail message.
     * 
     * @param message Exception message
     */
    public TqlMongoException(String message) {
        super(message);
    }

    /**
     * Constructs a <code>TqlMongoException</code> with the specified detail message
     * and the specified cause.
     * 
     * @param message Exception message
     * @param cause Exception cause
     */
    public TqlMongoException(String message, Throwable cause) {
        super(message, cause);
    }
}
