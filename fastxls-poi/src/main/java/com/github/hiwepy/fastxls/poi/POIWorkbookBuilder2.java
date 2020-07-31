/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.poi;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.github.hiwepy.fastxls.core.Constants;
import com.github.hiwepy.fastxls.core.builder.WorkbookBuilder2;
import com.github.hiwepy.fastxls.core.model.CellModel;
import com.github.hiwepy.fastxls.core.task.MapDataExportTask;
import com.github.hiwepy.fastxls.core.utils.ExecutorUtils;
import com.github.hiwepy.fastxls.core.utils.WorkbookUtils;
import com.github.hiwepy.fastxls.poi.utils.POIWorkbookUtils;

public class POIWorkbookBuilder2<T extends CellModel> extends WorkbookBuilder2<T, Workbook> {

	private POIWorkbookFiller workbookFiller = new POIWorkbookFiller();
	private POIWorkbookWriter workbookWriter = new POIWorkbookWriter();
	
	@Override
	public Workbook build() {
		try {
		
			///long currentMs = System.currentTimeMillis();
		
			
			
			// 生成workbook（POI）
			Workbook workbook = POIWorkbookUtils.getWorkbook(this.getSuffix());
						
			// 是否使用多线程进行构建
			if(this.isThreadPool()) {
				
				// 计算需要多少个sheet
				int sheetNum = 1;
				int dataNum = 0;
				if(this.getRowList() == null) {
					dataNum = this.getRowList().size();
					sheetNum = WorkbookUtils.getSheetCount(dataNum, this.getMaxRow());
				} else if(this.getRowSet() == null) {
					dataNum = this.getRowSet().getMaxRows();
					sheetNum = WorkbookUtils.getSheetCount(dataNum, this.getMaxRow());
				}
				
				// 计算全部数据需要多少个线程进行处理
				int threadNum = ExecutorUtils.getThreadCount(dataNum, this.getMaxThreadTaskSize());
				// 固定大小的创建线程池
				ExecutorService cachedThreadPool = Executors.newFixedThreadPool(threadNum);
				// 起始位
				int offset = 0;
				// 逐个创建Sheet,一页的结果集代表着一个sheet
				for (int sheetIndex = 0; sheetIndex < sheetNum; sheetIndex++) {
					
					// 获取或创建sheet
					String sheetName = this.getSheetName() + "-" + sheetIndex;
					Sheet sheet = workbook.getSheet(sheetName);
					if(sheet == null) {
						sheet = workbook.createSheet(sheetName);
					}
					// 设置默认宽度、高度值
					//sheet.setDefaultColumnWidth(this.getDefaultColumnWidth());
					//sheet.setDefaultRowHeightInPoints(this.getDefaultRowHeight());
					// 起始行号
					int startRow = 0;
					// 生成列标题Row
					if( this.getColumnList() != null) {
						// 构建表头
						getWorkbookFiller().fillHead(sheet, this.getColumnList(), 0);
						startRow = offset + 1;
					}
					// 计算当前Sheet数据需要的线程数
					int threadNumOfSheet = ExecutorUtils.getThreadCount(this.getMaxRow(), this.getMaxThreadTaskSize());
					// 打印日志
					LOG.info(Constants.THREAD_INFO, sheetName, this.getMaxRow(), this.getMaxThreadTaskSize() , threadNumOfSheet);
					// 循环构建线程
					for (int threadIndex = 0; threadIndex < threadNumOfSheet; threadIndex++) {
						// 线程名称
						String taskName = String.format(Constants.THREAD_TASKNAME, sheetName, this.getThreadNamePrefix(), threadIndex);
						// 截取集合片段
						List<Map<String,String>> rowList = this.getRowList().subList(offset , this.getMaxThreadTaskSize());
						// 向线程池增加一个线程任务
						cachedThreadPool.submit(new MapDataExportTask<Workbook, Sheet, Row, Cell>( taskName, sheet, this.getWorkbookFiller(),
								this.getColumnList(), rowList, startRow));
						// 计算下一个开始位置
						offset += this.getMaxThreadTaskSize();
						offset = Math.min(offset, dataNum);
						
					}
				}
				// 等待所有任务完成，关闭线程池
				ExecutorUtils.awaitShutdown(cachedThreadPool, 10 , TimeUnit.SECONDS);		
			} else {
				
				// 创建sheet
				Sheet sheet = workbook.createSheet(this.getSheetName());
				// 设置默认宽度、高度值
				//sheet.setDefaultColumnWidth(this.getDefaultColumnWidth());
				//sheet.setDefaultRowHeightInPoints(this.getDefaultRowHeight());
				// 起始行号
				int startRow = 0;
				// 生成列标题Row
				if( this.getColumnList() != null) {
					// 构建表头
					getWorkbookFiller().fillHead(sheet, this.getColumnList(), 0);
					startRow = 1;
				}
				
				if(null != this.getRowList() ) {
					getWorkbookFiller().fillRows(sheet, this.getColumnList(), this.getRowList(), startRow);
				}
				else if(null != this.getRowSet()) {
					getWorkbookFiller().fillSheet(sheet, this.getRowSet(), this.getColumnList());
				}
				
			}
			
			// 输出xls文档
			getWorkbookWriter().writeToStream(workbook, this.getOutput());
			
			return workbook;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}
	
	public POIWorkbookFiller getWorkbookFiller() {
		return workbookFiller;
	}
	
	public POIWorkbookWriter getWorkbookWriter() {
		return workbookWriter;
	}
	
}
