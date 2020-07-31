/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.poi.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.github.hiwepy.fastxls.core.Suffix;
import com.github.hiwepy.fastxls.core.utils.ExtensionUtils;

public class POIWorkbookUtils {
	
	//日期格式对象
	protected static SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd");
	 	
	public static Workbook getWorkbook(Suffix suffix) {
		// 支持97 ~2003
		if (Suffix.XLS.equals(suffix)) {
			return new HSSFWorkbook();
		} else if (Suffix.XLSX.equals(suffix)) {
			// 支持2007
			return new XSSFWorkbook();
		} else {
			return new SXSSFWorkbook();
		}
	}

	/**
	 * 根据文件的路径创建Workbook对象
	 * @throws InvalidFormatException 
	 * @throws EncryptedDocumentException 
	 * @throws IOException 
	 */
	public static Workbook getWorkbook(File file) throws IOException {
		try {
			Workbook workbook = null;
			if (file.exists()) {
				if (ExtensionUtils.isXls(file.getAbsolutePath()) || ExtensionUtils.isXlsx(file.getAbsolutePath())) {
					workbook = WorkbookFactory.create(file);
				}
			}
			return workbook;
		} catch (EncryptedDocumentException e) {
			throw new IOException(e);
		}
	}

	public static Workbook getWorkbook(InputStream inp) throws IOException {
		Workbook workbook = null;
		if (inp != null) {
			try {
				workbook = WorkbookFactory.create(inp);
			} catch (Exception e) {
				throw new IOException(e);
			}
		}
		return workbook;
	}

	public static void createCell(Row row, int columnIndex, Object value,
			CellStyle contentStyle){
		Cell cell = null;
		if (value == null) {
			cell = row.createCell(columnIndex, CellType.BLANK);
		} else {
			if (value instanceof Number) {
				String number = new BigDecimal(value.toString()).toPlainString();
				cell = row.createCell(columnIndex,CellType.NUMERIC);
				cell.setCellValue(number);
			} else if (value instanceof String) {
				cell = row.createCell(columnIndex, CellType.STRING);
				cell.setCellValue(row.getSheet().getWorkbook().getCreationHelper().createRichTextString(value.toString()));
			} else if (value instanceof Date) {
				cell = row.createCell(columnIndex, CellType.STRING);
				cell.setCellValue(row.getSheet().getWorkbook().getCreationHelper().createRichTextString(date_format.format(value)));
			} else{
				cell = row.createCell(columnIndex,CellType.STRING);
				cell.setCellValue(row.getSheet().getWorkbook().getCreationHelper().createRichTextString(value.toString()));
			}
		}
		cell.setCellStyle(contentStyle);
	} 
	
}
