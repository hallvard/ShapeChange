package de.interactive_instruments.shapechange.core.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * Validates {@link XsltWriter#parseFormEncoded(String, java.nio.charset.Charset)}
 * and {@link XsltWriter#formatFormEncoded(Iterable, java.nio.charset.Charset)}
 * against the {@code application/x-www-form-urlencoded} semantics they replace
 * (previously provided by httpcore5's {@code WWWFormCodec}).
 */
class XsltWriterTest {

    @Test
    void parsesKeyValuePairs() {
	List<Map.Entry<String, String>> result = XsltWriter.parseFormEncoded("a=1&b=2",
		XsltWriter.ENCODING_CHARSET);
	assertEquals(2, result.size());
	assertEquals("a", result.get(0).getKey());
	assertEquals("1", result.get(0).getValue());
	assertEquals("b", result.get(1).getKey());
	assertEquals("2", result.get(1).getValue());
    }

    @Test
    void urlDecodesKeysAndValues() {
	List<Map.Entry<String, String>> result = XsltWriter.parseFormEncoded("na%20me=val+ue",
		XsltWriter.ENCODING_CHARSET);
	assertEquals(1, result.size());
	assertEquals("na me", result.get(0).getKey());
	assertEquals("val ue", result.get(0).getValue());
    }

    @Test
    void treatsPairWithoutEqualsAsEmptyValue() {
	List<Map.Entry<String, String>> result = XsltWriter.parseFormEncoded("onlykey",
		XsltWriter.ENCODING_CHARSET);
	assertEquals(1, result.size());
	assertEquals("onlykey", result.get(0).getKey());
	assertEquals("", result.get(0).getValue());
    }

    @Test
    void skipsEmptyPairsAndHandlesNullOrEmptyInput() {
	List<Map.Entry<String, String>> result = XsltWriter.parseFormEncoded("a=1&&b=2&",
		XsltWriter.ENCODING_CHARSET);
	assertEquals(2, result.size());

	assertTrue(XsltWriter.parseFormEncoded(null, XsltWriter.ENCODING_CHARSET).isEmpty());
	assertTrue(XsltWriter.parseFormEncoded("", XsltWriter.ENCODING_CHARSET).isEmpty());
    }

    @Test
    void formatsKeyValuePairs() {
	List<Map.Entry<String, String>> pairs = List.of(new AbstractMap.SimpleImmutableEntry<>("a", "1"),
		new AbstractMap.SimpleImmutableEntry<>("b", "2"));
	assertEquals("a=1&b=2", XsltWriter.formatFormEncoded(pairs, XsltWriter.ENCODING_CHARSET));
    }

    @Test
    void formatUrlEncodesKeysAndValues() {
	List<Map.Entry<String, String>> pairs = List
		.of(new AbstractMap.SimpleImmutableEntry<>("na me", "val ue"));
	assertEquals("na+me=val+ue", XsltWriter.formatFormEncoded(pairs, XsltWriter.ENCODING_CHARSET));
    }

    @Test
    void formatAndParseRoundTrip() {
	List<Map.Entry<String, String>> original = List.of(new AbstractMap.SimpleImmutableEntry<>("key one", "val=1"),
		new AbstractMap.SimpleImmutableEntry<>("key&two", "val 2"));

	String encoded = XsltWriter.formatFormEncoded(original, XsltWriter.ENCODING_CHARSET);
	List<Map.Entry<String, String>> roundTripped = XsltWriter.parseFormEncoded(encoded,
		XsltWriter.ENCODING_CHARSET);

	assertEquals(original.size(), roundTripped.size());
	for (int i = 0; i < original.size(); i++) {
	    assertEquals(original.get(i).getKey(), roundTripped.get(i).getKey());
	    assertEquals(original.get(i).getValue(), roundTripped.get(i).getValue());
	}
    }
}
