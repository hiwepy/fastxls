/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.core.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.Builder;

public class DataUpdatingSQLBuilder implements Builder<String> {

	/**
	 *数据表名称 
	 */
	private String tableName;
	/**
	 *主键名称 
	 */
	private String primarykey;
	/**
	 * 数据列信息,当数据导出的数据为CachedRowSet、List<Map<String,String>> 时，需要用到
	 */
	private List<Map<String, String>> columnList;
	
	@Override
	public String build() {
		
		//生成动态SQL
		StringBuilder builder = new StringBuilder(" UPDATE ").append(this.getTableName()).append(" t SET ");
		StringBuilder prefix = new StringBuilder();
		StringBuilder suffix = new StringBuilder();
		List<String> prefixs = new ArrayList<String>();
		List<String> suffixs = new ArrayList<String>();
		// 遍历字段信息
		for (Map<String,String> column :  this.getColumnList()) {
			
			suffix.delete(0, suffix.length());
			prefix.delete(0, prefix.length());
			
			// 匹配字段
			String column_name = column.get("column").toLowerCase();
			//判断是否是主键
			boolean primary = MapUtils.getBoolean(column, "primary");
			if(primary){
				suffix.append(" t.").append(column_name).append(" = '#{").append(column_name).append("}'");
				suffixs.add(suffix.toString());
			}else{
				prefix.append(" t.").append(column_name).append(" = '#{").append(column_name).append("}'");
				prefixs.add(prefix.toString());
			}
		}
		builder.append(StringUtils.join(prefixs, " , "));
		builder.append(" WHERE ");
		builder.append(StringUtils.join(suffixs, " and "));
		
		return builder.toString();
	}

	protected String getTableName() {
		return tableName;
	}
	
	public String getPrimarykey() {
		return primarykey;
	}

	protected List<Map<String, String>> getColumnList() {
		return columnList;
	}
	
}
