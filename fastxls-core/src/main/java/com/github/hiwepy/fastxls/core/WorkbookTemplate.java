/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.core;

import java.io.InputStream;
import java.util.Map;

/**
 * 模板处理接口
 * @author <a href="https://github.com/vindell">vindell</a>
 */
public abstract class WorkbookTemplate<W,T> {
	
	/**
	 * @param template ：模板内容
	 * @param variables ：变量
	 * @throws Exception ：异常对象
	 */
	public abstract W process(T template, Map<String, Object> variables) throws Exception;


	/**
	 * 使用Jxls模板引擎渲染模板并加入自定义函数
	 * @param input
	 * @param variables
	 * @param customFunction
	 * @return
	 * @throws Exception
	 */
	public W processCustomFunction(InputStream input, Map<String, Object> variables, Map<String, Object> customFunction) throws Exception{
		return null;
	}

	/**
	 * 使用Jxls模板引擎渲染动态模板
	 * @param input
	 * @param variables
	 * @return
	 * @throws Exception
	 */
	public W processGrid(InputStream input, Map<String, Object> variables,String objectProps) throws Exception{
		return null;
	}
}
