package com.github.hiwepy.fastxls.poi;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.rowset.CachedRowSet;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.hiwepy.fastxls.core.WorkbookFiller;
import com.github.hiwepy.fastxls.core.model.CellModel;
import com.github.hiwepy.fastxls.core.model.MergedRegionModel;
import com.github.hiwepy.fastxls.core.model.RowModel;
import com.github.hiwepy.fastxls.core.model.SheetModel;
import com.github.hiwepy.fastxls.core.model.ValidateModel;
import com.github.hiwepy.fastxls.core.model.WorkBookModel;
import com.github.hiwepy.fastxls.core.utils.StringUtils;
import com.github.hiwepy.fastxls.core.utils.WorkbookUtils;
import com.github.hiwepy.fastxls.poi.factory.DataValidationFactory;
import com.github.hiwepy.fastxls.poi.utils.POICellStyleUtils;
import com.github.hiwepy.fastxls.poi.utils.POIFontUtils;
import com.github.hiwepy.fastxls.poi.utils.POIWorkbookUtils;

/**
 * Excel 文件内容渲染
 */
public class POIWorkbookFiller implements WorkbookFiller<Workbook, Sheet, Row, Cell> {

	private Logger LOG = LoggerFactory.getLogger(POIWorkbookFiller.class);
	private DataValidationFactory validationFactory = new DataValidationFactory();

	/**
	 * 根据数据填充当前Workbook 的每个sheet内容 @param workbook @param dataModel @throws
	 * Exception @return void 返回类型 @throws
	 */
	@Override
	public <T extends CellModel> void fillSheets(Workbook workbook, WorkBookModel<T> dataModel) throws Exception {
		fillSheets(workbook, dataModel, 0);
		POICellStyleUtils.destroy(workbook);
	}

	/**
	 * 根据数据从指定数字作为起始行填充当前Workbook 的每个sheet内容 @param workbook @param model @param
	 * startRow @return void 返回类型 @throws
	 */
	@Override
	public <T extends CellModel> void fillSheets(Workbook workbook, WorkBookModel<T> model, int startRow) throws Exception {
		Iterator<Map.Entry<String, SheetModel<T>>> ite = model.getSheets().entrySet().iterator();
		while (ite.hasNext()) {
			Entry<String, SheetModel<T>> entry = ite.next();
			Sheet sheet = workbook.getSheet(entry.getKey()) == null
					? workbook.createSheet(entry.getKey())
					: workbook.getSheet(entry.getKey());
			fillSheet(sheet, entry.getValue(), startRow);
		}
		POICellStyleUtils.destroy(workbook);
	}
	
	@Override
	public <T extends CellModel> void fillSheets(Workbook workbook, List<SheetModel<T>> sheets , int startRow) throws Exception {
		for (SheetModel<T> sheetModel : sheets) {
			fillSheets(workbook, sheetModel, startRow);
		}
		POICellStyleUtils.destroy(workbook);
	}
	
	@Override
	public <T extends CellModel> void fillSheets(Workbook workbook, SheetModel<T> sheetModel, int startRow) throws Exception {
		Sheet sheet = workbook.getSheet(sheetModel.getSheetName()) == null
				? workbook.createSheet(sheetModel.getSheetName())
				: workbook.getSheet(sheetModel.getSheetName());
		fillSheet(sheet, sheetModel, startRow);
		POICellStyleUtils.destroy(workbook);
	}

	/**
	 * 从第startRow行根据数据开始填充当前sheet并返回，第一行以最大行数合并作为大标题
	 * 
	 * @param sheet
	 * @param title 大标题
	 * @param data
	 */
	@Override
	public <T extends CellModel> void fillSheet(Sheet sheet, SheetModel<T> sheetModel, int startRow) throws Exception {
		// 设置默认参数
		sheet.setSelected(sheetModel.isSelected());
		// 设置默认宽度、高度值
		sheet.setDefaultColumnWidth(sheetModel.getDefaultColumnWidth());
		sheet.setDefaultRowHeightInPoints(sheetModel.getDefaultRowHeight());
		// 处理行数据
		List<RowModel<T>> rowModels = sheetModel.getRows();
		// 填充行数据
		fillRows(sheet, rowModels, startRow);
		
		//sheet.setAutoFilter(CellRangeAddress.valueOf("C5：F200"));
		// 设置数据校验规则
		ValidateModel[] validateModels = sheetModel.getValidates();
		if (validateModels != null) {
			for (int i = 0; i < validateModels.length; i++) {
				sheet.addValidationData(getValidationFactory().getValidation(sheet, validateModels[i]));
			}
		}
		POICellStyleUtils.destroy(sheet.getWorkbook());
	}

