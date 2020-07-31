/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.core;

import java.util.List;
import java.util.Map;

import javax.sql.rowset.CachedRowSet;

import com.github.hiwepy.fastxls.core.model.CellModel;
import com.github.hiwepy.fastxls.core.model.RowModel;
import com.github.hiwepy.fastxls.core.model.SheetModel;
import com.github.hiwepy.fastxls.core.model.WorkBookModel;

public interface WorkbookFiller<W, S, R, C> {

	public static String HT = "黑体";
	public static String ST = "宋体";
	
	public <T extends CellModel> void fillSheets(W workbook, WorkBookModel<T> dataModel) throws Exception;

	public <T extends CellModel> void fillSheets(W workbook, WorkBookModel<T> dataModel, int startRow) throws Exception;
	
	public <T extends CellModel> void fillSheets(W workbook, List<SheetModel<T>> sheets, int startRow) throws Exception;
	
	public <T extends CellModel> void fillSheets(W workbook, SheetModel<T> sheetModel, int startRow) throws Exception;
	
	public <T extends CellModel> void fillSheet(S sheet, SheetModel<T> sheetModel, int startRow) throws Exception;
	
	public <T extends CellModel> void fillSheet(S sheet, String title, SheetModel<T> sheetModel,
			int startRow) throws Exception;
	
	public void fillSheet(S sheet, CachedRowSet rowSet, List<Map<String, String>> columnList) throws Exception;
			
	public void fillSheet(S sheet, CachedRowSet rowSet, List<Map<String, String>> columnList, int offset, int limit) throws Exception;
	
	public <T extends CellModel> void fillRows(S sheet, List<RowModel<T>> rowList, int startRow) throws Exception;
	
	public void fillHead(S sheet, List<Map<String, String>> columnList, int rowNum) throws Exception ;
	
	public void fillRows(S sheet, List<Map<String, String>> columnList,  List<Map<String, String>> rowList, int startRow) throws Exception ;
	
	public void fillRow(S sheet, List<Map<String, String>> columnList,  Map<String, String> rowMap, int rowNum) throws Exception ;
	
	public <T extends CellModel> void fillRows(S sheet, RowModel<T> rowModel) throws Exception;
	
	public <T extends CellModel> void fillMerged(S sheet, RowModel<T> rowModel) throws Exception;
	
	public <T extends CellModel> void fillRow(R row, RowModel<T> rowModel) throws Exception;
	
	public <T extends CellModel> void fillCellWithStyle(C cell, T cellModel) throws Exception;
	
	public <T extends CellModel> void fillCell(C cell, T cellModel) throws Exception;
	
	public <T extends CellModel> void fillComment(C cell, T cellModel) throws Exception;
	
	public <T extends CellModel> void fillComment(C cell, String comments) throws Exception;
	
}
