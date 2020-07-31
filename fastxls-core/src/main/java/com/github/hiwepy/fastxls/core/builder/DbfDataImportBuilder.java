/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.core.builder;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import javax.validation.ConstraintViolation;

import org.jamel.dbf.DbfReader;
import org.jamel.dbf.structure.DbfHeader;
import org.jamel.dbf.structure.DbfRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.hiwepy.fastxls.core.model.ConstraintViolationResult;
import com.github.hiwepy.fastxls.core.model.RowMap;
import com.github.hiwepy.fastxls.core.model.RowMapper;
import com.github.hiwepy.fastxls.core.model.RowWrapper;

/**
 * @author <a href="https://github.com/vindell">wandl</a>
 */
public abstract class DbfDataImportBuilder<M extends RowMap> 
	extends DbfDataBuilder<DbfDataImportBuilder<M>, M>  {
	
	protected Logger LOG = LoggerFactory.getLogger(getClass());

	/**
	 * 最原始的数据处理方式：不进行数据转换仅进行数据校验
	 * @param dbf			*.dbf格式文件对象
	 * @param header		数据列信息提供函数
	 * @param consumer		数据处理函数
	 * @return
	 * @throws Exception
	 */
	public abstract ConstraintViolationResult<M> input(DbfReader dbf, 
			Function<DbfHeader, List<Map<String, String>>> header,
			BiConsumer<RowWrapper<DbfRow>, Integer> consumer) throws Exception;
	
	/**
	 * 进行Map对象转换的数据处理方式: 行数据被转换成Map对象然后进行数据校验
	 * @param dbf			*.dbf格式文件对象
	 * @param header		数据列信息提供函数
	 * @param consumer		数据处理函数
	 * @return
	 * @throws Exception
	 */
	public abstract ConstraintViolationResult<RowMap> input2(DbfReader dbf, 
			Function<DbfHeader, List<Map<String, String>>> header,
			BiConsumer<RowMapper<DbfRow, RowMap>, Integer> consumer) throws Exception;
	
	/**
	 * 进行DTO对象转换的数据处理方式: 行数据被转换成动态DTO对象然后进行数据校验
	 * @param dbf			*.dbf格式文件对象
	 * @param header		数据列信息提供函数
	 * @param consumer		数据处理函数
	 * @return
	 * @throws Exception
	 */
	public abstract ConstraintViolationResult<M> input3(DbfReader dbf, 
			Function<DbfHeader, List<Map<String, String>>> header,
			BiConsumer<RowMapper<DbfRow, M>, Integer> consumer) throws Exception;
	
	/**
	 * 交由调用者自行实现的数据处理方式：行数据由调用者转换成DTO对象然后由调用者进行数据校验
	 * @param dbf			*.dbf格式文件对象
	 * @param header		数据列信息提供函数
	 * @param rowMapper		数据转换函数
	 * @param validation	数据校验函数
	 * @param consumer		数据处理函数
	 * @return
	 * @throws Exception
	 */
	public abstract ConstraintViolationResult<M> input(DbfReader dbf, 
			Function<DbfHeader, List<Map<String, String>>> header,
			BiFunction<DbfRow, Integer, RowMapper<DbfRow,M>> rowMapper,
			BiFunction<RowMapper<DbfRow,M>, Integer, Set<ConstraintViolation<M>>> validation, 
			BiConsumer<RowMapper<DbfRow,M>, Integer> consumer) throws Exception;
	
	@Override
	public DbfDataImportBuilder<M> build() {
		init();
		return this;
	}
	
}
