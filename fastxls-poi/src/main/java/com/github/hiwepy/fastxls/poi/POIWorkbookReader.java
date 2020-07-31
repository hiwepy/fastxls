package com.github.hiwepy.fastxls.poi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.github.hiwepy.fastxls.core.WorkbookReader;
import com.github.hiwepy.fastxls.core.model.ColumnWrapper;
import com.github.hiwepy.fastxls.core.model.RowMap;
import com.github.hiwepy.fastxls.core.model.RowMapMapper;
import com.github.hiwepy.fastxls.poi.factory.POIFormulaEvaluatorFactory;
import com.github.hiwepy.fastxls.poi.utils.POIWorkbookUtils;

/**
 * 实现读取Excel的服务 Excel 97-2003和2007 版本
 */
public class POIWorkbookReader implements WorkbookReader<Workbook, Sheet, Row, Cell> {

	@Override
	public Sheet[] getSheets(InputStream input) throws IOException {
		Workbook wb = POIWorkbookUtils.getWorkbook(input);
		return getSheets(wb);
	}

	@Override
	public Sheet[] getSheets(File file) throws IOException {
		Workbook wb = POIWorkbookUtils.getWorkbook(file);
		return getSheets(wb);
	}

	@Override
	public Sheet[] getSheets(Workbook wb) throws IOException {
		Sheet[] sheets = null;
		if (wb != null) {
			sheets = new Sheet[wb.getNumberOfSheets()];
			for (int index = 0; index < wb.getNumberOfSheets(); index++) {
				sheets[index] = wb.getSheetAt(index);
			}
		}
		if (sheets != null) {
			List<Sheet> rtSheets = new ArrayList<>(); 
	 		for (int i = 0; i < sheets.length; i++) {
	 			if(!StringUtils.equalsIgnoreCase(sheets[i].getSheetName(), "hidden")) {
	 				rtSheets.add(sheets[i]);
	 			}
			}
			return rtSheets.toArray(new Sheet[rtSheets.size()]);
		}
		return sheets;
	}

	@Override
	public Sheet getSheet(File file, int sheetNum) throws IOException {
		Workbook wb = POIWorkbookUtils.getWorkbook(file);
		return getSheet(wb, sheetNum);
	}

	@Override
	public Sheet getSheet(File file, String sheetName) throws IOException {
		Workbook wb = POIWorkbookUtils.getWorkbook(file);
		return getSheet(wb, sheetName);
	}

	@Override
	public Sheet getSheet(Workbook wb, int sheetNum) throws IOException {
		Sheet sheet = null;
		if (wb != null) {
			sheet = wb.getSheetAt(sheetNum);
		}
		return sheet;
	}

	@Override
	public Sheet getSheet(Workbook wb, String sheetName) throws IOException {
		Sheet sheet = null;
		if (wb != null) {
			sheet = wb.getSheet(sheetName);
		}
		return sheet;
	}

	@Override
	public Map<String, String> getSheetInfo(File file, int index) throws IOException {
		//sheet的信息
		Map<String,String> info = new HashMap<String, String>();
		Sheet[] sheets = getSheets(file);
		Sheet sheet = sheets[index];
		//获得内容信息
		info.put("name", sheet.getSheetName());
		info.put("rows", String.valueOf(sheet.getLastRowNum()));
		return info;
	}

	@Override
	public List<Row> getRows(Sheet sheet, int fromIndex, int toIndex) throws IOException {
		fromIndex = fromIndex >= sheet.getLastRowNum() ? sheet.getLastRowNum() : fromIndex;
		toIndex = toIndex >= sheet.getLastRowNum() ? sheet.getLastRowNum() : toIndex;
		List<Row> rows = null;
		if (toIndex >= fromIndex) {
			rows = new ArrayList<Row>();
			for (int row_index = fromIndex; row_index <= toIndex; row_index++) {
				rows.add(sheet.getRow(row_index));
			}
		}
		return rows;
	}

	@Override
 	public int getFirstRowNum(Sheet sheet) throws IOException {
		return sheet.getFirstRowNum();
 	}
 	
 	@Override
 	public int getLastRowNum(Sheet sheet) throws IOException {
 		return sheet.getLastRowNum();
 	}
 	
 	@Override
 	public String getSheetName(Sheet sheet) {
 		return sheet.getSheetName();
 	}
	
	@Override
	public Row getRow(File file, int sheetNum, int rowNum) throws IOException {
		Sheet sheet = getSheet(file, sheetNum);
		return getRow(sheet, rowNum);
	}

	@Override
	public Row getRow(File file, String sheetName, int rowNum) throws IOException {
		Sheet sheet = getSheet(file, sheetName);
		return getRow(sheet, rowNum);
	}

