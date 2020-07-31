/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.template.jxls;

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.poi.ss.usermodel.Workbook;
import org.jxls.common.Context;
import org.jxls.expression.JexlExpressionEvaluator;
import org.jxls.transform.Transformer;
import org.springframework.util.CollectionUtils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;

/**
 * 该模板仅负责使用Jxls模板引擎将指定模板生成XML后，作为模板生成Workbook对象
 * 
 * @author <a href="https://github.com/vindell">vindell</a>
 */
@SuppressWarnings("all")
public class POIWorkbookJxlsTemplate extends WorkbookJxlsTemplate<ByteArrayOutputStream> {

	/**
	 * 使用Jxls模板引擎渲染模板
	 * 
	 * @param template  ：模板内容
	 * @param variables ：变量
	 * @return {@link Workbook} 对象
	 * @throws Exception ：异常对象
	 */
	@Override
	public ByteArrayOutputStream process(InputStream input, Map<String, Object> variables) throws Exception {
		return common(input,variables,null,false,null);
	}

	/**
	 * 使用Jxls模板引擎渲染模板并加入自定义函数
	 * @param input
	 * @param variables
	 * @param customFunction
	 * @return
	 * @throws Exception
	 */
	@Override
	public ByteArrayOutputStream processCustomFunction(InputStream input, Map<String, Object> variables, Map<String, Object> customFunction) throws Exception{
		return common(input,variables,customFunction,false,null);
	}

	/**
	 * 使用Jxls模板引擎渲染动态模板
	 * @param input
	 * @param variables
	 * @return
	 * @throws Exception
	 */
	@Override
	public ByteArrayOutputStream processGrid(InputStream input, Map<String, Object> variables, String objectProps) throws Exception {
		return common(input,variables,null,true,objectProps);
	}

	private ByteArrayOutputStream common(InputStream input, Map<String, Object> variables, Map<String, Object> customFunction,boolean ifGrid,String objectProps) throws Exception{
		Context context = new Context();
		if (variables != null) {
			for (String key : variables.keySet()) {
				context.putVar(key, variables.get(key));
			}
		}
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		Transformer transformer = getEngine().createTransformer(input, output);
		JexlExpressionEvaluator evaluator = (JexlExpressionEvaluator) transformer.getTransformationConfig().getExpressionEvaluator();
		//添加自定义功能
		if(!CollectionUtils.isEmpty(customFunction)){
			evaluator.setJexlEngine(new JexlBuilder().silent(true).strict(false).namespaces(customFunction).create());
		}
		if(ifGrid){
			getEngine().processGridTemplate(input,output,context,objectProps);
		}else{
			getEngine().processTemplate(context, transformer);
		}
		return output;
	}
}
