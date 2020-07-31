/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.core.builder;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import javax.sql.rowset.CachedRowSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.hiwepy.fastxls.core.WorkbookFiller;
import com.github.hiwepy.fastxls.core.model.CellModel;
import com.github.hiwepy.fastxls.core.model.RowMap;
import com.github.hiwepy.fastxls.core.model.SheetModel;
import com.github.hiwepy.fastxls.core.model.WorkBookModel;

/**
 * @author <a href="https://github.com/vindell">wandl</a>
 */
public abstract class DataExportBuilder<W, S, R, C, T extends CellModel, M extends RowMap>
	extends DataBuilder<DataExportBuilder<W, S, R, C, T, M>, R, C, M> {
	
	protected Logger LOG = LoggerFactory.getLogger(getClass());
	protected final WorkbookFiller<W, S, R, C> workbookFiller;
	
	public DataExportBuilder(WorkbookFiller<W, S, R, C> workbookFiller) {
		this.workbookFiller = workbookFiller;
	}
	
	public abstract void output(WorkBookModel<T> model, OutputStream output) throws Exception;
	
	public abstract void output(SheetModel<T> model, OutputStream output) throws Exception;
	
	public abstract void output(List<Map<String, String>> columnList, String sheetName, OutputStream output) throws Exception;
	
	public abstract void output(List<Map<String, String>> columnList, String sheetName, CachedRowSet rowSet, OutputStream output) throws Exception;
	
	public abstract void output(List<Map<String, String>> columnList, String sheetName, List<Map<String,String>> rowList, OutputStream output) throws Exception;
	
	public DataExportBuilder<W, S, R, C, T, M> and() {
		return this;
	}
	
	@Override
	public DataExportBuilder<W, S, R, C, T, M> build() {
		init();
		return this;
	}
	
	public WorkbookFiller<W, S, R, C> getWorkbookFiller() {
		return workbookFiller;
	}
	
}
