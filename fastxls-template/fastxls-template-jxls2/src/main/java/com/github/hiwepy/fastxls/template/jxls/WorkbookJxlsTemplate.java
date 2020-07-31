/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.template.jxls;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.jxls.util.JxlsHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.hiwepy.fastxls.core.FastxlsProperties;
import com.github.hiwepy.fastxls.core.WorkbookTemplate;
import com.github.hiwepy.fastxls.core.utils.ConfigUtils;

public abstract class WorkbookJxlsTemplate<W> extends WorkbookTemplate<W, InputStream> {
	
	protected final Logger LOG = LoggerFactory.getLogger(WorkbookJxlsTemplate.class);
	private static final String DEFAULT_EXPRESSION_BEGIN = "${";
    private static final String DEFAULT_EXPRESSION_END = "}";
	protected JxlsHelper engine;
	
	public JxlsHelper getEngine() throws IOException {
		return engine == null ? getInternalEngine() : engine;
	}
	
	public void setEngine(JxlsHelper engine) {
		this.engine = engine;
	}
	
	protected synchronized JxlsHelper getInternalEngine() throws IOException{
		
		JxlsHelper engine = JxlsHelper.getInstance();
		
		Properties props = ConfigUtils.filterWithPrefix("fastxls.jxls.", "fastxls.jxls.", FastxlsProperties.getProperties(), false);
		
		engine.setDeleteTemplateSheet(Boolean.parseBoolean(props.getProperty("deleteTemplateSheet", "true")));
		engine.setHideTemplateSheet(Boolean.parseBoolean(props.getProperty("hideTemplateSheet", "false")));
		engine.setProcessFormulas(Boolean.parseBoolean(props.getProperty("processFormulas", "true")));
		engine.setUseFastFormulaProcessor(Boolean.parseBoolean(props.getProperty("useFastFormulaProcessor", "false")));
		engine.buildExpressionNotation(props.getProperty("expressionPrefix", DEFAULT_EXPRESSION_BEGIN), 
				props.getProperty("expressionSuffix", DEFAULT_EXPRESSION_END));
		
		// 设置模板引擎，减少重复初始化消耗
        this.setEngine(engine);
        return engine;
	}
	
}
