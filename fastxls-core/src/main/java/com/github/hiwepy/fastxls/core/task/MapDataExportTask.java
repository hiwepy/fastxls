package com.github.hiwepy.fastxls.core.task;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.hiwepy.fastxls.core.Constants;
import com.github.hiwepy.fastxls.core.WorkbookFiller;

/**
 * Excel 数据导出线程
 * @author <a href="https://github.com/vindell">wandl</a>
 * @param <W>
 * @param <S>
 * @param <R>
 * @param <C>
 */
public class MapDataExportTask<W, S, R, C> implements Callable<Boolean> {
	
	protected Logger LOG = LoggerFactory.getLogger(getClass());
	
	private final String taskName;
	private final S sheet;
	private final int startRow;

	private WorkbookFiller<W, S, R, C> workbookFiller;
	private final List<Map<String, String>> columnList;
	private final List<Map<String,String>> rowList;
	
	public MapDataExportTask(
			String taskName,
			S sheet,
			WorkbookFiller<W, S, R, C> workbookFiller,
			List<Map<String, String>> columnList,
			List<Map<String,String>> rowList, 
			int startRow
		) {
		this.taskName = taskName;
		this.workbookFiller = workbookFiller;
		this.sheet = sheet;
		this.startRow = startRow;
		this.columnList = columnList;
		this.rowList = rowList;
	}
	
	@Override
	public Boolean call() throws Exception {
		LOG.info(Constants.THREAD_RUNNING, this.getTaskName());
		if(CollectionUtils.isNotEmpty(getRowList())){
			// 渲染数据
			getWorkbookFiller().fillRows(sheet, getColumnList(), getRowList(), getStartRow());
		}
		LOG.info(Constants.THREAD_COMPLETE, this.getTaskName());
		return true;
	}
	
	public String getTaskName() {
		return taskName;
	}

	public WorkbookFiller<W, S, R, C> getWorkbookFiller() {
		return workbookFiller;
	}
	
	protected S getSheet() {
		return sheet;
	}

	public int getStartRow() {
		return startRow;
	}
	
	public List<Map<String, String>> getColumnList() {
		return columnList;
	}
	
	public List<Map<String,String>> getRowList() {
		return rowList;
	}
	
}
