/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.template.jxls;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.jxls.common.Context;
import org.jxls.expression.JexlExpressionEvaluator;
import org.jxls.transform.Transformer;

import jxl.Workbook;

/**
 * 该模板仅负责使用Jxls模板引擎将指定模板生成XML后，作为模板生成Workbook对象
 * @author <a href="https://github.com/vindell">vindell</a>
 */
public class JXLWorkbookJxlsTemplate extends WorkbookJxlsTemplate<ByteArrayOutputStream> {
	
	/**
	 * 使用Jxls模板引擎渲染模板
	 * @param template ：模板内容
	 * @param variables ：变量
	 * @return {@link Workbook} 对象
	 * @throws Exception ：异常对象
	 */
	public ByteArrayOutputStream process(InputStream input, Map<String, Object> variables) throws Exception{
		
		Context context = new Context();
		if (variables != null) {
			for (String key : variables.keySet()) {
				context.putVar(key, variables.get(key));
			}
		}

		ByteArrayOutputStream output = new ByteArrayOutputStream();

		Transformer transformer = getEngine().createTransformer(input, output);
		
		JexlExpressionEvaluator evaluator = (JexlExpressionEvaluator) transformer.getTransformationConfig()
				.getExpressionEvaluator();

		Map<String, Object> funcs = new HashMap<String, Object>();
		// funcs.put("utils", new JxlsUtils()); //添加自定义功能
		// evaluator.getJexlEngine().setFunctions(funcs);
		getEngine().processTemplate(context, transformer);
		
		/*try (
			// 获取模板渲染后的结果
			InputStream input2 = new ByteArrayInputStream(output.toByteArray());) {
			return Workbook.getWorkbook(input2);
		}*/
		return output;
		
	}
	
}
