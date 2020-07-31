package com.github.hiwepy.fastxls.template.jxls;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;

import com.github.hiwepy.fastxls.core.builder.WorkbookBuilder2;
import com.github.hiwepy.fastxls.core.model.CellModel;
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
public class JXLSWorkbookBuilder2<T extends CellModel> extends WorkbookBuilder2<T, Workbook> {

	private InputStream template;
	private XLSTransformer transformer;
	
	public JXLSWorkbookBuilder2<T> template(String template) {
		this.template = IOUtils.toInputStream(template, Charset.defaultCharset());
		return this;
	}
	
	public JXLSWorkbookBuilder2<T> template(File template) throws IOException {
		this.template = new FileInputStream(template);
		return this;
	}
	
	public JXLSWorkbookBuilder2<T> template(InputStream template) throws IOException {
		this.template = template;
		return this;
	}
	
	public JXLSWorkbookBuilder2<T> transformer(XLSTransformer transformer) {
		this.transformer = transformer;
		return this;
	}
	
	@Override
	public Workbook build() {
		try {
			
			transformer = transformer == null ? new XLSTransformer(): transformer;
			
			Map<String, Object> variables = new HashMap<String, Object>();
			if( this.getColumnList() != null) {
				variables.put("columnList", this.getColumnList());
			}
			else if(this.getRowList() == null) {
				variables.put("rowList", this.getRowList());
			}
			else if(this.getRowSet() == null) {
				variables.put("rowSet", this.getRowSet());
			}
 
			try (InputStream input = template;) {

				try {
					return transformer.transformXLS(input, variables);
				} catch (Exception e) {
					return new HSSFWorkbook();
				}
			}
			 
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
