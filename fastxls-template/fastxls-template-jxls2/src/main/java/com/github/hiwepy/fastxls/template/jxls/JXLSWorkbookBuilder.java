package com.github.hiwepy.fastxls.template.jxls;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.jxls.common.Context;
import org.jxls.expression.JexlExpressionEvaluator;
import org.jxls.transform.Transformer;
import org.jxls.util.JxlsHelper;

import com.github.hiwepy.fastxls.core.builder.WorkbookBuilder;
import com.github.hiwepy.fastxls.core.model.CellModel;

/**
 */
public class JXLSWorkbookBuilder<T extends CellModel> extends WorkbookBuilder<T, Void> {

	private InputStream template;
	
	public JXLSWorkbookBuilder<T> template(String template) {
		this.template = IOUtils.toInputStream(template, Charset.defaultCharset());
		return this;
	}
	
	public JXLSWorkbookBuilder<T> template(File template) throws IOException {
		this.template = new FileInputStream(template);
		return this;
	}
	
	public JXLSWorkbookBuilder<T> template(InputStream template) throws IOException {
		this.template = template;
		return this;
	}
	
	@Override
	public Void build() {
		try {
			
			Context context = new Context();
			if(this.getWorkbook() != null) {
				context.putVar("workbook", this.getWorkbook());
			}
			else if(this.getSheets() == null) {
				context.putVar("sheets", this.getSheets());
			}
 
			try (InputStream input = template;) {

				JxlsHelper jxlsHelper = JxlsHelper.getInstance();
				Transformer transformer = jxlsHelper.createTransformer(input, this.getOutput());
				
				JexlExpressionEvaluator evaluator = (JexlExpressionEvaluator) transformer.getTransformationConfig()
						.getExpressionEvaluator();

				Map<String, Object> funcs = new HashMap<String, Object>();
				// funcs.put("utils", new JxlsUtils()); //添加自定义功能
				// evaluator.getJexlEngine().setFunctions(funcs);
				jxlsHelper.processTemplate(context, transformer);
				
				// 释放资源
				this.getOutput().flush();
				this.getOutput().close();
			}
			 
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return null;
	}

}
