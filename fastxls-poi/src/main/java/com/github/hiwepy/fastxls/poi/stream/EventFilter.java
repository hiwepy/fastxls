/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.poi.stream;

import com.github.hiwepy.fastxls.poi.stream.events.XLSEvent;

public interface EventFilter {
	
	 public boolean accept(XLSEvent event);
	 
}
