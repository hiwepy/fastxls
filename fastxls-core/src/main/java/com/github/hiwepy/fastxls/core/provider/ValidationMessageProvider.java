/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.core.provider;

import javax.validation.ConstraintViolation;

import com.github.hiwepy.fastxls.core.model.RowMap;

public interface ValidationMessageProvider {

	public <M extends RowMap> String getMessage(ConstraintViolation<M> cvl);
	
}
