/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.core.builder;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import javax.validation.Validation;
import javax.validation.Validator;

import com.github.hiwepy.fastxls.core.interceptor.Interceptor;
import com.github.hiwepy.fastxls.core.model.ColumnWrapper;
import com.github.hiwepy.fastxls.core.model.RowMap;
import com.github.hiwepy.fastxls.core.property.ExportProperties;
import com.github.hiwepy.fastxls.core.provider.ValidationMessageProvider;
import com.github.hiwepy.fastxls.core.provider.def.DefaultValidationMessageProvider;
import com.github.hiwepy.fastxls.core.utils.Assert;
import com.google.common.collect.Lists;

/**
 * 数据导入逻辑构建器
 * @author <a href="https://github.com/vindell">wandl</a>
 */
public abstract class DataBuilder<W, R, C, M extends RowMap> extends AbstractBuilder<W> {
	
	private List<Interceptor<R, M>> interceptors = Lists.newArrayList();
    private Validator validator;
    private ValidationMessageProvider messageProvider;
    private BiConsumer<String, Float> process;
    private BiConsumer<String, Float> finish;
    private BiFunction<ColumnWrapper<C>, String, String> transformer;
    
	private int firstDataRowOfSheet = 1;
    private boolean ignoreException;
    /**
	 * 是否使用多线程，默认：false
	 */
	private boolean threadPool = false;
	/**
	 * 线程名称前缀，默认：thread-export-
	 */
	private String threadNamePrefix = "thread-export-";
	/**
	 * 每次的导出中允许创建的线程池最大容量，默认：20
	 */
	private int maxThreadSize = 20;
	/**
	 * 导出xls文件时,单个线程最大处理行，默认 500.
	 */
	private int maxThreadTaskSize = 500;
    
	public DataBuilder<W, R, C, M> threadPool(boolean threadPool){
		Assert.notNull(threadPool, " threadPool must not be null ");
		this.threadPool = threadPool;
		return this;
	}
	
	public DataBuilder<W, R, C, M> threadNamePrefix(String threadNamePrefix){
		Assert.notNull(threadNamePrefix, " threadNamePrefix must not be null ");
		this.threadNamePrefix = threadNamePrefix;
		return this;
	}
	
	public DataBuilder<W, R, C, M> maxThreadSize(int maxThreadSize){
		Assert.notNull(maxThreadSize, " maxThreadSize must not be null ");
		this.maxThreadSize = maxThreadSize;
		return this;
	}
	
	public DataBuilder<W, R, C, M> maxThreadTaskSize(int maxThreadTaskSize){
		Assert.notNull(maxThreadTaskSize, " maxThreadTaskSize must not be null ");
		this.maxThreadTaskSize = maxThreadTaskSize;
		return this;
	}
	
	public DataBuilder<W, R, C, M> properties(ExportProperties properties){
		Assert.notNull(properties, " properties must not be null ");
		super.properties(properties);
		this.threadPool = properties.isThreadPool();
		this.threadNamePrefix = properties.getThreadNamePrefix();
		this.maxThreadSize = properties.getMaxThreadSize();
		this.maxThreadTaskSize = properties.getMaxThreadTaskSize();
		return this;
	}
	  
	protected boolean isThreadPool() {
		return threadPool;
	}

	protected String getThreadNamePrefix() {
		return threadNamePrefix;
	}

	protected int getMaxThreadSize() {
		return maxThreadSize;
	}

	protected int getMaxThreadTaskSize() {
		return maxThreadTaskSize;
	}

    public DataBuilder<W, R, C, M> transform(BiFunction<ColumnWrapper<C>, String, String> transformer) {
    	Assert.notNull(transformer, " transformer must not be null ");
		this.transformer = transformer;
		return this;
    }
	
    public DataBuilder<W, R, C, M> watch(BiConsumer<String, Float> process, BiConsumer<String, Float> finish) {
    	Assert.notNull(process, " process must not be null ");
    	Assert.notNull(finish, " finish must not be null ");
		this.process = process;
		this.finish = finish;
		return this;
    }

    public DataBuilder<W, R, C, M> firstDataRowOfSheet(int firstDataRowOfSheet){
		this.firstDataRowOfSheet = firstDataRowOfSheet;
		return this;
	}
    
    public DataBuilder<W, R, C, M> ignoreException(boolean ignoreException){
		this.ignoreException = ignoreException;
		return this;
	}
    
    public DataBuilder<W, R, C, M> messageProvider(ValidationMessageProvider messageProvider){
    	Assert.notNull(messageProvider, " messageProvider must not be null ");
		this.messageProvider = messageProvider;
		return this;
	}
 
    
    protected void defaultValidator(final Validator validator) {
        if (this.validator == null) {
            this.validator = validator;
        }
    }
    
    protected void defaultMessageProvider(final ValidationMessageProvider messageProvider) {
        if (this.messageProvider == null) {
            this.messageProvider = messageProvider;
        }
    }
    
    /**
     * Internal initialization of the object.
     */
    @Override
    protected void internalInit() {
    	// 初始化默认的校验对象
    	defaultValidator(Optional.ofNullable(validator).orElseGet(() -> {
    		return Validation.buildDefaultValidatorFactory().getValidator();
    	}));
    	// 初始化默认的校验消息获取实现
    	defaultMessageProvider(Optional.ofNullable(messageProvider).orElseGet(() -> {
    		return new DefaultValidationMessageProvider();
    	}));
    };

    public BiConsumer<String, Float> getProcess() {
		return process;
	}
    
    public BiConsumer<String, Float> getFinish() {
		return finish;
	}
    
	public List<Interceptor<R, M>> getInterceptors() {
		return interceptors;
	}
	
	public Validator getValidator() {
		return validator;
	}

	public ValidationMessageProvider getMessageProvider() {
		return messageProvider;
	}
	
	public int getFirstDataRowOfSheet() {
		return firstDataRowOfSheet;
	}
	
	public boolean isIgnoreException() {
		return ignoreException;
	}
	
	public BiFunction<ColumnWrapper<C>, String, String> getTransformer() {
		return transformer;
	}
	
}
