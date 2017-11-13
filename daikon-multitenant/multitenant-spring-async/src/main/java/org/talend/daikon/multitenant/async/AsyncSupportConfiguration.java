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
package org.talend.daikon.multitenant.async;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

/**
 * Configures a custom async executor which propagates the request context to the child threads
 *
 * Asynchronous services which need to be aware of the request context have to declare this executor
 * with @Async("contextAwarePoolExecutor")
 */
@Configuration
@Import({ RequestContextPropagationConfiguration.class, TenancyContextPropagationConfiguration.class,
        SecurityContextPropagationConfiguration.class })
@EnableAsync
public class AsyncSupportConfiguration {

    @Bean(name = "contextAwarePoolExecutor")
    public Executor contextAwarePoolExecutor(List<ContextPropagatorFactory> contextPropagatorFactories) {
        return new ContextAwarePoolExecutor(contextPropagatorFactories);
    }

    public static class ContextAwarePoolExecutor extends ThreadPoolTaskExecutor {

        private final List<ContextPropagatorFactory> contextPropagatorFactories;

        public ContextAwarePoolExecutor(List<ContextPropagatorFactory> contextPropagatorProviders) {
            this.contextPropagatorFactories = contextPropagatorProviders;
        }

        @Override
        public <T> Future<T> submit(Callable<T> task) {
            return super.submit(new ContextAwareCallable<>(task, this.contextPropagatorFactories));
        }

        @Override
        public <T> ListenableFuture<T> submitListenable(Callable<T> task) {
            return super.submitListenable(new ContextAwareCallable<T>(task, this.contextPropagatorFactories));
        }
    }
}