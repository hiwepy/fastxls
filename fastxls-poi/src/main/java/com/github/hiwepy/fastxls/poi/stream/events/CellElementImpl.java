/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.poi.stream.events;

import org.apache.poi.ss.usermodel.Cell;

public class CellElementImpl extends XLSEventImpl implements CellElement {

	public CellElementImpl(Cell cell) {
		super(CELL_ELEMENT);
	}

	@Override
	public String getText() {
		return null;
	}

}
