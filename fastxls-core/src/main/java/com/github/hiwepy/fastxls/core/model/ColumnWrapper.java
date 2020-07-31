/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.core.model;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class ColumnWrapper<C> {

	final Map<String, String> columnMap;
	final C column;
	final int rowNum;
	final int colNum;
	final Map<String, Object> rowMap;
	
	public ColumnWrapper(C column, Map<String, String> columnMap, Map<String, Object> rowMap, int rowNum, int colNum) {
		this.column = column;
		this.columnMap = columnMap;
		this.colNum = colNum;
		this.rowMap = rowMap;
		this.rowNum = rowNum;
	}
	
	public ColumnWrapper(ColumnWrapper<C> wraper) {
		this.rowNum = wraper.getRowNum();
		this.rowMap = wraper.getRowMap();
		this.column = wraper.getColumn();
		this.columnMap = wraper.getColumnMap();
		this.colNum = wraper.getColNum();
	}
	
}
