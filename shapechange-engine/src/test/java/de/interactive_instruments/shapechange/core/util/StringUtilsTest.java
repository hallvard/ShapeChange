package de.interactive_instruments.shapechange.core.util;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Validates {@link StringUtils} against the commons-lang3 {@code StringUtils}
 * behavior it replaces, in particular the character-set (not regex/literal)
 * semantics of {@code split}/{@code splitPreserveAllTokens}.
 */
class StringUtilsTest {

    @Test
    void blankChecks() {
	assertTrue(StringUtils.isBlank(null));
	assertTrue(StringUtils.isBlank(""));
	assertTrue(StringUtils.isBlank("  \t "));
	assertFalse(StringUtils.isBlank(" a "));

	assertFalse(StringUtils.isNotBlank(null));
	assertTrue(StringUtils.isNotBlank("a"));
    }

    @Test
    void defaultIfBlankUsesDefaultOnlyWhenBlank() {
	assertEquals("default", StringUtils.defaultIfBlank(null, "default"));
	assertEquals("default", StringUtils.defaultIfBlank("  ", "default"));
	assertEquals("value", StringUtils.defaultIfBlank("value", "default"));
    }

    @Test
    void stripToNullAndEmpty() {
	assertNull(StringUtils.stripToNull(null));
	assertNull(StringUtils.stripToNull("   "));
	assertEquals("a", StringUtils.stripToNull("  a  "));

	assertEquals("", StringUtils.stripToEmpty(null));
	assertEquals("", StringUtils.stripToEmpty("   "));
	assertEquals("a", StringUtils.stripToEmpty("  a  "));
    }

    @Test
    void trimIsNullSafe() {
	assertNull(StringUtils.trim(null));
	assertEquals("a", StringUtils.trim("  a  "));
    }

    @Test
    void joinTreatsNullElementsAsEmpty() {
	assertEquals("a, b, c", StringUtils.join(List.of("a", "b", "c"), ", "));
	assertEquals("a, , c", StringUtils.join(Arrays.asList("a", null, "c"), ", "));
	assertNull(StringUtils.join((Iterable<?>) null, ", "));
	assertEquals("", StringUtils.join(List.of(), ", "));

	assertEquals("a, b", StringUtils.join(new String[] { "a", "b" }, ", "));
	assertNull(StringUtils.join((String[]) null, ", "));
    }

    @Test
    void splitOnWhitespaceDiscardsEmptyTokens() {
	assertArrayEquals(new String[] { "a", "b", "c" }, StringUtils.split("  a   b c  "));
	assertNull(StringUtils.split(null));
    }

    @Test
    void splitTreatsSeparatorCharsAsASet() {
	// ", " means comma OR space is a delimiter, not the literal substring ", "
	assertArrayEquals(new String[] { "a", "b", "c" }, StringUtils.split("a, b,c", ", "));
	assertArrayEquals(new String[] { "a", "b" }, StringUtils.split("a#b", "#"));
	// consecutive delimiters collapse, like commons-lang3's split (not String.split)
	assertArrayEquals(new String[] { "a", "b" }, StringUtils.split("a##b", "#"));
	assertArrayEquals(new String[] { "a", "b" }, StringUtils.split(",a,,b,", ","));
    }

    @Test
    void splitPreserveAllTokensKeepsEmptyTokens() {
	assertArrayEquals(new String[] { "a", "", "b" }, StringUtils.splitPreserveAllTokens("a##b", "#"));
	assertArrayEquals(new String[] { "", "a", "" }, StringUtils.splitPreserveAllTokens("#a#", "#"));
    }

    @Test
    void substringAfterLastMatchesCommonsSemantics() {
	assertEquals("a", StringUtils.substringAfterLast("abcba", "b"));
	assertEquals("", StringUtils.substringAfterLast("abc", "c"));
	assertEquals("", StringUtils.substringAfterLast("abc", "x"));
	assertEquals("", StringUtils.substringAfterLast("abc", null));
	assertNull(StringUtils.substringAfterLast(null, "b"));
    }

    @Test
    void compareIsNullSafeWithNullsFirst() {
	assertEquals(0, StringUtils.compare(null, null));
	assertTrue(StringUtils.compare(null, "a") < 0);
	assertTrue(StringUtils.compare("a", null) > 0);
	assertTrue(StringUtils.compare("a", "b") < 0);
    }

    @Test
    void equalsIgnoreCaseIsNullSafe() {
	assertTrue(StringUtils.equalsIgnoreCase(null, null));
	assertFalse(StringUtils.equalsIgnoreCase("a", null));
	assertTrue(StringUtils.equalsIgnoreCase("TRUE", "true"));
    }

    @Test
    void equalsAnyMatchesNullCandidateAgainstNullTarget() {
	// mirrors AbstractConfigurationValidator's optional-boolean-parameter check
	assertTrue(StringUtils.equalsAny(null, null, "true", "false"));
	assertTrue(StringUtils.equalsAny("true", "1", "true"));
	assertFalse(StringUtils.equalsAny("maybe", "1", "true"));
    }

    @Test
    void equalsIgnoreCaseAnyIsCaseInsensitive() {
	assertTrue(StringUtils.equalsIgnoreCaseAny("TRUE", "true", "false"));
	assertFalse(StringUtils.equalsIgnoreCaseAny("maybe", "true", "false"));
    }

    @Test
    void countMatchesCountsNonOverlappingOccurrences() {
	assertEquals(0, StringUtils.countMatches("abc", ":"));
	assertEquals(1, StringUtils.countMatches("a:b", ":"));
	assertEquals(2, StringUtils.countMatches("a:b:c", ":"));
    }

    @Test
    void removeStartAndEnd() {
	assertEquals("value", StringUtils.removeEnd("value==", "=="));
	assertEquals("value=", StringUtils.removeEnd("value=", "=="));
	assertEquals("bc", StringUtils.removeStart("abc", "a"));
	assertEquals("abc", StringUtils.removeStart("abc", "x"));
    }

    @Test
    void joinSkipNullsExcludesNullElementsEntirely() {
	assertEquals("a, c", StringUtils.joinSkipNulls(Arrays.asList("a", null, "c"), ", "));
	assertEquals("", StringUtils.joinSkipNulls(Arrays.asList((String) null, null), ", "));
	assertNull(StringUtils.joinSkipNulls(null, ", "));
    }

    @Test
    void splitToListMatchesGuavaSplitterDefaults() {
	// default: literal substring separator, empty tokens kept, no trimming
	assertEquals(List.of("a", "b", "c"), StringUtils.splitToList("a,b,c", ",", false, false));
	assertEquals(List.of("a", "", "c"), StringUtils.splitToList("a,,c", ",", false, false));
	assertEquals(List.of(" a ", " b "), StringUtils.splitToList(" a , b ", ",", false, false));
    }

    @Test
    void splitToListTrimsBeforeOmittingEmptyStrings() {
	assertEquals(List.of("a", "b"), StringUtils.splitToList(" a , b ", ",", true, true));
	// a token that is only whitespace becomes empty after trimming, then omitted
	assertEquals(List.of("a", "b"), StringUtils.splitToList("a, ,b", ",", true, true));
    }

    @Test
    void splitToListSplitsOnLiteralMultiCharSeparator() {
	// "::" is a literal separator here, unlike split()'s character-set semantics
	assertEquals(List.of("a", "b", "c"), StringUtils.splitToList("a::b::c", "::", false, false));
    }
}
