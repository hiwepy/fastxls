/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.core.task;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import com.github.hiwepy.fastxls.core.Constants;
import com.github.hiwepy.fastxls.core.WorkbookReader;
import com.github.hiwepy.fastxls.core.model.RowConstraintViolation;
import com.github.hiwepy.fastxls.core.model.RowMap;
import com.github.hiwepy.fastxls.core.model.RowWrapper;

/**
 * Excel 数据导入线程
 * @author <a href="https://github.com/vindell">wandl</a>
 * @param <W>
 * @param <S>
 * @param <R>
 * @param <C>
 * @param <M>
 */
public class DataImportTask<W, S, R, C, M extends RowMap> implements Callable<List<RowConstraintViolation<M>>> {

	protected Logger LOG = LoggerFactory.getLogger(getClass());

	private final String taskNamePrefix;
	private final S sheet;
	private final List<Map<String, String>> columns;
	private final int offset;
	private final int limit;
	private final int firstDataRowOfSheet;
	private final boolean ignoreException;
	
	private final WorkbookReader<W, S, R, C> workbookReader;
	private final BiFunction<RowWrapper<R>, Integer, Set<ConstraintViolation<M>>> validation;
	private final BiConsumer<RowWrapper<R>, Integer> consumer;
	
	public DataImportTask(
			String taskNamePrefix,
			S sheet, 
			int offset,
			int limit, 
			int firstDataRowOfSheet, 
			List<Map<String, String>> columns,
			boolean ignoreException,
			WorkbookReader<W, S, R, C> workbookReader,
			BiFunction<RowWrapper<R>, Integer, Set<ConstraintViolation<M>>> validation,
		    BiConsumer<RowWrapper<R>, Integer> consumer
		   ){
		this.taskNamePrefix = taskNamePrefix;
		this.sheet = sheet;
		this.offset = offset;
		this.limit = limit;
		this.firstDataRowOfSheet = firstDataRowOfSheet;
		this.columns = columns;
		this.ignoreException = ignoreException;
		this.workbookReader = workbookReader;
		this.validation = validation;
		this.consumer = consumer;
	}
	
	@Override
	public List<RowConstraintViolation<M>> call() throws Exception {

		List<RowConstraintViolation<M>> rowConstraintViolations = Lists.newArrayList();
		// sheet 最后一行
		int lastRowNum = getWorkbookReader().getLastRowNum(sheet);
		// 计算结束位置
		int toIndex = this.getOffset() + this.getLimit() - 1;
			toIndex = Math.min(toIndex, lastRowNum);
		//根据起始结束位获取部分row
		List<R> rows = getWorkbookReader().getRows(sheet, this.getOffset(), toIndex);
		if(CollectionUtils.isNotEmpty(rows)){
			
			//String sheetName = getWorkbookReader().getName(sheet);
			
			for (int rowNum = this.getOffset(); rowNum < this.getOffset() + rows.size(); rowNum++) {
				// 跳过非数据行
				if(rowNum < this.getFirstDataRowOfSheet()) {
					continue;
				}
				// 行数据
				R row = getWorkbookReader().getRow(sheet, rowNum);
				// 行对象包装
				RowWrapper<R> wrapper = new RowWrapper<R>(row, columns ,rowNum, firstDataRowOfSheet);
				// 执行验证
				Set<ConstraintViolation<M>> constraintViolations = getValidation().apply(wrapper, rowNum);
				if(CollectionUtils.isNotEmpty(constraintViolations) && !isIgnoreException()) {
					throw new ConstraintViolationException("Data validation failed on line " + rowNum, constraintViolations);
				} else {
					rowConstraintViolations.add(new RowConstraintViolation<M>(rowNum, constraintViolations));
				}
				// 调用消费函数 
				getConsumer().accept(wrapper, rowNum);
			}
			//记录状态日志
			LOG.info(Constants.THREAD_COMPLETE, this.getTaskNamePrefix());
		}
		
		return rowConstraintViolations;
	}
	
	public String getTaskNamePrefix() {
		return taskNamePrefix;
	}

	protected S getSheet() {
		return sheet;
	}

	protected int getOffset() {
		return offset;
	}

	protected int getLimit() {
		return limit;
	}
	
	public int getFirstDataRowOfSheet() {
		return firstDataRowOfSheet;
	}
	
	public List<Map<String, String>> getColumns() {
		return columns;
	}

	public boolean isIgnoreException() {
		return ignoreException;
	}
	
	protected WorkbookReader<W, S, R, C> getWorkbookReader() {
		return workbookReader;
	}

	public BiFunction<RowWrapper<R>, Integer, Set<ConstraintViolation<M>>> getValidation() {
		return validation;
	}

	public BiConsumer<RowWrapper<R>, Integer> getConsumer() {
		return consumer;
	}
	
}