	@Override
	public <T extends CellModel> void fillSheet(Sheet sheet, String title, SheetModel<T> sheetModel, int startRow)
			throws Exception {
		// 处理行数据
		List<RowModel<T>> rowModels = sheetModel.getRows();
		if (title != null && title.length() > 0) {
			int lastCol = WorkbookUtils.getLastNumOfRow(rowModels);
			try {
				// 合並單元格,下標從0開始
				// int firstRow, int lastRow, int firstCol, int lastCol
				sheet.addMergedRegion(new CellRangeAddress(0, startRow, lastCol, startRow));
				Cell cell = sheet.getRow(0).getCell(0);
				cell.setCellValue(title);
			} catch (Exception e) {
				LOG.error(e.getMessage(), e.getCause());
			}
		}
		fillRows(sheet, rowModels, startRow + 1);
	}

	@Override
	public void fillSheet(Sheet sheet, CachedRowSet rowSet, List<Map<String, String>> columnList )
			throws Exception {
		
		// 判断是否超出一页的数据量
		boolean overPage = (rowSet.size() / rowSet.getPageSize() > 1);
		// 分页
		if (overPage) {
			while (rowSet.nextPage()) {
				// 遍历当前分页的每一个行
				while (rowSet.next()) {
					//判断当前线程内行下标是否在限制范围内，否则跳过交给其他线程处理
					int columnIndex = 0;
					// 迭代循环当前配置的列
					Iterator<Map<String, String>> iterator = columnList.iterator();
					while (iterator.hasNext()) {
						Map<String, String> item = iterator.next();
						String column = MapUtils.getString(item, "column").toLowerCase();
						// 判断该列是否在用户选择的列范围内
						Object value = rowSet.getObject(column);
						Row row = sheet.createRow(rowSet.getRow());
						POIWorkbookUtils.createCell(row, columnIndex, value, POICellStyleUtils.getNormalStyle(sheet.getWorkbook()));
						
						columnIndex ++;
					}
				}
			}
		} else {
			// 遍历当前分页的每一个行
			while (rowSet.next()) {
				//判断当前线程内行下标是否在限制范围内，否则跳过交给其他线程处理
				int columnIndex = 0;
				// 迭代循环当前配置的列
				Iterator<Map<String, String>> iterator = columnList.iterator();
				while (iterator.hasNext()) {
					Map<String, String> item = iterator.next();
					String column = MapUtils.getString(item, "column").toLowerCase();
					// 判断该列是否在用户选择的列范围内
					Object value = rowSet.getObject(column);
					
					Row row = sheet.createRow(rowSet.getRow());
					POIWorkbookUtils.createCell(row, columnIndex, value, POICellStyleUtils.getNormalStyle(sheet.getWorkbook()));
					
					columnIndex ++;
				}
			}
		}
		for (int i = 0; i < sheet.getLastRowNum(); i++) {
			sheet.autoSizeColumn(i); // 自动调整列宽度
		}
	}
	

