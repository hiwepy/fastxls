/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.jexcel;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import com.github.hiwepy.fastxls.core.builder.DataAccessBuilder;
import com.github.hiwepy.fastxls.core.model.RowMap;

/**
 * Excel 工作簿 范围多线程访问，可借助此方法实现多线程同时进行内容检查，单元格数据校验
 */
public class JXLDataAccessBuilder<M extends RowMap> extends DataAccessBuilder<Workbook, Sheet, Cell[], Cell , M> {

	public JXLDataAccessBuilder() {
		this(new JXLWorkbookReader());
	}

	public JXLDataAccessBuilder(JXLWorkbookReader workbookReader) {
		super(workbookReader);
	}
	
}
