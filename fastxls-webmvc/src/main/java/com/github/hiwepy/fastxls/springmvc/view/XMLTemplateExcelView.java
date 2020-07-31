/*
 * Copyright (c) 2010-2020, wandalong (hnxyhcwdl1003@163.com).
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
package com.github.hiwepy.fastxls.springmvc.view;


import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.LocalizedResourceHelper;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.AbstractView;

import net.sf.jxls.transformer.XLSTransformer;

public class XMLTemplateExcelView extends AbstractView {

	private static String content_type = "application/vnd.ms-excel; charset=UTF-8"; 
	private static String extension=".xml";
	
	private String fileName;
	private String location;
	private Map<String, Object> beanParams;
	private XLSTransformer transformer = new XLSTransformer();
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
	
	public void setBeanParams(Map<String, Object> beanParams) {
		this.beanParams = beanParams;
	}
	
	public XMLTemplateExcelView() {
		setContentType(content_type);
	}
	 
	/**
	 * @description	： TODO(描述这个方法的作用)
	 * @author 		： <a href="mailto:hnxyhcwdl1003@163.com">wandalong</a>
	 * @date 		：Feb 21, 2016 10:32:17 PM
	 * @param model
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	
	@Override
	protected void renderMergedOutputModel(Map<String, Object> model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		LocalizedResourceHelper helper = new LocalizedResourceHelper(getApplicationContext());
		Locale userLocale = RequestContextUtils.getLocale(request);
		Resource inputFile = helper.findLocalizedResource(location, extension, userLocale);
		
		Workbook workbook = transformer.transformXLS(inputFile.getInputStream(), beanParams);
		
		response.setContentType(getContentType());
		response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
		
		ServletOutputStream out=response.getOutputStream();
		workbook.write(out);
		out.flush();		
		
	}


}
