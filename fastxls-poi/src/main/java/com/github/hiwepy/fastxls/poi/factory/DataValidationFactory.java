/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.poi.factory;

import java.util.stream.Stream;

import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationConstraint.OperatorType;
import org.apache.poi.ss.usermodel.DataValidationConstraint.ValidationType;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;
import org.apache.poi.xssf.usermodel.XSSFDataValidationConstraint;

import com.github.hiwepy.fastxls.core.model.ValidateModel;
import com.github.hiwepy.fastxls.core.utils.StringUtils;

/**
 * 构建Excel单元格数据有效性对象
 * @author <a href="https://github.com/vindell">wandl</a>
 * @since 2019-11-02
 */
public class DataValidationFactory {

	public DataValidation getValidation(Cell cell, ValidateModel model) {
		return getValidation(cell.getSheet(), model);
	}
	
	public DataValidation getValidation(Sheet sheet, ValidateModel model) {

		// 设置数据有效性加载在哪个单元格上,四个参数分别是：起始行、终止行、起始列、终止列
		CellRangeAddressList cellRangeAddressList = new CellRangeAddressList(model.getFirstRow(), model.getLastRow(),
				model.getFirstCol(), model.getLastCol());

		DataValidationHelper dvHelper = sheet.getDataValidationHelper();
		DataValidationConstraint dvConstraint = null;
		// 计算总字符数
		int length = Stream.of(model.getListOfValues()).mapToInt(str -> {
			if (StringUtils.hasText(str)) {
				// 计算字符串的显示长度，半角算１个长度，全角算两个长度
				int len = 0;
				for (int i = 0; i < str.length(); ++i) {
					char c = str.charAt(i);
					if (c >= '\u0000' && c <= '\u00FF') {
						len = len + 1;
					} else {
						len = len + 2;
					}
				}
				return len;

			}
			return 0;
		}).sum();
		// 单元格字节数不能超过255个字符
		if(ValidationType.LIST == model.getValidationType() && length > 255) {
			
			// https://blog.csdn.net/sunyu3981/article/details/70807132
			// 因为超出长度，这里需要调整有效性的实现逻辑 ： https://blog.csdn.net/john1337/article/details/81074619
			Sheet hidden = sheet.getWorkbook().getSheet("hidden");
			if(null == hidden ) {
				hidden = sheet.getWorkbook().createSheet("hidden");
			    // 数据源sheet页不显示    
				sheet.getWorkbook().setSheetHidden(sheet.getWorkbook().getAllNames().size() + 1, true);
			}
			// 创建校验数据源
		    Row vRow = null;
		    Cell vCell = null;
		    int rowNum = model.getFirstRow();
		    for (int i = 0; i < model.getListOfValues().length; i++) {
				vRow = hidden.getRow(rowNum) == null ? hidden.createRow(rowNum) : hidden.getRow(rowNum);
				vCell = vRow.getCell(model.getFirstCol()) == null ? vRow.createCell(model.getFirstCol()) : vRow.getCell(model.getFirstCol());
		    	vCell.setCellValue(model.getListOfValues()[i]);
		    	rowNum += 1;
			}
		    
		    String colName = CellReference.convertNumToColString(model.getFirstCol());
		    String refersNname = String.format("hidden%s%s%s%s", colName, model.getFirstRow(), colName,
		    		model.getFirstRow() + model.getListOfValues().length);
		    
		    Name namedCell = sheet.getWorkbook().createName();
		    namedCell.setNameName(refersNname);
		    namedCell.setRefersToFormula(String.format("hidden!$%s$%s:$%s$%s", colName, model.getFirstRow() , colName,
		    		model.getFirstRow() + model.getListOfValues().length));
		    
		    dvConstraint = dvHelper.createFormulaListConstraint(refersNname);
		     
		} else {
			dvConstraint = getConstraint(dvHelper, sheet, model);
		}
		
		// 数据有效性对象
		DataValidation dataValidation = dvHelper.createValidation(dvConstraint, cellRangeAddressList);
		
		// 处理Excel兼容性问题
        if(dataValidation instanceof XSSFDataValidation){
            //数据校验
            dataValidation.setSuppressDropDownArrow(true);
            dataValidation.setShowErrorBox(true);
        }else{
            dataValidation.setSuppressDropDownArrow(false);
        }
		
		// 是否允许空白输入
		dataValidation.setEmptyCellAllowed(model.isEmptyCellAllowed());
		// 设置 ：选定单元格时显示的提示信息
		dataValidation.setShowErrorBox(model.isShowErrorBox());
		if(model.isShowErrorBox()) {
			dataValidation.createErrorBox(model.getErrorTitle(), model.getErrorContent());
		}
		// 设置 ：输入无效数据时显示的警告信息
		dataValidation.setShowPromptBox(model.isShowPromptBox());
		if(model.isShowPromptBox()) {
			dataValidation.createPromptBox(model.getPromptTitle(), model.getPromptContent());
		}
		
		// 返回对象
		return dataValidation;
	}


	public DataValidationConstraint getConstraint(DataValidationHelper dvHelper, Sheet sheet, ValidateModel model) {
		DataValidationConstraint ret = null;
		// 根据不同验证类型创建数据有效性约束对象
		switch (model.getValidationType()) {
			case ValidationType.ANY: {
				if (sheet instanceof HSSFSheet) {
					ret = DVConstraint.createNumericConstraint(ValidationType.ANY, OperatorType.IGNORED, null, null);
				} else {
					ret = new XSSFDataValidationConstraint(ValidationType.ANY, "");
				}
			}; break;
			case ValidationType.INTEGER: {
				ret = dvHelper.createIntegerConstraint(model.getOperatorType(), model.getFormula1(), model.getFormula2());
			}; break;
			case ValidationType.DECIMAL: {
				ret = dvHelper.createDecimalConstraint(model.getOperatorType(), model.getFormula1(), model.getFormula2());
			}; break;
			case ValidationType.LIST: {
				ret = dvHelper.createExplicitListConstraint(model.getListOfValues());
			}; break;
			case ValidationType.DATE: {
				ret = dvHelper.createDateConstraint(model.getOperatorType(), model.getFormula1(), model.getFormula2(), model.getDateFormat());
			}; break;
			case ValidationType.TIME: {
				ret = dvHelper.createTimeConstraint(model.getOperatorType(), model.getFormula1(), model.getFormula2());
			}; break;
			case ValidationType.TEXT_LENGTH: {
				ret = dvHelper.createTextLengthConstraint(model.getOperatorType(), model.getFormula1(), model.getFormula2());
			}; break;
			case ValidationType.FORMULA: {
				ret = dvHelper.createCustomConstraint(model.getFormula());
			}; break;
		}
		return ret;
	}

}
