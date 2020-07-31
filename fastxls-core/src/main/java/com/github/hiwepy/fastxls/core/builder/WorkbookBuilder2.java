package com.github.hiwepy.fastxls.core.builder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import javax.sql.rowset.CachedRowSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.hiwepy.fastxls.core.model.CellModel;
import com.github.hiwepy.fastxls.core.property.ExportProperties;
import com.github.hiwepy.fastxls.core.utils.Assert;

public abstract class WorkbookBuilder2<T extends CellModel, S> extends AbstractBuilder<S> {
	
	protected Logger LOG = LoggerFactory.getLogger(getClass());
	
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
	/**
	 * 生成的xls文件是否只读
	 */
	private boolean readOnly;
	/**
	 *Sheet名称 
	 */
	private String sheetName;
	/**
	 * 数据列信息,当数据导出的数据为CachedRowSet、List<Map<String,String>> 时，需要用到
	 */
	private List<Map<String, String>> columnList;
	/**
	 * RowSet 对象数据, 用于大量数据表格构建 
	 */
	private CachedRowSet rowSet;
	/**
	 * Map对象行数据集合
	 */
	private List<Map<String,String>> rowList;
	/**
	 * 构建结果输出流
	 */
	protected OutputStream output;

	@Override
	protected void internalInit() {
		
	}

	public WorkbookBuilder2<T, S> threadPool(boolean threadPool){
		Assert.notNull(threadPool, " threadPool must not be null ");
		this.threadPool = threadPool;
		return this;
	}
	
	public WorkbookBuilder2<T, S> threadNamePrefix(String threadNamePrefix){
		Assert.notNull(threadNamePrefix, " threadNamePrefix must not be null ");
		this.threadNamePrefix = threadNamePrefix;
		return this;
	}
	
	public WorkbookBuilder2<T, S> maxThreadSize(int maxThreadSize){
		Assert.notNull(maxThreadSize, " maxThreadSize must not be null ");
		this.maxThreadSize = maxThreadSize;
		return this;
	}
	
	public WorkbookBuilder2<T, S> maxThreadTaskSize(int maxThreadTaskSize){
		Assert.notNull(maxThreadTaskSize, " maxThreadTaskSize must not be null ");
		this.maxThreadTaskSize = maxThreadTaskSize;
		return this;
	}
	
	public WorkbookBuilder2<T, S> properties(ExportProperties properties){
		Assert.notNull(properties, " properties must not be null ");
		super.properties(properties);
		this.threadPool = properties.isThreadPool();
		this.threadNamePrefix = properties.getThreadNamePrefix();
		this.maxThreadSize = properties.getMaxThreadSize();
		this.maxThreadTaskSize = properties.getMaxThreadTaskSize();
		return this;
	}
	  
	
	public WorkbookBuilder2<T, S> sheetName(String sheetName){
		this.sheetName = sheetName;
		return this;
	}
	
	public WorkbookBuilder2<T, S> rowSet(CachedRowSet rowSet){
		this.rowSet = rowSet;
		return this;
	}
	
	public WorkbookBuilder2<T, S> rowList(List<Map<String,String>> rowList){
		this.rowList = rowList;
		return this;
	}
	
	public WorkbookBuilder2<T, S> columnList(List<Map<String,String>> columnList){
		this.columnList = columnList;
		return this;
	}
	
	public WorkbookBuilder2<T, S> output(File output) throws IOException {
		this.output = new FileOutputStream(output);
		return this;
	}
	
	public WorkbookBuilder2<T, S> output(OutputStream output) {
		this.output = output;
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

	protected boolean isReadOnly() {
		return readOnly;
	}

	protected String getSheetName() {
		return sheetName;
	}

	protected CachedRowSet getRowSet() {
		init();
		return rowSet;
	}

	protected List<Map<String, String>> getRowList() {
		init();
		return rowList;
	}
	
	public List<Map<String, String>> getColumnList() {
		return columnList;
	}

	protected OutputStream getOutput() {
		return output;
	}

}