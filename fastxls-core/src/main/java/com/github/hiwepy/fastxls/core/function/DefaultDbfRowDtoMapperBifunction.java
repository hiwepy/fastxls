/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.core.function;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.jamel.dbf.structure.DbfRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ResolvableType;
import org.springframework.util.ReflectionUtils;

import com.github.hiwepy.fastxls.core.JdbcType;
import com.github.hiwepy.fastxls.core.model.ColumnWrapper;
import com.github.hiwepy.fastxls.core.model.RowMap;
import com.github.hiwepy.fastxls.core.model.RowMapper;
import com.github.hiwepy.fastxls.core.model.RowWrapper;
import com.github.hiwepy.fastxls.core.utils.ByteBuddyUtils;

@SuppressWarnings("unchecked")
public class DefaultDbfRowDtoMapperBifunction<M extends RowMap> implements BiFunction<RowWrapper<DbfRow>, Integer, RowMapper<DbfRow, M>> {
	
	protected Logger LOG = LoggerFactory.getLogger(getClass());
	protected final BiFunction<ColumnWrapper<DbfRow>, String, String> transformer;
	protected volatile Class<M> clazz;
	
	public DefaultDbfRowDtoMapperBifunction(
			BiFunction<ColumnWrapper<DbfRow>, String, String> transformer) {
		this.transformer = transformer;
	}
	
	@Override
	public RowMapper<DbfRow, M> apply(RowWrapper<DbfRow> wrapper, Integer rowNum) {
		
		try {
			
			// 构建动态Dto对象
			if (null == clazz) {
				clazz = ByteBuddyUtils.dynamicDtoClass((Class<M>) ResolvableType.forClass(this.getClass()).getGeneric(4).resolve(), 
						wrapper.getColumns());
			}
			// 数据行
			DbfRow row = wrapper.getRow();
			// 构建模型对象
			M target = clazz.newInstance();
			// 循环处理数据，按需进行数据转换
			for (Map<String, String> columnMap : wrapper.getColumns()) {
				// 列索引
				int cellnum = MapUtils.getIntValue(columnMap, "cellnum");
				// 列名称
				String column = StringUtils.lowerCase(MapUtils.getString(columnMap, "column"));
				// 列说明
				String label = 	MapUtils.getString(columnMap, "label", "");
				// 列转换配置
				String transSQL = MapUtils.getString(columnMap, "trans");
				// 数据值
				String value = row.getString(label);
				// 尝试进行数据转换
				if(null != getTransformer() && StringUtils.isNotBlank(transSQL)) {
					try {
						ColumnWrapper<DbfRow> colWrapper = new ColumnWrapper<DbfRow>(wrapper.getRow(), columnMap, new HashMap<>(), rowNum, cellnum);
						value = getTransformer().apply(colWrapper, value);
					} catch (Exception e) {
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
					case TIMESTAMP:
					default:{
						ReflectionUtils.setField(ReflectionUtils.findField(clazz, column), target, value);
					};break;
				}
			}
			
			return new RowMapper<DbfRow, M>(false, row, wrapper.getColumns(), rowNum, target);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public BiFunction<ColumnWrapper<DbfRow>, String, String> getTransformer() {
		return transformer;
	}
 
	
}
