package com.github.hiwepy.fastxls.poi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.poi.ss.usermodel.Workbook;

import com.github.hiwepy.fastxls.core.utils.Assert;

/**
 * Workbook写操作方法对象
 */
public class POIWorkbookWriter {

	public void writeToPath(Workbook workbook, String outPath) throws IOException {
		Assert.notNull(workbook, " workbook is not specified!");
		Assert.notNull(outPath, " outPath is not specified!");
		writeToFile(workbook, new File(outPath));
	}

	public void writeToFile(Workbook workbook, File outFile) throws IOException {
		writeToStream(workbook, new FileOutputStream(outFile));
	}

	public void writeToStream(Workbook workbook, OutputStream output) throws IOException {
		Assert.notNull(workbook, " workbook is not specified!");
		Assert.notNull(output, " os is not specified!");
		try {
			workbook.write(output);
			output.flush();
		} finally {
			try {
				workbook.close();
				if (output != null) {
					output.close();
				}
			} catch (final IOException ioe) {
				// ignore
			}
		}
	}

}
