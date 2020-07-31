/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.core.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class RowWrapper<R> {

	List<Map<String, String>> columns;
	final R row;
	final int rowNum;
	final int startNum;
	Map<String, Object> transMap = new HashMap<String, Object>();
	
	public RowWrapper(R row, List<Map<String, String>> columns, int rowNum, int startNum) {
		this.row = row;
		this.columns = columns;
		this.rowNum = rowNum;
		this.startNum = startNum;
	}
	
	public RowWrapper(R row, Map<String, Object> transMap, List<Map<String, String>> columns, int rowNum, int startNum) {
		this.row = row;
		this.columns = columns;
		this.rowNum = rowNum;
		this.startNum = startNum;
		this.transMap = transMap;
	}
	 
	public RowWrapper(RowWrapper<R> wraper) {
		this.row = wraper.getRow();
		this.rowNum = wraper.getRowNum();
		this.columns = wraper.getColumns();
		this.startNum = wraper.getStartNum();
		this.transMap = wraper.getTransMap();
	}
	
}
