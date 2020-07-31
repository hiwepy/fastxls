/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.core;

public final class Constants {

	public static final String DEFAULT_CHARSETNAME = "UTF-8";
	public static final int DEFAULT_TIMEOUTMILLIS = 5 * 1000;
	
	/* 使用线程池进行workbook 创建或者读取时线程信息 */
	public final static String THREAD_INFO = "工作表[{0}]共有数据{1}行，每个线程处理：{2}行,共需要线程{3}个!";
	/* 使用线程池进行workbook 创建或者读取时线程名称 */
	public final static String THREAD_TASKNAME = "工作表[%s] : [%s-%d]！";
	/* 使用线程池进行workbook 创建或者读取时线程状态信息 */
	public final static String THREAD_STATUS = "工作表[{0}]共有数据{1}行;由线程{2}进行处理!";
	/* 使用线程池进行workbook 创建或者读取时线程运行时信息 */
	public final static String THREAD_RUNNING = "线程池:数据【{0}】线程正在执行...";
	/* 使用线程池进行workbook 创建或者读取时一个子线程执行完成信息 */
	public final static String THREAD_COMPLETE = "线程池:数据【{0}】线程已经完成！";
	/* 使用线程池进行workbook 创建或者读取时线程池执行完成使用时间信息 */
	public final static String THREAD_TIME = "操作完成，累计用时：[{1}]秒！";

}
