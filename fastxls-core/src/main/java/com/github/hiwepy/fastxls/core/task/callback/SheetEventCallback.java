package com.github.hiwepy.fastxls.core.task.callback;

/**
 * 线程完成当前sheet上的操作后的回调
 */
public interface SheetEventCallback<S> {
	
	public void doCallback(S sheet);

}
