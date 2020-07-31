/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.template.jxls;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * 该模板仅负责使用Jxls模板引擎将指定模板生成XML后，作为模板生成Workbook对象
 * 
 * @author <a href="https://github.com/vindell">vindell</a>
 */
public class POIWorkbookJxlsTemplate extends WorkbookJxlsTemplate<Workbook> {

	/**
	 * 使用Jxls模板引擎渲染模板
	 * 
	 * @param template  ：模板内容
	 * @param variables ：变量
	 * @return {@link Workbook} 对象
	 * @throws Exception ：异常对象
	 */
	public Workbook process(InputStream input, Map<String, Object> variables) throws Exception {
		try (ByteArrayOutputStream output = new ByteArrayOutputStream();) {
			try {
				return getEngine().transformXLS(input, variables);
			} catch (Exception e) {
				return new HSSFWorkbook();
			}
		}
	}

}
