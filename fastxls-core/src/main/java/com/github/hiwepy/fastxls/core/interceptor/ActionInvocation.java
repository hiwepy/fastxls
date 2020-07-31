package com.github.hiwepy.fastxls.core.interceptor;

import java.io.Serializable;

import com.github.hiwepy.fastxls.core.model.RowMap;
import com.github.hiwepy.fastxls.core.model.RowWrapper;

public interface ActionInvocation<R, M extends RowMap> extends Serializable {
	
	RowWrapper<R> getRow();
    
}
