// ============================================================================
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// https://github.com/Talend/data-prep/blob/master/LICENSE
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package org.talend.daikon.security.access;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.GrantedAuthority;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A helper annotation to indicate what authorities are needed to execute method.
 *
 * @see RequiresAuthorityAspect
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target({ ElementType.METHOD })
public @interface RequiresAuthority {

    /**
     * @return The authority (as returned by {@link GrantedAuthority#getAuthority()} needed to execute method. Value is
     * ignored if {@link #authority()} is defined.
     */
    String[] value() default {};

    /**
     * @return The authority (as returned by {@link GrantedAuthority#getAuthority()} needed to execute method. This has
     * precedence over {@link #value()}.
     */
    String authority() default StringUtils.EMPTY;

    /**
     * @return A {@link AccessDenied} implementation to handle access denials.
     */
    Class<? extends AccessDenied> onDeny() default AccessDeniedDefaults.ThrowException.class;
}
