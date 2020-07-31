/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.core.utils;


import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.hiwepy.fastxls.core.FastxlsProperties;

public class ResourceUtils {
	
	protected static Logger log = LoggerFactory.getLogger(ResourceUtils.class);	

    /**
     * Get this resource from the location specified in docx4j.properties;
     * if none is specified, fallback to the default specified
     * 
     * @param propName
     * @param defaultPath
     * @return
     * @throws java.io.IOException
     * @since 3.2.0
     */
    public static java.io.InputStream getResourceViaProperty(String propName, String defaultPath) throws java.io.IOException
    {
    	String resourcePath= FastxlsProperties.getProperty(propName, defaultPath);
    	log.debug(propName + " resolved to " + resourcePath);
    	InputStream resourceIS = null;
    	try {
    		resourceIS = getResource(resourcePath);
    	} catch (IOException ioe) {
    		log.warn(resourcePath + ": " + ioe.getMessage());
    	}
    	if (resourceIS==null) {
    		log.warn("Property " + propName + " resolved to missing resource " + resourcePath + "; using " +  defaultPath);
    		return getResource(defaultPath);
    	} else {
    		return resourceIS;
    	}
    }
    
    /**
     * Use ClassLoader.getResource to get the named resource
     * @param filename
     * @return
     * @throws java.io.IOException if resource not found
     */
    public static java.io.InputStream getResource(String filename) throws java.io.IOException
    {
    	log.debug("Attempting to load: " + filename);
    	
        // Try to load resource from jar.  
        ClassLoader loader = ResourceUtils.class.getClassLoader();
        
        java.net.URL url = loader.getResource(filename);
        
		if (url == null
				&& System.getProperty("java.vendor").contains("Android")) {
			url = loader.getResource("assets/" + filename);
			if (url!=null) System.out.println("found " + filename + " in assets");
		}

        if (url == null) {
        	// this is convenient when trying to load a resource from an arbitrary path,
        	// since in IKVM you can setContextClassLoader to a URLClassLoader,
        	// which in turn can be configured at run time to search some dir.
        	log.debug("Trying Thread.currentThread().getContextClassLoader()");
        	loader = Thread.currentThread().getContextClassLoader();
        	url = loader.getResource(filename);
        }
        
        if (url == null) {
        	if (filename.contains("jaxb.properties")){
        		log.debug("Not using MOXy, since no resource: " + filename);        		
        	} else {
        		log.warn("Couldn't get resource: " + filename);
        	}
        	throw new IOException(filename + " not found via classloader.");
        }
        
        // Get the jar file
//      JarURLConnection conn = (JarURLConnection) url.openConnection();
        java.io.InputStream is = url.openConnection().getInputStream();
        return is;
    }
	

}
