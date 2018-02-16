// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.daikon.avro;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Helper class that provides utility methods for validation of identifiers according to Java naming conventions.
 */
public class AvroNamesValidationHelper {

    private static final Set<String> JAVA_KEYWORDS = new HashSet<String>(
            Arrays.asList("abstract", "continue", "for", "new", "switch", "assert", "default", "goto", "package", "synchronized",
                    "boolean", "do", "if", "private", "this", "break", "double", "implements", "protected", "throw", "byte",
                    "else", "import", "public", "throws", "case", "enum", "instanceof", "return", "transient", "catch", "extends",
                    "int", "short", "try", "char", "final", "interface", "static", "void", "class", "finally", "long", "strictfp",
                    "volatile", "const", "float", "native", "super", "while", "true", "false", "null"));

    private static final Pattern CONTEXT_AND_VARIABLE_PATTERN = Pattern.compile("^[a-zA-Z_][a-zA-Z_0-9]*$");

    /**
     * Checks whether specified name is a valid identifier according to Java conventions,
     * if specified name is invalid - generates a new name value that can be used in Avro schema.
     *
     * @param name identifier to validate
     * @return valid name that can be used in Avro schema
     */
    public static String getAvroCompatibleName(String name) {
        if (isValidParameterName(name)) {
            return name;
        } else {
            return "_" + name;
        }
    }

    /**
     * Checks whether specified name is a valid identifier according to Java conventions.
     *
     * @param name identifier to validate
     * @return true, if name is a valid Java identifier
     */
    public static boolean isValidParameterName(String name) {
        if (name == null || isJavaKeyWord(name)) {
            return false;
        }
        return CONTEXT_AND_VARIABLE_PATTERN.matcher(name).matches();
    }

    /**
     * Checks whether specified name is a Java keyword.
     *
     * @param name identifier to validate
     * @return true, if name is a Java keyword
     */
    public static boolean isJavaKeyWord(String name) {
        return JAVA_KEYWORDS.contains(name);
    }
}
