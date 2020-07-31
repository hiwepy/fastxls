/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.template.freemarker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.SimpleHash;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.HtmlEscape;
import freemarker.template.utility.XmlEscape;
import com.github.hiwepy.fastxls.core.FastxlsProperties;
import com.github.hiwepy.fastxls.core.WorkbookTemplate;
import com.github.hiwepy.fastxls.core.utils.ConfigUtils;

public abstract class WorkbookFreemarkerTemplate<W> extends WorkbookTemplate<W,String> {
	
	protected final Logger LOG = LoggerFactory.getLogger(WorkbookFreemarkerTemplate.class);
	protected Configuration engine;
	protected Properties freemarkerSettings;
	protected Map<String, Object> freemarkerVariables;
	protected String defaultEncoding;
	protected final List<TemplateLoader> templateLoaders = new ArrayList<TemplateLoader>();
	protected List<TemplateLoader> preTemplateLoaders;
	protected List<TemplateLoader> postTemplateLoaders;
	protected TemplateModel templateModel;
	
	public Configuration getEngine() throws IOException, TemplateException {
		return engine == null ? getInternalEngine() : engine;
	}

	public void setEngine(Configuration engine) {
		this.engine = engine;
	}
	
	protected Configuration getInternalEngine() throws IOException, TemplateException{
		
		try {
			BeansWrapper beansWrapper = new BeansWrapper(Configuration.VERSION_2_3_23);
			this.templateModel = beansWrapper.getStaticModels().get(String.class.getName());
		} catch (TemplateModelException e) {
			throw new IOException(e.getMessage(),e.getCause());
		}

		// 创建 Configuration 实例
		Configuration config = new Configuration(Configuration.VERSION_2_3_23);
		
		Properties props = ConfigUtils.filterWithPrefix("fastxls.freemarker.", "fastxls.freemarker.", FastxlsProperties.getProperties(), false);

		// FreeMarker will only accept known keys in its setSettings and
		// setAllSharedVariables methods.
		if (!props.isEmpty()) {
			config.setSettings(props);
		}

		if (this.freemarkerVariables != null && !this.freemarkerVariables.isEmpty()) {
			config.setAllSharedVariables(new SimpleHash(this.freemarkerVariables, config.getObjectWrapper()));
		}

		if (this.defaultEncoding != null) {
			config.setDefaultEncoding(this.defaultEncoding);
		}

		List<TemplateLoader> templateLoaders = new LinkedList<TemplateLoader>(this.templateLoaders);
		
		// Register template loaders that are supposed to kick in early.
		if (this.preTemplateLoaders != null) {
			templateLoaders.addAll(this.preTemplateLoaders);
		}
		
		postProcessTemplateLoaders(templateLoaders);
		
		// Register template loaders that are supposed to kick in late.
		if (this.postTemplateLoaders != null) {
			templateLoaders.addAll(this.postTemplateLoaders);
		}
		
		TemplateLoader loader = getAggregateTemplateLoader(templateLoaders);
		if (loader != null) {
			config.setTemplateLoader(loader);
		}
		//config.setClassLoaderForTemplateLoading(classLoader, basePackagePath);
		//config.setCustomAttribute(name, value);
		//config.setDirectoryForTemplateLoading(dir);
		//config.setServletContextForTemplateLoading(servletContext, path);
		//config.setSharedVariable(name, value);
		//config.setSharedVariable(name, tm);
		config.setSharedVariable("fmXmlEscape", new XmlEscape());
		config.setSharedVariable("fmHtmlEscape", new HtmlEscape());
		//config.setSharedVaribles(map);
		
		// 设置模板引擎，减少重复初始化消耗
        this.setEngine(config);
        return config;
	}

