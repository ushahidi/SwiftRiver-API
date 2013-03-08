package com.ushahidi.swiftriver.core.api.controller;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.ushahidi.swiftriver.core.api.dto.ErrorDTO;
import com.ushahidi.swiftriver.core.api.exception.BadRequestException;
import com.ushahidi.swiftriver.core.api.exception.ForbiddenException;
import com.ushahidi.swiftriver.core.api.exception.NotFoundException;

public abstract class AbstractController {
	
	final Logger logger = LoggerFactory.getLogger(AbstractController.class);
	
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorDTO> handleIOException(Exception ex) {
		ErrorDTO errorTO = new ErrorDTO();
		errorTO.setMessage(ex.getMessage());
		
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
		
		if (ex instanceof NotFoundException) {
			status = HttpStatus.NOT_FOUND;
		} else if (ex instanceof BadRequestException) {
			status = HttpStatus.BAD_REQUEST;
			errorTO.setErrors(((BadRequestException) ex).getErrors());			
		} else if (ex instanceof ForbiddenException) {
			status = HttpStatus.FORBIDDEN;
		} else {
			errorTO.setMessage("Unknown error");
			logger.error(ExceptionUtils.getStackTrace(ex));
		}
		
		return new ResponseEntity<ErrorDTO>(errorTO, status);
	}
}
