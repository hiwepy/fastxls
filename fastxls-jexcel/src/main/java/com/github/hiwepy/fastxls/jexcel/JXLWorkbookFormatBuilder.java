package com.github.hiwepy.fastxls.jexcel;

import org.apache.commons.lang3.builder.Builder;

import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WriteException;
import com.github.hiwepy.fastxls.jexcel.utils.FormatSort;

public class JXLWorkbookFormatBuilder implements Builder<WritableCellFormat> {

	private FormatSort rank = FormatSort.BOLD_BORDER_ALL_THIN;
	private Alignment align = Alignment.CENTRE;
	private VerticalAlignment valign = VerticalAlignment.CENTRE;
	private int fontSize = 12;

	public JXLWorkbookFormatBuilder rank(FormatSort rank) {
		this.rank = rank;
		return this;
	}

	public JXLWorkbookFormatBuilder align(Alignment align) {
		this.align = align;
		return this;
	}

	public JXLWorkbookFormatBuilder valign(VerticalAlignment valign) {
		this.valign = valign;
		return this;
	}

	public JXLWorkbookFormatBuilder fontSize(int fontSize) {
		this.fontSize = fontSize;
		return this;
	}

	public static WritableCellFormat titleFormat(int fontSize) throws WriteException {
		return new JXLWorkbookFormatBuilder().align(Alignment.CENTRE).rank(FormatSort.BOLD_BORDER_ALL_THIN)
				.fontSize(fontSize).valign(VerticalAlignment.CENTRE).build();
	}

	@Override
	public WritableCellFormat build() {
		try {
			WritableCellFormat format = null;
			WritableFont font = null;
			switch (rank) {
				case NORMAL_BORDER_NONE_THIN:
					font = new WritableFont(WritableFont.TIMES, fontSize);
					break;
				case NORMAL_BORDER_TOP_THIN:
					font = new WritableFont(WritableFont.TIMES, fontSize);
					format = new WritableCellFormat(font);
					format.setAlignment(align);
					format.setVerticalAlignment(valign);
					format.setBorder(Border.TOP, BorderLineStyle.THIN);
					break;
				case NORMAL_BORDER_BOTTOM_THIN:
					font = new WritableFont(WritableFont.TIMES, fontSize);
					format = new WritableCellFormat(font);
					format.setAlignment(align);
					format.setVerticalAlignment(valign);
					format.setBorder(Border.BOTTOM, BorderLineStyle.THIN);
					break;
				case NORMAL_BORDER_LEFT_THIN:
					font = new WritableFont(WritableFont.TIMES, fontSize);
					format = new WritableCellFormat(font);
					format.setAlignment(align);
					format.setVerticalAlignment(valign);
					format.setBorder(Border.LEFT, BorderLineStyle.THIN);
					break;
				case NORMAL_BORDER_RIGHT_THIN:
					font = new WritableFont(WritableFont.TIMES, fontSize);
					format = new WritableCellFormat(font);
					format.setAlignment(align);
					format.setVerticalAlignment(valign);
					format.setBorder(Border.RIGHT, BorderLineStyle.THIN);
					break;
				case NORMAL_BORDER_ALL_THIN:
					font = new WritableFont(WritableFont.TIMES, fontSize);
					format = new WritableCellFormat(font);
					format.setAlignment(align);
					format.setVerticalAlignment(valign);
					format.setBorder(Border.ALL, BorderLineStyle.THIN);
					break;
				case BOLD_BORDER_NONE_THIN:
					font = new WritableFont(WritableFont.TIMES, fontSize);
					format = new WritableCellFormat(font);
					format.setAlignment(align);
					format.setVerticalAlignment(valign);
					format.setBorder(Border.NONE, BorderLineStyle.THIN);
					break;
				case BOLD_BORDER_TOP_THIN:
					font = new WritableFont(WritableFont.TIMES, fontSize, WritableFont.BOLD);
					format = new WritableCellFormat(font);
					format.setAlignment(Alignment.RIGHT);
					format.setVerticalAlignment(VerticalAlignment.CENTRE);
					format.setBorder(Border.TOP, BorderLineStyle.THIN);
					break;
				case BOLD_BORDER_BOTTOM_THIN:
					font = new WritableFont(WritableFont.TIMES, fontSize, WritableFont.BOLD);
					format = new WritableCellFormat(font);
					format.setAlignment(align);
					format.setVerticalAlignment(valign);
					format.setBorder(Border.BOTTOM, BorderLineStyle.THIN);
					break;
				case BOLD_BORDER_LEFT_THIN:
					font = new WritableFont(WritableFont.TIMES, fontSize, WritableFont.BOLD);
					format = new WritableCellFormat(font);
					format.setAlignment(align);
					format.setVerticalAlignment(valign);
					format.setBorder(Border.LEFT, BorderLineStyle.THIN);
					break;
				case BOLD_BORDER_RIGHT_THIN:
					font = new WritableFont(WritableFont.TIMES, fontSize, WritableFont.BOLD);
					format = new WritableCellFormat(font);
					format.setAlignment(align);
					format.setVerticalAlignment(valign);
					format.setBorder(Border.RIGHT, BorderLineStyle.THIN);
					break;
				case BOLD_BORDER_ALL_THIN:
					font = new WritableFont(WritableFont.TIMES, fontSize, WritableFont.BOLD);
					format = new WritableCellFormat(font);
					format.setAlignment(align);
					format.setVerticalAlignment(valign);
					format.setBorder(Border.ALL, BorderLineStyle.THIN);
					break;
			default:
				break;
				}
			return format;
		} catch (WriteException e) {
			e.printStackTrace();
		}
		return null;
	}

}
