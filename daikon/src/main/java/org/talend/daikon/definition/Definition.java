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
 * Provide information about a {@link Properties} that can be queried before having an instance.
 * 
 * This is used to associate extra data with the properties, notably images and internationalized text, and acts as a
 * factory to create new instances.
 *
 * Subinterfaces can be used to logically group {@link Definition} that provide similar business logic.
 *
 * All definitions {@link #getName()} must be unique within a single jvm.
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
     * @deprecated use the {@link #getImagePath(DefinitionImageType)}
     */
    @Deprecated
    String getImagePath();

    /**
     * A resource path to the current package corresponding to an image. The service api requesting an image resource
     * will use the following code:
     *
     * <pre>
     * {@code
     *    this.getClass().getResourceAsStream(getImagePath(DefinitionImageType.SVG_ICON))
     * }
     * </pre>
     *
     * @param type The type of image resource to fetch.
     * @return the path to the image resource, or null if an image type is not available or required for the definition.
     * @see {@link java.lang.Class#getResourceAsStream(String)}
     */
    String getImagePath(DefinitionImageType type);

    /**
     * An icon key can optionally be used to "override" the icon resource associated with the definition by an icon
     * provided and styled by the specific product.
     *
     * If this is present, it will be the preferred source for the icon for the definition. If this is null or the icon
     * key is unrecognized, the product will fall back on one of the images defined by
     * {@link #getImagePath(DefinitionImageType)}.
     *
     * The list of standard icons is available at the {@code Talend/ui} repository and reusable contributions are
     * welcome.
     *
     * @return the path to the image resource, or null if an image type is not available or required for the definition.
     * @see <a href="https://github.com/Talend/ui/tree/master/packages/icons/src/svg">Available icons</a>
     */
    String getIconKey();
}
