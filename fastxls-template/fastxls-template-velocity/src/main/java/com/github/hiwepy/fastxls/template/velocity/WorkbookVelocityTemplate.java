/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.template.velocity;

import java.io.IOException;
import java.util.Properties;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.tools.generic.DateTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.hiwepy.fastxls.core.Constants;
import com.github.hiwepy.fastxls.core.FastxlsProperties;
import com.github.hiwepy.fastxls.core.WorkbookTemplate;

public abstract class WorkbookVelocityTemplate<W> extends WorkbookTemplate<W,String> {
	
	protected final Logger LOG = LoggerFactory.getLogger(WorkbookVelocityTemplate.class);
	protected VelocityEngine engine;
	protected DateTool dateTool = new DateTool();
	
	public VelocityEngine getEngine() throws IOException {
		return engine == null ? getInternalEngine() : engine;
	}

	public void setEngine(VelocityEngine engine) {
		this.engine = engine;
	}

	protected synchronized VelocityEngine getInternalEngine() throws IOException{
		
		VelocityEngine engine = new VelocityEngine();
        
		Properties ps = new Properties();
        ps.setProperty(";runtime.log", FastxlsProperties.getProperty("fastxls.velocity.runtime.log", "velocity.log"));
        ps.setProperty(";runtime.log.logsystem.class", FastxlsProperties.getProperty("fastxls.velocity.runtime.log.logsystem.class", "org.apache.velocity.runtime.log.NullLogSystem"));
        ps.setProperty("resource.loader", FastxlsProperties.getProperty("fastxls.velocity.resource.loader", "file"));
        ps.setProperty("file.resource.loader.cache", FastxlsProperties.getProperty("fastxls.velocity.file.resource.loader.cache", "true"));
        ps.setProperty("file.resource.loader.class ", FastxlsProperties.getProperty("fastxls.velocity.file.resource.loader.class", "Velocity.Runtime.Resource.Loader.FileResourceLoader") );
        ps.setProperty(";resource.loader", FastxlsProperties.getProperty("fastxls.velocity.resource.loader", "webapp"));
        ps.setProperty(";webapp.resource.loader.class", FastxlsProperties.getProperty("fastxls.velocity.webapp.resource.loader.class", "org.apache.velocity.tools.view.servlet.WebappLoader"));
        ps.setProperty(";webapp.resource.loader.cache", FastxlsProperties.getProperty("fastxls.velocity.webapp.resource.loader.cache", "true"));
        ps.setProperty(";webapp.resource.loader.modificationCheckInterval", FastxlsProperties.getProperty("fastxls.velocity.webapp.resource.loader.modificationCheckInterval", "3") );
        ps.setProperty(";directive.foreach.counter.name", FastxlsProperties.getProperty("fastxls.velocity.directive.foreach.counter.name", "velocityCount"));
        ps.setProperty(";directive.foreach.counter.initial.value", FastxlsProperties.getProperty("fastxls.velocity.directive.foreach.counter.initial.value", "1"));
        ps.setProperty("file.resource.loader.path", this.getClass().getResource(FastxlsProperties.getProperty("fastxls.velocity.file.resource.loader.path", "/template")).getPath());
        //模板输入输出编码格式
        String input_charset = FastxlsProperties.getProperty("fastxls.velocity.input.encoding", Constants.DEFAULT_CHARSETNAME);
        String output_charset = FastxlsProperties.getProperty("fastxls.velocity.output.encoding", Constants.DEFAULT_CHARSETNAME );
        ps.setProperty("input.encoding", input_charset);
        ps.setProperty("output.encoding", output_charset);
        engine.init(ps);
        // 设置模板引擎，减少重复初始化消耗
        this.setEngine(engine);
        return engine;
	}
	
}
