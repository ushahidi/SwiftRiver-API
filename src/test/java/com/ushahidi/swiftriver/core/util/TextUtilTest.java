package com.ushahidi.swiftriver.core.util;

import static org.junit.Assert.*;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class TextUtilTest {

	@Test
	public void getURLSlug() {
		assertEquals("my-phrase-", TextUtil.getURLSlug("My Phrase!"));
	}
	
	@Test
	public void convertStringToHex() {
		assertEquals("006162636465", TextUtil.convertStringToHex("\0abcde"));
	}
}
