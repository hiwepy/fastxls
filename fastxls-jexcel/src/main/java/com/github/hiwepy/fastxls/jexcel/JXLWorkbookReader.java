package com.github.hiwepy.fastxls.jexcel;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.regex.Pattern;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jxl.BooleanCell;
import jxl.Cell;
import jxl.CellType;
import jxl.DateCell;
import jxl.ErrorCell;
import jxl.ErrorFormulaCell;
import jxl.FormulaCell;
import jxl.LabelCell;
import jxl.NumberCell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.biff.CellReferenceHelper;
import jxl.biff.formula.FormulaException;
import jxl.format.CellFormat;
import jxl.write.NumberFormat;
import com.github.hiwepy.fastxls.core.WorkbookReader;
import com.github.hiwepy.fastxls.core.model.ColumnWrapper;
import com.github.hiwepy.fastxls.core.model.RowMap;
import com.github.hiwepy.fastxls.core.model.RowMapMapper;
import com.github.hiwepy.fastxls.jexcel.utils.JXLWorkbookUtils;

/**
 * 构建xls文件内容
 */
public class JXLWorkbookReader implements WorkbookReader<Workbook, Sheet, Cell[], Cell> {

	/**
     * The following patterns are used in {@link #isADateFormat(int, String)}
     */
    private final Pattern date_ptrn1 = Pattern.compile("^\\[\\$\\-.*?\\]");
    private final Pattern date_ptrn2 = Pattern.compile("^\\[[a-zA-Z]+\\]");
    private final Pattern date_ptrn3 = Pattern.compile("^[yYmMdDhHsS\\-/,. :\"\\\\]+0?[ampAMP/]*$");
    //日期格式对象
 	protected SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd");
 	protected Logger LOG = LoggerFactory.getLogger(JXLWorkbookReader.class);

 	@Override
	public Sheet[] getSheets(InputStream input) throws IOException {
 		Sheet[] sheets = JXLWorkbookUtils.getWorkbook(input).getSheets();
 		if (sheets == null) {
 			return sheets;
 		}
 		List<Sheet> rtSheets = new ArrayList<>(); 
 		for (int i = 0; i < sheets.length; i++) {
 			if(!StringUtils.equalsIgnoreCase(sheets[i].getName(), "hidden")) {
 				rtSheets.add(sheets[i]);
 			}
		}
		return rtSheets.toArray(new Sheet[rtSheets.size()]);
	}

 	@Override
	public Sheet[] getSheets(File file) throws IOException {
 		Sheet[] sheets = JXLWorkbookUtils.getWorkbook(file).getSheets();
 		if (sheets == null) {
 			return sheets;
 		}
 		List<Sheet> rtSheets = new ArrayList<>(); 
 		for (int i = 0; i < sheets.length; i++) {
 			if(!StringUtils.equalsIgnoreCase(sheets[i].getName(), "hidden")) {
 				rtSheets.add(sheets[i]);
 			}
		}
		return rtSheets.toArray(new Sheet[rtSheets.size()]);
	}
 	
 	@Override
	public Sheet[] getSheets(Workbook wb) throws IOException{
		Sheet[] sheets = null;
		if(wb!=null){
			sheets = wb.getSheets();
		}
		if (sheets == null) {
 			return sheets;
 		}
		List<Sheet> rtSheets = new ArrayList<>(); 
 		for (int i = 0; i < sheets.length; i++) {
 			if(!StringUtils.equalsIgnoreCase(sheets[i].getName(), "hidden")) {
 				rtSheets.add(sheets[i]);
 			}
		}
		return rtSheets.toArray(new Sheet[rtSheets.size()]);
	}
 	
 	@Override
	public Sheet getSheet(File file, int sheetNum) throws IOException {
		return JXLWorkbookUtils.getWorkbook(file).getSheet(sheetNum);
	}
 	
