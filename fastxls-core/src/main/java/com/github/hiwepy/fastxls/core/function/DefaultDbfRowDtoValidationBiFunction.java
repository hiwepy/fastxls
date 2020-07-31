/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.core.function;

import java.util.Set;
import java.util.function.BiFunction;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.jamel.dbf.structure.DbfRow;

import com.github.hiwepy.fastxls.core.model.RowMap;
import com.github.hiwepy.fastxls.core.model.RowMapper;

public class DefaultDbfRowDtoValidationBiFunction<M extends RowMap> implements BiFunction<RowMapper<DbfRow, M>, Integer, Set<ConstraintViolation<M>>> {
	
	private final Validator validator;
	
    public DefaultDbfRowDtoValidationBiFunction(Validator validator){
		this.validator = validator;
	}

	@Override
	public Set<ConstraintViolation<M>> apply(RowMapper<DbfRow, M> mapper, Integer rowNum) {
		// 对转换的数据果进行验证
		return getValidator().validate(mapper.getMapper());
	}
	
	public Validator getValidator() {
		return validator;
	}

}
