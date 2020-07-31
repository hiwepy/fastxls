/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.core.builder;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.Builder;
import org.springframework.util.StringUtils;


public class DataImportSQLBuilder implements Builder<String> {

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

	public DataImportSQLBuilder tableName(String tableName){
		this.tableName = tableName;
		return this;
	}
	
	public DataImportSQLBuilder primarykey(String primarykey){
		this.primarykey = primarykey;
		return this;
	}
	
	public DataImportSQLBuilder sheetName(List<Map<String, String>> columnList){
		this.columnList = columnList;
		return this;
	}
	
	@Override
	public String build() {

		// 生成动态SQL
		StringBuilder prefix = new StringBuilder("INSERT INTO ").append( this.getTableName() ).append("(");
		StringBuilder suffix = new StringBuilder(" VALUES (");
		// 主键不为空
		if(StringUtils.hasText(this.getPrimarykey())){
			prefix.append(this.getPrimarykey()).append(",");
			// 使用占位符取值
			suffix.append(" '#{").append(this.getPrimarykey()).append("}' ,");
		}
		// 遍历字段信息
		for (Map<String,String> column :  this.getColumnList()) {
			// 匹配字段
			String column_name = column.get("column").toLowerCase();
			// 判断当前字段是否需要动态生成
			//boolean auto_key = MapUtils.getBoolean(column, "auto_key");
			
			prefix.append(column_name).append(",");
			suffix.append(" '#{").append(column_name).append("}' ,");
			//如果做了映射表示此处需要插入ID的同时，也要插入原数据名称，则进一步处理，否则无操作
			String name_mapper = column.get("mapper");
			if(name_mapper!=null){
				prefix.append(name_mapper + ",");
				suffix.append(" '#{").append(name_mapper).append("}' ,");
			}
		}
		prefix = prefix.deleteCharAt(prefix.length() - 1).append(") ");
		suffix = suffix.deleteCharAt(suffix.length() - 1).append(")");
		return prefix.toString() + prefix.toString();
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
