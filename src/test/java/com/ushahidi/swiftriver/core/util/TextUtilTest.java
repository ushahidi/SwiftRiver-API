package com.ushahidi.swiftriver.core.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class TextUtilTest {

	@Test
	public void getURLSlug() {
		assertEquals("my-phrase-", TextUtil.getURLSlug("My Phrase!"));
	}
}