	@Override
	public Row getRow(Sheet sheet, int rowNum) throws IOException {
		Row row = null;
		if (sheet != null) {
			row = sheet.getRow(rowNum);
		}
		return row;
	}

	@Override
	public List<Cell> getColumn(Sheet sheet, int colIndex) throws IOException {
		List<Cell> columns = null;
		if (sheet != null) {
			columns = new ArrayList<Cell>();
			for (int i = sheet.getFirstRowNum(); i < sheet.getLastRowNum(); i++) {
				columns.add(sheet.getRow(i).getCell(colIndex, MissingCellPolicy.CREATE_NULL_AS_BLANK));
			}
		}
		return columns;
	}

	@Override
	public List<Cell> getCells(Row row) throws IOException {
		List<Cell> cells = null;
		if (row != null) {
			cells = new ArrayList<Cell>();
			// 获得第一行的单元格
			Iterator<Cell> ite = row.cellIterator();
			while (ite.hasNext()) {
				cells.add(ite.next());
			}
		}
		return cells;
	}

	@Override
	public Cell getCell(Sheet sheet, int rowNum, int colIndex) throws IOException {
		Row row = getRow(sheet, rowNum);
		return getCell(row, colIndex);
	}

	@Override
	public Cell getCell(Row row, int colIndex) throws IOException {
		Cell cell = null;
		if (row != null) {
			cell = row.getCell(colIndex, MissingCellPolicy.CREATE_NULL_AS_BLANK);
		}
		return cell;
	}

	/**
	 * 
	 * Excel存储日期、时间均以数值类型进行存储，读取时POI先判断是是否是数值类型，再进行判断转化
	 * 1、数值格式(CELL_TYPE_NUMERIC):
	 * 1.纯数值格式：getNumericCellValue() 直接获取数据
	 * 2.日期格式：处理yyyy-MM-dd, d/m/yyyy h:mm, HH:mm 等不含文字的日期格式
	 * 1).判断是否是日期格式：HSSFDateUtil.isCellDateFormatted(cell)
	 * 2).判断是日期或者时间
	 * 
	 * cell.getCellStyle().getDataFormat() == HSSFDataFormat.getBuiltinFormat("h:mm")
	 * 
	 * OR:
	 * 
	 * cell.getCellStyle().getDataFormat() == HSSFDataFormat.getBuiltinFormat("yyyy-MM-dd")
	 * 
	 * 3.自定义日期格式：处理yyyy年m月d日,h时mm分,yyyy年m月等含文字的日期格式
	 * 
	 * 判断cell.getCellStyle().getDataFormat()值，解析数值格式：yyyy年m月d日----->31；m月d日---->58；h时mm分--->32等
	 * 
	 * 万能处理方案： 所有日期格式都可以通过getDataFormat()值来判断 
	 * yyyy-MM-dd------14
	 * yyyy年m月d日-----31 
	 * yyyy年m月--------57 
	 * m月d日 ----------58
	 * HH:mm-----------20 
	 * h时mm分 --------32	 * @param cell
	 * @param cellType
	 * @return
	 */
	public String getCellValue(Cell cell, CellType cellType) {
		String result = "";
		try {
			if (cell != null) {
			switch (cellType) {
				// 数字类型
				case NUMERIC: {
					double value = cell.getNumericCellValue();
					// 处理自定义日期格式：m月d日(通过判断单元格的格式id解决，id的值是58)
					short format = cell.getCellStyle().getDataFormat();
					SimpleDateFormat sdf = null;
					// Excel存储日期、时间均以数值类型进行存储，读取时POI先判断是是否是数值类型，再进行判断转化
					// 数字日期型
					// 1.日期格式：处理yyyy-MM-dd, d/m/yyyy h:mm, HH:mm 等不含文字的日期格式
					if (DateUtil.isCellDateFormatted(cell)) {// 处理日期格式、时间格式
						// 1).判断是否是日期格式：HSSFDateUtil.isCellDateFormatted(cell)
						// 2).判断是日期或者时间
						if (format == HSSFDataFormat.getBuiltinFormat("h:mm")) {
							result = DateFormatUtils.format(cell.getDateCellValue(), "HH:mm");
						} else {
							result = DateFormatUtils.format(cell.getDateCellValue(), "yyyy-MM-dd");
						}
					} else if (format == 14) {
						result = DateFormatUtils.format(DateUtil.getJavaDate(value), "yyyy-MM-dd");
					} else if (format == 20) {
						result = DateFormatUtils.format(DateUtil.getJavaDate(value), "HH:mm");
					} else if (format == 31) {
						result = DateFormatUtils.format(DateUtil.getJavaDate(value), "yyyy年m月d日");
					} else if (format == 32) {
						result = DateFormatUtils.format(DateUtil.getJavaDate(value), "h时mm分");
					} else if (format == 57) {
						result = DateFormatUtils.format(DateUtil.getJavaDate(value), "yyyy年m月");
					} else if (format == 58) {
						result = DateFormatUtils.format(DateUtil.getJavaDate(value), "m月d日");
					} else {
						CellStyle style = cell.getCellStyle();
						DecimalFormat format3 = new DecimalFormat();
						String temp = style.getDataFormatString();
						// 单元格设置成常规
						if (temp.equals("General")) {
							format3.applyPattern("#");
						}
						result = format3.format(value);
						// .纯数值格式：getNumericCellValue() 直接获取数据
						result = new BigDecimal(result).toPlainString(); // 數字格式, 避免出現科學符號
					}
					break;
				}
				// String类型
				case STRING: {
					// 字串型
					result = cell.getStringCellValue();
					break;
				}
				case FORMULA: {
					// 公式型
					// POI计算公式，得出结果
					FormulaEvaluator evaluator = POIFormulaEvaluatorFactory.getFormulaEvaluator(cell);
					CellType type = evaluator.evaluateFormulaCell(cell);
					result = getCellValue(cell, type);
					break;
				}
				case BLANK: {
					// 空白型
					result = "";
					break;
				}
				case BOOLEAN: {
					// 布尔型
					result = Boolean.toString(cell.getBooleanCellValue());
					break;
				}
				case ERROR: {
					// Error
					result = "";
					break;
				}
			default:
				break;
				}
			}
		} catch (Exception err2) {
			result = "";
		}
		return result;
	}
	
