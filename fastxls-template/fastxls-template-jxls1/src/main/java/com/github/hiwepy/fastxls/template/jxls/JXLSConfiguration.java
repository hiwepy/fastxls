package com.github.hiwepy.fastxls.template.jxls;

import net.sf.jxls.transformer.Configuration;

public class JXLSConfiguration extends Configuration {
	
	public JXLSConfiguration() {
		super();
		//registerTagLib(new JxTaglib(), "jx");
    }

    public JXLSConfiguration(String startExpressionToken, String endExpressionToken, String startFormulaToken, String endFormulaToken, String metaInfoToken) {
    	super(startExpressionToken, endExpressionToken, startFormulaToken, endFormulaToken, metaInfoToken);
    }

    public JXLSConfiguration(String startExpressionToken, String endExpressionToken, String startFormulaToken, String endFormulaToken, String metaInfoToken, boolean isUTF16) {
    	super(startExpressionToken, endExpressionToken, startFormulaToken, endFormulaToken, metaInfoToken, isUTF16);
    }
    
}
