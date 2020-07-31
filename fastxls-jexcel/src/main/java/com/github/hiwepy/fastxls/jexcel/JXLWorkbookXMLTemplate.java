/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.jexcel;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import jxl.Workbook;
import jxl.write.WritableWorkbook;
import com.github.hiwepy.fastxls.core.WorkbookTemplate;

/**
 * 模板处理接口
 * @author <a href="https://github.com/vindell">vindell</a>
 */
public class JXLWorkbookXMLTemplate extends WorkbookTemplate<Workbook, String> {
	
	/**
	 * 变量占位符开始位，默认：${
	 */
	protected String placeholderStart = "${";
	/**
	 * 变量占位符结束位，默认：}
	 */
	protected String placeholderEnd = "}";
	
	/**
	 * @param template ：模板内容
	 * @param variables ：变量
	 * @return {@link WritableWorkbook} 对象
	 * @throws Exception ：异常对象
	 */
	public Workbook process(String template, Map<String, Object> variables) throws Exception{
		
		// 进行变量替换
		for (Map.Entry<String, Object> entry : variables.entrySet()) {
			//替换变量
			template = StringUtils.replace(template, this.placeholderStart + entry.getKey() + this.placeholderEnd, String.valueOf(entry.getValue()));
		}
		
		try (InputStream input = new ByteArrayInputStream(template.getBytes());) {
			return Workbook.getWorkbook(input);
		}
	}
	
}
