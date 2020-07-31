/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.poi.utils;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.hiwepy.fastxls.poi.cahce.POICellStyleCacheManager;

public abstract class POICellStyleUtils {

	protected static Logger LOG = LoggerFactory.getLogger(POICellStyleUtils.class);
	protected static POICellStyleCacheManager cacheManager = POICellStyleCacheManager.getInstance();

	// 普通字段标题单元格格式（黑色，黑体，不加粗，12磅字）
	public static CellStyle getNormalStyle(Workbook wb) {
		// 获取提示信息字体
		Font font = POIFontUtils.getNormalFont(wb);
		// 优先取缓存的样式取不到则创建新的对象，以此减少内存消耗
		CellStyle style = cacheManager.getCellStyle(wb, font);
		// 设置其他样式
		setCenter(style);
		setBorder(style);
		setFillColor(wb, style);
		return style;
	}

	// 特殊字段[如：必填，唯一]标题单元格格式（红色，黑体，不加粗，12磅字）
	public static CellStyle getRequiredStyle(Workbook wb) {
		// 获取提示信息字体
		Font font = POIFontUtils.getRequiredFont(wb);
		// 优先取缓存的样式取不到则创建新的对象，以此减少内存消耗
		CellStyle style = cacheManager.getCellStyle(wb, font);
		// 设置其他样式
		setCenter(style);
		setBorder(style);
		setFillColor(wb, style);
		return style;
	}

	// 普通内容字段标题单元格格式（黑色，宋体，不加粗，12磅字）
	public static CellStyle getTextStyle(Workbook wb) {
		// 获取提示信息字体
		Font font = POIFontUtils.getTextFont(wb);
		// 优先取缓存的样式取不到则创建新的对象，以此减少内存消耗
		CellStyle style = cacheManager.getCellStyle(wb, font);
		// 设置其他样式
		setCenter(style);
		setBorder(style);
		return style;
	}

	// 单元格备注内容提示框格式（红色，Arial，12磅字）
	public static CellStyle getCommentStyle(Workbook wb) {
		// 获取提示信息字体
		Font font = POIFontUtils.getCommentFont(wb);
		// 优先取缓存的样式取不到则创建新的对象，以此减少内存消耗
		CellStyle style = cacheManager.getCellStyle(wb, font);
		// 设置其他样式
		setCenter(style);
		setBorder(style);
		return style;
	}

	public static CellStyle getPOIStyle(Workbook wb, String fontName, boolean bold, short color,
			short fontHeightInPoints, boolean italic, boolean strikeout, short typeOffset, byte underline,
			int charSet) {
		// 获取字体
		Font font = POIFontUtils.getPOIFont(wb, fontName, bold, color, fontHeightInPoints, italic, strikeout,
				typeOffset, underline, charSet);
		// 优先取缓存的样式取不到则创建新的对象，以此减少内存消耗
		CellStyle style = cacheManager.getCellStyle(wb, font);
		// 设置其他样式
		setCenter(style);
		setBorder(style);
		return style;
	}

	public static void setFillColor(Workbook wb, CellStyle style) {
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setFillBackgroundColor(IndexedColors.GREY_25_PERCENT.getIndex());// 设置背景色
		style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());// 设置前景色
	}

	public static void setCenter(CellStyle style) {
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
	}

	/**
	 * 设置边框
	 */
	public static void setBorder(CellStyle style) {
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
	}

	public static void destroy(Workbook wb) {
		cacheManager.destroy(wb);
	}

}
