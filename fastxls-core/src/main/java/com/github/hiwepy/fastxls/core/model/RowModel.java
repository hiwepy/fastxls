package com.github.hiwepy.fastxls.core.model;

import java.io.Serializable;
import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 */
@SuppressWarnings("serial")
@Setter
@Getter
@ToString
public class RowModel<T extends CellModel>  extends HashMap<String, T>  implements Serializable{
	
	/**
	 * 单元格是否是标题- true|false
	 */
	private boolean title = false;
	/**
	 * 单元行索引
	 */
	private int rowNum = -1;
	/**
	 * 单元行高度
	 */
	private short height = 20;
	/**
	 * 单元行合并信息
	 */
	private MergedRegionModel cellRangeAddress;
	

	public RowModel(int rowNum){
		this.rowNum = rowNum;
	}
	
	public RowModel(MergedRegionModel cellRangeAddress, T cell){
		this.cellRangeAddress = cellRangeAddress;
		this.put(String.valueOf(cellRangeAddress.getFirstCol()),cell);
	}
	
	public RowModel(int rowNum,int height){
		this.rowNum = rowNum;
		this.height = (short) height;
	}

	public int getRowNum() {
		return rowNum;
	}

	public void setCell(int cellIndex,T cell) {
		this.put(String.valueOf(cellIndex),cell);
	}
	
	public T getCell(int cellIndex) {
		return this.get(String.valueOf(cellIndex));
	}

	public short getHeight() {
	
		return height;
	}

	public void setHeight(short height) {
	
		this.height = height;
	}

	public void setRowNum(int rowNum) {
	
		this.rowNum = rowNum;
	}

	public boolean isTitle() {
	
		return title;
	}

	public void setTitle(boolean title) {
	
		this.title = title;
	}

	public MergedRegionModel getCellRangeAddress() {
		return cellRangeAddress;
	}

	public void setCellRangeAddress(MergedRegionModel cellRangeAddress) {
		this.cellRangeAddress = cellRangeAddress;
	}
	
	
	
}
