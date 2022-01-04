package com.github.hiwepy.fastxls.core.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.github.hiwepy.fastxls.core.Suffix;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Excel 电子表格 抽象化数据 对象
 * @param <T>
 */
@SuppressWarnings("serial")
@Getter
@Setter
@ToString
public class WorkBookModel<T extends CellModel> implements Serializable {

	/**
	 * 抽象化的excel表格对象 String(sheet名称),SheetModel<T> (sheet数据)
	 */
	private Map<String, SheetModel<T>> sheets = new HashMap<String, SheetModel<T>>(0);

	private Suffix suffix = Suffix.XLS;

	public WorkBookModel() {
		this.sheets.clear();
	}

	public WorkBookModel(Suffix suffix) {
		this.suffix = suffix;
		this.sheets.clear();
	}

	public WorkBookModel(Suffix suffix, Map<String, SheetModel<T>> sheets) {
		this.suffix = suffix;
		this.sheets.clear();
		this.sheets.putAll(sheets);
	}

	public int getNumberOfSheets() {
		return (sheets.size() > 0) ? sheets.size() : 0;
	}

	/**
	 * 得到第一个工作表数据对象
	 */
	public Entry<String, SheetModel<T>> getFirstSheet() {
		return getSheet(0);
	}

	/**
	 * 得到指定索引的工作表对象
	 */
	public Entry<String, SheetModel<T>> getSheet(int index) {
		Entry<String, SheetModel<T>> entry = null;
		if (sheets.size() > 0) {
			Iterator<Entry<String, SheetModel<T>>> ite = sheets.entrySet().iterator();
			int i = 0;
			while (ite.hasNext()) {
				if (index == i) {
					entry = ite.next();
					break;
				}
				i++;
			}
		}
		return entry;
	}

	/**
	 * 设置指定名称的工作表数据对象
	 */
	public void setSheet(String sheetName, SheetModel<T> sheetModel) {
		sheets.remove(sheetName);
		sheets.put(sheetName, sheetModel);
	}

	/**
	 * 得到指定名称的工作表数据对象
	 */
	public SheetModel<T> getSheet(String sheetName) {
		return sheets.get(sheetName);
	}

	/**
	 * 得到指定索引的工作表对象的指定行索引对应的行对象
	 */
	public RowModel<T> getRow(int sheetIndex, int rownum) {
		RowModel<T> row = null;
		if (sheets.size() > 0) {
			Iterator<Entry<String, SheetModel<T>>> ite = sheets.entrySet().iterator();
			int i = 0;
			while (ite.hasNext()) {
				if (sheetIndex == i) {
					row = ite.next().getValue().getRow(rownum);
					break;
				}
				i++;
			}
		}
		return row;
	}

	/**
	 * 设置指定索引的工作表对象的指定行索引对应的行对象
	 */
	public void setRow(int sheetIndex, int rowIndex, RowModel<T> element) {
		if (sheets.size() > 0) {
			Iterator<Entry<String, SheetModel<T>>> ite = sheets.entrySet().iterator();
			int i = 0;
			while (ite.hasNext()) {
				if (sheetIndex == i) {
					SheetModel<T> sheet = ite.next().getValue();
					sheet.setRow(rowIndex, element);
					break;
				}
				i++;
			}
		}
	}
	 

}
