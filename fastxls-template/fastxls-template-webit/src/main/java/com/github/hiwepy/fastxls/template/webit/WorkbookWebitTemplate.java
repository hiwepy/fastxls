/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.template.webit;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.hiwepy.fastxls.core.FastxlsProperties;
import com.github.hiwepy.fastxls.core.WorkbookTemplate;
import webit.script.CFG;
import webit.script.Engine;

public abstract class WorkbookWebitTemplate<W> extends WorkbookTemplate<W,String> {
	
	protected final Logger LOG = LoggerFactory.getLogger(WorkbookWebitTemplate.class);
	protected Engine engine;
	
	public Engine getEngine() throws IOException {
		return engine == null ? getInternalEngine() : engine;
	}

	public void setEngine(Engine engine) {
		this.engine = engine;
	}

	protected synchronized Engine getInternalEngine() throws IOException{
		
		Map<String, Object> ps = new HashMap<String, Object>();
		ps.put(CFG.APPEND_LOST_SUFFIX, FastxlsProperties.getProperty("fastxls.webit.engine.appendLostSuffix", false));
		ps.put(CFG.INIT_TEMPLATES, FastxlsProperties.getProperty("fastxls.webit.engine.initTemplates"));
		//ps.put(CFG.FILTER, input_charset);
		ps.put(CFG.LOADER, FastxlsProperties.getProperty("fastxls.webit.engine.resourceLoader","webit.script.loaders.impl.ClasspathLoader"));
        ps.put(CFG.LOADER_ENCODING, FastxlsProperties.getProperty("fastxls.webit.loader.encoding", Engine.UTF_8) );
        ps.put(CFG.LOADER_ROOT, FastxlsProperties.getProperty("fastxls.webit.loader.root") );
        ps.put(CFG.LOGGER, FastxlsProperties.getProperty("fastxls.webit.engine.logger", "webit.script.loggers.impl.NOPLogger"));
		ps.put(CFG.LOOSE_VAR, FastxlsProperties.getProperty("fastxls.webit.engine.looseVar", false));
		ps.put(CFG.OUT_ENCODING, FastxlsProperties.getProperty("fastxls.webit.engine.encoding", Engine.UTF_8));
		//ps.put(CFG.RESOLVERS, input_charset);
		//ps.put(CFG.SECURITY_LIST, FastxlsProperties.getProperty("fastxls.webit.nativeSecurity.list"));
        //ps.put(CFG.SERVLET_CONTEXT, output_charset );
        ps.put(CFG.SHARE_ROOT, FastxlsProperties.getProperty("fastxls.webit.engine.shareRootData", true));
        ps.put(CFG.SUFFIX, FastxlsProperties.getProperty("fastxls.webit.engine.suffix", ".wit"));
        ps.put(CFG.TEXT_FACTORY, FastxlsProperties.getProperty("fastxls.webit.engine.textStatementFactory", CFG.SIMPLE_TEXT_FACTORY));
        ps.put(CFG.TRIM_CODE_LINE, FastxlsProperties.getProperty("fastxls.webit.engine.trimCodeBlockBlankLine",true));
        ps.put(CFG.VARS, FastxlsProperties.getProperty("fastxls.webit.engine.vars"));
        
        Engine engine = Engine.create("", ps);
        // 设置模板引擎，减少重复初始化消耗
        this.setEngine(engine);
        return engine;
	}
	
}
