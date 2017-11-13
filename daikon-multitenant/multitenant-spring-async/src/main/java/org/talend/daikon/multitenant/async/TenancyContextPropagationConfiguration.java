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

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.talend.daikon.multitenant.context.TenancyContext;
import org.talend.daikon.multitenant.context.TenancyContextHolder;

@Configuration
@ConditionalOnClass(TenancyContextHolder.class)
public class TenancyContextPropagationConfiguration {

    @Bean
    public ContextPropagatorFactory tenancyContextPropagatorProvider() {
        return TenancyContextPropagator::new;
    }

    private static class TenancyContextPropagator implements ContextPropagator {

        private TenancyContext tenancyContext;

        @Override
        public void captureContext() {
            tenancyContext = TenancyContextHolder.getContext();
        }

        @Override
        public void setupContext() {
            TenancyContextHolder.setContext(tenancyContext);
        }

        @Override
        public void restoreContext() {
            TenancyContextHolder.clearContext();
        }
    }
}
