/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.core.interceptor;

import com.github.hiwepy.fastxls.core.model.RowMap;

/**
 * 抽象的数据更新拦截器
 */
public abstract class AbstractUpdateInterceptor<R, M extends RowMap> implements Interceptor<R, M> {

	public void afterPropertiesSet() throws Exception {
		
	}

    /**
     * Does nothing
     */
    public void init() {
    }
   
    public int getOrder(){
    	return 1;
    }
    
    /**
     */
    public abstract void intercept(ActionInvocation<R, M> invocation) throws Exception;
    
    /**
     * Does nothing
     */
    public void destroy() {
    }
    
}
