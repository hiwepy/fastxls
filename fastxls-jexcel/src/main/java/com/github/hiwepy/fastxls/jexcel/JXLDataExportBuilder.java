/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.jexcel;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import javax.sql.rowset.CachedRowSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jxl.Cell;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import com.github.hiwepy.fastxls.core.builder.DataExportBuilder;
import com.github.hiwepy.fastxls.core.model.CellModel;
import com.github.hiwepy.fastxls.core.model.RowMap;
import com.github.hiwepy.fastxls.core.model.SheetModel;
import com.github.hiwepy.fastxls.core.model.WorkBookModel;

public class JXLDataExportBuilder<T extends CellModel, M extends RowMap> extends 
	DataExportBuilder<WritableWorkbook, WritableSheet, Cell[], Cell, T, M> {

	protected static Logger LOG = LoggerFactory.getLogger(JXLDataExportBuilder.class);
	private JXLWorkbookFiller workbookFiller = new JXLWorkbookFiller();

	public JXLDataExportBuilder() {
		this(new JXLWorkbookFiller());
	}

	public JXLDataExportBuilder(JXLWorkbookFiller workbookFiller) {
		super(workbookFiller);
	}
	
	@Override
	public void output(WorkBookModel<T> model, OutputStream output) throws Exception {
		new JXLWorkbookBuilder<T>()
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
		new JXLWorkbookBuilder<T>()
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
		new JXLWorkbookBuilder2<T>()
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
		new JXLWorkbookBuilder2<T>()
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
		new JXLWorkbookBuilder2<T>()
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

	public JXLWorkbookFiller getWorkbookFiller() {
		return workbookFiller;
	}
	
}
