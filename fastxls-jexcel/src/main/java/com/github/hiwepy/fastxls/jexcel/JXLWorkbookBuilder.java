package com.github.hiwepy.fastxls.jexcel;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.WritableWorkbook;
import com.github.hiwepy.fastxls.core.builder.WorkbookBuilder;
import com.github.hiwepy.fastxls.core.model.CellModel;
import com.github.hiwepy.fastxls.jexcel.utils.JXLWorkbookUtils;

 /**
  * xls文档内容构建
  */
public class JXLWorkbookBuilder<T extends CellModel> extends WorkbookBuilder<T, WritableWorkbook> {
	
	protected Logger LOG = LoggerFactory.getLogger(getClass());
	private JXLWorkbookFiller workbookFiller = new JXLWorkbookFiller();
	private JXLSettings settings;
	private Workbook template;
	
	public JXLWorkbookBuilder<T> settings(JXLSettings settings) {
		this.settings = settings;
		return this;
	}
	
	public JXLWorkbookBuilder<T> template(Workbook template) {
		this.template = template;
		return this;
	}
	
	public JXLWorkbookBuilder<T> template(File template) throws BiffException, IOException {
		this.template = Workbook.getWorkbook(template);
		return this;
	}
	
	public JXLWorkbookBuilder<T> template(InputStream template) throws BiffException, IOException {
		this.template = Workbook.getWorkbook(template);
		return this;
	}
	
	@Override
	public WritableWorkbook build() {

		try {
			
			// 生成workbook（JXL）
			WritableWorkbook workbook = JXLWorkbookUtils.createWorkbook(this.getOutput(), template, getSettings());
			// 构建表头
			if(this.getWorkbook() != null) {
				getWorkbookFiller().fillSheets(workbook, this.getWorkbook());
			}
			else if(this.getSheets() == null) {
				getWorkbookFiller().fillSheets(workbook, this.getSheets(), 0);
			}
			else if(this.getSheet() == null) {
				getWorkbookFiller().fillSheets(workbook, this.getSheet(), 0);
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

}



