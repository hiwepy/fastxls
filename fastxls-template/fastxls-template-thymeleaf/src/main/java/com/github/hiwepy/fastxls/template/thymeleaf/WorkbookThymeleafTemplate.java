/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.template.thymeleaf;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.AbstractConfigurableTemplateResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.UrlTemplateResolver;

import com.github.hiwepy.fastxls.core.FastxlsProperties;
import com.github.hiwepy.fastxls.core.WorkbookTemplate;
import com.github.hiwepy.fastxls.core.utils.ArrayUtils;
import com.github.hiwepy.fastxls.core.utils.StringUtils;

public abstract class WorkbookThymeleafTemplate<W> extends WorkbookTemplate<W,String> {
	
	protected final Logger LOG = LoggerFactory.getLogger(WorkbookThymeleafTemplate.class);
	protected TemplateEngine engine;
	protected AbstractConfigurableTemplateResolver templateResolver;
	
	public TemplateEngine getEngine() throws IOException {
		return engine == null ? getInternalEngine() : engine;
	}

	public void setEngine(TemplateEngine engine) {
		this.engine = engine;
	}
	
	protected synchronized TemplateEngine getInternalEngine() throws IOException{
		//初始化模板解析器
		AbstractConfigurableTemplateResolver templateResolver =  getTemplateResolver();
		if( getTemplateResolver() == null){
			String resolver = FastxlsProperties.getProperty("fastxls.thymeleaf.templateResolver","org.thymeleaf.templateresolver.FileTemplateResolver");
			if("org.thymeleaf.templateresolver.FileTemplateResolver".equalsIgnoreCase(resolver)){
				templateResolver = new FileTemplateResolver();
			}else if("org.thymeleaf.templateresolver.ClassLoaderTemplateResolver".equalsIgnoreCase(resolver)){
				templateResolver = new ClassLoaderTemplateResolver();
			}else if("org.thymeleaf.templateresolver.UrlTemplateResolver".equalsIgnoreCase(resolver)){
				templateResolver = new UrlTemplateResolver();
			}else{
				templateResolver = new FileTemplateResolver();
			}
		}
		templateResolver.setCacheable(FastxlsProperties.getProperty("fastxls.thymeleaf.cacheable", true));
		templateResolver.setCacheablePatterns(ArrayUtils.asSet(StringUtils.tokenizeToStringArray(FastxlsProperties.getProperty("fastxls.thymeleaf.cacheablePatterns", ""))));
		String cacheTTLMs = FastxlsProperties.getProperty("fastxls.thymeleaf.cacheTTLMs");
		templateResolver.setCacheTTLMs( cacheTTLMs == null ? null : Long.valueOf(cacheTTLMs)); 
		templateResolver.setCharacterEncoding(FastxlsProperties.getProperty("fastxls.thymeleaf.charset","UTF-8"));
		templateResolver.setCheckExistence(FastxlsProperties.getProperty("fastxls.thymeleaf.checkExistence", false ));
		
		templateResolver.setCSSTemplateModePatterns(ArrayUtils.asSet(StringUtils.tokenizeToStringArray(FastxlsProperties.getProperty("fastxls.thymeleaf.newCSSTemplateModePatterns", ""))));
		templateResolver.setHtmlTemplateModePatterns(ArrayUtils.asSet(StringUtils.tokenizeToStringArray(FastxlsProperties.getProperty("fastxls.thymeleaf.newHtmlTemplateModePatterns", ""))));
		templateResolver.setJavaScriptTemplateModePatterns(ArrayUtils.asSet(StringUtils.tokenizeToStringArray(FastxlsProperties.getProperty("fastxls.thymeleaf.newJavaScriptTemplateModePatterns", ""))));
		templateResolver.setName(FastxlsProperties.getProperty("fastxls.thymeleaf.name",templateResolver.getClass().getName()));
		templateResolver.setNonCacheablePatterns(ArrayUtils.asSet(StringUtils.tokenizeToStringArray(FastxlsProperties.getProperty("fastxls.thymeleaf.nonCacheablePatterns", ""))));
		templateResolver.setOrder(Integer.valueOf(FastxlsProperties.getProperty("fastxls.thymeleaf.order","1")));
		templateResolver.setPrefix(FastxlsProperties.getProperty("fastxls.thymeleaf.prefix"));
		templateResolver.setRawTemplateModePatterns(ArrayUtils.asSet(StringUtils.tokenizeToStringArray(FastxlsProperties.getProperty("fastxls.thymeleaf.newRawTemplateModePatterns", ""))));
		templateResolver.setResolvablePatterns(ArrayUtils.asSet(StringUtils.tokenizeToStringArray(FastxlsProperties.getProperty("fastxls.thymeleaf.resolvablePatterns", ""))));
		templateResolver.setSuffix(FastxlsProperties.getProperty("fastxls.thymeleaf.suffix",".tpl"));
		//templateResolver.setTemplateAliases(templateAliases);
		templateResolver.setTemplateMode(FastxlsProperties.getProperty("fastxls.thymeleaf.templateMode","XHTML"));
		templateResolver.setTextTemplateModePatterns(ArrayUtils.asSet(StringUtils.tokenizeToStringArray(FastxlsProperties.getProperty("fastxls.thymeleaf.newTextTemplateModePatterns", ""))));
		templateResolver.setUseDecoupledLogic(FastxlsProperties.getProperty("fastxls.thymeleaf.useDecoupledLogic", false ));
		templateResolver.setXmlTemplateModePatterns(ArrayUtils.asSet(StringUtils.tokenizeToStringArray(FastxlsProperties.getProperty("fastxls.thymeleaf.newXmlTemplateModePatterns", ""))));
        //初始化引擎对象
		TemplateEngine engine = new TemplateEngine();
		engine.setTemplateResolver(templateResolver);
        //调用getConfiguration初始化引擎
		engine.getConfiguration();
		// 设置模板引擎，减少重复初始化消耗
        this.setEngine(engine);
        return engine;
	}

	public AbstractConfigurableTemplateResolver getTemplateResolver() {
		return templateResolver;
	}

	public void setTemplateResolver(AbstractConfigurableTemplateResolver templateResolver) {
		this.templateResolver = templateResolver;
	}
	
}
