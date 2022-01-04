/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.core.task;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.BiFunction;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.hiwepy.fastxls.core.Constants;
import com.github.hiwepy.fastxls.core.WorkbookReader;
import com.github.hiwepy.fastxls.core.model.RowConstraintViolation;
import com.github.hiwepy.fastxls.core.model.RowMap;
import com.google.common.collect.Lists;

/**
 * Excel 数据检查线程
 * @author <a href="https://github.com/vindell">wandl</a>
 * @param <W>
 * @param <S>
 * @param <R>
 * @param <C>
 * @param <M>
 */
public class DataCheckTask<W, S, R, C, M extends RowMap> implements Callable<List<RowConstraintViolation<M>>> {

	protected Logger LOG = LoggerFactory.getLogger(getClass());
	
	private final String taskName;
	private final S sheet;
	private final int offset;
	private final int limit;
	private final int firstDataRowOfSheet;
	private final boolean ignoreException;
	
	private final WorkbookReader<W, S, R, C> workbookReader;
	private final BiFunction<R, Integer, Set<ConstraintViolation<M>>> validation;
	
	public DataCheckTask(
			String taskName,
			S sheet, 
			int offset,
			int limit, 
			int firstDataRowOfSheet,
			boolean ignoreException,
			WorkbookReader<W, S, R, C> workbookReader,
		    BiFunction<R, Integer, Set<ConstraintViolation<M>>> validation
		){
		this.taskName = taskName;
		this.sheet = sheet;
		this.offset = offset;
		this.limit = limit;
		this.firstDataRowOfSheet = firstDataRowOfSheet;
		this.ignoreException = ignoreException;
		this.workbookReader = workbookReader;
		this.validation = validation;
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
				LOG.info(Constants.THREAD_RUNNING, this.getTaskName());
				// 行数据
				R row = getWorkbookReader().getRow(sheet, rowNum);
				// 执行验证
				Set<ConstraintViolation<M>> constraintViolations = getValidation().apply(row, rowNum);
				if(CollectionUtils.isNotEmpty(constraintViolations) && !isIgnoreException()) {
					throw new ConstraintViolationException("Data validation failed on line " + rowNum, constraintViolations);
				} else {
					rowConstraintViolations.add(new RowConstraintViolation<M>(rowNum, constraintViolations));
				}
				
			}
		}
		//记录状态日志
		LOG.info(Constants.THREAD_COMPLETE, this.getTaskName());
		return rowConstraintViolations;
	}
	

	public String getTaskName() {
		return taskName;
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
	
	public boolean isIgnoreException() {
		return ignoreException;
	}
	
	protected WorkbookReader<W, S, R, C> getWorkbookReader() {
		return workbookReader;
	}

	public BiFunction<R, Integer, Set<ConstraintViolation<M>>> getValidation() {
		return validation;
	}
	
}
