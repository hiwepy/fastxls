package com.github.hiwepy.fastxls.jexcel;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.rowset.CachedRowSet;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jxl.Cell;
import jxl.JXLException;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.CellFormat;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.Blank;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import com.github.hiwepy.fastxls.core.WorkbookFiller;
import com.github.hiwepy.fastxls.core.model.CellModel;
import com.github.hiwepy.fastxls.core.model.RowModel;
import com.github.hiwepy.fastxls.core.model.SheetModel;
import com.github.hiwepy.fastxls.core.model.WorkBookModel;
import com.github.hiwepy.fastxls.core.utils.WorkbookUtils;
import com.github.hiwepy.fastxls.jexcel.cache.JXLCellFormatCacheManager;

/**
 * 构建xls文件内容
 */
public class JXLWorkbookFiller implements WorkbookFiller<WritableWorkbook, WritableSheet, Cell[], Cell> {
	
	// 日期格式对象
	private SimpleDateFormat date_format = null;
	// 数字格式对象
	private DecimalFormat numberFormat = null;

	protected Logger LOG = LoggerFactory.getLogger(JXLWorkbookFiller.class);

	protected JXLCellFormatCacheManager cacheManager = JXLCellFormatCacheManager.getInstance(null);
	
	public JXLWorkbookFiller() {
		
	}
	
	/**
	 * 
	 * 根据数据填充当前WritableWorkbook 的每个sheet内容 @param workbook @param dataModel @throws
	 * Exception @return void 返回类型 @throws
	 */
	@Override
	public <T extends CellModel> void fillSheets(WritableWorkbook workbook, WorkBookModel<T> dataModel) throws Exception {
		fillSheets(workbook, dataModel, 0);
	}

	/**
	 * 
	 * 根据数据从指定数字作为起始行填充当前WritableWorkbook 的每个sheet内容 @param workbook @param
	 * model @param startRow @return void 返回类型 @throws
	 */
	@Override
	public <T extends CellModel> void fillSheets(WritableWorkbook workbook, WorkBookModel<T> model, int startRow) throws Exception {
		Iterator<Map.Entry<String, SheetModel<T>>> ite = model.getSheets().entrySet().iterator();
		while (ite.hasNext()) {
			Entry<String, SheetModel<T>> entry = ite.next();
			WritableSheet sheet = workbook.createSheet(entry.getKey(), workbook.getNumberOfSheets());
			fillSheet(sheet, null, entry.getValue(), startRow);
		}
	}
	
	@Override
	public <T extends CellModel> void fillSheets(WritableWorkbook workbook, List<SheetModel<T>> sheets, int startRow) throws Exception {
		for (SheetModel<T> sheetModel : sheets) {
			WritableSheet sheet = workbook.createSheet(sheetModel.getSheetName(), workbook.getNumberOfSheets());
			fillSheet(sheet, null, sheetModel, startRow);
		}
	}
	
	@Override
	public <T extends CellModel> void fillSheet(WritableSheet sheet, SheetModel<T> sheetModel, int startRow) throws Exception {
		fillSheet(sheet, null, sheetModel, startRow);
	}

	/**
	 * 从第startRow行根据数据开始填充当前sheet并返回，第一行以最大行数合并作为大标题
	 * 
	 * @param sheet
	 * @param title 大标题
	 * @param data
	 */
	@Override
	public <T extends CellModel> void fillSheet(WritableSheet sheet, String title, SheetModel<T> sheetModel,
			int startRow) throws Exception {
		// 处理行数据
		List<RowModel<T>> rowModels = sheetModel.getRows();
		if (title != null && title.length() > 0) {
			int lastCol = WorkbookUtils.getLastNumOfRow(rowModels);
			try {
				// 合並單元格,下標從0開始
				// int firstCol, int firstRow, int lastCol, int lastRow
				sheet.mergeCells(0, startRow, lastCol, startRow);
				sheet.addCell(new Label(0, startRow, title, this.getTitleCellFormat()));
			} catch (RowsExceededException e) {
				LOG.error(e.getMessage(), e.getCause());
			} catch (WriteException e) {
				LOG.error(e.getMessage(), e.getCause());
			}
		}
		fillRows(sheet, rowModels, startRow + 1);
	}

