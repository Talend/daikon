// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.daikon.multitenant.async;

import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.talend.daikon.logging.event.field.MdcKeys;

@Configuration
@ConditionalOnClass(SecurityContextHolder.class)
public class SecurityContextPropagationConfiguration {

    @Bean
    public ContextPropagatorFactory securityContextPropagatorProvider() {
        return SecurityContextPropagator::new;
    }

    private static class SecurityContextPropagator implements ContextPropagator {

        private SecurityContext securityContext;

        @Override
        public void captureContext() {
            this.securityContext = SecurityContextHolder.getContext();
        }

        @Override
        public void setupContext() {
            SecurityContextHolder.setContext(this.securityContext);
            setMdc(securityContext);
        }

        @Override
        public void restoreContext() {
            SecurityContextHolder.clearContext();
            removeMdc();
        }

        private static void setMdc(SecurityContext context) {
            if (context != null) {
                Authentication authentication = context.getAuthentication();
                if (authentication != null) {
                    MDC.put(MdcKeys.USER_ID, authentication.getName());
                }
            }
        }

        private static void removeMdc() {
            MDC.remove(MdcKeys.USER_ID);
        }
    }

}
