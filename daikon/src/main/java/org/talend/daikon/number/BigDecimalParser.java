package org.talend.daikon.number;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * Parse a {@link BigDecimal} instance from a {@link String}.
 */
public class BigDecimalParser {

    /**
     * Patterns used to check different cases in guessSeparators(String):
     */
    private static final Pattern ENDS_BY_NOT_3_DIGITS_PATTERN = Pattern.compile("^[(-]?\\d+([,.'\\h])(?:\\d{0,2}|\\d{4,})[)]?");

    private static final Pattern STARTS_WITH_DECIMAL_SEPARATOR_PATTERN = Pattern
            .compile("^[(-]?(?:\\d{3,}|\\d{0})([,.])\\d+[)]?");

    private static final Pattern FEW_GROUP_SEP_PATTERN = Pattern.compile("^[(-]?\\d+([.,\\h'])\\d{3}(\\1\\d{3})+[)]?");

    private static final Pattern TWO_DIFFERENT_SEPARATORS_PATTERN = Pattern.compile(".*\\d+([.\\h'])\\d+([,.])\\d+[)]?");

    private static final Pattern CONTAINS_AT_LEAST_ONE_WHITESPACE = Pattern.compile("^[(-]?\\d+( \\d{3})+[)]?");

    public static final DecimalFormat US_DECIMAL_PATTERN = new DecimalFormat("#,##0.##",
            DecimalFormatSymbols.getInstance(Locale.US));

    public static final DecimalFormat EU_DECIMAL_PATTERN = new DecimalFormat("#,##0.##",
            DecimalFormatSymbols.getInstance(Locale.FRENCH));

    public static final DecimalFormat EU_SCIENTIFIC_DECIMAL_PATTERN = new DecimalFormat("0.###E0",
            DecimalFormatSymbols.getInstance(Locale.FRENCH));

    public static final DecimalFormat US_SCIENTIFIC_DECIMAL_PATTERN = new DecimalFormat("0.###E0",
            DecimalFormatSymbols.getInstance(Locale.US));

    public static final DecimalFormat EU_PERCENTAGE_DECIMAL_PATTERN = new DecimalFormat("#.##%",
            DecimalFormatSymbols.getInstance(Locale.FRENCH));

    public static final DecimalFormat US_PERCENTAGE_DECIMAL_PATTERN = new DecimalFormat("#.##%",
            DecimalFormatSymbols.getInstance(Locale.US));

    private BigDecimalParser() {
    }

    /**
     * Parses the given string to a BigDecimal with default BigDecimal(String) constructor.
     * <p>
     * This is useful when the number is standard US format (decimal separator='.' and grouping separator in {'', ',', '
     * '}) and for scientific notation.
     *
     * @param from string to convert to BigDecimal
     * @return an instance of BigDecimal
     * @throws NumberFormatException if <code>from</code> can not be parsed as a number or if <code>from</code> is
     * <code>null</code> or empty
     */
    public static BigDecimal toBigDecimal(String from) throws NumberFormatException {
        if (StringUtils.isEmpty(from)) {
            throw new NumberFormatException("null or empty is not a valid number");
        }
        final DecimalFormatSymbols decimalFormatSymbols = guessSeparators(from);
        return toBigDecimal(from, decimalFormatSymbols.getDecimalSeparator(), decimalFormatSymbols.getGroupingSeparator());
    }

    /**
     * Parses the given string to a BigDecimal with decimal separator explicitly defined.
     * <p>
     * Useful only when decimal separator is different than '.' or grouping separator is different than {'', ',' }.
     *
     * @param from string to convert to BigDecimal
     * @param decimalSeparator the character used for decimal sign
     * @param groupingSeparator the grouping separator
     * @return an instance of BigDecimal
     * @throws NumberFormatException if <code>from</code> can not be parsed as a number with the given separators
     */
    public static BigDecimal toBigDecimal(String from, char decimalSeparator, char groupingSeparator)
            throws NumberFormatException {
        if (StringUtils.isEmpty(from)) {
            throw new NumberFormatException("null or empty is not a valid number");
        }
        // Remove grouping separators:
        from = from.replaceAll("[" + groupingSeparator + "]", "");

        // Replace decimal separator:
        from = from.replaceAll("[" + decimalSeparator + "]", ".");

        // Remove spaces:
        from = from.replaceAll("\\h", "");

        // Detect and transform alternative negative pattern:
        if (from.startsWith("(") && from.endsWith(")")) {
            from = "-" + from.substring(1, from.length() - 1);
        }

        // Detect a percentage
        boolean isPercentage = from.endsWith("%");
        if (isPercentage) {
            from = from.substring(0, from.length() - 1);
        }

        try {
            BigDecimal bigDecimal = new BigDecimal(from);
            return isPercentage ? bigDecimal.movePointLeft(2) : bigDecimal;
        } catch (NumberFormatException e) {
            throw new NumberFormatException("'" + from + "' can not parsed as a number");
        }
    }