	@Override
	public void fillSheet(Sheet sheet, CachedRowSet rowSet, List<Map<String, String>> columnList, int offset, int limit)
			throws Exception {
		/*
		// 遍历行数据
		int rowIndex = 0;
		Row row = null;
		boolean isIn = true;
		//移动游标到当前页数据的指定起始行
		//rowSet.absolute(arguments.getOffset()+1);
		//遍历当前分页的每一个行
		while (rowSet.next()&&isIn == true) {
			//判断当前线程内行下标是否在限制范围内，否则跳过交给其他线程处理
			if(rowIndex<=arguments.getLimit()){
				log.info(arguments.getTaskName()+"-正在执行...");
				//创建xls行对象
				row = sheet.createRow((arguments.getOffset()==0?1:arguments.getOffset())+rowIndex);
				int columnIndex = 0;
				//迭代循环当前配置的列
				Iterator<Map<String, String>> iterator = arguments.getMapperColumnsList().iterator();
				while (iterator.hasNext()) {
					Map<String, String> item = iterator.next();
					String column = getString(item, "column").toLowerCase();
					//判断该列是否在用户选择的列范围内
					if(arguments.getColumnsList().contains(column)){
						Object value = rowSet.getObject(column);
						buildCell(row, columnIndex, value, helper, contentStyle);
						columnIndex ++;
					}
				}
				isIn = true;
				rowIndex++;
			}else{
				//改变参数，终止循环
				isIn = false;
			}
		}*/
		// 遍历行数据
		int rowIndex = 0;
		boolean isIn = true;
		// 移动游标到当前页数据的指定起始行
		rowSet.absolute(offset + 1);
		// 遍历当前分页的每一个行
		while (isIn == true && rowSet.next()) {
			//判断当前线程内行下标是否在限制范围内，否则跳过交给其他线程处理
			if(rowIndex <= limit){
				int columnIndex = 0;
				// 迭代循环当前配置的列
				Iterator<Map<String, String>> iterator = columnList.iterator();
				while (iterator.hasNext()) {
					Map<String, String> item = iterator.next();
					String column = MapUtils.getString(item, "column").toLowerCase();
					// 判断该列是否在用户选择的列范围内
					Object value = rowSet.getObject(column);
					
					Row row = sheet.createRow(rowSet.getRow());
					POIWorkbookUtils.createCell(row, columnIndex, value, POICellStyleUtils.getNormalStyle(sheet.getWorkbook()));
					
					columnIndex ++;
				}
				rowIndex ++;
			} else{
				//改变参数，终止循环
				isIn = false;
			}
		}
		for (int i = 0; i < sheet.getLastRowNum(); i++) {
			sheet.autoSizeColumn(i); // 自动调整列宽度
		}
	}
	
	@Override
	public <T extends CellModel> void fillRows(Sheet sheet, List<RowModel<T>> rowList, int startRow)  throws Exception {
		Iterator<RowModel<T>> ite = rowList.iterator();
		MergedRegionModel cellRangeAddress = null;
		while (ite.hasNext()) {
			RowModel<T> rowModel = ite.next();
			cellRangeAddress = rowModel.getCellRangeAddress();
			if (cellRangeAddress != null) {
				fillMerged(sheet, rowModel);
			} else {
				Row row = sheet.createRow(rowModel.getRowNum());
				fillRow(row, rowModel);
			}
		}
		for (int i = 0; i < sheet.getLastRowNum(); i++) {
			sheet.autoSizeColumn(i); // 自动调整列宽度
		}
		POICellStyleUtils.destroy(sheet.getWorkbook());
	}
	
	@Override
	public <T extends CellModel> void fillRows(Sheet sheet, RowModel<T> rowModel) throws Exception {
		Row row = sheet.getRow(rowModel.getRowNum());
		fillRow(row, rowModel);
		for (int i = 0; i < sheet.getLastRowNum(); i++) {
			sheet.autoSizeColumn(i); // 自动调整列宽度
		}
	}

	@Override
	public void fillHead(Sheet sheet, List<Map<String, String>> rowMap, int rowNum) throws Exception {
		// 生成列标题HSSFRow
		Row row = sheet.createRow(rowNum);
		Cell cell = null;
		//迭代循环当前配置的列
		Iterator<Map<String, String>> iterator = rowMap.iterator();
		int columnIndex = 0;// 列序号
		while (iterator.hasNext()) {
			Map<String, String> item = iterator.next();
			cell = row.createCell(columnIndex);
			// String column = MapUtils.getString(item, "column").toLowerCase();
			String content = MapUtils.getString(item, "comments");
			boolean warning = BooleanUtils.toBoolean(MapUtils.getString(item, "warning"));
			boolean requisite = BooleanUtils.toBoolean(MapUtils.getString(item, "requisite"));
			boolean unique = BooleanUtils.toBoolean(MapUtils.getString(item, "unique"));
			if (warning && (requisite || unique)) {
				// 特殊字段[如：必填，唯一]标题单元格格式（红色，黑体，加粗，15磅字）
				CellStyle requiredStyle = POICellStyleUtils.getRequiredStyle(sheet.getWorkbook());
				cell.setCellStyle(requiredStyle);
			} else {
				// 普通字段标题单元格格式（黑色，黑体，15磅字）
				CellStyle normalStyle = POICellStyleUtils.getNormalStyle(sheet.getWorkbook());
				cell.setCellStyle(normalStyle);
			}
			cell.setCellValue(sheet.getWorkbook().getCreationHelper().createRichTextString(content));
			columnIndex ++ ;
		}
	}
	
