/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.poi.stream.events;

public interface XLSEvent extends XLSConstants {

	  /**
	   * Returns an integer code for this event.
	   * @see #CELL_ELEMENT
	   * @see #CHARACTERS
	   * @see #COMMENT
	   * @see #ROW_ELEMENT
	   * @see #SHEET_ELEMENT
	   */
	  public int getEventType();
	 
	  public boolean isSheet();
	  
	  public boolean isRow();

	  public boolean isCell();
	  
	  public boolean isComment();
	  
	  public boolean isCharacters();
	  
	  public CellElement asCellElement();
	
	  public Characters asCharacters();
	  
	  public Comment asComment();
	  
	  public RowElement asRowElement();
	  
	  public SheetElement asSheetElement();
}
