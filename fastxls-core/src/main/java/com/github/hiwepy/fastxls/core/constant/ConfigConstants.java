package com.github.hiwepy.fastxls.core.constant;

public abstract class ConfigConstants {
	

	//---------初始化---------------------------------------
	
	/**
	 * Key[workbook.config.encoding] : 编码格式 ，默认： UTF-8
	 */
	public static  String XLS_CONFIG_ENCODING = "workbook.config.encoding";
	
	/**
	 * Key[workbook.config.support] : 用于导入导出的类包支持 POI或者JXL，默认： POI
	 */
	public static  String XLS_CONFIG_SUPPORT = "workbook.config.support";
	
	//---------UI组件---------------------------------------
	/**
	 * Key[workbook.uiwidget.width] : 页面workbook组件的宽度，默认：800px
	 */
	public static String XLS_UIWIDGET_WIDTH = "workbook.uiwidget.width";
	/**
	 * Key[workbook.uiwidget.width] : 页面workbook组件的高度，默认：500px
	 */
	public static String XLS_UIWIDGET_HEIGHT = "workbook.uiwidget.height";
	/**
	 * Key[workbook.font.names] : XLS默认初始的字体名称
	 */
	public static String XLS_FONT_NAMES = "workbook.font.names";
	
	//---------存储---------------------------------------
	/**
	 * Key[workbook.store.dir] : workbook组件使用的根目录  ，默认 ：workbookDir
	 */
	public static String XLS_STORE_DIR = "workbook.store.dir";
	/**
	 * Key[workbook.store.tmpDir] : xls 临时文件存储路径 ，默认 ：tmpDir
	 */
	public static String XLS_STORE_TMPDIR = "workbook.store.tmpDir";
	/**
	 * Key[workbook.store.template] : xls 模板文件存储路径 ，默认 ：templateDir
	 */
	public static String XLS_STORE_TEMPLATE = "workbook.store.template";
	/**
	 * Key[workbook.store.prefix] : xls 文件存储时，文件名后的前缀字符串 【前缀字符串-文件名.xls】 ，默认 ：空
	 */
	public static String XLS_STORE_PREFIX = "workbook.store.prefix";
	/**
	 * Key[workbook.store.suffix] : xls 文件存储时，文件名后的后缀字符串 【文件名-后缀字符串.xls】生成方式 ，可选【Date,UUID】，默认 ：UUID
	 */
	public static String XLS_STORE_SUFFIX = "workbook.store.suffix";
	
	//---------验证---------------------------------------
	/**
	 * Key[workbook.validate.thread.max] : 每次的数据验证中允许创建的线程池最大容量，默认：20.
	 */
	public static String XLS_VALIDATE_THREAD_MAX = "workbook.validate.thread.max";
	/**
	 * Key[workbook.validate.thread.batchSize] : 数据验证xls文件时,单个线程最大处理行,默认 500.
	 */
	public static String XLS_VALIDATE_THREAD_BATCHSIZE = "workbook.validate.thread.batchSize";
	
	//---------导入---------------------------------------
	/**
	 * Key[workbook.import.store] : 是否存储导入的文件 . 默认 false.
	 */
	public static String XLS_IMPORT_STORE = "workbook.import.store";
	/**
	 * Key[workbook.import.store.prefix] : 是否在存储导入的文件时，文件名称加上前缀字符串  【前缀字符串-文件名.xls】, 默认 false
	 */
	public static String XLS_IMPORT_STORE_PREFIX = "workbook.import.store.prefix";
	/**
	 * Key[workbook.import.store.suffix] : 是否在存储导入的文件时，文件名称加上后缀字符串  【文件名-后缀字符串.xls】, 默认 false
	 */
	public static String XLS_IMPORT_STORE_SUFFIX = "workbook.import.store.suffix";
	/**
	 * Key[workbook.import.thread.max] : 每次的导入中允许创建的线程池最大容量，默认：20.
	 */
	public static String XLS_IMPORT_THREAD_MAX = "workbook.import.thread.max";
	/**
	 * Key[workbook.import.thread.batchSize] : 导入xls文件时,单个线程最大处理行,默认 500.
	 */
	public static String XLS_IMPORT_THREAD_BATCHSIZE = "workbook.import.thread.batchSize";

	
	//---------导出---------------------------------------
	/**
	 * Key[workbook.export.store] :是否在存储导出的文件 . 默认 false.
	 */
	public static String XLS_EXPORT_STORE = "workbook.export.store";
	/**
	 * Key[workbook.export.store.prefix] : 是否在存储导出的文件时，文件名称加上前缀字符串  【前缀字符串-文件名.xls】, 默认 false
	 */
	public static String XLS_EXPORT_STORE_PREFIX = "workbook.export.store.prefix";
	/**
	 * Key[workbook.export.store.suffix] : 是否在存储导出的文件时，文件名称加上后缀字符串  【文件名-后缀字符串.xls】, 默认 false
	 */
	public static String XLS_EXPORT_STORE_SUFFIX = "workbook.export.store.suffix";
	/**
	 * Key[workbook.export.thread.max] : 每次的导出中允许创建的线程池最大容量，默认：20
	 */
	public static String XLS_EXPORT_THREAD_MAX = "workbook.export.thread.max";
	/**
	 * Key[workbook.export.thread.batchSize] : 导出xls文件时,单个线程最大处理量,默认 500.
	 */
	public static String XLS_EXPORT_THREAD_BATCHSIZE = "workbook.export.thread.batchSize";
	/**
	 * Key[workbook.export.row.limit] : 导出xls文件时,每个Sheet最大允许有多少行，超过工作簿最大65536时候则以65536为最大值
	 */
	public static String XLS_EXPORT_ROW_LIMIT = "workbook.export.row.limit";
	/**
	 * Key[workbook.export.row.destruct] : 导出xls文件时,数据过多时候采用拆分方式sheet:多个工作簿|wookbook：多个xls文件，默认：sheet 即 拆分为多个工作簿
	 */
	public static String XLS_EXPORT_ROW_DESTRUCT = "workbook.export.row.destruct";
	 
	
}