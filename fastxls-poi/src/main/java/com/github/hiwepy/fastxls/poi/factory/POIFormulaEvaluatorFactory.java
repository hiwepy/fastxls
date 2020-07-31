/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.poi.factory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFFormulaEvaluator;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class POIFormulaEvaluatorFactory {

	protected static ConcurrentMap<String, FormulaEvaluator> COMPLIED_FORMULA_EVALUATOR = new ConcurrentHashMap<String, FormulaEvaluator>();

	public static FormulaEvaluator getFormulaEvaluator(Cell cell) {
		return getFormulaEvaluator(cell.getSheet());
	}
	
	public static FormulaEvaluator getFormulaEvaluator(Sheet sheet) {
		return getFormulaEvaluator(sheet.getWorkbook());
	}
	
	public static FormulaEvaluator getFormulaEvaluator(Workbook workbook) {
		
		FormulaEvaluator ret = COMPLIED_FORMULA_EVALUATOR.get(workbook.getClass().getName());
		if (ret != null) {
			return ret;
		}
		if (workbook instanceof XSSFWorkbook) {
			ret = new XSSFFormulaEvaluator((XSSFWorkbook) workbook);
		} else if (workbook instanceof SXSSFWorkbook) {
			ret = new SXSSFFormulaEvaluator((SXSSFWorkbook) workbook);
		} else if (workbook instanceof HSSFWorkbook) {
			ret = new HSSFFormulaEvaluator((HSSFWorkbook) workbook);
		}
		FormulaEvaluator existing = COMPLIED_FORMULA_EVALUATOR.putIfAbsent(workbook.getClass().getName(), ret);
		if (existing != null) {
			ret = existing;
		}
		return ret;
	}
	
}
