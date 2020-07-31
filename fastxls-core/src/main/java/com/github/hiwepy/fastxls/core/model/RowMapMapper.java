/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.core.model;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class RowMapMapper<R> extends RowMapper<R, RowMap > {
	
	public RowMapMapper(R row, List<Map<String, String>> columns, int rowNum,
			RowMap mapper) {
		super(false, row, columns, rowNum, mapper);
	}
	
	public RowMapMapper(R row, List<Map<String, String>> columns, int rowNum,
			RowMap mapper, boolean title) {
		super(title, row, columns, rowNum, mapper);
	}
 
}
