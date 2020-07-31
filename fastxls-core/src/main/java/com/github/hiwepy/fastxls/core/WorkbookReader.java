/*
 * Copyright (c) 2010-2020, vindell (https://github.com/vindell).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.github.hiwepy.fastxls.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import com.github.hiwepy.fastxls.core.model.ColumnWrapper;
import com.github.hiwepy.fastxls.core.model.RowMapMapper;

public interface WorkbookReader<W, S, R, C> {

	public S[] getSheets(InputStream input) throws IOException;
	
	public S[] getSheets(File file) throws IOException;
	
	public S[] getSheets(W wb) throws IOException;
	
	public S getSheet(File file, int sheetNum) throws IOException;
	
	public S getSheet(File file, String sheetName) throws IOException;
	
	public S getSheet(W wb, int sheetNum) throws IOException;
	
	public S getSheet(W wb, String sheetName) throws IOException;
	
	public Map<String, String> getSheetInfo(File file, int index) throws IOException;
	
	public List<R> getRows(S sheet, int fromIndex, int toIndex) throws IOException;
	
	public int getFirstRowNum(S sheet) throws IOException;
	
	public int getLastRowNum(S sheet) throws IOException;
	
	public String getSheetName(S sheet);
	
	public R getRow(File file, int sheetNum, int rowNum) throws IOException;

	public R getRow(File file, String sheetName, int rowNum) throws IOException;
	
	public R getRow(S sheet, int rowNum) throws IOException;
	
	public List<C> getColumn(S sheet, int colIndex) throws IOException;
	
	public List<C> getCells(R row) throws IOException;
	
	public C getCell(S sheet, int rowNum, int colIndex) throws IOException;
	
	public C getCell(R row, int colIndex) throws IOException;
	
	public String getCellValue(R row, int cellnum) ;

	public RowMapMapper<R> getRowMapper(R row, List<Map<String, String>> columns, int rowNum);

	public RowMapMapper<R> getRowMapper(R row, List<Map<String, String>> columns, int rowNum, BiFunction<ColumnWrapper<C>, String, String> transformer);
	
	public RowMapMapper<R> getRowMapper(R row, List<Map<String, String>> columns, int rowNum, boolean title);
	
	public RowMapMapper<R> getRowMapper(R row, List<Map<String, String>> columns, int rowNum, boolean title, BiFunction<ColumnWrapper<C>, String, String> transformer);

	
}
