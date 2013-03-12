package com.ushahidi.swiftriver.core.util;

import static org.junit.Assert.*;

import org.junit.Test;

import com.ushahidi.swiftriver.core.api.exception.BadRequestException;
import com.ushahidi.swiftriver.core.api.exception.ErrorField;

public class ErrorUtilTest {

	@Test
	public void getBadRequestException() {
		BadRequestException ex = ErrorUtil.getBadRequestException("my field", "my code");
		
		assertEquals(1, ex.getErrors().size());
		ErrorField error = ex.getErrors().get(0);
		assertEquals("my field", error.getField());
		assertEquals("my code", error.getCode());
	}
}
