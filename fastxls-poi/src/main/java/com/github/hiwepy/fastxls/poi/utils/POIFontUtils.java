/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.poi.utils;

import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.hiwepy.fastxls.poi.factory.POIFontFactory;

public abstract class POIFontUtils {

	protected static Logger LOG = LoggerFactory.getLogger(POIFontUtils.class);
	protected static POIFontFactory fontFactory = POIFontFactory.getInstance();
	public static String HT = "黑体";
	public static String ST = "宋体";
	public static String HWFS = "华文仿宋";

	// 普通字段标题字体（黑色，宋体，不加粗，12磅字）
	public static Font getNormalFont(Workbook wb) {
		return fontFactory.getFont(wb, HT, false, Font.COLOR_NORMAL, (short) 12, false, false, Font.SS_NONE, Font.U_NONE,
				Font.DEFAULT_CHARSET);
	}

	// 特殊字段[如：必填，唯一]标题字体（红色，黑体，不加粗，13磅字）
	public static Font getRequiredFont(Workbook wb) {
		return fontFactory.getFont(wb, HT, false, Font.COLOR_RED, (short) 12, false, false, Font.SS_NONE, Font.U_NONE,
				Font.DEFAULT_CHARSET);
	}

	// 普通内容字段标题字体（黑色，宋体，不加粗，12磅字）
	public static Font getTextFont(Workbook wb) {
		return fontFactory.getFont(wb, ST, false, Font.COLOR_NORMAL, (short) 12, false, false, Font.SS_NONE,
				Font.U_NONE, Font.DEFAULT_CHARSET);
	}

	// 单元格备注内容字体（红色，Arial，12磅字）
	public static Font getCommentFont(Workbook wb) {
		return fontFactory.getFont(wb, "Arial", false, Font.COLOR_RED, (short) 12, false, false, Font.SS_NONE,
				Font.U_NONE, Font.DEFAULT_CHARSET);
	}

	public static Font getPOIFont(Workbook wb, String fontName, boolean bold, short color, short fontHeightInPoints,
			boolean italic, boolean strikeout, short typeOffset, byte underline, int charSet) {
		return fontFactory.getFont(wb, fontName, bold, color, fontHeightInPoints, italic, strikeout, typeOffset,
				underline, charSet);
	}

}