	/**
	 * Return a TemplateLoader based on the given TemplateLoader list.
	 * If more than one TemplateLoader has been registered, a FreeMarker
	 * MultiTemplateLoader needs to be created.
	 * @param templateLoaders the final List of TemplateLoader instances
	 * @return the aggregate TemplateLoader
	 */
	protected TemplateLoader getAggregateTemplateLoader(List<TemplateLoader> templateLoaders) {
		int loaderCount = templateLoaders.size();
		switch (loaderCount) {
			case 0:
				LOG.info("No FreeMarker TemplateLoaders specified");
				return null;
			case 1:
				return templateLoaders.get(0);
			default:
				TemplateLoader[] loaders = templateLoaders.toArray(new TemplateLoader[loaderCount]);
				return new MultiTemplateLoader(loaders);
		}
	}
	

	/**
	 * Set properties that contain well-known FreeMarker keys which will be
	 * passed to FreeMarker's {@code Configuration.setSettings} method.
	 * @param settings properties
	 * @see freemarker.template.Configuration#setSettings
	 */
	public void setFreemarkerSettings(Properties settings) {
		this.freemarkerSettings = settings;
	}

	/**
	 * Set a Map that contains well-known FreeMarker objects which will be passed
	 * to FreeMarker's {@code Configuration.setAllSharedVariables()} method.
	 * @param variables   variables
	 * @see freemarker.template.Configuration#setAllSharedVariables
	 */
	public void setFreemarkerVariables(Map<String, Object> variables) {
		this.freemarkerVariables = variables;
	}

	/**
	 * Set the default encoding for the FreeMarker configuration.
	 * If not specified, FreeMarker will use the platform file encoding.
	 * <p>Used for template rendering unless there is an explicit encoding specified
	 * for the rendering process (for example, on Spring's FreeMarkerView).
	 * @param defaultEncoding Default Encoding
	 * @see freemarker.template.Configuration#setDefaultEncoding
	 */
	public void setDefaultEncoding(String defaultEncoding) {
		this.defaultEncoding = defaultEncoding;
	}

	/**
	 * Set a List of {@code TemplateLoader}s that will be used to search
	 * for templates. For example, one or more custom loaders such as database
	 * loaders could be configured and injected here.
	 * <p>The {@link TemplateLoader TemplateLoaders} specified here will be
	 * registered <i>before</i> the default template loaders that this factory
	 * registers (such as loaders for specified "templateLoaderPaths" or any
	 * loaders registered in {@link #postProcessTemplateLoaders}).
	 * @param preTemplateLoaders the Array of TemplateLoader instances,
	 * to be modified by a subclass
	 * @see #postProcessTemplateLoaders
	 */
	public void setPreTemplateLoaders(TemplateLoader... preTemplateLoaders) {
		this.preTemplateLoaders = Arrays.asList(preTemplateLoaders);
	}

	/**
	 * Set a List of {@code TemplateLoader}s that will be used to search
	 * for templates. For example, one or more custom loaders such as database
	 * loaders can be configured.
	 * <p>The {@link TemplateLoader TemplateLoaders} specified here will be
	 * registered <i>after</i> the default template loaders that this factory
	 * registers (such as loaders for specified "templateLoaderPaths" or any
	 * loaders registered in {@link #postProcessTemplateLoaders}).
	 * @param postTemplateLoaders the Array of TemplateLoader instances,
	 * to be modified by a subclass
	 * @see #postProcessTemplateLoaders
	 */
	public void setPostTemplateLoaders(TemplateLoader... postTemplateLoaders) {
		this.postTemplateLoaders = Arrays.asList(postTemplateLoaders);
	}
	
	/**
	 * To be overridden by subclasses that want to register custom
	 * TemplateLoader instances after this factory created its default
	 * template loaders.
	 * <p>Called by {@code createConfiguration()}. Note that specified
	 * "postTemplateLoaders" will be registered <i>after</i> any loaders
	 * registered by this callback; as a consequence, they are <i>not</i>
	 * included in the given List.
	 * @param templateLoaders the current List of TemplateLoader instances,
	 * to be modified by a subclass
	 * @see #setPostTemplateLoaders
	 */
	protected void postProcessTemplateLoaders(List<TemplateLoader> templateLoaders) {
		templateLoaders.add(new ClassTemplateLoader(WorkbookFreemarkerTemplate.class, ""));
		LOG.info("ClassTemplateLoader for WordprocessingMLFreemarkerTemplate added to FreeMarker configuration");
	}
	
}
