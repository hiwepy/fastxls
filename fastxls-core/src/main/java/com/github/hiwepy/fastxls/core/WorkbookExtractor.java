/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.core;

import java.io.IOException;

import com.github.hiwepy.fastxls.core.model.CellModel;
import com.github.hiwepy.fastxls.core.model.RowModel;
import com.github.hiwepy.fastxls.core.model.SheetModel;
import com.github.hiwepy.fastxls.core.model.WorkBookModel;

public interface WorkbookExtractor<W, S, R, C> {

	public <T extends CellModel> WorkBookModel<T> extract(String filePath) throws IOException;
	
	public <T extends CellModel> WorkBookModel<T> extract(String filePath, int startRow) throws IOException;
	
	public <T extends CellModel> WorkBookModel<T> extract(W workbook) throws IOException;
	
	public <T extends CellModel> WorkBookModel<T> extract(W workbook,int startRow) throws IOException;
	
	public <T extends CellModel> SheetModel<T> extractSheet(S sheet) throws IOException;
	
	public <T extends CellModel> SheetModel<T> extractSheet(S sheet,int startRowIndex) throws IOException;
	
	public <T extends CellModel> SheetModel<T> extractSheet(S sheet, int startRowIndex, int endRowIndex) throws IOException;
	
	public <T extends CellModel> RowModel<T> extractRow(R row) throws IOException;
	
	public <T extends CellModel> T extractCell(C cell) throws IOException;
	
}
