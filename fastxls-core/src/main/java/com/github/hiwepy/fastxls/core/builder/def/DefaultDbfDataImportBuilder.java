/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.core.builder.def;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.apache.commons.collections4.CollectionUtils;
import org.jamel.dbf.DbfReader;
import org.jamel.dbf.structure.DbfHeader;
import org.jamel.dbf.structure.DbfRow;

import com.github.hiwepy.fastxls.core.builder.DbfDataImportBuilder;
import com.github.hiwepy.fastxls.core.function.DefaultDbfRowDtoMapperBifunction;
import com.github.hiwepy.fastxls.core.function.DefaultDbfRowDtoValidationBiFunction;
import com.github.hiwepy.fastxls.core.function.DefaultDbfRowMapMapperBiFunction;
import com.github.hiwepy.fastxls.core.function.DefaultDbfRowMapValidationFunction;
import com.github.hiwepy.fastxls.core.function.DefaultDbfRowValidationFunction;
import com.github.hiwepy.fastxls.core.model.ColumnWrapper;
import com.github.hiwepy.fastxls.core.model.ConstraintViolationResult;
import com.github.hiwepy.fastxls.core.model.RowConstraintViolation;
import com.github.hiwepy.fastxls.core.model.RowMap;
import com.github.hiwepy.fastxls.core.model.RowMapper;
import com.github.hiwepy.fastxls.core.model.RowWrapper;

public class DefaultDbfDataImportBuilder<M extends RowMap> extends DbfDataImportBuilder<M> {
	
	private BiFunction<RowWrapper<DbfRow>, Integer, RowMapper<DbfRow, RowMap>> dbfRowmapMapper;
	private BiFunction<RowMapper<DbfRow, RowMap>, Integer, Set<ConstraintViolation<RowMap>>> dbfRowmapValidation;
	private BiFunction<RowWrapper<DbfRow>, Integer, RowMapper<DbfRow, M>> dbfRowDtoMapper;
	private BiFunction<RowMapper<DbfRow, M>, Integer, Set<ConstraintViolation<M>>> dbfRowDtoValidation;
	private BiFunction<RowWrapper<DbfRow>, Integer, Set<ConstraintViolation<M>>> dbfRowValidation;
	
	public DefaultDbfDataImportBuilder() {
	}
	
	public DefaultDbfDataImportBuilder(BiFunction<ColumnWrapper<DbfRow>, String, String> transformer) {
		this.transform(transformer);
	}
	
	@Override
	protected void internalInit() {
		super.internalInit();
		// DBF 文件处理
		dbfRowmapMapper = new DefaultDbfRowMapMapperBiFunction(this.getTransformer());
		dbfRowmapValidation = new DefaultDbfRowMapValidationFunction();
		dbfRowDtoMapper = new DefaultDbfRowDtoMapperBifunction<>(this.getTransformer());
		dbfRowDtoValidation = new DefaultDbfRowDtoValidationBiFunction<>(getValidator());
		dbfRowValidation = new DefaultDbfRowValidationFunction<>(this.getTransformer());
	}
	

	@Override
	public ConstraintViolationResult<M> input(DbfReader dbf, 
			Function<DbfHeader, List<Map<String, String>>> header,
			BiConsumer<RowWrapper<DbfRow>, Integer> consumer) throws Exception {
		ConstraintViolationResult<M> result = new ConstraintViolationResult<M>();
		if(dbf.getRecordCount() > 0 ) {
			// 获取转换后的头信息
			List<Map<String, String>> columns = header.apply(dbf.getHeader());
			// 
			DbfRow row = null;
			int rowNum = -1;
			BigDecimal count = BigDecimal.valueOf(dbf.getRecordCount());
			do {
				// 数据不为空
				if(row != null) {
					// 行对象包装
					RowWrapper<DbfRow> wrapper = new RowWrapper<>(row, columns, rowNum, 0);
					// 执行验证
					Set<ConstraintViolation<M>> constraintViolations = getDbfRowValidation().apply(wrapper, rowNum);
					if(CollectionUtils.isNotEmpty(constraintViolations)) {
						if( !isIgnoreException()) {
							throw new ConstraintViolationException(String.format("第%s行数据验证失败", rowNum + 1), constraintViolations);
						} else {
							result.addConstraintViolation(new RowConstraintViolation<M>(rowNum, constraintViolations));
						}
					}
					// 调用消费函数 
					consumer.accept(wrapper, rowNum);
					// 记录完成进度
					getProcess().accept(this.getUuid(), BigDecimal.valueOf(rowNum).divide(count, 2, BigDecimal.ROUND_HALF_UP).floatValue());
				}
				// 获取下一行数据
				row = dbf.nextRow();
				rowNum += 1;
			} while (row != null);
			// 操作完成
			getFinish().accept(this.getUuid(), 100f);					
		}
		return result;
	}
	
