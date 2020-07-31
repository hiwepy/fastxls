/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.poi.stream.events;

import org.apache.poi.ss.usermodel.Sheet;

public interface SheetElement extends XLSEvent{
 
	public Sheet getSheet();
  
}