 	@Override
	public Sheet getSheet(File file, String sheetName) throws IOException {
		return JXLWorkbookUtils.getWorkbook(file).getSheet(sheetName);
	}
	
 	@Override
	public Sheet getSheet(Workbook wb, int sheetNum) throws IOException {
		Sheet sheet = null;
		if(wb!=null){
			sheet = wb.getSheet(sheetNum);
		}
		return sheet;
	}
 	
 	@Override
	public Sheet getSheet(Workbook wb, String sheetName) throws IOException {
		Sheet sheet = null;
		if(wb!=null){
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
		info.put("name", sheet.getName());
		info.put("rows", sheet.getRows()+"");
		return info;
	}
	
 	@Override
 	public int getFirstRowNum(Sheet sheet) throws IOException {
 		return 0;
 	}
 	
 	@Override
 	public int getLastRowNum(Sheet sheet) throws IOException {
 		return sheet.getRows();
 	}
 	
 	@Override
 	public String getSheetName(Sheet sheet) {
 		return sheet.getName();
 	}
 	
 	@Override
	public List<Cell[]> getRows(Sheet sheet, int fromIndex, int toIndex) {
		fromIndex = fromIndex >= sheet.getRows() ? sheet.getRows() : fromIndex;
		toIndex = toIndex >= sheet.getRows() ? sheet.getRows() : toIndex;
		List<Cell[]> rows = null;
		if (toIndex > fromIndex) {
			rows = new ArrayList<Cell[]>();
			for (int row_index = fromIndex; row_index < toIndex; row_index++) {
				rows.add(sheet.getRow(row_index));
			}
		}
		return rows;
	}
	
	@Override
	public Cell[] getRow(File file, int sheetNum, int rowNum) throws IOException {
		Sheet sheet = getSheet(file, sheetNum);
		return getRow(sheet, rowNum);
	}

	@Override
	public Cell[] getRow(File file, String sheetName, int rowNum) throws IOException {
		Sheet sheet = getSheet(file, sheetName);
		return getRow(sheet, rowNum);
	}
	
	@Override
	public Cell[] getRow(Sheet sheet, int rowNum) {
		Cell[] row = null;
		if(sheet!=null){
			row = sheet.getRow(rowNum);
		}
		return row;
	}
	
	@Override
	public List<Cell> getColumn(Sheet sheet, int colIndex) {
		Cell[] column = null;
		if(sheet!=null){
			column = sheet.getColumn(colIndex);
		}
		return Arrays.asList(column);
	}

	@Override
	public List<Cell> getCells(Cell[] row) {
		List<Cell> cells = null;
		if(row!=null){
			cells = new ArrayList<Cell>();
			for (int i = 0; i < row.length; i++) {
				cells.add(row[i]);
			}
		}
		return cells;
	}
	
	@Override
	public Cell getCell(Sheet sheet, int rowNum, int colIndex) {
		Cell[] row = getRow(sheet, rowNum);
		return getCell(row, colIndex);
	}
	
	@Override
	public Cell getCell(Cell[] row, int colIndex) {
		Cell cell = null;
		if(row!=null){
			cell = row[colIndex];
		}
		return cell;
	}

	public String getCellValue(Cell cell){
		String value = "";
		if (cell == null) {
			value = "";
		} else {
			if (cell.getType() == CellType.BOOLEAN) {
				value = ((BooleanCell) cell).getValue() + "";
			} else if (cell.getType() == CellType.DATE) {
				DateFormat format = ((DateCell) cell).getDateFormat();
				value = format.format(((DateCell) cell).getDate());
			} else if (cell.getType() == CellType.EMPTY) {
				value = "";
			} else if (cell.getType() == CellType.ERROR) {
				value = ((ErrorCell) cell).getContents();
				value = ((ErrorFormulaCell) cell).getErrorCode() + "";
			} else if (cell.getType() == CellType.LABEL) {
				value = ((LabelCell) cell).getString();
			} else if (cell.getType() == CellType.NUMBER) {
				// 数字日期型
				if (isCellDateFormatted(cell)){
					String formatString = cell.getCellFormat().getFormat().getFormatString();
					jxl.write.DateFormat format = new jxl.write.DateFormat(formatString);
					// 日期格式
					value = format.getDateFormat().format(cell.getContents());
				}else {
					value = BigDecimal.valueOf(((NumberCell) cell).getValue()).toPlainString(); // 數字格式, 避免出現科學符號
				}
			} else if (cell.getType() == CellType.NUMBER_FORMULA
					|| cell.getType() == CellType.STRING_FORMULA
					|| cell.getType() == CellType.BOOLEAN_FORMULA
					|| cell.getType() == CellType.DATE_FORMULA
					|| cell.getType() == CellType.FORMULA_ERROR) {
				value = cell.getContents();
				FormulaCell nfc = (FormulaCell) cell;
				StringBuffer sb = new StringBuffer();
				CellReferenceHelper.getCellReference(cell.getColumn(), cell.getRow(),sb);
				try {
					System.out.println(" formula: " + nfc.getFormula() + " ; Formula in "  + sb.toString() +  " ;value:  " + cell.getContents());
				} catch (FormulaException e) {
					LOG.error(e.getMessage(),e.getCause());
				}
			} else {
				value = "";
			}
		}
		return value;
	}
	
	 /**
     *  Check if a cell contains a date
     *  Since dates are stored internally in Excel as double values
     *  we infer it is a date if it is formatted as such.
     *  @see #isADateFormat(int, String)
     *  @see #isInternalDateFormat(int)
     */
    public boolean isCellDateFormatted(Cell cell) {
        if (cell == null) return false;
        boolean bDate = false;
        double d = ((NumberCell) cell).getValue();
        if ( isValidExcelDate(d) ) {
        	CellFormat cellFormat = cell.getCellFormat();
        	if(cellFormat==null) return false;
        	String formatString =  cellFormat.getFormat().getFormatString();
        	NumberFormat format = new NumberFormat(formatString);
            int i = format.getFormatIndex();
            String f = format.getFormatString();
            bDate = isADateFormat(i, f);
        }
        return bDate;
    }
    
    /**
     * Given a double, checks if it is a valid Excel date.
     *
     * @return true if valid
     * @param  value the double value
     */

    public boolean isValidExcelDate(double value)
    {
        return (value > -Double.MIN_VALUE);
    }
    
    /**
     * Given a format ID and its format String, will check to see if the
     *  format represents a date format or not.
     * Firstly, it will check to see if the format ID corresponds to an
     *  internal excel date format (eg most US date formats)
     * If not, it will check to see if the format string only contains
     *  date formatting characters (ymd-/), which covers most
     *  non US date formats.
     *
     * @param formatIndex The index of the format, eg from ExtendedFormatRecord.getFormatIndex
     * @param formatString The format string, eg from FormatRecord.getFormatString
     * @see #isInternalDateFormat(int)
     */
    public boolean isADateFormat(int formatIndex, String formatString) {
        // First up, is this an internal date format?
        if(isInternalDateFormat(formatIndex)) {
            return true;
        }

        // If we didn't get a real string, it can't be
        if(formatString == null || formatString.length() == 0) {
            return false;
        }

        String fs = formatString;
        StringBuilder sb = new StringBuilder(fs.length());
        for (int i = 0; i < fs.length(); i++) {
            char c = fs.charAt(i);
            if (i < fs.length() - 1) {
                char nc = fs.charAt(i + 1);
                if (c == '\\') {
                    switch (nc) {
                        case '-':
                        case ',':
                        case '.':
                        case ' ':
                        case '\\':
                            // skip current '\' and continue to the next char
                            continue;
                    }
                } else if (c == ';' && nc == '@') {
                    i++;
                    // skip ";@" duplets
                    continue;
                }
            }
            sb.append(c);
        }
        fs = sb.toString();
        
        // If it starts with [$-...], then could be a date, but
        //  who knows what that starting bit is all about
        fs = date_ptrn1.matcher(fs).replaceAll("");
        // If it starts with something like [Black] or [Yellow],
        //  then it could be a date
        fs = date_ptrn2.matcher(fs).replaceAll("");
        // You're allowed something like dd/mm/yy;[red]dd/mm/yy
        //  which would place dates before 1900/1904 in red
        // For now, only consider the first one
        if(fs.indexOf(';') > 0 && fs.indexOf(';') < fs.length()-1) {
           fs = fs.substring(0, fs.indexOf(';'));
        }

        // Otherwise, check it's only made up, in any case, of:
        //  y m d h s - \ / , . :
        // optionally followed by AM/PM
        return date_ptrn3.matcher(fs).matches();
    }

    /**
     * Given a format ID this will check whether the format represents
     *  an internal excel date format or not.
     * @see #isADateFormat(int, java.lang.String)
     */
    public boolean isInternalDateFormat(int format) {
            switch(format) {
                // Internal Date Formats as described on page 427 in
                // Microsoft Excel Dev's Kit...
                case 0x0e:
                case 0x0f:
                case 0x10:
                case 0x11:
                case 0x12:
                case 0x13:
                case 0x14:
                case 0x15:
                case 0x16:
                case 0x2d:
                case 0x2e:
                case 0x2f:
                    return true;
            }
       return false;
    }
 
	@Override
	public RowMapMapper<Cell[]> getRowMapper(Cell[] row, List<Map<String, String>> columnList, int rowNum) {
		return this.getRowMapper(row, columnList, rowNum, Boolean.FALSE);
	}

	@Override
	public RowMapMapper<Cell[]> getRowMapper(Cell[] row, List<Map<String, String>> columnList, int rowNum, BiFunction<ColumnWrapper<Cell>, String, String> transformer) {
		return this.getRowMapper(row, columnList, rowNum, Boolean.FALSE, transformer);
	}
	
	@Override
	public RowMapMapper<Cell[]> getRowMapper(Cell[] row, List<Map<String, String>> columnList, int rowNum, boolean title) {
		return this.getRowMapper(row, columnList, rowNum, title, null);
	}
	
	@Override
	public RowMapMapper<Cell[]> getRowMapper(Cell[] row, List<Map<String, String>> columnList, int rowNum, boolean title, BiFunction<ColumnWrapper<Cell>, String, String> transformer) {
		
		RowMap rowMap = new RowMap();
		
		// 循环列信息
		for (Map<String, String> columnMap : columnList) {
			// 列名称
			String column = StringUtils.lowerCase(MapUtils.getString(columnMap, "column"));
			// 列索引
			int cellnum = MapUtils.getIntValue(columnMap, "cellnum");
			// 获取列索引对应单元格对象
			Cell cell = cellnum > 0 && cellnum < row.length ?  row[cellnum] : null;
			if(cell != null) {
				// 单元格数据
				String value = getCellValue(cell);
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
				Cell cell = cellnum > 0 && cellnum < row.length ?  row[cellnum] : null;
				if(cell != null) {
					try {
						// 数据值
						String value = MapUtils.getString(rowMap, column);
						ColumnWrapper<Cell> colWrapper = new ColumnWrapper<Cell>(cell, columnMap, rowMap, rowNum, cellnum);
						value = transformer.apply(colWrapper, value);
						// 添加行数据
						rowMap.put(column, value);
					} catch (Exception e) {
					}
				}
			}
		}
		return new RowMapMapper<Cell[]>(row, columnList,rowNum, rowMap, title);
	}
	
	@Override
	public String getCellValue(Cell[] row, int cellnum) {
		Cell cell = row.length > cellnum && cellnum > 0 ? row[cellnum] : null;
		if(null != cell){
			return getCellValue(cell);
		}
		return "";
	}
 
}