	@Override
	public ConstraintViolationResult<RowMap> input2(DbfReader dbf, 
			Function<DbfHeader, List<Map<String, String>>> header,
			BiConsumer<RowMapper<DbfRow, RowMap>, Integer> consumer) throws Exception {
		
		ConstraintViolationResult<RowMap> result = new ConstraintViolationResult<RowMap>();
		if(dbf.getRecordCount() > 0 ) {
			// 获取转换后的头信息
			List<Map<String, String>> columns = header.apply(dbf.getHeader());
			// 
			DbfRow row = null;
			int rowNum = -1;
			BigDecimal count = BigDecimal.valueOf(dbf.getRecordCount());
			do {
				// 数据不为空
				if(row != null) {
					// 行对象包装
					RowWrapper<DbfRow> wrapper = new RowWrapper<>(row, columns, rowNum, 0);
					// 数据转换
					RowMapper<DbfRow, RowMap> mapper = getDbfRowmapMapper().apply(wrapper, rowNum);
					// 执行验证
					Set<ConstraintViolation<RowMap>> constraintViolations = getDbfRowmapValidation().apply(mapper, rowNum);
					if(CollectionUtils.isNotEmpty(constraintViolations)) {
						if( !isIgnoreException()) {
							throw new ConstraintViolationException(String.format("第%s行数据验证失败", rowNum + 1), constraintViolations);
						} else {
							result.addConstraintViolation(new RowConstraintViolation<RowMap>(rowNum, constraintViolations));
						}
					}
					// 调用消费函数 
					consumer.accept(mapper, rowNum);
					// 记录完成进度
					getProcess().accept(this.getUuid(), BigDecimal.valueOf(rowNum).divide(count, 2, BigDecimal.ROUND_HALF_UP).floatValue());
				}
				// 获取下一行数据
				row = dbf.nextRow();
				rowNum += 1;
			} while (row != null);
			// 操作完成
			getFinish().accept(this.getUuid(), 100f);	
		}
		return result;
	}
	
	@Override
	public ConstraintViolationResult<M> input3(DbfReader dbf, 
			Function<DbfHeader, List<Map<String, String>>> header,
			BiConsumer<RowMapper<DbfRow, M>, Integer> consumer) throws Exception {
		
		ConstraintViolationResult<M> result = new ConstraintViolationResult<M>();
		if(dbf.getRecordCount() > 0 ) {
			// 获取转换后的头信息
			List<Map<String, String>> columns = header.apply(dbf.getHeader());
			// 
			DbfRow row = null;
			int rowNum = -1;
			BigDecimal count = BigDecimal.valueOf(dbf.getRecordCount());
			do {
				// 数据不为空
				if(row != null) {
					// 行对象包装
					RowWrapper<DbfRow> wrapper = new RowWrapper<>(row, columns, rowNum, 0);
					// 数据转换
					RowMapper<DbfRow, M> mapper = getDbfRowDtoMapper().apply(wrapper, rowNum);
					// 执行验证
					Set<ConstraintViolation<M>> constraintViolations = getDbfRowDtoValidation().apply(mapper, rowNum);
					if(CollectionUtils.isNotEmpty(constraintViolations)) {
						if( !isIgnoreException()) {
							throw new ConstraintViolationException(String.format("第%s行数据验证失败", rowNum + 1), constraintViolations);
						} else {
							result.addConstraintViolation(new RowConstraintViolation<M>(rowNum, constraintViolations));
						}
					}
					// 调用消费函数 
					consumer.accept(mapper, rowNum);
					// 记录完成进度
					getProcess().accept(this.getUuid(), BigDecimal.valueOf(rowNum).divide(count, 2, BigDecimal.ROUND_HALF_UP).floatValue());
				}
				// 获取下一行数据
				row = dbf.nextRow();
				rowNum += 1;
			} while (row != null);
			// 操作完成
			getFinish().accept(this.getUuid(), 100f);	
		}
		return result;
	}
	
