/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.core.provider.def;

import javax.validation.ConstraintViolation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import com.github.hiwepy.fastxls.core.model.RowMap;
import com.github.hiwepy.fastxls.core.provider.ValidationMessageProvider;

public class DefaultValidationMessageProvider implements ValidationMessageProvider {
	
	private MessageSource source;
	
	public DefaultValidationMessageProvider() {}
	
	public DefaultValidationMessageProvider(MessageSource source) {
		this.source = source;
	}
	
	public <M extends RowMap> String getMessage(ConstraintViolation<M> cvl) {
		// see ConstraintViolationExceptionMessage
		return StringUtils.defaultString(cvl.getMessage(),
				getSource() != null ? getSource().getMessage(cvl.getMessageTemplate(),
						new Object[] { cvl.getLeafBean().getClass().getSimpleName(), cvl.getPropertyPath().toString(), cvl.getInvalidValue() },
						cvl.getMessage(), LocaleContextHolder.getLocale()) : "");
	}
	
	public MessageSource getSource() {
		return source;
	}
	
}
