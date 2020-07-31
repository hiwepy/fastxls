/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.poi.factory;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Workbook字体工厂类
 */
public class POIFontFactory {

	protected static Logger LOG = LoggerFactory.getLogger(POIFontFactory.class);
	private volatile static POIFontFactory singleton;

	public static POIFontFactory getInstance() {
		if (singleton == null) {
			synchronized (POIFontFactory.class) {
				if (singleton == null) {
					singleton = new POIFontFactory();
				}
			}
		}
		return singleton;
	}

	private POIFontFactory() {

	}

	public Font getFont(Workbook wb, String fontName, short fontHeightInPoints) {
		return getFont(wb, fontName, Font.COLOR_NORMAL, fontHeightInPoints);
	}

	public Font getFont(Workbook wb, String fontName, short color, short fontHeightInPoints) {
		return getFont(wb, fontName, true, color, fontHeightInPoints);
	}

	public Font getFont(Workbook wb, String fontName, boolean bold, short color, short fontHeightInPoints) {
		return getFont(wb, fontName, bold, color, fontHeightInPoints, false, false, Font.SS_NONE, Font.U_NONE,
				Font.DEFAULT_CHARSET);
	}

	public Font getFont(Workbook wb, String fontName, boolean bold, short color, short fontHeightInPoints,
			boolean italic, boolean strikeout, short typeOffset, byte underline, int charSet) {
		if (wb != null && StringUtils.isNotEmpty(fontName)) {
			Font font = wb.findFont(bold, color, fontHeightInPoints, fontName, italic, strikeout, typeOffset,
					underline);
			if (font == null) {
				// 生成一个字体
				font = wb.createFont();
				// 常规粗细
				font.setBold(bold);
				// 字体颜色
				font.setColor(color);
				// 字体大小
				font.setFontHeightInPoints(fontHeightInPoints);
				// font.setFontHeight((short)(12 * 20));
				// 字体
				font.setFontName(fontName);
				// 是否斜体
				font.setItalic(italic);
				// 是否有删除线
				font.setStrikeout(strikeout);
				// 设置上标，下标
				font.setTypeOffset(typeOffset);
				// 下划线
				font.setUnderline(underline);
				// 字符集
				font.setCharSet(charSet);

				LOG.debug("Create Font [" + StringUtils.join(new Object[] { fontName, bold, color, fontHeightInPoints,
						italic, strikeout, typeOffset, underline, charSet }, "-") + " ]");
			}
			return font;
		}
		return null;
	}

}
