/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.core.property;

import org.apache.commons.lang3.SystemUtils;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import com.github.hiwepy.fastxls.core.FilenameSuffix;
import com.github.hiwepy.fastxls.core.Suffix;

@Getter
@Setter
@ToString
public class ExportProperties {
	
	static final int UNSET_INT = -1;
	 
	/**
	 * 生成的Excel文件后缀;决定了使用什么对象去构建wookbook：默认xls;支持xls,xlsx
	 */
	protected Suffix suffix = Suffix.XLS;
	/**
	 * 导出xls文件时,每个Sheet最大允许有多少行，超过工作簿最大65536时候则以65536为最大值
	 */
	private int maxRow = 10000;
	/*
	 * xls文件读取密码
	 */
	private String password;
	/**
	 * 生成的xls文件是否只读
	 */
	private boolean readOnly;
	/**
	 * 是否在缓存导出的文件，以便在一定时间内可重复使用 . 默认 false.
	 */
	private boolean cache = false;
	/**
	 * 设置缓存容器的初始容量
	 */
	int initialCapacity = UNSET_INT;
	/**
	 * 设置并发级别，并发级别是指可以同时写缓存的线程数
	 */
	int concurrencyLevel = UNSET_INT;
	/**
	 * 设置缓存最后一次访问多少分钟后过期; 单位：TimeUnit.MINUTES
	 */
	int expireAfterAccess = UNSET_INT;
	/**
	 * 设置缓存被创建或值被替换后多少分钟后过期; 单位：TimeUnit.MINUTES
	 */
	int expireAfterWrite = UNSET_INT;
	/**
	 * 设置缓存最大容量限制，超过限制之后就会按照LRU最近虽少使用算法来移除缓存项
	 */
	long maximumSize = UNSET_INT;
	long maximumWeight = UNSET_INT;
	
	/**
	 * 是否使用多线程，默认：false
	 */
	private boolean threadPool = false;
	/**
	 * 线程名称前缀，默认：thread-export-
	 */
	private String threadNamePrefix = "thread-export-";
	/**
	 * 每次的导出中允许创建的线程池最大容量，默认：20
	 */
	private int maxThreadSize = 20;
	/**
	 * 导出xls文件时,单个线程最大处理行，默认 500.
	 */
	private int maxThreadTaskSize = 500;
	/**
	 * xls 临时文件存储路径 ，默认 ：tmpDir
	 */
	private String tmpdir = SystemUtils.getUserDir().getAbsolutePath();
	/**
	 * xls 模板文件存储路径 ，默认 ：template
	 */
	private String template = "template";
	/**
	 * xls 文件存储时，文件名后的前缀字符串 【前缀字符串-文件名.xls】 ，默认 ：空
	 */
	private String namePrefix = "";
	/**
	 * xls 文件存储时，文件名后的后缀字符串 【文件名-后缀字符串.xls】生成方式 ，可选【Date,UUID】，默认 ：UUID
	 */
	private FilenameSuffix nameSuffix = FilenameSuffix.UUID;
	 
	
}
