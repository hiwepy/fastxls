package com.github.hiwepy.fastxls.jexcel;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jxl.Cell;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import com.github.hiwepy.fastxls.core.Constants;
import com.github.hiwepy.fastxls.core.builder.WorkbookBuilder2;
import com.github.hiwepy.fastxls.core.model.CellModel;
import com.github.hiwepy.fastxls.core.task.MapDataExportTask;
import com.github.hiwepy.fastxls.core.utils.ExecutorUtils;
import com.github.hiwepy.fastxls.core.utils.WorkbookUtils;
import com.github.hiwepy.fastxls.jexcel.utils.JXLWorkbookUtils;

 /**
  * xls文档内容构建
  */
public class JXLWorkbookBuilder2<T extends CellModel> extends WorkbookBuilder2<T, WritableWorkbook> {
	
	protected Logger LOG = LoggerFactory.getLogger(getClass());
	private JXLWorkbookFiller workbookFiller = new JXLWorkbookFiller();
	private JXLWorkbookReader workbookReader = new JXLWorkbookReader();
	private JXLSettings settings;
	private Workbook template;
	
	public JXLWorkbookBuilder2<T> settings(JXLSettings settings) {
		this.settings = settings;
		return this;
	}
	
	public JXLWorkbookBuilder2<T> template(Workbook template) {
		this.template = template;
		return this;
	}
	
	public JXLWorkbookBuilder2<T> template(File template) throws BiffException, IOException {
		this.template = Workbook.getWorkbook(template);
		return this;
	}
	
	public JXLWorkbookBuilder2<T> template(InputStream template) throws BiffException, IOException {
		this.template = Workbook.getWorkbook(template);
		return this;
	}
	
	@Override
	public WritableWorkbook build() {
		
		try {
			
			// 生成workbook（JXL）
			WritableWorkbook workbook = JXLWorkbookUtils.createWorkbook(this.output, template, getSettings());
						
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
					WritableSheet sheet = workbook.getSheet(sheetName);
					if(sheet == null) {
						sheet = workbook.createSheet(sheetName, sheetIndex);
					}
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
						cachedThreadPool.submit(new MapDataExportTask<WritableWorkbook, WritableSheet, Cell[], Cell>( 
								taskName, sheet, this.getWorkbookFiller(), this.getColumnList(), rowList, startRow));
						// 计算下一个开始位置
						offset += this.getMaxThreadTaskSize();
						offset = Math.min(offset, dataNum);
						
					}
				}
				// 等待所有任务完成，关闭线程池
				ExecutorUtils.awaitShutdown(cachedThreadPool, 10 , TimeUnit.SECONDS);
				
			} else {
				
				// 创建sheet
				WritableSheet sheet = workbook.createSheet(this.getSheetName(), 0);
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
			workbook.write();
			workbook.close();
			this.getOutput().flush();
			this.getOutput().close();
			if(template != null) {
				template.close();
			}
			return workbook;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}

	public JXLSettings getSettings() {
		return settings;
	}
	 
	public JXLWorkbookFiller getWorkbookFiller() {
		return workbookFiller;
	}
	
	public JXLWorkbookReader getWorkbookReader() {
		return workbookReader;
	}

}



