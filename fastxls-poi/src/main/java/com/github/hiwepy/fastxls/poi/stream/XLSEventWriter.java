/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.poi.stream;

import com.github.hiwepy.fastxls.poi.stream.events.XLSEvent;

public interface XLSEventWriter extends XLSEventConsumer {
	
    public void add(XLSEvent event) throws XLSStreamException;

    public void add(XLSEventReader reader) throws XLSStreamException;

    public void close() throws XLSStreamException;

    public void flush() throws XLSStreamException;

    public String getPrefix(String uri) throws XLSStreamException;

    public void setPrefix(String prefix, String uri) throws XLSStreamException;
    
}
