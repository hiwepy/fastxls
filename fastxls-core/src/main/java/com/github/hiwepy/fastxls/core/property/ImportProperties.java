/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.core.property;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ImportProperties {
	
	/**
	 * 每次的导入中允许创建的线程池最大容量，默认：20
	 */
	private int threadMax = 20;
	
	/**
	 * 导入xls文件时,单个线程最大处理行,默认 500.
	 */
	private int threadBatchSize = 500;

	private String taskNamePrefix = "task-import-";
	
}