	@Override
	public void fillSheet(WritableSheet sheet, CachedRowSet rowSet, List<Map<String, String>> columnList) throws RowsExceededException, WriteException, SQLException {
		// 遍历行数据
		int rowIndex = 0;
		// 判断是否超出一页的数据量
		boolean overPage = (rowSet.size() / rowSet.getPageSize() > 1);
		// 分页
		if (overPage) {
			while (rowSet.nextPage()) {
				// 遍历当前分页的每一个行
				while (rowSet.next()) {
					int columnIndex = 0;
					// 迭代循环当前配置的列
					Iterator<Map<String, String>> iterator = columnList.iterator();
					while (iterator.hasNext()) {
						Map<String, String> item = iterator.next();
						String column_name = MapUtils.getString(item, "column").toLowerCase();
						// 判断该列是否在用户选择的列范围内
						Object value = rowSet.getObject(column_name);
						sheet.addCell(fillCell(rowIndex+1, columnIndex, value, this.getNormalCellFormat()));
						columnIndex ++;
					}
					rowIndex ++;
				}
			}
		} else {
			// 遍历当前分页的每一个行
			while (rowSet.next()) {
				int columnIndex = 0;
				// 迭代循环当前配置的列
				Iterator<Map<String, String>> iterator = columnList.iterator();
				while (iterator.hasNext()) {
					Map<String, String> item = iterator.next();
					String column_name = MapUtils.getString(item, "column").toLowerCase();
					// 判断该列是否在用户选择的列范围内
					Object value = rowSet.getObject(column_name);
					sheet.addCell(fillCell(rowIndex+1, columnIndex, value, this.getNormalCellFormat()));
					columnIndex ++;
				}
				rowIndex ++;
			}
		}
	}
	
