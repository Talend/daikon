package org.talend.daikon.security.access;

import org.springframework.stereotype.Component;

import java.util.function.Supplier;

public class RequiresAuthorityConditionDefaults {

    private RequiresAuthorityConditionDefaults() {
    }

    /**
     * Return true
     */
    @Component
    public static class AlwaysTrue implements Supplier<Boolean> {

        @Override
        public Boolean get() {
            return Boolean.TRUE;
        }
    }
}
