package com.github.hiwepy.fastxls.core.task;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.hiwepy.fastxls.core.Constants;
import com.github.hiwepy.fastxls.core.WorkbookFiller;
import com.github.hiwepy.fastxls.core.model.CellModel;
import com.github.hiwepy.fastxls.core.model.RowModel;

/**
 * Excel 数据导出线程
 * @author <a href="https://github.com/vindell">wandl</a>
 * @param <W>
 * @param <S>
 * @param <R>
 * @param <C>
 * @param <T>
 */
public class DataExportTask<W, S, R, C, T extends CellModel> implements Callable<Boolean> {
	
	protected Logger LOG = LoggerFactory.getLogger(getClass());
	
	private final String taskName;
	private final S sheet;
	private final int startRow;
	
	private WorkbookFiller<W, S, R, C> workbookFiller;
	private final List<RowModel<T>> rowList;
	
	public DataExportTask(
			String taskName,
			S sheet, 
			WorkbookFiller<W, S, R, C> workbookFiller ,
			List<RowModel<T>> rowList, 
			int startRow
		) {
		this.taskName = taskName;
		this.sheet = sheet;
		this.workbookFiller = workbookFiller;
		this.rowList = rowList;
		this.startRow = startRow;
	}
	
	@Override
	public Boolean call() throws Exception {
		if(CollectionUtils.isNotEmpty(getRowList())){
			int rowIndex = 0;
			// 迭代循环当前配置的列
			Iterator<RowModel<T>> iterator = getRowList().iterator();
			while (iterator.hasNext()) {
				// 记录状态日志
				LOG.info(Constants.THREAD_RUNNING, this.getTaskName());
				//判断当前线程内行下标是否在限制范围内，否则跳过交给其他线程处理
				RowModel<T> rowModel = iterator.next();
				rowModel.setRowNum(getStartRow() + rowIndex);
				//渲染数据
				getWorkbookFiller().fillRows(sheet, rowModel);
				rowIndex ++;
			}
		}
		//记录状态日志
		LOG.info(Constants.THREAD_COMPLETE, this.getTaskName());
		return true;
	}
	
	public String getTaskName() {
		return taskName;
	}
	
	protected S getSheet() {
		return sheet;
	}

	public WorkbookFiller<W, S, R, C> getWorkbookFiller() {
		return workbookFiller;
	}
 
	public List<RowModel<T>> getRowList() {
		return rowList;
	}

	public int getStartRow() {
		return startRow;
	}
	
}
