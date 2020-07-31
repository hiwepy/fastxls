package com.github.hiwepy.fastxls.poi;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import com.github.hiwepy.fastxls.core.model.PairModel;

/**
 * xls文档内容访问
 */
public class POIWorkbookMapper {

	private POIWorkbookReader workbookReader = new POIWorkbookReader();
	
	/**
	 * 获取指定内容在当前行的单元格索引
	 * 
	 * @param row     ：行对象
	 * @param content ：内容
	 * @return
	 */
	public int indexOfColumn(Row row, String content) {
		int index = 0;
		boolean finded = false;
		String cellVal = null;
		for (int cellnum = 0; cellnum < row.getLastCellNum(); cellnum++) {
			index = cellnum;
			Cell cell = row.getCell(cellnum);
			if (cell != null) {
				cellVal = getWorkbookReader().getCellValue(cell, cell.getCellType());
				if (StringUtils.isNotBlank(cellVal) && cellVal.startsWith(content)) {
					finded = true;
					break;
				}
			}
		}
		return finded ? index : -1;
	}

	/**
	 * 获取sheet中指定列中指定内容所在行索引
	 * 
	 * @param sheet    ：sheet对象
	 * @param colIndex ：列索引
	 * @param content  ：内容
	 * @return
	 */
	public int indexOfRow(Sheet sheet, int colIndex, String content) {
		int index = 0;
		boolean finded = false;
		String cellVal = null;
		for (int rownum = 0; rownum < sheet.getLastRowNum(); rownum++) {
			index = rownum;
			Cell cell = sheet.getRow(rownum).getCell(colIndex);
			if (cell != null) {
				cellVal = getWorkbookReader().getCellValue(cell, cell.getCellType());
				if (StringUtils.isNotBlank(cellVal) && cellVal.indexOf(content) > -1) {
					finded = true;
					break;
				}
			}
		}
		return finded ? index : -1;
	}

	public List<String> getRow(Sheet sheet, int rowIndex) {
		if (sheet != null) {
			return this.getRow(sheet.getRow(rowIndex));
		}
		return null;
	}

	public List<String> getRow(Row row) {
		List<String> list = null;
		if (row != null) {
			list = new ArrayList<String>();
			for (int colIndex = 0; colIndex <= row.getLastCellNum(); colIndex++) {
				Cell cell = row.getCell(colIndex);
				list.add(getWorkbookReader().getCellValue(cell, cell.getCellType()));
			}
		}
		return list;
	}

	/**
	 * 获取sheet表中指定列数据起始行到数据结束行的单元格内容集合
	 * 
	 * @param sheet
	 * @param colIndex
	 * @return
	 */
	public List<String> getColumn(Sheet sheet, int colIndex) {
		return getColumn(sheet, colIndex, sheet.getFirstRowNum());
	}

	/**
	 * 获取sheet表中指定列指定起始行到数据结束行的单元格内容集合
	 * 
	 * @param sheet
	 * @param colIndex
	 * @param startRow
	 * @return
	 */
	public List<String> getColumn(Sheet sheet, int colIndex, int startRow) {
		List<String> columns = null;
		if (sheet != null) {
			columns = new ArrayList<String>();
			for (int i = startRow; i <= sheet.getLastRowNum(); i++) {
				Row row = sheet.getRow(i);
				if (row != null) {
					Cell cell = row.getCell(colIndex);
					if (cell != null) {
						columns.add(getWorkbookReader().getCellValue(cell, cell.getCellType()));
					} else {
						columns.add("");
					}
				} else {
					columns.add("");
				}
			}
		}
		return columns;
	}

	public List<PairModel> getColumns(File file) throws Exception {
		List<PairModel> result = new ArrayList<PairModel>();
		// 获得上传xls文件的表头
		Sheet sheet = getWorkbookReader().getSheet(file, 0);
		// 获得第一行的单元格
		Iterator<Cell> cells = sheet.getRow(0).cellIterator();
		while (cells.hasNext()) {
			Cell cell = cells.next();
			// 单元格内容
			String value = getWorkbookReader().getCellValue(cell, cell.getCellType());
			result.add(new PairModel(cell.getColumnIndex() + "", value));
		}
		return result;
	}
	
	public POIWorkbookReader getWorkbookReader() {
		return workbookReader;
	}

}
