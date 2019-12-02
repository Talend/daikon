package org.talend.daikon.security.access;

import org.springframework.context.ApplicationContext;

import java.util.function.Function;

public class RequiresAuthorityActiveIfDefaults {

    private RequiresAuthorityActiveIfDefaults() {
    }

    /**
     * {@link RequiresAuthority} annotations are enabled by default
     * Return true Always true
     */
    public static class AlwaysTrue implements Function<ApplicationContext, Boolean> {

        @Override
        public Boolean apply(ApplicationContext applicationContext) {
            return Boolean.TRUE;
        }
    }
}
