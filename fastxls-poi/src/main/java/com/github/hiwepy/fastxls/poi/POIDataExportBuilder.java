/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.poi;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import javax.sql.rowset.CachedRowSet;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.github.hiwepy.fastxls.core.builder.DataExportBuilder;
import com.github.hiwepy.fastxls.core.model.CellModel;
import com.github.hiwepy.fastxls.core.model.RowMap;
import com.github.hiwepy.fastxls.core.model.SheetModel;
import com.github.hiwepy.fastxls.core.model.WorkBookModel;

public class POIDataExportBuilder<T extends CellModel, M extends RowMap> 
	extends DataExportBuilder<Workbook, Sheet, Row, Cell , T, M> {

	public POIDataExportBuilder() {
		this(new POIWorkbookFiller());
	}

	public POIDataExportBuilder(POIWorkbookFiller workbookFiller) {
		super(workbookFiller);
	}
	
	

	@Override
	public void output(WorkBookModel<T> model, OutputStream output) throws Exception {
		new POIWorkbookBuilder<T>()
			.wookbook(model)
			.output(output)
			.maxRow(this.getMaxRow())
			.namePrefix(this.getNamePrefix())
			.nameSuffix(this.getNameSuffix())
			.password(this.getPassword())
			.suffix(this.getSuffix())
			.tmpdir(this.getTmpdir())
			.build();
	}

	@Override
	public void output(SheetModel<T> model, OutputStream output) throws Exception {
		new POIWorkbookBuilder<T>()
			.sheet(model)
			.output(output)
			.maxRow(this.getMaxRow())
			.namePrefix(this.getNamePrefix())
			.nameSuffix(this.getNameSuffix())
			.password(this.getPassword())
			.suffix(this.getSuffix())
			.tmpdir(this.getTmpdir())
			.build();
	}

	@Override
	public void output(List<Map<String, String>> columnList, String sheetName, OutputStream output) throws Exception {
		new POIWorkbookBuilder2<T>()
			.columnList(columnList)
			.output(output)
			.sheetName(sheetName)
			.namePrefix(this.getNamePrefix())
			.nameSuffix(this.getNameSuffix())
			.password(this.getPassword())
			.suffix(this.getSuffix())
			.build();
	}
	
	@Override
	public void output(List<Map<String, String>> columnList, String sheetName, CachedRowSet rowSet, OutputStream output) throws Exception {
		new POIWorkbookBuilder2<T>()
			.columnList(columnList)
			.rowSet(rowSet)
			.output(output)
			.sheetName(sheetName)
			.maxThreadSize(this.getMaxThreadSize())
			.maxThreadTaskSize(this.getMaxThreadTaskSize())
			.threadNamePrefix(this.getThreadNamePrefix())
			.threadPool(this.isThreadPool())
			.maxRow(this.getMaxRow())
			.namePrefix(this.getNamePrefix())
			.nameSuffix(this.getNameSuffix())
			.password(this.getPassword())
			.suffix(this.getSuffix())
			.tmpdir(this.getTmpdir())
			.build();
	}

	@Override
	public void output(List<Map<String, String>> columnList, String sheetName, List<Map<String, String>> rowList, OutputStream output) throws Exception {
		new POIWorkbookBuilder2<T>()
		.columnList(columnList)
		.rowList(rowList)
		.output(output)
		.sheetName(sheetName)
		.maxThreadSize(this.getMaxThreadSize())
		.maxThreadTaskSize(this.getMaxThreadTaskSize())
		.threadNamePrefix(this.getThreadNamePrefix())
		.threadPool(this.isThreadPool())
		.maxRow(this.getMaxRow())
		.namePrefix(this.getNamePrefix())
		.nameSuffix(this.getNameSuffix())
		.password(this.getPassword())
		.suffix(this.getSuffix())
		.tmpdir(this.getTmpdir())
		.build();
	}

	
}