	@Override
	public RowMapMapper<Row> getRowMapper(Row row, List<Map<String, String>> columnList, int rowNum) {
		return this.getRowMapper(row, columnList, rowNum, Boolean.FALSE);
	}

	@Override
	public RowMapMapper<Row> getRowMapper(Row row, List<Map<String, String>> columnList, int rowNum, BiFunction<ColumnWrapper<Cell>, String, String> transformer) {
		return this.getRowMapper(row, columnList, rowNum, Boolean.FALSE, transformer);
	}
	
	@Override
	public RowMapMapper<Row> getRowMapper(Row row, List<Map<String, String>> columnList, int rowNum, boolean title) {
		return this.getRowMapper(row, columnList, rowNum, title, null);
	}
	
	@Override
	public RowMapMapper<Row> getRowMapper(Row row, List<Map<String, String>> columnList, int rowNum, boolean title, BiFunction<ColumnWrapper<Cell>, String, String> transformer) {
		
		RowMap rowMap = new RowMap();
		
		// 循环列信息
		for (Map<String, String> columnMap : columnList) {
			// 列名称
			String column = StringUtils.lowerCase(MapUtils.getString(columnMap, "column"));
			// 列索引
			int cellnum = MapUtils.getIntValue(columnMap, "cellnum");
			// 获取列索引对应单元格对象
			Cell cell = row.getCell(cellnum, MissingCellPolicy.CREATE_NULL_AS_BLANK);
			if(cell != null) {
				// 单元格数据
				String value = getCellValue(cell, cell.getCellType());
				// 添加行数据
				rowMap.put(column, value);
			}
		}
		// 循环处理数据，按需进行数据转换
		for (Map<String, String> columnMap : columnList) {
			// 列转换配置
			String transSQL = MapUtils.getString(columnMap, "trans");
			// 尝试进行数据转换
			if(null != transformer && StringUtils.isNotBlank(transSQL)) {
				// 列名称
				String column = StringUtils.lowerCase(MapUtils.getString(columnMap, "column"));
				// 列索引
				int cellnum = MapUtils.getIntValue(columnMap, "cellnum");
				// 获取列索引对应单元格对象
				Cell cell = row.getCell(cellnum, MissingCellPolicy.CREATE_NULL_AS_BLANK);
				if(cell != null) {
					try {
						// 数据值
						String value = MapUtils.getString(rowMap, column);
						// 尝试进行数据转换
						ColumnWrapper<Cell> colWrapper = new ColumnWrapper<Cell>(cell, columnMap, rowMap, rowNum, cellnum);
						value = transformer.apply(colWrapper, value);
						// 添加行数据
						rowMap.put(column, value);
					} catch (Exception e) {
					}
				}
			}
		}
		 
		return new RowMapMapper<Row>(row, columnList,  rowNum, rowMap, title);
	}

	@Override
	public String getCellValue(Row row, int cellnum) {
		Cell cell = row.getCell(cellnum, MissingCellPolicy.CREATE_NULL_AS_BLANK);
		return this.getCellValue(cell, cell.getCellType());
	}

}
