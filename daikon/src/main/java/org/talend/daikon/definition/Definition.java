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
package org.talend.daikon.definition;

import org.talend.daikon.NamedThing;
import org.talend.daikon.properties.Properties;

/**
 * Provides information about a Properties, such as an associated name and image and a factory to create an instance.
 * All definitions {@link #getName()} must return a unique value within a single jvm
 */
public interface Definition<P extends Properties> extends NamedThing {

    /**
     * @return the Properties class associated with this definition. This class must have a constructor with a String
     * parameter to set its name.
     */
    Class<P> getPropertiesClass();

    /**
     * A path relative to the current instance, ideally it should just be the name of the png image if placed in the
     * same resource folder as the implementing class. The service api requesting an icon will use the following code:
     * 
     * <pre>
     * {@code
     *    this.getClass().getResourceAsStream(getImagePath())
     * }
     * </pre>
     * 
     * @see {@link java.lang.Class#getResourceAsStream(String)}
     * @return the path to the image resource or null if an image is not required.
     */
    String getImagePath();
}
