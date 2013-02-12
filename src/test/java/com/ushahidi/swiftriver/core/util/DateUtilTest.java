package com.ushahidi.swiftriver.core.util;

import static org.junit.Assert.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

public class DateUtilTest {
	
	@Test
	public void formatRFC822WithTimezone() throws ParseException {
		
		DateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
		Date date = formatter.parse("Wed, 2 Jan 2013 03:00:02 +0300");
		
		assertEquals("Wed, 2 Jan 2013 00:00:02 +0000", DateUtil.formatRFC822(date, "UTC"));
	}	
}
