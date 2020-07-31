/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.template.beetl;

import java.io.IOException;
import java.util.Map;

import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.resource.ClasspathResourceLoader;

import com.github.hiwepy.fastxls.core.Constants;
import com.github.hiwepy.fastxls.core.FastxlsProperties;
import com.github.hiwepy.fastxls.core.WorkbookTemplate;

public abstract class WorkbookBeetlTemplate<W> extends WorkbookTemplate<W,String> {
	
	protected GroupTemplate engine;
	
	public GroupTemplate getEngine() throws IOException {
		return engine == null ? getInternalEngine() : engine;
	}

	public void setEngine(GroupTemplate engine) {
		this.engine = engine;
	}
	
	protected synchronized GroupTemplate getInternalEngine() throws IOException{
        ClasspathResourceLoader loader = new ClasspathResourceLoader();
        //加载默认参数
        Configuration cfg = Configuration.defaultConfiguration();
        //模板字符集
        cfg.setCharset(FastxlsProperties.getProperty("fastxls.beetl.charset", Constants.DEFAULT_CHARSETNAME));
        //模板占位起始符号 
        cfg.setPlaceholderStart(FastxlsProperties.getProperty("fastxls.beetl.placeholderStart", "${"));
        //模板占位结束符号
        cfg.setPlaceholderEnd(FastxlsProperties.getProperty("fastxls.beetl.placeholderEnd", "<%"));
        //控制语句起始符号
        cfg.setStatementStart(FastxlsProperties.getProperty("fastxls.beetl.statementStart", "%>"));
        //控制语句结束符号
        cfg.setStatementEnd(FastxlsProperties.getProperty("fastxls.beetl.statementEnd", "}"));
        //是否允许html tag，在web编程中，有可能用到html tag，最好允许 
        cfg.setHtmlTagSupport(FastxlsProperties.getProperty("fastxls.beetl.htmlTagSupport", false));
        //html tag 标示符号 
        cfg.setHtmlTagFlag(FastxlsProperties.getProperty("fastxls.beetl.htmlTagFlag", "#"));
        //html 绑定的属性，如&lt;aa var="customer">
        cfg.setHtmlTagBindingAttribute(FastxlsProperties.getProperty("fastxls.beetl.htmlTagBindingAttribute", "var"));
        //是否允许直接调用class
        cfg.setNativeCall(FastxlsProperties.getProperty("fastxls.beetl.nativeCall", false));
        //输出模式，默认是字符集输出，改成byte输出提高性能 
        cfg.setDirectByteOutput(FastxlsProperties.getProperty("fastxls.beetl.directByteOutput", true));
        //严格mvc应用，只有变态的的人才打开此选项 
        cfg.setStrict(FastxlsProperties.getProperty("fastxls.beetl.strict", false));
        //是否忽略客户端的网络异常
        cfg.setIgnoreClientIOError(FastxlsProperties.getProperty("fastxls.beetl.ignoreClientIOError", true));
        //错误处理类
        cfg.setErrorHandlerClass(FastxlsProperties.getProperty("fastxls.beetl.errorHandlerClass", "org.beetl.core.ConsoleErrorHandler"));
        
        //资源参数
        Map<String,String> resourceMap = cfg.getResourceMap();
        //classpath 跟路径
        resourceMap.put("root", FastxlsProperties.getProperty("fastxls.beetl.resource.root", "/"));
        //是否检测文件变化
        resourceMap.put("autoCheck", FastxlsProperties.getProperty("fastxls.beetl.resource.autoCheck", "true"));
        //自定义脚本方法文件位置
        resourceMap.put("functionRoot", FastxlsProperties.getProperty("fastxls.beetl.resource.functionRoot", "functions"));
        //自定义脚本方法文件的后缀
        resourceMap.put("functionSuffix", FastxlsProperties.getProperty("fastxls.beetl.resource.functionSuffix", "html"));
        //自定义标签文件位置
        resourceMap.put("tagRoot", FastxlsProperties.getProperty("fastxls.beetl.resource.tagRoot", "htmltag"));
        //自定义标签文件后缀
        resourceMap.put("tagSuffix", FastxlsProperties.getProperty("fastxls.beetl.resource.tagSuffix", "tag"));
        cfg.setResourceMap(resourceMap);
        GroupTemplate engine = new GroupTemplate(loader, cfg);
        // 设置模板引擎，减少重复初始化消耗
        this.setEngine(engine);
        return engine;
	}
	
}
