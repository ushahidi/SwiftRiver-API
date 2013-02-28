package com.ushahidi.swiftriver.core.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class MD5UtilTest {

	@Test
	public void md5Hex() {
		assertEquals("98340fd0d8989278874081e95113e645", MD5Util.md5Hex("Ushahidi Rocks"));
	}
}
