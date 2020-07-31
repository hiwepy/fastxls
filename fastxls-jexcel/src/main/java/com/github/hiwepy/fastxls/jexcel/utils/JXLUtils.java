/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.jexcel.utils;

import jxl.Cell;
import jxl.CellView;
import jxl.JXLException;
import jxl.format.CellFormat;
import jxl.write.Blank;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCell;
import jxl.write.WritableSheet;
import jxl.write.biff.JxlWriteException;

public class JXLUtils {

	public static void resizeRowHeight(WritableSheet sheet, int startRow, int rowheight) throws JxlWriteException {
		for (int i = startRow; i < sheet.getRows(); i++) {
			sheet.setRowView(i, cellView(rowheight));
		}
	}

	public static void resizeColumnWidth(WritableSheet sheet, int rowIndex, int[] cellWidth) {
		Cell[] cells = sheet.getRow(rowIndex);
		for (int i = 0; i < cells.length; i++) {
			int width = cellWidth.length > i ? cellWidth[i] : 300;
			sheet.setColumnView(i, cellView(width));
		}
	}

	public static void mergeCell(WritableSheet sheet, int columnIndex, int rowIndex, int columnNum, int rowNum)
			throws JXLException {
		sheet.mergeCells(columnIndex, rowIndex, columnIndex + columnNum, rowIndex + rowNum);
	}

	public static WritableCell cell(int columnIndex, int rowIndex, Object content, CellFormat format) {
		if (content != null) {
			String tmpStr = String.valueOf(content);
			if (tmpStr.matches("/^\\d+$/ig")) {
				return number(columnIndex, rowIndex, Double.parseDouble(tmpStr), format);
			} else {
				return label(columnIndex, rowIndex, content, format);
			}
		} else {
			return blank(columnIndex, rowIndex, format);
		}
	}

	public static CellView cellView(int width) {
		CellView view = new CellView();
		view.setAutosize(true);
		view.setSize(width);
		return view;
	}

	public static Label label(int columnIndex, int rowIndex, Object content, CellFormat format) {
		return new Label(columnIndex, rowIndex, String.valueOf(content), format);
	}

	public static Blank blank(int columnIndex, int rowIndex, CellFormat format) {
		return new Blank(columnIndex, rowIndex, format);
	}

	public static Number number(int columnIndex, int rowIndex, double num, CellFormat format) {
		return new Number(columnIndex, rowIndex, num, format);
	}

}
