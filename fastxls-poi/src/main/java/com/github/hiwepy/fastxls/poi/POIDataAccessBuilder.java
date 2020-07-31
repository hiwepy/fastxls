/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.poi;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.github.hiwepy.fastxls.core.builder.DataAccessBuilder;
import com.github.hiwepy.fastxls.core.model.RowMap;

public class POIDataAccessBuilder<M extends RowMap> extends DataAccessBuilder<Workbook, Sheet, Row, Cell , M> {

	public POIDataAccessBuilder() {
		this(new POIWorkbookReader());
	}

	public POIDataAccessBuilder(POIWorkbookReader workbookReader) {
		super(workbookReader);
	}

}
