/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.template.jetbrick;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jetbrick.config.ConfigLoader;
import jetbrick.template.JetConfig;
import jetbrick.template.JetEngine;
import com.github.hiwepy.fastxls.core.FastxlsProperties;
import com.github.hiwepy.fastxls.core.WorkbookTemplate;
import com.github.hiwepy.fastxls.core.utils.ConfigUtils;

public abstract class WorkbookJetbrickTemplate<W> extends WorkbookTemplate<W,String> {
	
	protected final Logger LOG = LoggerFactory.getLogger(WorkbookJetbrickTemplate.class);
	protected JetEngine engine;
	
	public JetEngine getEngine() throws IOException {
		return engine == null ? getInternalEngine() : engine;
	}

	public void setEngine(JetEngine engine) {
		this.engine = engine;
	}
	
	protected synchronized JetEngine getInternalEngine() throws IOException{
		Properties ps = new Properties();
		ConfigLoader loader = new ConfigLoader();
		try {
			LOG.info("Loading config file: {}", JetConfig.DEFAULT_CONFIG_FILE);
		    loader.load(JetConfig.DEFAULT_CONFIG_FILE);
		    ps = loader.asProperties();
		} catch (Exception e) {
		     // 默认配置文件不存在
			LOG.warn("No default config file found: {}", JetConfig.DEFAULT_CONFIG_FILE);
			ps = ConfigUtils.filterWithPrefix("docx4j.jetx.", "docx4j.", FastxlsProperties.getProperties(), true);
		}
		JetEngine engine = JetEngine.create(ps);
		// 设置模板引擎，减少重复初始化消耗
        this.setEngine(engine);
        return engine;
	}
	
}
