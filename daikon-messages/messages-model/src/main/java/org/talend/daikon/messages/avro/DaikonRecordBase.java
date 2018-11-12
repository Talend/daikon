package org.talend.daikon.messages.avro;

import org.apache.avro.specific.SpecificRecordBase;

/**
 * This subclass of {@link SpecificRecordBase} hides the default {@link #toString()} method that may potentially show
 * sensitive information in the logs (e.g. Authorization token).
 */
public abstract class DaikonRecordBase extends SpecificRecordBase {

    @Override
    public String toString() {
        return SecuredData.get().toString(this);
    }
}
