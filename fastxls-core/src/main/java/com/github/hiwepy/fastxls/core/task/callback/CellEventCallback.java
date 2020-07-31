package com.github.hiwepy.fastxls.core.task.callback;

/**
 * 表格检查线程回调
 */
public interface CellEventCallback<C> {
	
	public void doCallback(int row, C cell);

}
