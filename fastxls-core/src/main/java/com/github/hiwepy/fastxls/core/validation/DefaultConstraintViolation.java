/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.core.validation;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.metadata.ConstraintDescriptor;

public class DefaultConstraintViolation<T extends Object> implements ConstraintViolation<T> {

	final String message;
	final Object invalidValue;
	
	public DefaultConstraintViolation(Object invalidValue, String message) {
		this.invalidValue = invalidValue;
		this.message = message;
	}
	
	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public String getMessageTemplate() {
		return null;
	}

	@Override
	public T getRootBean() {
		return null;
	}

	@Override
	public Class<T> getRootBeanClass() {
		return null;
	}

	@Override
	public Object getLeafBean() {
		return null;
	}

	@Override
	public Object[] getExecutableParameters() {
		return null;
	}

	@Override
	public Object getExecutableReturnValue() {
		return null;
	}

	@Override
	public Path getPropertyPath() {
		return null;
	}

	@Override
	public Object getInvalidValue() {
		return invalidValue;
	}

	@Override
	public ConstraintDescriptor<?> getConstraintDescriptor() {
		return null;
	}

	@Override
	public <U> U unwrap(Class<U> type) {
		return null;
	}
	
}
