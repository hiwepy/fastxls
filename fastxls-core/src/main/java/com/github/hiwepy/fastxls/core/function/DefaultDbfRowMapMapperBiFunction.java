/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.core.function;

import java.util.Map;
import java.util.function.BiFunction;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.jamel.dbf.structure.DbfRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.hiwepy.fastxls.core.model.ColumnWrapper;
import com.github.hiwepy.fastxls.core.model.RowMap;
import com.github.hiwepy.fastxls.core.model.RowMapMapper;
import com.github.hiwepy.fastxls.core.model.RowMapper;
import com.github.hiwepy.fastxls.core.model.RowWrapper;

public class DefaultDbfRowMapMapperBiFunction implements BiFunction<RowWrapper<DbfRow>, Integer, RowMapper<DbfRow, RowMap>> {
	
	protected Logger LOG = LoggerFactory.getLogger(getClass());
	protected final BiFunction<ColumnWrapper<DbfRow>, String, String> transformer;

	public DefaultDbfRowMapMapperBiFunction(
			BiFunction<ColumnWrapper<DbfRow>, String, String> transformer) {
		this.transformer = transformer;
	}

	@Override
	public RowMapper<DbfRow, RowMap> apply(RowWrapper<DbfRow> wrapper, Integer rowNum) {
		
		RowMap rowMap = new RowMap();
		// 数据行
		DbfRow row = wrapper.getRow();
		
		// 循环列信息
		for (Map<String, String> columnMap : wrapper.getColumns()) {
			// 列名称
			String column = StringUtils.lowerCase(MapUtils.getString(columnMap, "column"));
			// 列说明
			String label = 	MapUtils.getString(columnMap, "label", "");
			// 数据值
			String value = row.getString(label);
			// 添加行数据
			rowMap.put(column, value);
		}
		
		// 循环处理数据，按需进行数据转换
		for (Map<String, String> columnMap : wrapper.getColumns()) {
			// 列名称
			String column = StringUtils.lowerCase(MapUtils.getString(columnMap, "column"));
			// 列索引
			int cellnum = MapUtils.getIntValue(columnMap, "cellnum");
			// 列转换配置
			String transSQL = MapUtils.getString(columnMap, "trans");
			// 尝试进行数据转换
			if(null != getTransformer() && StringUtils.isNotBlank(transSQL)) {
				try {
					// 数据值
					String value = MapUtils.getString(rowMap, column);
					ColumnWrapper<DbfRow> colWrapper = new ColumnWrapper<DbfRow>(wrapper.getRow(), columnMap, rowMap, rowNum, cellnum);
					value = getTransformer().apply(colWrapper, value);
					// 添加行数据
					rowMap.put(column, value);
				} catch (Exception e) {
				}
			}
		}
		
		return new RowMapMapper<DbfRow>(row, wrapper.getColumns(),  rowNum, rowMap, false);
	}
	
	public BiFunction<ColumnWrapper<DbfRow>, String, String> getTransformer() {
		return transformer;
	}

}
