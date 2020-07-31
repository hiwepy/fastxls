/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.core.function;

import java.util.function.BiFunction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.hiwepy.fastxls.core.WorkbookReader;
import com.github.hiwepy.fastxls.core.model.ColumnWrapper;
import com.github.hiwepy.fastxls.core.model.RowMap;
import com.github.hiwepy.fastxls.core.model.RowMapper;
import com.github.hiwepy.fastxls.core.model.RowWrapper;

public class DefaultRowMapMapperBiFunction<W, S, R, C> implements BiFunction<RowWrapper<R>, Integer, RowMapper<R, RowMap>> {
	
	protected Logger LOG = LoggerFactory.getLogger(getClass());
	protected final WorkbookReader<W, S, R, C> workbookReader;
	protected final BiFunction<ColumnWrapper<C>, String, String> transformer;
	
	public DefaultRowMapMapperBiFunction(WorkbookReader<W, S, R, C> workbookReader
			, BiFunction<ColumnWrapper<C>, String, String> transformer) {
		this.workbookReader = workbookReader;
		this.transformer = transformer;
	}

	@Override
	public RowMapper<R, RowMap> apply(RowWrapper<R> wrapper, Integer rowNum) {
		return getWorkbookReader().getRowMapper(wrapper.getRow(), wrapper.getColumns(), rowNum, transformer);
	}
	
	public WorkbookReader<W, S, R, C> getWorkbookReader() {
		return workbookReader;
	}
	
}
