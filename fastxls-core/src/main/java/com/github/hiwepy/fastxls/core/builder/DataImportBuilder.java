/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.core.builder;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import javax.validation.ConstraintViolation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.hiwepy.fastxls.core.WorkbookReader;
import com.github.hiwepy.fastxls.core.model.CellModel;
import com.github.hiwepy.fastxls.core.model.ConstraintViolationResult;
import com.github.hiwepy.fastxls.core.model.RowMap;
import com.github.hiwepy.fastxls.core.model.RowMapper;
import com.github.hiwepy.fastxls.core.model.RowWrapper;

/**
 * Fastxls 操作模板 
 * @author <a href="https://github.com/vindell">wandl</a>
 */
public abstract class DataImportBuilder<W, S, R, C, T extends CellModel, M extends RowMap> 
	extends DataBuilder<DataImportBuilder<W, S, R, C, T, M>, R, C, M>  {
	
	protected Logger LOG = LoggerFactory.getLogger(getClass());
	protected final WorkbookReader<W, S, R, C> workbookReader;
	  
	public DataImportBuilder(WorkbookReader<W, S, R, C> workbookReader) {
		this.workbookReader = workbookReader;
	}

	/**
	 * 最原始的数据处理方式：不进行数据转换仅进行数据校验
	 * @param file			Excel 文件对象
	 * @param header		数据列信息提供函数
	 * @param consumer		数据处理函数
	 * @return
	 * @throws Exception
	 */
	public abstract ConstraintViolationResult<M> input(File file, 
			BiFunction<S, Integer, List<Map<String, String>>> header,
			BiConsumer<RowWrapper<R>, Integer> consumer) throws Exception;
	
	/**
	 * 最原始的数据处理方式：不进行数据转换仅进行数据校验
	 * @param input			Excel 文件输入流
	 * @param header		数据列信息提供函数
	 * @param consumer		数据处理函数
	 * @return
	 * @throws Exception
	 */
	public abstract ConstraintViolationResult<M> input(InputStream input, 
			BiFunction<S, Integer, List<Map<String, String>>> header,
			BiConsumer<RowWrapper<R>, Integer> consumer) throws Exception;
	
	/**
	 * 最原始的数据处理方式：不进行数据转换仅进行数据校验
	 * @param sheets		Excel文件Sheet对象
	 * @param header		数据列信息提供函数
	 * @param consumer		数据处理函数
	 * @return
	 * @throws Exception
	 */
	public abstract ConstraintViolationResult<M> input(S[] sheets,
			BiFunction<S, Integer, List<Map<String, String>>> header,
			BiConsumer<RowWrapper<R>, Integer> consumer) throws Exception;
	
	/**
	 * 进行Map对象转换的数据处理方式: 行数据被转换成Map对象然后进行数据校验
	 * @param file			Excel 文件对象
	 * @param header		数据列信息提供函数
	 * @param consumer		数据处理函数
	 * @return
	 * @throws Exception
	 */
	public abstract ConstraintViolationResult<RowMap> input2(File file, 
			BiFunction<S, Integer, List<Map<String, String>>> header,
			BiConsumer<RowMapper<R, RowMap>, Integer> consumer) throws Exception;
	
	
	/**
	 * 进行Map对象转换的数据处理方式: 行数据被转换成Map对象然后进行数据校验
	 * @param input			Excel 文件输入流
	 * @param header		数据列信息提供函数
	 * @param consumer		数据处理函数
	 * @return
	 * @throws Exception
	 */
	public abstract ConstraintViolationResult<RowMap> input2(InputStream input, 
			BiFunction<S, Integer, List<Map<String, String>>> header,
			BiConsumer<RowMapper<R, RowMap>, Integer> consumer) throws Exception;

	/**
	 * 进行Map对象转换的数据处理方式: 行数据被转换成Map对象然后进行数据校验
	 * @param sheets		Excel文件Sheet对象
	 * @param header		数据列信息提供函数
	 * @param consumer		数据处理函数
	 * @return
	 * @throws Exception
	 */
	public abstract ConstraintViolationResult<RowMap> input2(S[] sheets,
			BiFunction<S, Integer, List<Map<String, String>>> header,
			BiConsumer<RowMapper<R, RowMap>, Integer> consumer) throws Exception;
	
	/**
	 * 进行DTO对象转换的数据处理方式: 行数据被转换成动态DTO对象然后进行数据校验
	 * @param file			Excel 文件对象
	 * @param header		数据列信息提供函数
	 * @param consumer		数据处理函数
	 * @return
	 * @throws Exception
	 */
	public abstract ConstraintViolationResult<M> input3(File file, 
			BiFunction<S, Integer, List<Map<String, String>>> header,
			BiConsumer<RowMapper<R, M>, Integer> consumer) throws Exception;
	
	/**
	 * 进行DTO对象转换的数据处理方式: 行数据被转换成动态DTO对象然后进行数据校验
	 * @param input			Excel 文件输入流
	 * @param header		数据列信息提供函数
	 * @param consumer		数据处理函数
	 * @return
	 * @throws Exception
	 */
	public abstract ConstraintViolationResult<M> input3(InputStream input, 
			BiFunction<S, Integer, List<Map<String, String>>> header,
			BiConsumer<RowMapper<R, M>, Integer> consumer) throws Exception;
	
	/**
	 * 进行DTO对象转换的数据处理方式: 行数据被转换成动态DTO对象然后进行数据校验
	 * @param sheets		Excel文件Sheet对象
	 * @param header		数据列信息提供函数
	 * @param consumer		数据处理函数
	 * @return
	 * @throws Exception
	 */
	public abstract ConstraintViolationResult<M> input3(S[] sheets,
			BiFunction<S, Integer, List<Map<String, String>>> header,
			BiConsumer<RowMapper<R, M>, Integer> consumer) throws Exception;

	/**
	 * 交由调用者自行实现的数据处理方式：行数据由调用者转换成DTO对象然后由调用者进行数据校验
	 * @param file			Excel 文件对象
	 * @param header		数据列信息提供函数
	 * @param rowMapper		数据转换函数
	 * @param validation	数据校验函数
	 * @param consumer		数据处理函数
	 * @return
	 * @throws Exception
	 */
	public abstract ConstraintViolationResult<M> input(File file, 
			BiFunction<S, Integer, List<Map<String, String>>> header,
			BiFunction<RowWrapper<R>, Integer, RowMapper<R,M>> rowMapper,
			BiFunction<RowMapper<R,M>, Integer, Set<ConstraintViolation<M>>> validation, 
			BiConsumer<RowMapper<R,M>, Integer> consumer) throws Exception;
	
	
	/**
	 * 交由调用者自行实现的数据处理方式：行数据由调用者转换成DTO对象然后由调用者进行数据校验
	 * @param input			Excel 文件输入流
	 * @param header		数据列信息提供函数
	 * @param rowMapper		数据转换函数
	 * @param validation	数据校验函数
	 * @param consumer		数据处理函数
	 * @return
	 * @throws Exception
	 */
	public abstract ConstraintViolationResult<M> input(InputStream input,
			BiFunction<S, Integer, List<Map<String, String>>> header,
			BiFunction<RowWrapper<R>, Integer, RowMapper<R,M>> rowMapper,
			BiFunction<RowMapper<R,M>, Integer, Set<ConstraintViolation<M>>> validation,
			BiConsumer<RowMapper<R,M>, Integer> consumer) throws Exception;
	
	/**
	 * 交由调用者自行实现的数据处理方式：行数据由调用者转换成DTO对象然后由调用者进行数据校验
	 * @param sheets		Excel文件Sheet对象
	 * @param header		数据列信息提供函数
	 * @param rowMapper		数据转换函数
	 * @param validation	数据校验函数
	 * @param consumer		数据处理函数
	 * @return
	 * @throws Exception
	 */
	public abstract ConstraintViolationResult<M> input(S[] sheets,
			BiFunction<S, Integer, List<Map<String, String>>> header,
			BiFunction<RowWrapper<R>, Integer, RowMapper<R,M>> rowMapper,
			BiFunction<RowMapper<R,M>, Integer, Set<ConstraintViolation<M>>> validation,
			BiConsumer<RowMapper<R,M>, Integer> consumer) throws Exception;
	
	public DataImportBuilder<W, S, R, C, T, M> and() {
		return this;
	}
	
	@Override
	public DataImportBuilder<W, S, R, C, T, M> build() {
		init();
		return this;
	}
	
	public WorkbookReader<W, S, R, C> getWorkbookReader() {
		return workbookReader;
	}
	
}
