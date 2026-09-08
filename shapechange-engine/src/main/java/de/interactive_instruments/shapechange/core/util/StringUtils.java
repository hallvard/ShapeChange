package de.interactive_instruments.shapechange.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Small set of null-safe string helpers, replacing the subset of
 * {@code org.apache.commons.lang3.StringUtils} (and
 * {@code org.apache.commons.lang3.Strings}) actually used across
 * shapechange-engine, so the module does not need commons-lang3 on its
 * classpath. Method names and semantics intentionally match their
 * commons-lang3 counterparts.
 */
public class StringUtils {

    private StringUtils() {
    }

    public static boolean isBlank(CharSequence cs) {
	if (cs == null) {
	    return true;
	}
	int len = cs.length();
	if (len == 0) {
	    return true;
	}
	for (int i = 0; i < len; i++) {
	    if (!Character.isWhitespace(cs.charAt(i))) {
		return false;
	    }
	}
	return true;
    }

    public static boolean isNotBlank(CharSequence cs) {
	return !isBlank(cs);
    }

    public static String defaultIfBlank(String str, String defaultStr) {
	return isBlank(str) ? defaultStr : str;
    }

    public static String stripToNull(String str) {
	if (str == null) {
	    return null;
	}
	String stripped = str.strip();
	return stripped.isEmpty() ? null : stripped;
    }

    public static String stripToEmpty(String str) {
	return str == null ? "" : str.strip();
    }

    public static String trim(String str) {
	return str == null ? null : str.trim();
    }

    /**
     * Joins the string representation of the elements of the given iterable,
     * separated by {@code separator}. A {@code null} element contributes
     * nothing (not the literal "null").
     */
    public static String join(Iterable<?> iterable, String separator) {
	return iterable == null ? null : join(iterable.iterator(), separator);
    }

    public static String join(Object[] array, String separator) {
	return array == null ? null : join(Arrays.asList(array).iterator(), separator);
    }

    private static String join(java.util.Iterator<?> iterator, String separator) {
	StringBuilder sb = new StringBuilder();
	boolean first = true;
	while (iterator.hasNext()) {
	    Object o = iterator.next();
	    if (!first) {
		sb.append(separator);
	    }
	    first = false;
	    if (o != null) {
		sb.append(o);
	    }
	}
	return sb.toString();
    }

    /**
     * Like {@link #join(Iterable, String)}, but {@code null} elements are
     * skipped entirely (no separator artifact is left for them), matching
     * Guava's {@code Joiner.skipNulls()}.
     */
    public static String joinSkipNulls(Iterable<?> iterable, String separator) {
	if (iterable == null) {
	    return null;
	}
	StringBuilder sb = new StringBuilder();
	boolean first = true;
	for (Object o : iterable) {
	    if (o == null) {
		continue;
	    }
	    if (!first) {
		sb.append(separator);
	    }
	    first = false;
	    sb.append(o);
	}
	return sb.toString();
    }

    /**
     * Splits {@code str} on runs of whitespace, discarding empty tokens (and
     * leading/trailing whitespace).
     */
    public static String[] split(String str) {
	return splitWorker(str, null, false);
    }

    /**
     * Splits {@code str} on any character contained in {@code separatorChars}
     * (i.e. {@code separatorChars} is a set of delimiter characters, not a
     * literal substring or regex), discarding empty tokens.
     */
    public static String[] split(String str, String separatorChars) {
	return splitWorker(str, separatorChars, false);
    }

    /**
     * Like {@link #split(String, String)}, but keeps empty tokens (e.g.
     * between two consecutive delimiters).
     */
    public static String[] splitPreserveAllTokens(String str, String separatorChars) {
	return splitWorker(str, separatorChars, true);
    }

    /**
     * Splits {@code str} on the literal (non-empty) substring
     * {@code separator} (not a character set or regex, unlike
     * {@link #split(String, String)}), matching Guava's
     * {@code Splitter.on(separator)}. By default all tokens (including empty
     * ones) are kept, in original order; {@code omitEmptyStrings} drops empty
     * tokens (checked after trimming, if {@code trimResults} is also set) and
     * {@code trimResults} strips each token.
     */
    public static List<String> splitToList(String str, String separator, boolean omitEmptyStrings,
	    boolean trimResults) {
	List<String> result = new ArrayList<>();
	if (str == null) {
	    return result;
	}
	int start = 0;
	while (true) {
	    int idx = str.indexOf(separator, start);
	    String token = idx == -1 ? str.substring(start) : str.substring(start, idx);
	    if (trimResults) {
		token = token.strip();
	    }
	    if (!(omitEmptyStrings && token.isEmpty())) {
		result.add(token);
	    }
	    if (idx == -1) {
		break;
	    }
	    start = idx + separator.length();
	}
	return result;
    }

