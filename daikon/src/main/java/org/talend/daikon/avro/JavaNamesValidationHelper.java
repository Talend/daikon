package org.talend.daikon.avro;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Helper class that provides utility methods for validation of identifiers according to Java naming conventions.
 */
public class JavaNamesValidationHelper {

    private static final Set<String> JAVA_KEYWORDS = new HashSet<String>(Arrays.asList("abstract", "continue", "for", "new",
            "switch", "assert", "default", "goto", "package", "synchronized", "boolean", "do", "if", "private", "this", "break",
            "double", "implements", "protected", "throw", "byte", "else", "import", "public", "throws", "case", "enum",
            "instanceof", "return", "transient", "catch", "extends", "int", "short", "try", "char", "final", "interface",
            "static", "void", "class", "finally", "long", "strictfp", "volatile", "const", "float", "native", "super", "while"));

    private static final String CONTEXT_AND_VARIABLE_PATTERN = "^[a-zA-Z_][a-zA-Z_0-9]*$";

    /**
     * Checks whether specified name is a valid identifier according to Java conventions.
     *
     * @param name identifier to validate
     * @return true, if name is a valid Java identifier
     */
    public static boolean isValidParameterName(String name) {
        if (name != null) {
            if (isJavaKeyWord(name)) {
                return false;
            }
            return Pattern.matches(CONTEXT_AND_VARIABLE_PATTERN, name);
        }
        return false;
    }

    /**
     * Checks whether specified name is a Java keyword
     *
     * @param name identifier to validate
     * @return true, if name is a Java keyword
     */
    public static boolean isJavaKeyWord(String name) {
        return JAVA_KEYWORDS.contains(name);
    }
}
