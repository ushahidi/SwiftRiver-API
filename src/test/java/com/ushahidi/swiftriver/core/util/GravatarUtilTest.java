package com.ushahidi.swiftriver.core.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class GravatarUtilTest {

	@Test
	public void gravatar() {
		assertEquals(
				"https://secure.gravatar.com/avatar/95db92b97e6b611f76d18921733aa79a?s=80&d=mm&r=g",
				GravatarUtil.gravatar(" Info@ushahidi.com"));
	}
}
