/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.template.httl;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import httl.Engine;
import com.github.hiwepy.fastxls.core.Constants;
import com.github.hiwepy.fastxls.core.FastxlsProperties;
import com.github.hiwepy.fastxls.core.WorkbookTemplate;
import com.github.hiwepy.fastxls.core.utils.ConfigUtils;

public abstract class WorkbookHttlTemplate<W> extends WorkbookTemplate<W,String> {
	
	protected final Logger LOG = LoggerFactory.getLogger(WorkbookHttlTemplate.class);
	protected Engine engine;
	
	public Engine getEngine() throws IOException {
		return engine == null ? getInternalEngine() : engine;
	}

	public void setEngine(Engine engine) {
		this.engine = engine;
	}
	
	protected synchronized Engine getInternalEngine() throws IOException{
		
		Properties props = ConfigUtils.filterWithPrefix("fastxls.httl.", "fastxls.httl.", FastxlsProperties.getProperties(), false);
        //props.setProperty("filter", "null");
        //props.setProperty("logger", "null");
		props.setProperty("template.directory", props.getProperty("template.directory"));
		props.setProperty("template.suffix", props.getProperty("template.suffix",".httl"));
		props.setProperty("input.encoding", props.getProperty("input.encoding", Constants.DEFAULT_CHARSETNAME));
		props.setProperty("output.encoding", props.getProperty("output.encoding", Constants.DEFAULT_CHARSETNAME));
		Engine engine = Engine.getEngine(props);
		// 设置模板引擎，减少重复初始化消耗
        this.setEngine(engine);
        return engine;
	}
	
}
