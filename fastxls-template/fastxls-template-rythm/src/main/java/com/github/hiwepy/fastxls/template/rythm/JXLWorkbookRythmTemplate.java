/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.template.rythm;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Map;

import jxl.Workbook;

/**
 * 该模板仅负责使用Rythm模板引擎将指定模板生成XML后，作为模板生成Workbook对象
 * @author <a href="https://github.com/vindell">vindell</a>
 */
public class JXLWorkbookRythmTemplate extends WorkbookRythmTemplate<Workbook> {
	
	/**
	 * 使用Rythm模板引擎渲染模板
	 * @param template ：模板内容
	 * @param variables ：变量
	 * @return {@link Workbook} 对象
	 * @throws Exception ：异常对象
	 */
	public Workbook process(String template, Map<String, Object> variables) throws Exception{
		
		// 创建模板输出内容接收对象
		StringWriter output = new StringWriter();
		// 使用Rythm模板引擎渲染模板
		getEngine().getTemplate(template , variables).render(output);
		//获取模板渲染后的结果
		String xml = output.toString();
		
		try (InputStream input = new ByteArrayInputStream(xml.getBytes());) {
			return Workbook.getWorkbook(input);
		}
		
	}
	
}
