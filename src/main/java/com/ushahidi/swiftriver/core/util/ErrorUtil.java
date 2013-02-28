package com.ushahidi.swiftriver.core.util;

import java.util.ArrayList;
import java.util.List;

import com.ushahidi.swiftriver.core.api.exception.BadRequestException;
import com.ushahidi.swiftriver.core.api.exception.ErrorField;

public class ErrorUtil {

	public static BadRequestException getBadRequestException(String field, String code) {
		BadRequestException ex = new BadRequestException(
				"Invalid parameter");
		List<ErrorField> errors = new ArrayList<ErrorField>();
		errors.add(new ErrorField(field, code));
		ex.setErrors(errors);
		
		return ex;
	}
}