    private static String[] splitWorker(String str, String separatorChars, boolean preserveAllTokens) {
	if (str == null) {
	    return null;
	}
	int len = str.length();
	if (len == 0) {
	    return new String[0];
	}
	List<String> list = new ArrayList<>();
	int i = 0, start = 0;
	boolean match = false;
	boolean lastMatch = false;
	if (separatorChars == null) {
	    while (i < len) {
		if (Character.isWhitespace(str.charAt(i))) {
		    if (match || preserveAllTokens) {
			list.add(str.substring(start, i));
			match = false;
			lastMatch = true;
		    }
		    start = ++i;
		    continue;
		}
		lastMatch = false;
		match = true;
		i++;
	    }
	} else {
	    while (i < len) {
		if (separatorChars.indexOf(str.charAt(i)) >= 0) {
		    if (match || preserveAllTokens) {
			list.add(str.substring(start, i));
			match = false;
			lastMatch = true;
		    }
		    start = ++i;
		    continue;
		}
		lastMatch = false;
		match = true;
		i++;
	    }
	}
	if (match || (preserveAllTokens && lastMatch)) {
	    list.add(str.substring(start, i));
	}
	return list.toArray(new String[0]);
    }

    public static String substringAfterLast(String str, String separator) {
	if (isEmpty(str)) {
	    return str;
	}
	if (isEmpty(separator)) {
	    return "";
	}
	int pos = str.lastIndexOf(separator);
	if (pos == -1 || pos == str.length() - separator.length()) {
	    return "";
	}
	return str.substring(pos + separator.length());
    }

    /**
     * Null-safe comparison: two nulls are equal, a null is less than any
     * non-null value.
     */
    public static int compare(String str1, String str2) {
	if (str1 == str2) {
	    return 0;
	}
	if (str1 == null) {
	    return -1;
	}
	if (str2 == null) {
	    return 1;
	}
	return str1.compareTo(str2);
    }

    public static boolean equalsIgnoreCase(CharSequence cs1, CharSequence cs2) {
	if (cs1 == cs2) {
	    return true;
	}
	if (cs1 == null || cs2 == null) {
	    return false;
	}
	return cs1.toString().equalsIgnoreCase(cs2.toString());
    }

    /**
     * Case-sensitive, null-safe: {@code true} if {@code string} equals (or,
     * for a {@code null} candidate, if {@code string} is also {@code null})
     * any of {@code searchStrings}.
     */
    public static boolean equalsAny(CharSequence string, CharSequence... searchStrings) {
	if (searchStrings != null) {
	    for (CharSequence candidate : searchStrings) {
		if (equals(string, candidate)) {
		    return true;
		}
	    }
	}
	return false;
    }

    /**
     * Case-insensitive, null-safe variant of {@link #equalsAny}.
     */
    public static boolean equalsIgnoreCaseAny(CharSequence string, CharSequence... searchStrings) {
	if (searchStrings != null) {
	    for (CharSequence candidate : searchStrings) {
		if (equalsIgnoreCase(string, candidate)) {
		    return true;
		}
	    }
	}
	return false;
    }

    public static int countMatches(CharSequence str, CharSequence sub) {
	if (isEmpty(str) || isEmpty(sub)) {
	    return 0;
	}
	int count = 0;
	int idx = 0;
	String s = str.toString();
	String subStr = sub.toString();
	while ((idx = s.indexOf(subStr, idx)) != -1) {
	    count++;
	    idx += subStr.length();
	}
	return count;
    }

    /**
     * Removes {@code remove} from the start of {@code str}, if present.
     */
    public static String removeStart(String str, String remove) {
	if (isEmpty(str) || isEmpty(remove)) {
	    return str;
	}
	return str.startsWith(remove) ? str.substring(remove.length()) : str;
    }

    /**
     * Removes {@code remove} from the end of {@code str}, if present.
     */
    public static String removeEnd(String str, String remove) {
	if (isEmpty(str) || isEmpty(remove)) {
	    return str;
	}
	return str.endsWith(remove) ? str.substring(0, str.length() - remove.length()) : str;
    }

    private static boolean isEmpty(CharSequence cs) {
	return cs == null || cs.length() == 0;
    }

    private static boolean equals(CharSequence cs1, CharSequence cs2) {
	if (cs1 == cs2) {
	    return true;
	}
	if (cs1 == null || cs2 == null) {
	    return false;
	}
	return cs1.toString().contentEquals(cs2);
    }
}