    public static DecimalFormatSymbols guessSeparators(String from) {
        final DecimalFormatSymbols toReturn = DecimalFormatSymbols.getInstance(Locale.US);

        /*
         * This part checks cases where two separators are present. In this case, the first one is probably the grouping
         * separator, and the second the decimal separator.
         * 
         * Like in 1.254.789,45 or 1 254 789.45
         */
        Matcher matcher = TWO_DIFFERENT_SEPARATORS_PATTERN.matcher(from);
        if (matcher.matches()) {
            if (matcher.groupCount() >= 2) {
                toReturn.setGroupingSeparator(matcher.group(1).charAt(0));
                toReturn.setDecimalSeparator(matcher.group(2).charAt(0));
            }
        }

        /*
         * This part checks cases where there is one separator, following by not 3 digits (less or more). In this case,
         * it's probably a decimal separator. Like in 12,3456 or 12,34
         */
        matcher = ENDS_BY_NOT_3_DIGITS_PATTERN.matcher(from);
        if (matcher.matches()) {
            String firstMatchingGroup = matcher.group(1);
            final char decimalSeparator = firstMatchingGroup.charAt(0);
            toReturn.setDecimalSeparator(decimalSeparator);
            toReturn.setGroupingSeparator(inferGroupingSeparator(decimalSeparator));
        }

        /*
         * This part checks 2 cases: - where value starts with a separator. In this case, it's probably a decimal
         * separator. Like in .254 or ,888 - where value starts with more than 3 digits then a separator. In this case,
         * it's probably a decimal separator. Like in 1234.24 or 1234,888
         */
        matcher = STARTS_WITH_DECIMAL_SEPARATOR_PATTERN.matcher(from);
        if (matcher.matches()) {
            String firstMatchingGroup = matcher.group(1);
            final char decimalSeparator = firstMatchingGroup.charAt(0);
            toReturn.setDecimalSeparator(decimalSeparator);
            toReturn.setGroupingSeparator(inferGroupingSeparator(decimalSeparator));
        }

        /*
         * This part checks cases where a single separator is present, but many times. In this case, it's probably a
         * grouping separator.
         *
         * Like in 2.452.254 or 1 454 888
         */
        matcher = FEW_GROUP_SEP_PATTERN.matcher(from);
        if (matcher.matches()) {
            String firstMatchingGroup = matcher.group(1);
            final char groupingSeparator = firstMatchingGroup.charAt(0);
            toReturn.setGroupingSeparator(groupingSeparator);
            toReturn.setDecimalSeparator(inferDecimalSeparator(groupingSeparator));
        }

        /*
         * This part checks cases where a whitespace separator is present. In this case, it's probably a
         * grouping separator.
         *
         * Like in 3 254
         */
        matcher = CONTAINS_AT_LEAST_ONE_WHITESPACE.matcher(from);
        if (matcher.matches()) {
            String firstMatchingGroup = matcher.group(1);
            final char groupingSeparator = firstMatchingGroup.charAt(0);
            toReturn.setGroupingSeparator(groupingSeparator);
            toReturn.setDecimalSeparator(inferDecimalSeparator(groupingSeparator));
        }

        return toReturn;
    }

    /**
     * Infers the probable decimal separator given a grouping separator.
     * <p>
     * To use when you've guess a probable grouping separator but no clue about a decimal separator (like in a integer
     * for example).
     * <p>
     * Its based on the hypothesis that if we have standard EU grouping separator, it returns standard EU decimal
     * separator, standard US decimal separator otherwise.
     */
    private static char inferDecimalSeparator(char groupingSeparator) {
        switch (groupingSeparator) {
        case '.':
        case ' ':
            return ',';
        default:
            return '.';
        }
    }

    /**
     * Infers the probable grouping separator given a decimal separator.
     * <p>
     * To use when you've guess a probable decimal separator but no clue about a grouping separator (not group, or less
     * than 3 digits in integer part).
     * <p>
     * Its based on the hypothesis that if we have standard US decimal separator, it returns standard US grouping
     * separator, standard EU possible decimal separator otherwise.
     */
    private static char inferGroupingSeparator(char decimalSeparator) {
        switch (decimalSeparator) {
        case '.':
            return ',';
        default:
            return '.';
        }
    }

    /**
     * Basic implementation to get a BigDecimal instance from a Number instance WITHOUT precision lost.
     */
    private static BigDecimal toBigDecimal(Number number) {
        return new BigDecimal(number.toString());
    }

    /**
     * Get the supported decimal formats.
     * <p>
     * Useful to check if a number is supported and can be parsed.
     * 
     * @return the list of supported Formats
     */
    public static List<DecimalFormat> getSupportedFormats() {
        return Arrays.asList(US_DECIMAL_PATTERN, EU_DECIMAL_PATTERN, EU_PERCENTAGE_DECIMAL_PATTERN, US_PERCENTAGE_DECIMAL_PATTERN, //
                US_SCIENTIFIC_DECIMAL_PATTERN, EU_SCIENTIFIC_DECIMAL_PATTERN);
    }

}
