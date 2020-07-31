/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.core.utils;

import java.util.Iterator;
import java.util.List;

import com.github.hiwepy.fastxls.core.model.CellModel;
import com.github.hiwepy.fastxls.core.model.RowModel;

public class WorkbookUtils {

	public static <T extends CellModel> int getLastNumOfRow(List<RowModel<T>> data) {
		int num = 0;
		for (RowModel<T> map : data) {
			num = Math.max(num,map.getRowNum());
		}
		return num;
	}
	
	public static <T extends CellModel> int getLastNumOfCell(RowModel<T> rowModel) {
		Iterator<String> iterator = rowModel.keySet().iterator();
		int num = 0;
		while (iterator.hasNext()) {
			T item = rowModel.get(iterator.next());
			num = Math.max(num,item.getColumnIndex());
		}
		return num;
	}
	
	public static int getSheetCount(int count, int rowLimit){
		// 得到当前sheet 的总行数，并计算当前sheet需要多少个线程进行导入操作
		int sheet_num = 1;
		if(count > rowLimit){
			sheet_num = (count - count%rowLimit)/rowLimit;
			if(count%rowLimit>0){
				sheet_num = sheet_num+1;
			}
		}
		return sheet_num;
	}
	
	
}
