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
package org.talend.daikon.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.talend.daikon.service.ApiVersionRequestMappingHandlerMapping;

/**
 * When a class is annotated with <code>&#064;ApiVersion</code> or extends a class annotated with
 * <code>&#064;ApiVersion</code>, in a SpringWebMvc environment, web request routes will be created for each
 * <code>value()</code> specified. When the annotation is used at the Interface or Class level all the routes in the
 * class will be prepended with the values. If the annotation is used on single method, only the associated route will
 * be prepended and the class level annotation is ignored.
 *
 * For example, if your api is available in v1 and v2 you could do:
 *
 * <pre>
 * &#064;RestController
 * &#064;ApiVersion({ "1", "2" })
 * public class ABCSuite {
 *      &#064;RequestMapping("bananas")
 *      public List&lt;Banana&gt; getBananas(){...}
 * }
 * </pre>
 * 
 * You will then have 2 routes exposed :
 * <ul>
 * <li>/v1/bananas</li>
 * <li>/v2/bananas</li>
 * </ul>
 * 
 * <br>
 * In order to enable the version mapping of the routes, you need to override the Spring RequestMappingHandlerMapping
 * using the {@link ApiVersionRequestMappingHandlerMapping}, you can create a configuration class for you application
 * like this (see test class : org.talend.daikon.services.version.ApiVersionTestConfig)
 * 
 * <pre>
 * &#064;Configuration
 * public class ApiVersionConfig extends DelegatingWebMvcConfiguration {
 * 
 *     &#064;Override
 *     protected RequestMappingHandlerMapping createRequestMappingHandlerMapping() {
 *         return new ApiVersionRequestMappingHandlerMapping();
 *     }
 * }
 * </pre>
 * 
 * 
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiVersion {

    String[] value();
}