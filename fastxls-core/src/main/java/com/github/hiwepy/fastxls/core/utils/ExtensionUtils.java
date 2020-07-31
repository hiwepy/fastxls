package com.github.hiwepy.fastxls.core.utils;

import java.io.File;

import org.apache.commons.io.FilenameUtils;

/**
 * 文档后缀判断方法
 */
public class ExtensionUtils {
	
	public static boolean isXls(String filePath) {
		return ExtensionUtils.isXls(new File(filePath));
	}
	
	public static boolean isXls(File file) {
		Assert.notNull(file, " file is not specified!");
		Assert.isTrue(file.exists(), " file is not found !");
		Assert.isTrue(file.isFile(), " file is not a file !");
		return "xls".equalsIgnoreCase(FilenameUtils.getExtension(file.getName()));
	}
	
	public static boolean isXlsx(String filePath) {
		return ExtensionUtils.isXlsx(new File(filePath));
	}
	
	public static boolean isXlsx(File file) {
		Assert.notNull(file, " file is not specified!");
		Assert.isTrue(file.exists(), " file is not found !");
		Assert.isTrue(file.isFile(), " file is not a file !");
		return "xlsx".equalsIgnoreCase(FilenameUtils.getExtension(file.getName()));
	}
	
	
}
