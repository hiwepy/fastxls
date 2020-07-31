/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.poi.stream.events;

public interface CellElement extends XLSEvent{
 
	/**
	 * Return the string data of the comment, returns empty string if it
	 * does not exist
	 */
	public String getText();
  
}
