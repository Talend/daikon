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
package org.talend.daikon.messages.spring.consumer;

import org.springframework.context.annotation.Import;
import org.talend.daikon.messages.spring.consumer.security.SpringSecuritySettersConfiguration;
import org.talend.daikon.messages.spring.consumer.sleuth.SpringSleuthSettersConfiguration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({ SpringSecuritySettersConfiguration.class, SpringSleuthSettersConfiguration.class,
        ExecutionContextUpdaterConfiguration.class, MultiTenantConsumerSettersConfiguration.class,
        DefaultConsumerSettersConfiguration.class, })
public @interface EnableMessagesConsumerAutoConfig {
}
