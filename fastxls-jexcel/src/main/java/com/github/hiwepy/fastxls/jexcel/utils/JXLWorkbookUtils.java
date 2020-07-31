package com.github.hiwepy.fastxls.jexcel.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;
import jxl.write.WritableWorkbook;
import com.github.hiwepy.fastxls.jexcel.JXLSettings;

public class JXLWorkbookUtils {
	
	protected static Logger LOG = LoggerFactory.getLogger(JXLWorkbookUtils.class);
	
	public static Workbook getWorkbook(String path) throws IOException {
		return getWorkbook(new File(path));
	}
	
	/**
	 * 从文件中读取Workbook
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static Workbook getWorkbook(File file) throws IOException {
		try {
			return Workbook.getWorkbook(file);
		} catch (BiffException e) {
			LOG.error(e.getMessage(),e.getCause());
			throw new IOException(e);
		}
	}
	

	public static Workbook getWorkbook(InputStream input) throws IOException  {
		try {
			return Workbook.getWorkbook(input);
		} catch (BiffException e) {
			LOG.error(e.getMessage(),e.getCause());
			throw new IOException(e);
		}
	}
	
    public static WritableWorkbook createWorkbook(OutputStream output) throws IOException {
	    return createWorkbook(output, null);
    }
    
    public static WritableWorkbook createWorkbook(OutputStream output, Workbook template) throws IOException {
    	//1.避免乱码的设置
	    WorkbookSettings setting = new WorkbookSettings();
	    setting.setEncoding("ISO-8859-1");
	    setting.setLocale(new Locale("zh","CN"));
 	    setting.setTemplate(true);
	    //2.创建可读写的副本
	    return  template == null ? Workbook.createWorkbook(output, setting) :  Workbook.createWorkbook(output, template, setting);
    }

    public static WritableWorkbook createWorkbook(OutputStream output, Workbook template, JXLSettings settings) throws IOException {
    	//1.避免乱码的设置
	    WorkbookSettings setting = new WorkbookSettings();
	    
	    if(null != settings) {
	    	
	    	setting.setArrayGrowSize(settings.getArrayGrowSize());
	    	setting.setAutoFilterDisabled(settings.isAutoFilterDisabled());
	    	setting.setCellValidationDisabled(settings.isCellValidationDisabled());
	    	setting.setCharacterSet(settings.getCharacterSet());
	    	setting.setDrawingsDisabled(settings.isDrawingsDisabled());
	    	setting.setEncoding(settings.getEncoding());
	    	setting.setExcel9File(settings.isExcel9file());
	    	setting.setExcelDisplayLanguage(settings.getExcelDisplayLanguage());
	    	setting.setExcelRegionalSettings(settings.getExcelRegionalSettings());
	    	setting.setFormulaAdjust(settings.isFormulaAdjust());
	    	setting.setGCDisabled(settings.isGcDisabled());
	    	setting.setHideobj(settings.getHideobj());
	    	setting.setIgnoreBlanks(settings.isIgnoreBlankCells());
	    	setting.setInitialFileSize(settings.getInitialFileSize());
	    	setting.setLocale(settings.getLocale());
	    	setting.setMergedCellChecking(settings.isMergedCellChecking());
	    	setting.setNamesDisabled(settings.isNamesDisabled());
	    	setting.setPropertySets(settings.isPropertySets());
	    	setting.setRationalization(settings.isRationalization());
	    	setting.setRefreshAll(settings.isRefreshAll());
	    	setting.setSuppressWarnings(settings.isSuppressWarnings());
	    	setting.setTemplate(true);
	    	setting.setTemporaryFileDuringWriteDirectory(settings.getTemporaryFileDuringWriteDirectory());
	    	setting.setWriteAccess(settings.getWriteAccess());
	    	
	    } else {
	    	setting.setEncoding("ISO-8859-1");
	 	    setting.setLocale(new Locale("zh","CN"));
	 	    setting.setTemplate(true);
	    }
	   
	    //2.创建可读写的副本
	    return  template == null ? Workbook.createWorkbook(output, setting) :  Workbook.createWorkbook(output, template, setting);
    }

	
}
