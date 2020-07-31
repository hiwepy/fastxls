/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.template.thymeleaf;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Map;

import org.apache.poi.ooxml.util.DocumentHelper;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.thymeleaf.context.Context;
import org.w3c.dom.Document;

/**
 * 该模板仅负责使用Thymeleaf模板引擎将指定模板生成XML后，作为模板生成Workbook对象
 * @author <a href="https://github.com/vindell">vindell</a>
 */
public class POIWorkbookThymeleafTemplate extends WorkbookThymeleafTemplate<Workbook> {
	
	/**
	 * 使用Thymeleaf模板引擎渲染模板
	 * @param template ：模板内容
	 * @param variables ：变量
	 * @return {@link Workbook} 对象
	 * @throws Exception ：异常对象
	 */
	public Workbook process(String template, Map<String, Object> variables) throws Exception {
		
		// 创建模板输出内容接收对象
		StringWriter output = new StringWriter();
		//设置上下文参数
		Context ctx = new Context();
        ctx.setVariables(variables);
		// 使用Thymeleaf模板引擎渲染模板
		getEngine().process(template , ctx , output);
		//获取模板渲染后的结果
		String xml = output.toString();
				
		InputStream input = new ByteArrayInputStream(xml.getBytes());
         
		Document document = DocumentHelper.readDocument(input);
		
		return WorkbookFactory.create(document);
		
	}
	
}