	@Override
	public void fillRows(Sheet sheet, List<Map<String, String>> columnList,  List<Map<String, String>> rowList, int startRow) throws Exception {
		int rowNum = startRow;
		for (Map<String, String> rowMap : rowList) {
			this.fillRow(sheet, columnList, rowMap, rowNum);
			rowNum ++;
		}
		for (int i = 0; i < sheet.getLastRowNum(); i++) {
			sheet.autoSizeColumn(i); // 自动调整列宽度
		}
	}
	
	@Override
	public void fillRow(Sheet sheet, List<Map<String, String>> columnList,  Map<String, String> rowMap, int rowNum) throws Exception {
		// 生成列标题HSSFRow
		Row row = sheet.createRow(rowNum);
		Cell cell = null;
		//迭代循环当前配置的列
		Iterator<Map<String, String>> iterator = columnList.iterator();
		int columnIndex = 0;// 列序号
		while (iterator.hasNext()) {
			Map<String, String> item = iterator.next();
			cell = row.createCell(columnIndex);
			String column = MapUtils.getString(item, "column");
			String content = MapUtils.getString(rowMap, column);
			Integer height = MapUtils.getInteger(item, "height");
			if(null != height) {
				row.setHeightInPoints(height);
			}
			// 普通字段标题单元格格式（黑色，黑体，15磅字）
			CellStyle normalStyle = POICellStyleUtils.getTextStyle(sheet.getWorkbook());
			cell.setCellStyle(normalStyle);
			cell.setCellValue(sheet.getWorkbook().getCreationHelper().createRichTextString(content));
			columnIndex ++ ;
		}
	}
	
	@Override
	public <T extends CellModel> void fillMerged(Sheet sheet, RowModel<T> rowModel) throws Exception {
		MergedRegionModel cellRangeAddress = rowModel.getCellRangeAddress();
		if (cellRangeAddress != null) {
			// 合並單元格,下標從0開始
			// int firstRow, int lastRow, int firstCol, int lastCol
			sheet.addMergedRegion(new CellRangeAddress(cellRangeAddress.getFirstRow(), cellRangeAddress.getLastRow(),
					cellRangeAddress.getFirstCol(), cellRangeAddress.getLastCol()));
			Row row = sheet.createRow(cellRangeAddress.getFirstRow());
			// heightInPoints 设置的值永远是height属性值的20倍
			row.setHeightInPoints(rowModel.getHeight());
			Cell cell = row.createCell(0);
			T cellModel = rowModel.getCell(0);
			cellModel.setTitle(true);
			fillCellWithStyle(cell, cellModel);
		}
		POICellStyleUtils.destroy(sheet.getWorkbook());
	}
	
	@Override
	public <T extends CellModel> void fillRow(Row row, RowModel<T> rowModel) throws Exception {
		// heightInPoints 设置的值永远是height属性值的20倍
		// row.setHeightInPoints(rowModel.getHeight());
		// Set the row's height or set to ff (-1) for undefined/default-height.
		// Set the height in "twips" or
		// 1/20th of a point.
		row.setHeight((short) (rowModel.getHeight() * 20));

		Iterator<String> iterator = rowModel.keySet().iterator();
		Cell cell = null;
		while (iterator.hasNext()) {
			T cellModel = rowModel.get(iterator.next());
			cell = row.createCell(cellModel.getColumnIndex());
			fillCellWithStyle(cell, cellModel);
		}
	}

