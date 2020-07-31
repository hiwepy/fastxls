/*
 * Copyright (c) 2018, hiwepy (https://github.com/hiwepy).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.github.hiwepy.fastxls.core.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 扩展org.apache.commons.io.IOUtils工具对象
 */
public abstract class IOUtils extends org.apache.commons.io.IOUtils {

	public static final int BUFFER_SIZE = 1024 * 4;
	private final static Logger LOG = LoggerFactory.getLogger(IOUtils.class);

	// ---------------------------------------------------------------------
	// Stream Warp methods for java.io.InputStream / java.io.OutputStream
	// ---------------------------------------------------------------------

	public static InputStream toBufferedInputStream(File localFile, int bufferSize) throws IOException {
		// 包装文件输入流
		return toBufferedInputStream(new FileInputStream(localFile), bufferSize);
	}

	public static InputStream toBufferedInputStream(InputStream input) throws IOException {
		if (isBuffered(input)) {
			return (BufferedInputStream) input;
		} else {
			return org.apache.commons.io.output.ByteArrayOutputStream.toBufferedInputStream(input);
		}
	}

	public static InputStream toBufferedInputStream(InputStream input, int bufferSize) throws IOException {
		if (isBuffered(input)) {
			return (BufferedInputStream) input;
		} else {
			if (bufferSize > 0) {
				return new BufferedInputStream(input, bufferSize);
			}
			return new BufferedInputStream(input);
		}
	}

	public static OutputStream toBufferedOutputStream(OutputStream output) throws IOException {
		if (isBuffered(output)) {
			return (BufferedOutputStream) output;
		} else {
			return toBufferedOutputStream(output, BUFFER_SIZE);
		}
	}

	public static OutputStream toBufferedOutputStream(OutputStream output, int bufferSize) throws IOException {
		if (isBuffered(output)) {
			return (BufferedOutputStream) output;
		} else {
			if (bufferSize > 0) {
				return new BufferedOutputStream(output, bufferSize);
			}
			return new BufferedOutputStream(output);
		}
	}

	public static boolean isBuffered(InputStream input) {
		return input instanceof BufferedInputStream;
	}

	public static boolean isBuffered(OutputStream output) {
		return output instanceof BufferedOutputStream;
	}

	public static BufferedReader toBufferedReader(InputStream input) {
		return new BufferedReader(new InputStreamReader(input));
	}

	public static BufferedWriter toBufferedWriter(OutputStream output) {
		return new BufferedWriter(new OutputStreamWriter(output));
	}

	public static boolean isPrint(OutputStream output) {
		return output instanceof PrintStream;
	}

	public static InputStream toByteArrayInputStream(byte[] inputBytes) {
		return new ByteArrayInputStream(inputBytes);
	}

	public static InputStream toByteArrayInputStream(String text) {
		return toByteArrayInputStream(text.getBytes());
	}

	public static DataInputStream toDataInputStream(InputStream input) {
		return isDataInput(input) ? (DataInputStream) input : new DataInputStream(input);
	}

	private static boolean isDataInput(InputStream input) {
		return input instanceof DataInputStream;
	}

	public static DataOutputStream toDataOutputStream(OutputStream output) {
		return isDataOutput(output) ? (DataOutputStream) output : new DataOutputStream(output);
	}

	private static boolean isDataOutput(OutputStream output) {
		return output instanceof DataOutputStream;
	}

	public static FileInputStream toFileInputStream(File file) throws IOException {
		return new FileInputStream(file);
	}

	/**
	 * 获得一个FileOutputStream对象
	 * 
	 * @param file
	 * @param append
	 *            ： true:向文件尾部追见数据; false:清楚旧数据
	 * @return
	 * @throws FileNotFoundException
	 */
	public static FileOutputStream toFileOutputStream(File file, boolean append) throws FileNotFoundException {
		return new FileOutputStream(file, append);
	}

	public static PrintStream toPrintStream(File file) {
		FileOutputStream stream = null;
		try {
			stream = new FileOutputStream(file, true);
			return toPrintStream(stream);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public static PrintStream toPrintStream(OutputStream output) {
		return isPrint(output) ? (PrintStream) output : new PrintStream(output);
	}

}
