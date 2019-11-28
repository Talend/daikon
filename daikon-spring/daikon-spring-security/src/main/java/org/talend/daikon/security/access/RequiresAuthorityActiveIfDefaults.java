package org.talend.daikon.security.access;

import org.springframework.stereotype.Component;

import java.util.function.Supplier;

public class RequiresAuthorityActiveIfDefaults {

    private RequiresAuthorityActiveIfDefaults() {
    }

    /**
     * {@link RequiresAuthority} annotations are enabled by default
     * Return true Always true
     */
    @Component
    public static class AlwaysTrue implements Supplier<Boolean> {

        @Override
        public Boolean get() {
            return Boolean.TRUE;
        }
    }
}
