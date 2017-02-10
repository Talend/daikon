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

/**
 * Image resource types that a {@link Definition} can support.
 *
 * This is used by {@link Definition#getImagePath(DefinitionImageType)} and the component service to provide image
 * resources. Not all {@link Definition} need to support all image types.
 */
public enum DefinitionImageType {
    /** A rescalable SVG icon to represent the definition. */
    SVG_ICON,
    /** A 32x32 PNG icon to represent the definition. */
    PALETTE_ICON_32X32,
    /** A 16x16 PNG icon to represent the definition. */
    TREE_ICON_16X16,
    /** A PNG banner used for the definition. */
    WIZARD_BANNER_75X66
}
