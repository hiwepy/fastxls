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
public class RowMapper<R, M extends RowMap> {

	/**
	 * 当前行是否是标题行- true|false
	 */
	final boolean title;
	/**
	 * 原始行对象
	 */
	final R row;
	/**
	 * 当前行数据对应的列信息：特别是列索引信息
	 */
	final List<Map<String, String>> columns;
	/**
	 * 当前行的行号
	 */
	final int rowNum;
	/**
	 * 行数据转换后的对象
	 */
	final M mapper;
	
	public RowMapper(boolean title, R row, List<Map<String, String>> columns, int rowNum, M mapper) {
		this.title = title;
		this.row = row;
		this.columns = columns;
		this.rowNum = rowNum;
		this.mapper = mapper;
	}
	
}
