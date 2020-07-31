package com.github.hiwepy.fastxls.core.builder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.hiwepy.fastxls.core.model.CellModel;
import com.github.hiwepy.fastxls.core.model.SheetModel;
import com.github.hiwepy.fastxls.core.model.WorkBookModel;

public abstract class WorkbookBuilder<T extends CellModel, W> extends AbstractBuilder<W> {
	
	protected Logger LOG = LoggerFactory.getLogger(getClass());
	
	/**
	 * 生成的xls文件是否只读
	 */
	private boolean readOnly;
	/**
	 *Excel 电子表格 抽象化数据 对象
	 */
	private WorkBookModel<T> workbook;
	/**
	 * 抽象化的excel表格对象  SheetModel<T> (sheet数据)
	 */
	private List<SheetModel<T>> sheets;
	/**
	 * 抽象化的excel表格对象 SheetModel<T> (sheet数据)
	 */
	private SheetModel<T> sheet;
	/**
	 * 构建结果输出流
	 */
	protected OutputStream output;

	@Override
	protected void internalInit() {
		
	}
	
	public WorkbookBuilder<T, W> wookbook(WorkBookModel<T> workbook){
		this.workbook = workbook;
		this.suffix(workbook.getSuffix());
		return this;
	}
	
	public WorkbookBuilder<T, W> sheets(List<SheetModel<T>> sheets){
		this.sheets = sheets;
		return this;
	}
	
	public WorkbookBuilder<T, W> sheet(SheetModel<T> sheet){
		this.sheet = sheet;
		return this;
	}
	
	public WorkbookBuilder<T, W> output(File output) throws IOException {
		this.output = new FileOutputStream(output);
		return this;
	}
	
	public WorkbookBuilder<T, W> output(OutputStream output) {
		this.output = output;
		return this;
	}

	protected boolean isReadOnly() {
		return readOnly;
	}

	protected WorkBookModel<T> getWorkbook() {
		init();
		return workbook;
	}

	protected List<SheetModel<T>> getSheets() {
		init();
		return sheets;
	}
 
	protected SheetModel<T> getSheet() {
		init();
		return sheet;
	}

	protected OutputStream getOutput() {
		return output;
	}

}