	@Override
	public ConstraintViolationResult<M> input(DbfReader dbf, 
			Function<DbfHeader, List<Map<String, String>>> header,
			BiFunction<DbfRow, Integer, RowMapper<DbfRow,M>> rowMapper,
			BiFunction<RowMapper<DbfRow, M>, Integer, Set<ConstraintViolation<M>>> validation, 
			BiConsumer<RowMapper<DbfRow, M>, Integer> consumer) throws Exception {
		
		ConstraintViolationResult<M> result = new ConstraintViolationResult<M>();
		if(dbf.getRecordCount() > 0 ) {
			// 
			DbfRow row = null;
			int rowNum = -1;
			BigDecimal count = BigDecimal.valueOf(dbf.getRecordCount());
			do {
				// 数据不为空
				if(row != null) {
					// 数据转换
					RowMapper<DbfRow, M> mapper = rowMapper.apply(row, rowNum);
					// 执行验证
					Set<ConstraintViolation<M>> constraintViolations = validation.apply(mapper, rowNum);
					if(CollectionUtils.isNotEmpty(constraintViolations)) {
						if( !isIgnoreException()) {
							throw new ConstraintViolationException(String.format("第%s行数据验证失败", rowNum + 1), constraintViolations);
						} else {
							result.addConstraintViolation(new RowConstraintViolation<M>(rowNum, constraintViolations));
						}
					}
					// 调用消费函数 
					consumer.accept(mapper, rowNum);
					// 记录完成进度
					getProcess().accept(this.getUuid(), BigDecimal.valueOf(rowNum).divide(count, 2, BigDecimal.ROUND_HALF_UP).floatValue());
				}
				// 获取下一行数据
				row = dbf.nextRow();
				rowNum += 1;
			} while (row != null);
			// 操作完成
			getFinish().accept(this.getUuid(), 100f);	
		}
		return result;
	}

	public BiFunction<RowWrapper<DbfRow>, Integer, RowMapper<DbfRow, RowMap>> getDbfRowmapMapper() {
		return dbfRowmapMapper;
	}

	public void setDbfRowmapMapper(BiFunction<RowWrapper<DbfRow>, Integer, RowMapper<DbfRow, RowMap>> dbfRowmapMapper) {
		this.dbfRowmapMapper = dbfRowmapMapper;
	}

	public BiFunction<RowMapper<DbfRow, RowMap>, Integer, Set<ConstraintViolation<RowMap>>> getDbfRowmapValidation() {
		return dbfRowmapValidation;
	}

	public void setDbfRowmapValidation(
			BiFunction<RowMapper<DbfRow, RowMap>, Integer, Set<ConstraintViolation<RowMap>>> dbfRowmapValidation) {
		this.dbfRowmapValidation = dbfRowmapValidation;
	}

	public BiFunction<RowWrapper<DbfRow>, Integer, RowMapper<DbfRow, M>> getDbfRowDtoMapper() {
		return dbfRowDtoMapper;
	}

	public void setDbfRowDtoMapper(BiFunction<RowWrapper<DbfRow>, Integer, RowMapper<DbfRow, M>> dbfRowDtoMapper) {
		this.dbfRowDtoMapper = dbfRowDtoMapper;
	}

	public BiFunction<RowMapper<DbfRow, M>, Integer, Set<ConstraintViolation<M>>> getDbfRowDtoValidation() {
		return dbfRowDtoValidation;
	}

	public void setDbfRowDtoValidation(
			BiFunction<RowMapper<DbfRow, M>, Integer, Set<ConstraintViolation<M>>> dbfRowDtoValidation) {
		this.dbfRowDtoValidation = dbfRowDtoValidation;
	}

	public BiFunction<RowWrapper<DbfRow>, Integer, Set<ConstraintViolation<M>>> getDbfRowValidation() {
		return dbfRowValidation;
	}

	public void setDbfRowValidation(BiFunction<RowWrapper<DbfRow>, Integer, Set<ConstraintViolation<M>>> dbfRowValidation) {
		this.dbfRowValidation = dbfRowValidation;
	}
	
}
