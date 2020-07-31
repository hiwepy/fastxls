/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.poi.stream;

import com.github.hiwepy.fastxls.poi.stream.events.CellElement;
import com.github.hiwepy.fastxls.poi.stream.events.Characters;
import com.github.hiwepy.fastxls.poi.stream.events.Comment;
import com.github.hiwepy.fastxls.poi.stream.events.RowElement;
import com.github.hiwepy.fastxls.poi.stream.events.SheetElement;

public abstract class XLSEventFactory {

	protected XLSEventFactory() {
	}

	/**
	 * Create a new instance of the factory
	 * 
	 * @throws FactoryConfigurationError
	 *             if an instance of this factory cannot be loaded
	 */
	public static XLSEventFactory newFactory() throws FactoryConfigurationError {
		return (XLSEventFactory) FactoryFinder.find(
				"com.github.hiwepy.fastxls.poi.factory.XLSEventFactory",
				"com.github.hiwepy.fastxls.poi.factory.XLSEventFactoryImpl");
	}

	/**
	 * Create a new instance of the factory. If the classLoader argument is
	 * null, then the ContextClassLoader is used.
	 * 
	 * Note that this is a new method that replaces the deprecated
	 * newInstance(String factoryId, ClassLoader classLoader) method. No changes
	 * in behavior are defined by this replacement method relative to the
	 * deprecated method.
	 * 
	 * @param factoryId  Name of the factory to find, same as a property name
	 * @param classLoader classLoader to use
	 * @return the factory implementation
	 * @throws FactoryConfigurationError if an instance of this factory cannot be loaded
	 */
	public static XLSEventFactory newFactory(String factoryId,
			ClassLoader classLoader) throws FactoryConfigurationError {
		try {
			// do not fallback if given classloader can't find the class, throw
			// exception
			return (XLSEventFactory) FactoryFinder.newInstance(factoryId, classLoader, false);
		} catch (FactoryFinder.ConfigurationError e) {
			throw new FactoryConfigurationError(e.getException(), e.getMessage());
		}
	}
	
	public abstract CellElement createCellElement(String namespaceURI);
	
	public abstract Characters createCharacters(String content);
	
	public abstract Comment createComment(String namespaceURI);
	
	public abstract RowElement createRowElement(String namespaceURI);
	
	public abstract SheetElement createSheetElement(String namespaceURI);
	 
	  

}
