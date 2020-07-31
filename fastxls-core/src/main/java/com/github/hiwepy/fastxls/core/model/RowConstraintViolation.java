/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.core.model;

import java.util.Set;

import javax.validation.ConstraintViolation;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RowConstraintViolation<M extends RowMap> {

	private int row;
	private Set<ConstraintViolation<M>> constraintViolations;

	public RowConstraintViolation(int row, Set<ConstraintViolation<M>> constraintViolations) {
		this.row = row;
		this.constraintViolations = constraintViolations;
	}
	
}
