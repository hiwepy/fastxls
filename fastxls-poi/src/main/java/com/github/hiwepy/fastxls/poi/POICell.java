package com.github.hiwepy.fastxls.poi;

import com.github.hiwepy.fastxls.core.Cell;

public class POICell implements Cell {

	protected org.apache.poi.ss.usermodel.Cell target;
	
	public POICell(org.apache.poi.ss.usermodel.Cell cell){
		this.target = cell;
	}
	
	@Override
	public int getRowIndex() {
		return this.target.getRowIndex();
	}

	@Override
	public int getColumnIndex() {
		return this.target.getColumnIndex();
	}
	
	public static POICell wrap(org.apache.poi.ss.usermodel.Cell cell) {
		return new POICell(cell);
	}

}