	@Override
	public <T extends CellModel> void fillCellWithStyle(Cell cell, T cellModel) throws Exception {
		try {

			Workbook workbook = cell.getSheet().getWorkbook();

			// 这里字体使用静态对象，且只有一个实例，因为字体太多生成的Excel会提示错误

			if (cellModel != null && cellModel.getContent() != null) {
				// 渲染内容
				Object content = cellModel.getContent();

				// 判断是否是标题
				if (cellModel.isTitle()) {
					
					boolean requisite = cellModel.isRequisite();
					boolean unique = cellModel.isUnique();
					if (requisite || unique) {
						
						// 特殊字段[如：必填，唯一]标题单元格格式（红色，黑体，加粗，13磅字）
						CellStyle requiredStyle = POICellStyleUtils.getRequiredStyle(workbook);
						// 设置内容单元格内的位置
						requiredStyle.setAlignment(HorizontalAlignment.forInt(cellModel.getAlignment()));
						requiredStyle.setVerticalAlignment(VerticalAlignment.forInt(cellModel.getVerticalAlignment()));
						// 设置此单元格是否锁定
						requiredStyle.setLocked(cellModel.isLocked());
						
						cellModel.setContent(content);
						cell.setCellStyle(requiredStyle);
					} else {
						
						// 普通字段标题单元格格式（黑色，黑体，加粗，13磅字）
						CellStyle normalStyle = POICellStyleUtils.getNormalStyle(workbook);
						// 设置此单元格是否锁定
						normalStyle.setLocked(cellModel.isLocked());
						// 设置内容单元格内的位置
						normalStyle.setAlignment(HorizontalAlignment.forInt(cellModel.getAlignment()));
						normalStyle.setVerticalAlignment(VerticalAlignment.forInt(cellModel.getVerticalAlignment()));
						
						cell.setCellStyle(normalStyle);
					}
				} else if (cellModel.isComment()) {
					
					CellStyle commentStyle = POICellStyleUtils.getCommentStyle(workbook);
					// 设置此单元格是否锁定
					commentStyle.setLocked(cellModel.isLocked());
					// 设置内容单元格内的位置
					commentStyle.setAlignment(HorizontalAlignment.forInt(cellModel.getAlignment()));
					commentStyle.setVerticalAlignment(VerticalAlignment.forInt(cellModel.getVerticalAlignment()));
					cell.setCellStyle(commentStyle);
					
				} else {
					
					// （黑色，宋体，不加粗，13磅字）
					CellStyle textStyle = POICellStyleUtils.getTextStyle(workbook);
					// 设置此单元格是否锁定
					textStyle.setLocked(cellModel.isLocked());
					// 设置内容单元格内的位置
					textStyle.setAlignment(HorizontalAlignment.forInt(cellModel.getAlignment()));
					textStyle.setVerticalAlignment(VerticalAlignment.forInt(cellModel.getVerticalAlignment()));
					
					cell.setCellStyle(textStyle);
				}
				fillCell(cell, cellModel);
			} else {
				fillCell(cell, null);
			}
			// fontStyle = null;
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage(), e.getCause());
		}
	}

	/**
	 * 填充单元格内容，并设置相应格式 @param cell @param cellModel @throws
	 */
	@Override
	public <T extends CellModel> void fillCell(Cell cell, T cellModel) throws Exception {
		Workbook workbook = cell.getSheet().getWorkbook();
		CreationHelper helper = workbook.getCreationHelper();
		if (cellModel == null) {
			cell.setCellValue(" ");
		} else {
			if (cellModel.getContent() != null) {
				// 渲染内容
				Object content = cellModel.getContent();
				String cellValue = "";
				if (content instanceof String) {
					cellValue = (String) content;
					cell.setCellValue(helper.createRichTextString(cellValue));
				} else if (content instanceof Number) {
					String format = cellModel.getFormat();
					if ((content instanceof Integer) || (content instanceof Long)) {
						cellValue = String.valueOf(content);
						cell.setCellValue(helper.createRichTextString(cellValue));
					} else if ((content instanceof Double) || (content instanceof Float)) {
						cellValue = String.valueOf(content);
						// 针对带小数点的数据的处理
						cell.setCellValue(Double.valueOf(String.valueOf(content)));
						if (StringUtils.hasText(format)) {
							try {
								CellStyle style = workbook.createCellStyle();
								style.setDataFormat(workbook.createDataFormat().getFormat(format));
								// 设定样式
								cell.setCellStyle(style);
							} catch (Exception e) {
								// 出现异常表示给出的格式为自定义格式
								// cell.setCellType(CellType.STRING);
								NumberFormat numberFormat = new DecimalFormat(format);
								cellValue = numberFormat.format(content);
								cell.setCellValue(helper.createRichTextString(cellValue));
							}
						} else {
							// cell.setCellType(CellType.NUMERIC);
						}
					} else {
						// cell.setCellType(CellType.STRING);
						cellValue = new BigDecimal(((Number) content).doubleValue()).toPlainString();
						cell.setCellValue(helper.createRichTextString(cellValue));
					}
				} else if (content instanceof Date) {
					// 针对Date格式
					// cell.setCellType(CellType.STRING);
					String format = cellModel.getFormat();
					cell.setCellValue((Date) content);
					if (StringUtils.hasText(format)) {
						try {
							CellStyle style = workbook.createCellStyle();
							style.setDataFormat(workbook.createDataFormat().getFormat(format));
							// 设定样式
							cell.setCellStyle(style);

						} catch (Exception e) {
							// 出现异常表示给出的格式为自定义格式
							// cell.setCellType(CellType.STRING);
							SimpleDateFormat date_format = new SimpleDateFormat(format);

							cellValue = date_format.format(content);

							cell.setCellValue(helper.createRichTextString(cellValue));
						}
					}
				} else if (content instanceof Boolean) {
					cellValue = String.valueOf(content);
					// cell.setCellType(CellType.BOOLEAN);
					cell.setCellValue((Boolean) content);
				} else {
					// cell.setCellType(CellType.STRING);
					cellValue = String.valueOf(content);
					cell.setCellValue(helper.createRichTextString(String.valueOf(content)));
				}
				// 设置列宽，如果当前单元格的宽度小于已经设置的列宽则以最大值为准
				// api 段信息 Set the width (in units of 1/256th of a character width)
				int width = Math
						.max(Math.min(255, Math.max(cell.getSheet().getColumnWidth(cellModel.getColumnIndex()) / 256,
								Math.max(cellModel.getWidth(), cellValue.length()))), 20);
				// api 段信息 Set the width (in units of 1/256th of a character width)
				cell.getSheet().setColumnWidth(cellModel.getColumnIndex(), width * 256);
				// 校验规则
				ValidateModel validateModel = cellModel.getValidate();
				if (validateModel != null) {
					DataValidation dataValidation = getValidationFactory().getValidation(cell.getSheet(), validateModel);
					if (dataValidation != null) {
						cell.getSheet().addValidationData(dataValidation);
					}
				}
			}
			// 添加批注
			fillComment(cell, cellModel);
		}
	}

	@Override
	public <T extends CellModel> void fillComment(Cell cell, T cellModel) throws Exception {
		if (StringUtils.hasText(cellModel.getComments())) {
			fillComment(cell, cellModel.getComments());
		}
	}
	
	@Override
	public <T extends CellModel> void fillComment(Cell cell, String comments) throws Exception {
		if (StringUtils.hasText(comments)) {
			// 创建绘图对象
			Drawing<?> drawing = cell.getSheet().createDrawingPatriarch();
			// 添加单元格注释
			// 定义注释的大小和位置,详见文档
			ClientAnchor anchor = null;
			RichTextString text = null;
			if (cell instanceof HSSFCell) {
				// 获取批注对象
				// (int dx1, int dy1, int dx2, int dy2, short col1, int row1, short col2, int
				// row2)
				// 前四个参数是坐标点,后四个参数是编辑和显示批注时的大小.
				anchor = new HSSFClientAnchor(0, 0, 0, 0, (short) 3, 3, (short) 5, 6);
				text = new HSSFRichTextString(comments);
			} else if (cell instanceof XSSFCell || cell instanceof SXSSFCell) {
				anchor = new XSSFClientAnchor(0, 0, 0, 0, (short) 3, 3, (short) 5, 6);
				text = new XSSFRichTextString(comments);
				Font commentFormatter = POIFontUtils.getCommentFont(cell.getSheet().getWorkbook());
				text.applyFont(commentFormatter);
			}
			if (anchor != null) {

				Comment comment = drawing.createCellComment(anchor);
				// 输入批注信息
				comment.setString(text);
				cell.setCellComment(comment);
			}
		}
	}

	public DataValidationFactory getValidationFactory() {
		return validationFactory;
	}

	public void setValidationFactory(DataValidationFactory validationFactory) {
		this.validationFactory = validationFactory;
	}

	

}
