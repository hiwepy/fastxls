/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.template.rythm;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;

import org.rythmengine.Rythm;
import org.rythmengine.RythmEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.hiwepy.fastxls.core.FastxlsProperties;
import com.github.hiwepy.fastxls.core.WorkbookTemplate;
import com.github.hiwepy.fastxls.core.utils.ConfigUtils;

public abstract class WorkbookRythmTemplate<W> extends WorkbookTemplate<W,String> {
	
	protected final Logger LOG = LoggerFactory.getLogger(WorkbookRythmTemplate.class);
	protected RythmEngine engine;
	
	public RythmEngine getEngine() throws IOException {
		return engine == null ? getInternalEngine() : engine;
	}

	public void setEngine(RythmEngine engine) {
		this.engine = engine;
	}
	
	protected synchronized RythmEngine getInternalEngine() throws IOException{
		Properties props =  ConfigUtils.filterWithPrefix("fastxls.rythm.", "fastxls.rythm.", FastxlsProperties.getProperties(), false);
		
		props.put("engine.mode", Rythm.Mode.valueOf(props.getProperty("engine.mode", "dev")));
		props.put("log.enabled", false);
		props.put("feature.smart_escape.enabled", false);
		props.put("feature.transform.enabled", false);
		try {
			props.put("home.template", Rythm.class.getResource(props.getProperty("home.template")).toURI().toURL().getFile());
		} catch (URISyntaxException e) {
			// ignore
			props.put("home.tmp", "/");
		}
		props.put("codegen.dynamic_exp.enabled", true);
		props.put("built_in.code_type", "false");
		props.put("built_in.transformer", "false");
		props.put("engine.file_write", "false");
		props.put("codegen.compact.enabled", "false");
		RythmEngine engine = new RythmEngine(props);
		// 设置模板引擎，减少重复初始化消耗
        this.setEngine(engine);
        return engine;
	}
	
}
