/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.core.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.github.hiwepy.fastxls.core.model.CellModel;
import com.github.hiwepy.fastxls.core.model.ColumnModel;
import com.github.hiwepy.fastxls.core.model.RowModel;

public class XlsDataUtils {

	public static <T extends CellModel> int getLastNumOfRow(List<RowModel<T>> data) {
		int num = 0;
		for (RowModel<T> map : data) {
			num = Math.max(num, map.getRowNum());
		}
		return num;
	}

	public static <T extends CellModel> int getLastNumOfCell(RowModel<T> rowModel) {
		Iterator<String> iterator = rowModel.keySet().iterator();
		int num = 0;
		while (iterator.hasNext()) {
			T item = rowModel.get(iterator.next());
			num = Math.max(num, item.getColumnIndex());
		}
		return num;
	}

	/**
	 * 
	 * 获得所有指段配置中指定的字段信息
	 * 
	 * @param columnsList
	 * @param column_name
	 * @return
	 */
	public static Map<String, String> getColumnMap(List<Map<String, String>> columnsList, String column_name) {
		Map<String, String> columnMap = null;
		for (Map<String, String> map : columnsList) {
			if (map.containsKey(column_name)) {
				columnMap = map;
				break;
			}
		}
		return columnMap == null ? new HashMap<String, String>() : columnMap;
	}

	/**
	 * 
	 * 获得匹配结果中是必填的字段集合
	 * 
	 * @param columnsList
	 * @param matchColumns
	 * @return
	 */
	public static List<Map<String, String>> getRequiredColums(List<Map<String, String>> columnsList,
			List<ColumnModel> matchColumns) {
		List<Map<String, String>> requiredColumList = new ArrayList<Map<String, String>>();
		for (int i = 0; i < matchColumns.size(); i++) {
			ColumnModel matchColumn = matchColumns.get(i);
			// 遍历字段信息
			for (Map<String, String> column : columnsList) {
				// 匹配字段
				if (column.get("column_name").equalsIgnoreCase(matchColumn.getName())) {
					// 判断此字段是否是为必填项
					String requisite = column.get("requisite");
					// 如果此字段为必选项,则验证此字段对应的列是否为空
					if (requisite != null && "1".equalsIgnoreCase(requisite)) {
						// 记录匹配列索引
						column.put("index", matchColumn.getIndex());
						requiredColumList.add(column);
					}
				}
			}
		}
		return requiredColumList;
	}

	/**
	 * 获得配置的字段集合中是必填的字段
	 * 
	 * @param columnsList
	 * @return
	 */
	public static Map<String, String> getRequiredColums(List<Map<String, String>> columnsList) {
		Map<String, String> requiredColumMap = new HashMap<String, String>();
		// 遍历字段信息
		for (Map<String, String> column : columnsList) {
			// 判断此字段是否是为必填项
			String requisite = column.get("requisite");
			// 如果此字段为必选项,则验证此字段对应的列是否为空
			if (requisite != null && "1".equalsIgnoreCase(requisite)) {
				requiredColumMap.put(column.get("column_name"), column.get("comments"));
			}
		}
		return requiredColumMap;
	}

	/**
	 * 获得匹配结果中是唯一约束的字段集合
	 * 
	 * @param columnsList
	 * @param matchColumns
	 * @return
	 */
	public static List<Map<String, String>> getUniqueColums(List<Map<String, String>> columnsList,
			List<ColumnModel> matchColumns) {
		List<Map<String, String>> uniqueColumList = new ArrayList<Map<String, String>>();
		for (int i = 0; i < matchColumns.size(); i++) {
			ColumnModel matchColumn = matchColumns.get(i);
			// 遍历字段信息
			for (Map<String, String> column : columnsList) {
				// 匹配字段
				if (column.get("column_name").equalsIgnoreCase(matchColumn.getName())) {
					// 判断此字段是否是为必填项
					String unique = column.get("unique");
					// 如果此字段为必选项,则验证此字段对应的列是否为空
					if (unique != null && "1".equalsIgnoreCase(unique)) {
						// 记录匹配列索引
						column.put("index", matchColumn.getIndex());
						uniqueColumList.add(column);
					}
				}
			}
		}
		return uniqueColumList;
	}

	/**
	 * 
	 * 获得配置的字段集合中是唯一约束的字段
	 * 
	 * @param columnsList
	 * @return
	 */
	public static Map<String, String> getUniqueColums(List<Map<String, String>> columnsList) {
		Map<String, String> uniqueColumMap = new HashMap<String, String>();
		// 遍历字段信息
		for (Map<String, String> column : columnsList) {
			// 判断此字段是否是为必填项
			String unique = column.get("unique");
			// 如果此字段为必选项,则验证此字段对应的列是否为空
			if (unique != null && "1".equalsIgnoreCase(unique)) {
				uniqueColumMap.put(column.get("column_name"), column.get("comments"));
			}
		}
		return uniqueColumMap;
	}

	/**
	 * 判断某个字段是否匹配了
	 * 
	 * @param columnsList
	 * @param column_name
	 * @return
	 */
	public static boolean isMatch(List<Map<String, String>> columnsList, String column_name) {
		return !getColumnMap(columnsList, column_name).isEmpty();
	}

}
