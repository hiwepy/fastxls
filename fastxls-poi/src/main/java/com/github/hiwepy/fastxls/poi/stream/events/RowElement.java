/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.poi.stream.events;

import org.apache.poi.ss.usermodel.Row;

public interface RowElement extends XLSEvent{
 
	public Row getRow();
  
}
