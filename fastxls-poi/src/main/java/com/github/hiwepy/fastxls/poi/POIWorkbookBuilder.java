package com.github.hiwepy.fastxls.poi;

import org.apache.poi.ss.usermodel.Workbook;

import com.github.hiwepy.fastxls.core.builder.WorkbookBuilder;
import com.github.hiwepy.fastxls.core.model.CellModel;
import com.github.hiwepy.fastxls.poi.utils.POIWorkbookUtils;

/**
 * xls文档内容构建
 */
public class POIWorkbookBuilder<T extends CellModel> extends WorkbookBuilder<T, Workbook> {

	private POIWorkbookFiller workbookFiller = new POIWorkbookFiller();
	private POIWorkbookWriter workbookWriter = new POIWorkbookWriter();
	
	@Override
	public Workbook build() {
		
		try {
			
			// 生成workbook（POI）
			Workbook workbook = POIWorkbookUtils.getWorkbook(this.getSuffix());
		
			if(this.getWorkbook() != null) {
				getWorkbookFiller().fillSheets(workbook, this.getWorkbook(), 0);
			}
			else if(this.getSheets() != null) {
				getWorkbookFiller().fillSheets(workbook, this.getSheets(), 0);
			}
			else if(this.getSheet() != null) {
				getWorkbookFiller().fillSheets(workbook, this.getSheet(), 0);
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