	@Override
	public void fillSheet(WritableSheet sheet, CachedRowSet rowSet, List<Map<String, String>> columnList, int offset, int limit) throws RowsExceededException, WriteException, SQLException {
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
					String column_name = MapUtils.getString(item, "column").toLowerCase();
					// 判断该列是否在用户选择的列范围内
					Object value = rowSet.getObject(column_name);
					Integer width = MapUtils.getInteger(item, "width");
					if(null != width) {
						sheet.setColumnView(columnIndex, width);
					}
					sheet.addCell(fillCell(rowIndex+1, columnIndex, value, this.getNormalCellFormat()));
					columnIndex ++;
				}
				rowIndex ++;
			} else{
				//改变参数，终止循环
				isIn = false;
			}
		}
	}
	
	@Override
	public <T extends CellModel> void fillRows(WritableSheet sheet, List<RowModel<T>> rowModels, int startRow) 
			throws Exception {
		Iterator<RowModel<T>> ite = rowModels.iterator();
		while (ite.hasNext()) {
			RowModel<T> rowModel = ite.next();
			fillRows(sheet, rowModel);
		}
	}
	
	@Override
	public void fillHead(WritableSheet sheet, List<Map<String, String>> rowMap, int rowNum) throws Exception {
		// 生成列标题
		Iterator<Map<String, String>> iterator = rowMap.iterator();
		WritableCell label = null;
		int columnIndex = 0;
		while (iterator.hasNext()) {
			Map<String, String> item = iterator.next();
			String comments = MapUtils.getString(item, "comments");
			boolean warning = BooleanUtils.toBoolean(MapUtils.getString(item, "warning"));
			boolean requisite = BooleanUtils.toBoolean(MapUtils.getString(item, "requisite"));
			boolean unique = BooleanUtils.toBoolean(MapUtils.getString(item, "unique"));
			Integer width = MapUtils.getInteger(item, "width");
			if(null != width) {
				sheet.setColumnView(columnIndex, width);
			}
			if (warning && (requisite || unique)) {
				label = new Label(columnIndex, rowNum, comments, this.getRequiredCellFormat());
			} else {
				label = new Label(columnIndex, rowNum, comments, this.getTitleCellFormat());
			}
			sheet.addCell(label);
			columnIndex ++;
		}
	}
	
	@Override
	public void fillRows(WritableSheet sheet, List<Map<String, String>> columnList,  List<Map<String, String>> rowList, int startRow) throws Exception {
		int rowNum = startRow;
		for (Map<String, String> rowMap : rowList) {
			this.fillRow(sheet, columnList, rowMap, rowNum);
			rowNum ++;
		}
	}
	
	@Override
	public void fillRow(WritableSheet sheet, List<Map<String, String>> columnList,  Map<String, String> rowMap, int rowNum) throws Exception {
		// 生成列标题
		Iterator<Map<String, String>> iterator = columnList.iterator();
		WritableCell label = null;
		int columnIndex = 0;
		while (iterator.hasNext()) {
			Map<String, String> item = iterator.next();
			String column = MapUtils.getString(item, "column");
			String content = MapUtils.getString(rowMap, column);
			Integer width = MapUtils.getInteger(item, "width");
			if(null != width) {
				sheet.setColumnView(columnIndex, width);
			}
			label = new Label(columnIndex, rowNum, content, this.getTextCellFormat());
			sheet.addCell(label);
			columnIndex ++;
		}
	}

	@Override
	public <T extends CellModel> void fillRows(WritableSheet sheet, RowModel<T> rowModel) throws Exception {
		if (!rowModel.isEmpty()) {
			sheet.setRowView(rowModel.getRowNum(), rowModel.getHeight());
			Iterator<String> iterator = rowModel.keySet().iterator();
			while (iterator.hasNext()) {
				T cellModel = rowModel.get(iterator.next());
				fillCellWithStyle(sheet, cellModel);
			}
		}
	}

	
	public void fillMergeCell(WritableSheet sheet, int columnIndex, int rowIndex, int columnNum, int rowNum,
			String content, CellFormat format) throws JXLException {
		sheet.addCell(new Label(columnIndex, rowIndex, content, format));
		sheet.mergeCells(columnIndex, rowIndex, columnIndex + columnNum, rowIndex + rowNum);
	}
	
	public <T extends CellModel> void fillCellWithStyle(WritableSheet sheet, T cellModel) throws Exception {
		try {
			if (cellModel != null && cellModel.getContent() != null && StringUtils.isNotBlank(cellModel.getContent().toString())) {
				// 渲染内容
				Object content = cellModel.getContent();
				WritableCellFormat style = null;
				// 判断是否是标题
				if (cellModel.isTitle()) {

					boolean requisite = cellModel.isRequisite();
					boolean unique = cellModel.isUnique();
					if (requisite || unique) {
						cellModel.setContent(content);
						// 特殊字段[如：必填，唯一]标题单元格格式（红色，黑体，加粗，15磅字）
						style = this.getRequiredCellFormat();
					} else {
						// 大标题单元格格式（黑色，黑体，加粗，23磅字）
						style = this.getTitleCellFormat();
					}
				} else {
					// 普通字段标题单元格格式（黑色，黑体，加粗，15磅字）
					style = this.getTextCellFormat();
				}
				fillCell(sheet, cellModel, style);
			} else {
				fillCell(sheet, null, null);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e.getCause());
		}
	}

	public WritableCell fillCell(int  rowIndex, int columnIndex, Object value, WritableCellFormat contentStyle){
		WritableCell label = null;
		if (value == null) {
			label = new Label(columnIndex, rowIndex, "", contentStyle);
		} else {
			if (value instanceof Number) {
				String number = new BigDecimal(value.toString()).toPlainString();
				label = new Label(columnIndex, rowIndex, number, contentStyle);
			} else if (value instanceof String) {
				label = new Label(columnIndex, rowIndex, value.toString(), contentStyle);
			} else if (value instanceof Date) {
				label = new Label(columnIndex, rowIndex, date_format.format(value), contentStyle);
			} else {
				label = new Label(columnIndex, rowIndex, value.toString(), contentStyle);
			}
		}
		return label;
	}

	public <T extends CellModel> void fillCell(WritableSheet sheet, T cellModel, WritableCellFormat style) {
		try {

			if (cellModel != null && cellModel.getContent() != null && StringUtils.isNotBlank(cellModel.getContent().toString())) {
				sheet.addCell(new Blank(cellModel.getColumnIndex(), cellModel.getRowIndex()));
			} else {
				// 设置列宽，如果当前单元格的宽度小于已经设置的列宽则以最大值为准
				int width = Math.max(sheet.getColumnView(cellModel.getColumnIndex()).getSize(), cellModel.getWidth());
				sheet.setColumnView(cellModel.getColumnIndex(), width);

				// 渲染内容
				Object content = cellModel.getContent();
				int col = cellModel.getColumnIndex();
				int rol = cellModel.getRowIndex();
				if (content instanceof String) {
					sheet.addCell(new Label(col, rol, (String) content, style));
				} else if (content instanceof Number) {
					String format = cellModel.getFormat();
					if ((content instanceof Integer) || (content instanceof Long)) {
						sheet.addCell(new jxl.write.Number(col, rol, Double.valueOf((String) content), style));
					} else if ((content instanceof Double) || (content instanceof Float)) {
						// 针对带小数点的数据的处理
						if (!StringUtils.isBlank(format)) {
							try {
								style = this.getNumberCellFormat(format);
								sheet.addCell(new jxl.write.Number(col, rol, Double.valueOf((String) content), style));
							} catch (Exception e) {
								// 出现异常表示给出的格式为自定义格式
								numberFormat = new DecimalFormat(format);
								sheet.addCell(new Label(col, rol, numberFormat.format(content), style));
							}
						} else {
							sheet.addCell(new jxl.write.Number(col, rol, Double.valueOf((String) content), style));
						}
					} else {
						String number = new BigDecimal(((Number) content).doubleValue()).toPlainString();
						sheet.addCell(new Label(col, rol, number, style));
					}
				} else if (content instanceof Date) {
					// 针对Date格式
					String format = cellModel.getFormat();
					if (!StringUtils.isBlank(format)) {
						try {
							style = this.getNumberCellFormat(format);
							sheet.addCell(new jxl.write.DateTime(col, rol, (Date) content, style));
						} catch (Exception e) {
							// 出现异常表示给出的格式为自定义格式
							date_format = new SimpleDateFormat(format);
							sheet.addCell(new Label(col, rol, date_format.format(content), style));
						}
					} else {
						sheet.addCell(new jxl.write.DateTime(col, rol, (Date) content, style));
					}
				} else if (content instanceof Boolean) {
					sheet.addCell(new jxl.write.Boolean(col, rol, (Boolean) content, style));
				} else {
					sheet.addCell(new Label(col, rol, String.valueOf(content), style));
				}
			}
		} catch (RowsExceededException e) {
			LOG.error(e.getMessage(), e.getCause());
		} catch (NumberFormatException e) {
			LOG.error(e.getMessage(), e.getCause());
		} catch (WriteException e) {
			LOG.error(e.getMessage(), e.getCause());
		}
	}

	@Override
	public <T extends CellModel> void fillSheets(WritableWorkbook workbook, SheetModel<T> sheetModel, int startRow)
			throws Exception {
		
	}

	@Override
	public <T extends CellModel> void fillMerged(WritableSheet sheet, RowModel<T> rowModel) throws Exception {
		
	}

	@Override
	public <T extends CellModel> void fillRow(Cell[] row, RowModel<T> rowModel) throws Exception {
		
	}

	@Override
	public <T extends CellModel> void fillCellWithStyle(Cell cell, T cellModel) throws Exception {
		
	}

	@Override
	public <T extends CellModel> void fillCell(Cell cell, T cellModel) throws Exception {
	}

	@Override
	public <T extends CellModel> void fillComment(Cell cell, T cellModel) throws Exception {
	}

	@Override
	public <T extends CellModel> void fillComment(Cell cell, String comments) throws Exception {
		
	}

	
	public WritableCellFormat getCellFormat(String name, int size, boolean bold,
			Alignment alignment, VerticalAlignment valignment,
			Colour background, Border border, BorderLineStyle borderLine) {
		return cacheManager.getCellFormat(name, size, bold, alignment, valignment, background, border, borderLine);
	}
	
	//大标题单元格格式（黑色，黑体，加粗，23磅字）
	public WritableCellFormat getTitleCellFormat() {
		return getCellFormat(HT, 23, true, Alignment.CENTRE, VerticalAlignment.CENTRE, Colour.GREY_50_PERCENT, Border.ALL, BorderLineStyle.THIN);
	}
	
	//普通字段标题单元格格式（黑色，黑体，不加粗，13磅字）
	public WritableCellFormat getNormalCellFormat(){
		return getCellFormat(HT, 13, false, Alignment.CENTRE, VerticalAlignment.CENTRE, Colour.GREY_25_PERCENT, Border.ALL, BorderLineStyle.THIN);
	}
	
	//特殊字段[如：必填，唯一]标题单元格格式（红色，黑体，加粗，15磅字）
	public WritableCellFormat getRequiredCellFormat(){
		return getCellFormat(HT, 15, true, Alignment.CENTRE, VerticalAlignment.CENTRE, Colour.GREY_25_PERCENT, Border.ALL, BorderLineStyle.THIN);
	}
	
	//普通内容字段标题单元格格式（黑色，宋体，13磅字）
	public WritableCellFormat getTextCellFormat(){
		return getCellFormat(ST, 13, false, Alignment.CENTRE, VerticalAlignment.CENTRE, Colour.WHITE, Border.ALL, BorderLineStyle.THIN);
	}
	
	//大标题单元格格式（黑色，黑体，加粗，23磅字）
	public WritableCellFormat getNumberCellFormat(String format) throws WriteException{
		WritableCellFormat style = new WritableCellFormat(new jxl.write.NumberFormat(format));
        // 设置居右
		style.setAlignment(Alignment.RIGHT);
		style.setVerticalAlignment(VerticalAlignment.CENTRE);
		// 设置单元格的背景颜色
		style.setBackground(Colour.GREY_25_PERCENT);
		 // 设置边框线
		style.setBorder(Border.ALL, BorderLineStyle.THIN);	
		return style;
	}
	
	public WritableCellFormat getDateCellFormat(String format) throws WriteException{
		WritableCellFormat style = new WritableCellFormat(new jxl.write.DateFormat(format));
        // 设置居右
		style.setAlignment(Alignment.RIGHT);
		style.setVerticalAlignment(VerticalAlignment.CENTRE);
		// 设置单元格的背景颜色
		style.setBackground(Colour.GREY_25_PERCENT);
		 // 设置边框线
		style.setBorder(Border.ALL, BorderLineStyle.THIN);	
		return style;
	}
}
