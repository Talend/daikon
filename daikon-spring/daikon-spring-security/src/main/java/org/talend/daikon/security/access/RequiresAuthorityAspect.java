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

import static java.util.Optional.ofNullable;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * The aspect configuration that takes care of {@link RequiresAuthority} annotations.
 */
@Configuration
@EnableAspectJAutoProxy
@Aspect
public class RequiresAuthorityAspect {

    @Autowired
    private ApplicationContext applicationContext;

    private static final AnonymousAuthenticationToken ANONYMOUS = new AnonymousAuthenticationToken("anonymous", //
            new Object(), //
            Collections.singleton(new SimpleGrantedAuthority("NONE")));

    private static final Logger LOGGER = LoggerFactory.getLogger(RequiresAuthorityAspect.class);

    /**
     * The interceptor method for method annotated with {@link RequiresAuthority}.
     *
     * @param pjp The method invocation.
     * @return The object
     * @throws Throwable Throws {@link org.springframework.security.access.AccessDeniedException} in case of denied
     * access to the invoked method.
     */
    @Around("@annotation(org.talend.daikon.security.access.RequiresAuthority)")
    public Object requires(ProceedingJoinPoint pjp) throws Throwable {
        final Authentication authentication = ofNullable(getContext().getAuthentication()).orElse(ANONYMOUS);
        LOGGER.debug("Checking @Required access on {} for user {}.", pjp, authentication);

        final MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        final Method method = methodSignature.getMethod();

        final RequiresAuthority annotation = method.getAnnotation(RequiresAuthority.class);
        if (annotation == null) {
            throw new IllegalArgumentException("Missing @RequiresAuthority annotation."); // Rather unexpected
        }

        final String[] authorityArray = annotation.authority();
        final Supplier<Stream<String>> authorityStreamSupplier = () -> Stream.of(authorityArray).filter(StringUtils::isNotBlank);

        final String[] valueArray = annotation.value();
        final Supplier<Stream<String>> valueStreamSupplier = () -> Stream.of(valueArray).filter(StringUtils::isNotBlank);

        Supplier<Stream<String>> streamSupplier = null;

        if (authorityStreamSupplier.get().count() > 0) {
            streamSupplier = authorityStreamSupplier;
        } else if (valueStreamSupplier.get().count() > 0) {
            streamSupplier = valueStreamSupplier;
        }

        final Class<? extends Function<ApplicationContext, Boolean>> activeIf = annotation.activeIf();
        final Boolean isActive = activeIf.newInstance().apply(applicationContext);
        if (streamSupplier != null && isActive && streamSupplier.get().noneMatch(RequiresAuthorityAspect::isAllowed)) {
            LOGGER.debug("Access denied for user {} on {}.", authentication, method);
            final Class<? extends AccessDenied> onDeny = annotation.onDeny();
            final AccessDenied accessDenied;
            try {
                accessDenied = onDeny.newInstance();
                return accessDenied.onDeny(annotation, method, pjp.getArgs());
            } catch (InstantiationException noInstance) {
                LOGGER.error("Unable to use on deny custom class {}", onDeny.getName(), noInstance);
                throw new AccessDeniedException("Access denied for " + method.getName() + ".", noInstance);
            }
        }

        LOGGER.debug("Access allowed for user {} on {}.", authentication, method);
        return pjp.proceed();
    }

    /**
     * Check if user should be allowed to an actions requiring supplied authority.
     *
     * @param authority the authority needed to proceed.
     * @return true if user is authenticated and has the permission, false otherwise.
     */
    private static boolean isAllowed(String authority) {
        if (authority == null) {
            return false;
        }
        // because of the security setup, auth must always be set
        final Authentication authentication = getContext().getAuthentication();
        if (authentication != null) {
            // sonar is not able to understand that RestrictedAction implements GrantedAuthority
            if (authentication.getAuthorities().stream().noneMatch(a -> StringUtils.equals(a.getAuthority(), authority))) {
                LOGGER.debug("User has not been allowed to use: {}", authority);
                return false;
            } else {
                LOGGER.debug("User has been allowed to use: {}", authority);
                return true;
            }
        }
        return false;
    }
}
