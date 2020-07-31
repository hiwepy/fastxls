/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.template.jxls;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.hiwepy.fastxls.core.FastxlsProperties;
import com.github.hiwepy.fastxls.core.WorkbookTemplate;
import com.github.hiwepy.fastxls.core.utils.ConfigUtils;
import net.sf.jxls.transformer.Configuration;
import net.sf.jxls.transformer.XLSTransformer;

public abstract class WorkbookJxlsTemplate<W> extends WorkbookTemplate<W,InputStream> {
	
	protected final Logger LOG = LoggerFactory.getLogger(WorkbookJxlsTemplate.class);
	private static final String DEFAULT_EXPRESSION_BEGIN = "${";
    private static final String DEFAULT_EXPRESSION_END = "}";
	private static final String DEFAULT_FORMULA_PART_BEGIN = "{";
    private static final String DEFAULT_FORMULA_PART_END = "}";
    private static final String DEFAULT_FORMULA_BEGIN = "$[";
    private static final String DEFAULT_FORMULA_END = "]";
    private static final String DEFAULT_META_INFO_TOKEN = "//";
    private static final String DEFAULT_EXCLUDE_SHEET_PROCESSING_MARK = "#Exclude";
    
	protected XLSTransformer engine;
	
	public XLSTransformer getEngine() throws IOException {
		return engine == null ? getInternalEngine() : engine;
	}
	
	public void setEngine(XLSTransformer engine) {
		this.engine = engine;
	}
	
	protected synchronized XLSTransformer getInternalEngine() throws IOException{
		
		Properties props = ConfigUtils.filterWithPrefix("fastxls.jxls1.", "fastxls.jxls1.", FastxlsProperties.getProperties(), false);
		
		Configuration configuration = new Configuration();
		
		configuration.setEncodeXMLAttributes(Boolean.parseBoolean(props.getProperty("encodeXMLAttributes", "true")));
		configuration.setStartExpressionToken(props.getProperty("expressionPrefix", DEFAULT_EXPRESSION_BEGIN));
		configuration.setEndExpressionToken(props.getProperty("expressionSuffix", DEFAULT_EXPRESSION_END));
		configuration.setStartFormulaPartToken(props.getProperty("formulaPartPrefix", DEFAULT_FORMULA_PART_BEGIN));
		configuration.setEndFormulaPartToken(props.getProperty("formulaPartSuffix", DEFAULT_FORMULA_PART_END));
		configuration.setStartFormulaToken(props.getProperty("formulaPrefix", DEFAULT_FORMULA_BEGIN));
		configuration.setEndFormulaToken(props.getProperty("formulaSuffix", DEFAULT_FORMULA_END));
		configuration.setMetaInfoToken(props.getProperty("metaInfoPrefix", DEFAULT_META_INFO_TOKEN));
		configuration.setExcludeSheetProcessingMark(props.getProperty("excludeSheetProcessingMark", DEFAULT_EXCLUDE_SHEET_PROCESSING_MARK));
		configuration.setRemoveExcludeSheetProcessingMark(Boolean.parseBoolean(props.getProperty("removeExcludeSheetProcessingMark", "false")));
		configuration.setUTF16(Boolean.parseBoolean(props.getProperty("UTF16", "false")));
		
		XLSTransformer engine = new XLSTransformer(configuration);
		
		// 设置模板引擎，减少重复初始化消耗
        this.setEngine(engine);
        return engine;
	}
	
}
