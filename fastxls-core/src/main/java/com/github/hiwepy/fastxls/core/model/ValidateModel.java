package com.github.hiwepy.fastxls.core.model;

import com.github.hiwepy.fastxls.core.OperatorType;
import com.github.hiwepy.fastxls.core.ValidationType;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 */
@Getter
@Setter
@ToString
public class ValidateModel {

	//有效类型
	private int validationType = ValidationType.ANY;
	//校验类型
	private int operatorType = OperatorType.IGNORED;
	
	private int firstRow = 0;
	private int lastRow = 1;
	private int firstCol = 0;
	private int lastCol = 1;

	private String formula;
	
	//数据限制范围：起始值，结束值 ；不是范围时取起始值
	private String formula1;
	private String formula2;
	
	//日期格式时的日期格式
	private String dateFormat;
	
	//预制的数据,用于显示下拉框
	private String[] listOfValues;
	
	//选定单元格时显示的提示信息
	private String promptTitle = "提示";
	private String promptContent = "";
	//输入无效数据时显示的警告信息
	private String errorTitle = "警告";
	private String errorContent = "";
	
	
	//  Sets if this object allows empty as a valid value
	private boolean emptyCellAllowed = true;
	private boolean showPromptBox = true;
	private boolean showErrorBox = true;
	/**
	 * 
	 */
	public ValidateModel() {
		// TODO Auto-generated constructor stub
	}
	
	public ValidateModel(int validationType, int operatorType, int firstRow, int lastRow, int firstCol, int lastCol, String formula1,String formula2) {
		this(validationType, operatorType, firstRow, lastRow, firstCol, lastCol, formula1, formula2 , null);
	}
	
	public ValidateModel(int validationType, int operatorType, int firstRow, int lastRow, int firstCol, int lastCol, String formula1,String formula2,String dateFormat) {
		this.validationType = validationType;
		this.operatorType = operatorType;
		this.firstRow = firstRow;
		this.lastRow = lastRow;
		this.firstCol = firstCol;
		this.lastCol = lastCol;
		this.formula1 = formula1;
		this.formula2 = formula2;
		this.dateFormat = dateFormat;
	}

	public ValidateModel(int validationType, int firstRow, int lastRow, int firstCol, int lastCol, String formula) {
		this.validationType = validationType;
		this.firstRow = firstRow;
		this.lastRow = lastRow;
		this.firstCol = firstCol;
		this.lastCol = lastCol;
		this.formula = formula;
	}

	public ValidateModel(int validationType, int firstRow, int lastRow, int firstCol, int lastCol, String[] listOfValues) {
		this.validationType = validationType;
		this.firstRow = firstRow;
		this.lastRow = lastRow;
		this.firstCol = firstCol;
		this.lastCol = lastCol;
		this.listOfValues = listOfValues;
	}
	
}
