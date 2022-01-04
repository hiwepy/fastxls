/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.core.builder;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.hiwepy.fastxls.core.Constants;
import com.github.hiwepy.fastxls.core.WorkbookReader;
import com.github.hiwepy.fastxls.core.model.RowMap;
import com.github.hiwepy.fastxls.core.task.DataAccessTask;
import com.github.hiwepy.fastxls.core.task.callback.CellEventCallback;
import com.github.hiwepy.fastxls.core.task.callback.RowEventCallback;
import com.github.hiwepy.fastxls.core.task.callback.SheetEventCallback;
import com.github.hiwepy.fastxls.core.utils.ExecutorUtils;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

/**
 * 文档对象访问
 * @author <a href="https://github.com/vindell">wandl</a>
 */
public abstract class DataAccessBuilder<W, S , R, C, M extends RowMap> extends DataBuilder<DataAccessBuilder<W, S , R, C, M>, R, C, M>  {

	protected Logger LOG = LoggerFactory.getLogger(getClass());
	protected final WorkbookReader<W, S, R, C> workbookReader;
	/**
	 * 自定义线程名称,方便的出错的时候溯源
     */
	protected ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("fastxls-pool-%d").build();

    
	public DataAccessBuilder(WorkbookReader<W, S, R, C> workbookReader) {
		this.workbookReader = workbookReader;
	}
	
	public void access(File file, SheetEventCallback<S> sheetEvent) throws Exception{
		this.access(file, sheetEvent, null, null);
	}
	
	public void access(File file, SheetEventCallback<S> sheetEvent, RowEventCallback<R> rowEvent) throws Exception{
		this.access(file, sheetEvent, rowEvent, null);
	}
	
	public void access(File file, SheetEventCallback<S> sheetEvent, RowEventCallback<R> rowEvent, CellEventCallback<C> cellEvent) throws Exception{
		
		long currentMs = System.currentTimeMillis();
		
		/**
	     * corePoolSize    线程池核心池的大小
	     * maximumPoolSize 线程池中允许的最大线程数量
	     * keepAliveTime   当线程数大于核心时，此为终止前多余的空闲线程等待新任务的最长时间
	     * unit            keepAliveTime 的时间单位
	     * workQueue       用来储存等待执行任务的队列
	     * threadFactory   创建线程的工厂类
	     * handler         拒绝策略类,当线程池数量达到上线并且workQueue队列长度达到上限时就需要对到来的任务做拒绝处理
	     */
	    ExecutorService threadPool = new ThreadPoolExecutor(
	    		this.getMaxThreadSize(),
	    		this.getMaxThreadSize(),
	            0L,
	            TimeUnit.MILLISECONDS,
	            new LinkedBlockingQueue<>(1024),
	            namedThreadFactory,
	            new ThreadPoolExecutor.AbortPolicy()
	    );

	   // CompletionService<Boolean> completionThreadPool = new ExecutorCompletionService<>(threadPool);
	    
		// 获取当前会话中上传的xls文件的sheet
		S[] sheets = getWorkbookReader().getSheets(file);
	    // 循环sheet
		for (int index = 0; index < sheets.length; index++) {
			// 第 n 个 sheet
			S sheet = sheets[index];
			// 总行数
			int row_count = getWorkbookReader().getLastRowNum(sheet);
			if(row_count > 0 ){
				// 得到当前sheet 的总行数，并计算当前sheet需要多少个线程进行处理
				int thread_num = ExecutorUtils.getThreadCount(row_count, this.getMaxThreadTaskSize());
				// 获取sheet名称
				String sheetName = getWorkbookReader().getSheetName(sheet);
				// 记录状态日志
				LOG.info(Constants.THREAD_INFO, sheetName , row_count, 0,  thread_num);
				// 循环线程数，准备数据
				for(int thread_index= 0; thread_index < thread_num; thread_index ++ ){
					// 计算起始行
					int offset = thread_index * this.getMaxThreadTaskSize();
					// 线程名称
					String task_name = String.format(Constants.THREAD_TASKNAME, sheetName, this.getThreadNamePrefix(), thread_index);
					// 记录状态日志
					LOG.info(Constants.THREAD_STATUS, sheetName, row_count, task_name);
					// 添加一个内容检查线程
					threadPool.submit(new DataAccessTask<W, S, R, C>( task_name,
							sheet , Math.min(offset, row_count), this.getMaxThreadTaskSize(), this.getWorkbookReader(), sheetEvent, rowEvent, cellEvent)); 
				}
			}
		}
		
		// 等待所有任务完成，关闭线程池
		ExecutorUtils.awaitShutdown(threadPool, 10 , TimeUnit.SECONDS);
		// 记录状态日志
		LOG.info(Constants.THREAD_TIME, (System.currentTimeMillis() - currentMs)/1000);
		
	};
	
	public DataAccessBuilder<W, S ,R, C, M> and() {
		return this;
	}
	
	@Override
	public DataAccessBuilder<W, S ,R, C, M> build() {
		init();
		return this;
	}

	public WorkbookReader<W, S, R, C> getWorkbookReader() {
		return workbookReader;
	}
	
}
