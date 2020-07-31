/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.core.function;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ResolvableType;
import org.springframework.util.ReflectionUtils;

import com.github.hiwepy.fastxls.core.JdbcType;
import com.github.hiwepy.fastxls.core.WorkbookReader;
import com.github.hiwepy.fastxls.core.model.ColumnWrapper;
import com.github.hiwepy.fastxls.core.model.RowMap;
import com.github.hiwepy.fastxls.core.model.RowMapper;
import com.github.hiwepy.fastxls.core.model.RowWrapper;
import com.github.hiwepy.fastxls.core.utils.ByteBuddyUtils;

/**
 * bytebuddy 动态构建行对象Dto 并 利用反射进行赋值
 * @author <a href="https://github.com/vindell">wandl</a>
 * @since 2019-11-30
 * @param <W>
 * @param <S>
 * @param <R>
 * @param <C>
 * @param <M>
 */
@SuppressWarnings("unchecked")
public class DefaultRowDtoMapperBiFunction<W, S, R, C, M extends RowMap> implements BiFunction<RowWrapper<R>, Integer, RowMapper<R, M>> {
	
	protected Logger LOG = LoggerFactory.getLogger(getClass());
	protected final WorkbookReader<W, S, R, C> workbookReader;
	protected final BiFunction<ColumnWrapper<C>, String, String> transformer;
	protected volatile Class<M> clazz;
	
	public DefaultRowDtoMapperBiFunction(WorkbookReader<W, S, R, C> workbookReader,
			BiFunction<ColumnWrapper<C>, String, String> transformer) {
		this.workbookReader = workbookReader;
		this.transformer = transformer;
	}
	
	@Override
	public RowMapper<R, M> apply(RowWrapper<R> wrapper, Integer rowNum) {
		try {
			// 构建动态Dto对象
			if (null == clazz) {
				clazz = ByteBuddyUtils.dynamicDtoClass((Class<M>) ResolvableType.forClass(this.getClass()).getGeneric(4).resolve(), 
						wrapper.getColumns());
			}
			// 构建模型对象
			M target = clazz.newInstance();
			// 循环处理数据，按需进行数据转换
			for (Map<String, String> columnMap : wrapper.getColumns()) {
				// 列名称
				String column = StringUtils.lowerCase(MapUtils.getString(columnMap, "column"));
				// 列索引
				int cellnum = MapUtils.getIntValue(columnMap, "cellnum");
				// 数据值
				String value = getWorkbookReader().getCellValue(wrapper.getRow(), cellnum);;
				// 列转换配置
				String transSQL = MapUtils.getString(columnMap, "trans");
				// 尝试进行数据转换
				if(null != getTransformer() && StringUtils.isNotBlank(transSQL)) {
					try {
						C columnObj = getWorkbookReader().getCell(wrapper.getRow(), cellnum);
						ColumnWrapper<C> colWrapper = new ColumnWrapper<C>(columnObj, columnMap, new HashMap<>(), rowNum, cellnum);
						value = getTransformer().apply(colWrapper, value);
					} catch (IOException e) {
					}
				}
				// 字段类型
				String type = MapUtils.getString(columnMap, "type");
				JdbcType jdbcType = JdbcType.fromString(type);
				switch (jdbcType) {
					case TINYINT:
					case SMALLINT:{
						ReflectionUtils.setField(ReflectionUtils.findField(clazz, column), target, Short.parseShort(value));
					};break;
					case INTEGER:{
						ReflectionUtils.setField(ReflectionUtils.findField(clazz, column), target, Integer.parseInt(value));
					};break;
					case BIGINT:{
						ReflectionUtils.setField(ReflectionUtils.findField(clazz, column), target, Long.parseLong(value));
					};break;
					case FLOAT:
					case REAL:{
						ReflectionUtils.setField(ReflectionUtils.findField(clazz, column), target, Float.parseFloat(value));
					};break;
					case DOUBLE:{
						ReflectionUtils.setField(ReflectionUtils.findField(clazz, column), target, Double.parseDouble(value));
					};break;
					case NUMERIC:
					case DECIMAL:{
						ReflectionUtils.setField(ReflectionUtils.findField(clazz, column), target, new BigDecimal(value));
					};break;
					case DATE:
					case TIME:
					case TIMESTAMP:{
						ReflectionUtils.setField(ReflectionUtils.findField(clazz, column), target, DateUtils.parseDate(value));
					};break;
					default:{
						ReflectionUtils.setField(ReflectionUtils.findField(clazz, column), target, value);
					};break;
				}
			}
			return new RowMapper<R, M>(false, wrapper.getRow(), wrapper.getColumns(), rowNum, target);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public WorkbookReader<W, S, R, C> getWorkbookReader() {
		return workbookReader;
	}
	
	public BiFunction<ColumnWrapper<C>, String, String> getTransformer() {
		return transformer;
	}
	
}
