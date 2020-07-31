package com.github.hiwepy.fastxls.core.task;

import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.hiwepy.fastxls.core.Constants;
import com.github.hiwepy.fastxls.core.WorkbookReader;
import com.github.hiwepy.fastxls.core.task.callback.CellEventCallback;
import com.github.hiwepy.fastxls.core.task.callback.RowEventCallback;
import com.github.hiwepy.fastxls.core.task.callback.SheetEventCallback;

/**
 * Excel 工作簿 多线程访问，可借助此方法实现多线程同时进行内容检查，单元格数据校验
 * @author <a href="https://github.com/vindell">wandl</a>
 * @param <W>
 * @param <S>
 * @param <R>
 * @param <C>
 */
public class DataAccessTask<W, S, R, C>  implements Callable<Boolean> {
	
	protected Logger LOG = LoggerFactory.getLogger(getClass());
	
	private final String taskName;
	private final S sheet;
	private final int offset;
	private final int limit;
	
	private final WorkbookReader<W, S, R, C> workbookReader;
	private final SheetEventCallback<S> sheetEvent;
	private final RowEventCallback<R> rowEvent;
	private final CellEventCallback<C> cellEvent;
	
	public DataAccessTask (
			String taskName, 
			S sheet,
			int offset,
			int limit, 
			WorkbookReader<W, S, R, C> workbookReader,
			SheetEventCallback<S> sheetEvent,
			RowEventCallback<R> rowEvent, 
			CellEventCallback<C> cellEvent){
		this.taskName = taskName;
		this.sheet = sheet;
		this.offset = offset;
		this.limit = limit;
		this.workbookReader = workbookReader;
		this.sheetEvent = sheetEvent;
		this.rowEvent = rowEvent;
		this.cellEvent = cellEvent;
	}
	
	@Override
	public Boolean call() throws Exception {
		
		// sheet 最后一行
		int lastRowNum = getWorkbookReader().getLastRowNum(sheet);
		// 计算结束位置
		int toIndex = this.getOffset() + this.getLimit() - 1;
			toIndex = Math.min(toIndex, lastRowNum);
		//根据起始结束位获取部分row
		List<R> rows = getWorkbookReader().getRows(sheet, this.getOffset(), toIndex);
		if(CollectionUtils.isNotEmpty(rows)){
			//获得起始行
			for (int rowIndex = 0;rowIndex < rows.size() ;rowIndex++) {
				//记录状态日志
				LOG.info(Constants.THREAD_RUNNING, this.getTaskName());
				R row = getWorkbookReader().getRow(sheet, rowIndex);
				if (row != null ) {
					List<C> cells = getWorkbookReader().getCells(row);
					for (int i = 0; i < cells.size(); i++) {
						if(this.getCellEvent() != null){
							this.getCellEvent().doCallback( i, cells.get(i));
						}
					}
				}
				if(this.getRowEvent() != null){
					this.getRowEvent().doCallback(row);
				}
			}
		}
		if(this.getSheetEvent() != null ){
			this.getSheetEvent().doCallback(sheet);
		}
		//记录状态日志
		LOG.info(Constants.THREAD_COMPLETE, this.getTaskName());
		
		return true;
	}

	public String getTaskName() {
		return taskName;
	}
	
	protected WorkbookReader<W, S, R, C> getWorkbookReader() {
		return workbookReader;
	}

	protected S getSheet() {
		return sheet;
	}

	protected SheetEventCallback<S> getSheetEvent() {
		return sheetEvent;
	}

	protected RowEventCallback<R> getRowEvent() {
		return rowEvent;
	}

	protected CellEventCallback<C> getCellEvent() {
		return cellEvent;
	}

	protected int getOffset() {
		return offset;
	}

	protected int getLimit() {
		return limit;
	}
	
}

