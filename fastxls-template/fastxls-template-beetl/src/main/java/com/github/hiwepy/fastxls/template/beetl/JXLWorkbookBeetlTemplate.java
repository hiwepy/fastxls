/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.template.beetl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

import org.beetl.core.Template;

import jxl.Workbook;

/**
 * 该模板仅负责使用Beetl模板引擎将指定模板生成XML后，作为模板生成Workbook对象
 * @author <a href="https://github.com/vindell">vindell</a>
 */
public class JXLWorkbookBeetlTemplate extends WorkbookBeetlTemplate<Workbook> {
	
	/**
	 * 使用Beetl模板引擎渲染模板
	 * @param template ：模板内容
	 * @param variables ：变量
	 * @return {@link WordprocessingMLPackage} 对象
	 * @throws Exception ：异常对象
	 */
	public Workbook process(String template, Map<String, Object> variables) throws Exception{
		
		// 使用Beetl模板引擎渲染模板
		Template beeTemplate = getEngine().getTemplate(template);
		beeTemplate.binding(variables);
		// 获取模板渲染后的结果
		String xml = beeTemplate.render();
		
		try (InputStream input = new ByteArrayInputStream(xml.getBytes());) {
			return Workbook.getWorkbook(input);
		}
		
	}
	
}
