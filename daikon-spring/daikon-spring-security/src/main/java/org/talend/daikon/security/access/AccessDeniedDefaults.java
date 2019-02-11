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

import static java.util.Collections.emptyList;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.talend.daikon.exception.TalendRuntimeException;
import org.talend.daikon.exception.error.CommonErrorCodes;

/**
 * Default/Helper implementations for {@link AccessDenied}
 */
public class AccessDeniedDefaults {

    private AccessDeniedDefaults() {
    }

    /**
     * Throw a {@link TalendRuntimeException} on denied access with a HTTP 403.
     * 
     * @see CommonErrorCodes#INSUFFICIENT_AUTHORITY
     */
    public static class ThrowException implements AccessDenied<Object> { // NOSONAR

        @Override
        public Object onDeny(RequiresAuthority requirements, Method method, Object[] args) {
            throw new TalendRuntimeException(CommonErrorCodes.INSUFFICIENT_AUTHORITY);
        }
    }

    /**
     * Return empty string in case of denied access.
     */
    public static class EmptyString implements AccessDenied<String> {

        @Override
        public String onDeny(RequiresAuthority requirements, Method method, Object[] args) {
            return StringUtils.EMPTY;
        }
    }

    /**
     * Return empty list in case of denied access.
     */
    public static class EmptyList implements AccessDenied<List> {

        @Override
        public List onDeny(RequiresAuthority requirements, Method method, Object[] args) {
            return emptyList();
        }
    }

    /**
     * Return false in case of denied access.
     */
    public static class False implements AccessDenied<Boolean> {

        @Override
        public Boolean onDeny(RequiresAuthority requirements, Method method, Object[] args) {
            return false;
        }
    }

    /**
     * A {@link AccessDenied} implementation to return an empty stream on denied access.
     */
    public static class EmptyStream implements AccessDenied<Stream> {

        @Override
        public Stream onDeny(RequiresAuthority requiresAuthority, Method method, Object[] objects) {
            return Stream.empty();
        }
    }

}
