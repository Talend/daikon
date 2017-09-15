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

import java.lang.reflect.Method;

/**
 * An interface to define {@link RequiresAuthority#onDeny()} behavior.
 * Implementations must have a no-argument constructor as it is instantiated through {@link Class#newInstance()}.
 *
 * @param <T> The return type of the annotated method.
 */
@FunctionalInterface
public interface AccessDenied<T> {

    /**
     * @return The expected value for the annotated method.
     * @param requirements The requirements defined in a {@link RequiresAuthority} annotation.
     * @param method The method protected with the {@link RequiresAuthority} annotation.
     * @param args The arguments used for <code>method</code>.
     */
    T onDeny(RequiresAuthority requirements, Method method, Object[] args);
}
