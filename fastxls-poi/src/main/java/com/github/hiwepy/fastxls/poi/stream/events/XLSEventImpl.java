/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.poi.stream.events;

public abstract class XLSEventImpl implements XLSEvent {

	 private int fEventType;
	 
	 /**
     * Constructor.
     */
	XLSEventImpl(final int eventType) {
        fEventType = eventType;
    }
	
	/**
     * @see com.github.hiwepy.fastxls.poi.stream.events.XLSEvent#getEventType()
     */
    public final int getEventType() {
        return fEventType;
    }

	@Override
	public final CellElement asCellElement() {
		return (CellElement) this;
	}

	@Override
	public final Characters asCharacters() {
		return (Characters) this;
	}

	@Override
	public final Comment asComment() {
		return (Comment) this;
	}

	@Override
	public final RowElement asRowElement() {
		return (RowElement) this;
	}

	@Override
	public final SheetElement asSheetElement() {
		return (SheetElement) this;
	}

	@Override
	public final boolean isCell() {
		 return CELL_ELEMENT == fEventType;
	}

	@Override
	public final boolean isCharacters() {
		 return CHARACTERS == fEventType;
	}

	@Override
	public final boolean isComment() {
		 return COMMENT == fEventType;
	}

	@Override
	public final boolean isRow() {
		 return ROW_ELEMENT == fEventType;
	}

	@Override
	public final boolean isSheet() {
		 return SHEET_ELEMENT == fEventType;
	}
	 
}
