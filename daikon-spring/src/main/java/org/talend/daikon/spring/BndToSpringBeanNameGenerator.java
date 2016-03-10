// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.daikon.spring;

import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;

/**
 * This class allow spring to recognize bnd @Component annotation as named Spring components. you can use iit like this
 * <br>
 * <code>
 * &#64;ComponentScan(nameGenerator = BndToSpringBeanNameGenerator.class, includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = aQute.bnd.annotation.component.Component.class) )

 * </code>
 */
public class BndToSpringBeanNameGenerator extends AnnotationBeanNameGenerator {

    static final public String BND_ANNOTATION = "aQute.bnd.annotation.component.Component"; //$NON-NLS-1$

    /**
     * Derive a bean name from one of the annotations on the class. First delegate to the super class and if it is not a
     * spring annotation then check for the bnd annotation
     * 
     * @param annotatedDef the annotation-aware bean definition
     * @return the bean name, or {@code null} if none is found
     */
    @Override
    protected String determineBeanNameFromAnnotation(AnnotatedBeanDefinition annotatedDef) {
        String beanName = super.determineBeanNameFromAnnotation(annotatedDef);
        if (beanName != null) {
            return beanName;
        } // else check for BND annotation
        AnnotationMetadata amd = annotatedDef.getMetadata();
        Set<String> types = amd.getAnnotationTypes();
        for (String type : types) {
            AnnotationAttributes attributes = AnnotationAttributes.fromMap(amd.getAnnotationAttributes(type, false));
            if (isStereotypeWithBndNameValue(type, amd.getMetaAnnotationTypes(type), attributes)) {
                Object value = attributes.get("name");
                if (value instanceof String) {
                    String strVal = (String) value;
                    if (StringUtils.hasLength(strVal)) {
                        if (beanName != null && !strVal.equals(beanName)) {
                            throw new IllegalStateException("Stereotype annotations suggest inconsistent " + "component names: '"
                                    + beanName + "' versus '" + strVal + "'");
                        }
                        beanName = strVal;
                    }
                }
            }
        }
        return beanName;
    }

    /**
     * Check whether the given annotation is a stereotype that is allowed to suggest a component name through its
     * annotation {@code value()}.
     * 
     * @param annotationType the name of the annotation class to check
     * @param metaAnnotationTypes the names of meta-annotations on the given annotation
     * @param attributes the map of attributes for the given annotation
     * @return whether the annotation qualifies as a stereotype with component name
     */
    protected boolean isStereotypeWithBndNameValue(String annotationType, Set<String> metaAnnotationTypes,
            Map<String, Object> attributes) {

        boolean isStereotype = annotationType.equals(BND_ANNOTATION)
                || (metaAnnotationTypes != null && metaAnnotationTypes.contains(BND_ANNOTATION));

        return (isStereotype && attributes != null && attributes.containsKey("name"));
    }
}