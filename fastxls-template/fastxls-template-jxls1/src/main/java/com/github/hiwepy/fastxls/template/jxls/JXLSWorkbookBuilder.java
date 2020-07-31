package com.github.hiwepy.fastxls.template.jxls;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;

import com.github.hiwepy.fastxls.core.builder.WorkbookBuilder;
import com.github.hiwepy.fastxls.core.model.CellModel;
import com.github.hiwepy.fastxls.core.model.RowModel;
import com.github.hiwepy.fastxls.core.model.SheetModel;
import net.sf.jxls.transformer.XLSTransformer;

/**
 * transformer.transformMultipleSheetsList(is, objects, newSheetNames, beanName, beanParams, startSheetNum) 
 * transformer.transformWorkbook(hssfWorkbook, beanParams) 
 * transformer.transformXLS(is, beanParams)
 * transformer.transformXLS(srcFilePath, beanParams, destFilePath)
 * transformer.transformXLS(is, templateSheetNameList, sheetNameList, beanParamsList) 
 * transformer.transformXLS(arguments, rowSet, outpath)
 * transformer.transformXLS(arguments, rowSet, outStream)
 * transformer.transformXLS(arguments, list, outpath)
 * transformer.transformXLS(arguments, list, outStream)
 */
public class JXLSWorkbookBuilder<T extends CellModel> extends WorkbookBuilder<T, Workbook> {

	private InputStream template;
	private XLSTransformer transformer;
	
	public JXLSWorkbookBuilder<T> template(String template) {
		this.template = IOUtils.toInputStream(template, Charset.defaultCharset());
		return this;
	}
	
	public JXLSWorkbookBuilder<T> template(File template) throws IOException {
		this.template = new FileInputStream(template);
		return this;
	}
	
	public JXLSWorkbookBuilder<T> template(InputStream template) {
		this.template = template;
		return this;
	}
	
	public JXLSWorkbookBuilder<T> transformer(XLSTransformer transformer) {
		this.transformer = transformer;
		return this;
	}
	
	@Override
	public Workbook build() {
		try {
			
			transformer = transformer == null ? new XLSTransformer(): transformer;
			
			List<String> sheetNames = new ArrayList<String>();
			// 所有sheet的数据
			List<List<RowModel<T>>> sheetRows = new ArrayList<List<RowModel<T>>>();
			// 提取行配置信息，生成参数
			Map<String, Object> beanParams = new HashMap<String, Object>();
			
			if(this.getWorkbook() != null) {
				Map<String, SheetModel<T>> sheets = this.getWorkbook().getSheets();
				for (SheetModel<T> sheetModel : sheets.values()) {
					sheetNames.add(sheetModel.getSheetIndex(), sheetModel.getSheetName());
					sheetRows.add(sheetModel.getSheetIndex(), sheetModel.getRows());
				}
			}
			else if(this.getSheets() == null) {
				for (SheetModel<T> sheetModel : this.getSheets()) {
					sheetNames.add(sheetModel.getSheetIndex(), sheetModel.getSheetName());
					sheetRows.add(sheetModel.getSheetIndex(), sheetModel.getRows());
				}
			}
 
			try (InputStream input = template;) {

				try {
					return transformer.transformMultipleSheetsList(input, sheetRows, sheetNames, "rows", beanParams, 0);
				} catch (Exception e) {
					return new HSSFWorkbook();
				}
			}
			 
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
