package com.github.hiwepy.fastxls.struts2.result;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.result.StrutsResultSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.util.ValueStack;

import com.github.hiwepy.fastxls.core.model.CellModel;
import com.github.hiwepy.fastxls.core.model.SheetModel;
import com.github.hiwepy.fastxls.core.model.WorkBookModel;
import com.github.hiwepy.fastxls.core.property.ExportProperties;
import com.github.hiwepy.fastxls.core.utils.IOUtils;
import com.github.hiwepy.fastxls.jexcel.JXLWorkbookBuilder;

/**
 * 
 * <b>Example:</b>
 * 
 * <pre>
 * <!-- START SNIPPET: example -->
 * &lt;result name="success" type="jxlworkbook"&gt;
 *   &lt;param name="contentType"&gt;application/vnd.ms-excel&lt;/param&gt;
 *   &lt;param name="contentCharSet"&gt;UTF-8&lt;/param&gt;
 *   &lt;param name="contentDisposition"&gt;attachment;filename="${fileName}"&lt;/param&gt;
 *   &lt;param name="allowThreadPool"&gt;false&lt;/param&gt;
 *   &lt;param name="bufferSize"&gt;1024&lt;/param&gt;
 *   &lt;param name="inputArgument"&gt;argumentModel&lt;/param&gt;
 *   &lt;param name="inputDataModel"&gt;dataModel&lt;/param&gt;
 *   &lt;param name="inputSheetModel"&gt;sheetModel&lt;/param&gt;
 *   &lt;param name="inputDataList"&gt;dataList&lt;/param&gt;
 * &lt;/result&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 * 
 */
@SuppressWarnings("serial")
public class JXLWorkbookResult extends StrutsResultSupport {

	private final static Logger LOG = LoggerFactory.getLogger(JXLWorkbookResult.class);
	protected StringBuilder message = new StringBuilder();
	protected String contentType = "application/vnd.ms-excel";
	protected String contentCharSet = "UTF-8";
	protected String contentDisposition = "inline";
	protected String userKey = "user";
	protected boolean useThreadPool = false;
	protected int bufferSize = 1024;
	
	// 用于导出的必需参数
	protected ExportProperties properties;
	// 用于导出的Workbook数据
	protected String inputDataModel = "dataModel";
	protected WorkBookModel<CellModel> dataModel;
	// 用于导出的sheet数据
	protected String inputSheetModel = "sheetModel";
	protected SheetModel<CellModel> sheetModel;

	// CONSTRUCTORS ----------------------------

	public JXLWorkbookResult(ExportProperties properties) {
		super();
		this.properties = properties;
	}

	// ACCESSORS ----------------------------


	/**
	 * @return Returns the Content-disposition header value.
	 */
	public String getContentDisposition() {
		return contentDisposition;
	}

	/**
	 * @param contentDisposition
	 *            the Content-disposition header value to use.
	 */
	public void setContentDisposition(String contentDisposition) {
		this.contentDisposition = contentDisposition;
	}

	/**
	 * @return Returns the charset specified by the user
	 */
	public String getContentCharSet() {
		return contentCharSet;
	}

	/**
	 * @param contentCharSet
	 *            the charset to use on the header when sending the stream
	 */
	public void setContentCharSet(String contentCharSet) {
		this.contentCharSet = contentCharSet;
	}

	public boolean isUseThreadPool() {
		return useThreadPool;
	}

	public void setUseThreadPool(boolean useThreadPool) {
		this.useThreadPool = useThreadPool;
	}

	public int getBufferSize() {
		return bufferSize;
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	public String getInputDataModel() {
		return inputDataModel;
	}

	public void setInputDataModel(String inputDataModel) {
		this.inputDataModel = inputDataModel;
	}

	public String getInputSheetModel() {
		return inputSheetModel;
	}

	public void setInputSheetModel(String inputSheetModel) {
		this.inputSheetModel = inputSheetModel;
	}
	
	// OTHER METHODS -----------------------


	/**
	 * Executes the result. Writes the given chart as a PNG or JPG to the
	 * servlet output stream.
	 * 
	 * @param invocation
	 *            an encapsulation of the action execution state.
	 * @throws Exception
	 *             if an error occurs when creating or writing the chart to the
	 *             servlet output stream.
	 */
	@SuppressWarnings("unchecked")
	public void doExecute(String finalLocation, ActionInvocation invocation) throws Exception {

        // Override any parameters using values on the stack
        resolveParamsFromStack(invocation.getStack(), invocation);
        
        if (dataModel == null) {
            // Find the WorkBookModel<CellModel> from the invocation variable stack
        	dataModel = (WorkBookModel<CellModel>) invocation.getStack().findValue(conditionalParse(inputDataModel, invocation));
        }
        if (sheetModel == null) {
            // Find the SheetModel<CellModel> from the invocation variable stack
        	sheetModel = (SheetModel<CellModel>) invocation.getStack().findValue(conditionalParse(inputSheetModel, invocation));
        }
        if (dataModel == null && sheetModel == null) {
        	message.delete(0, message.length());
        	message.append("Can not find " +"");
            message.append(" WorkBookModel<CellModel> with the name [" + inputDataModel + "]");
            message.append(" or SheetModel<CellModel> with the name [" + inputSheetModel + "] ");
            message.append(" in the invocation stack.");
            message.append(" Check the <param name=\"inputDataModel\"> or <param name=\"inputSheetModel\"> tag specified for this action.");
            LOG.error(message.toString());
            throw new IllegalArgumentException(message.toString());
        }
        
		// Find the Response in context
        HttpServletResponse response = ServletActionContext.getResponse();
        // Find the Session in context
        HttpSession  session  = ServletActionContext.getRequest().getSession();
        // Set the content type
        response.setContentType(contentType+";charset="+contentCharSet);
        // Set the content-disposition
        if (contentDisposition != null) {
        	response.addHeader("Content-Disposition", conditionalParse(contentDisposition, invocation));
        }
        
        // get login user
        String user = (String) session.getAttribute(userKey);
        //find tempdir 
        File tmpDir = new File(properties.getTmpdir(), user) ;
        // 拼接临时文件路径
        File tmpFile = new File(tmpDir, System.currentTimeMillis() + "_tmp.xls");
        // 创建临时文件输出流
        FileOutputStream fileOut = new FileOutputStream(tmpFile);
		if(dataModel!=null){
			if(dataModel.getSuffix() == null){
				dataModel.setSuffix(properties.getSuffix());
			}
			// 构建Workbook
			new JXLWorkbookBuilder<CellModel>()
				.wookbook(dataModel)
				.output(fileOut)
				.maxRow(properties.getMaxRow())
				.namePrefix(properties.getNamePrefix())
				.nameSuffix(properties.getNameSuffix())
				.suffix(dataModel.getSuffix())
				.tmpdir(tmpDir)
				.build();
		} else if(sheetModel!=null){
			// 构建Workbook
			new JXLWorkbookBuilder<CellModel>()
				.sheet(sheetModel)
				.output(fileOut)
				.maxRow(properties.getMaxRow())
				.namePrefix(properties.getNamePrefix())
				.nameSuffix(properties.getNameSuffix())
				.suffix(dataModel.getSuffix())
				.tmpdir(tmpDir)
				.build();
		}
		
		// Copy input to output
		LOG.debug("Streaming to output buffer +++ START +++");
		
    	// Get the outputstream
		OutputStream output = response.getOutputStream();
		// Copy input to output
		FileInputStream input = IOUtils.toFileInputStream(tmpFile);
		IOUtils.copy(input, output, bufferSize);
		IOUtils.closeQuietly(input);
		IOUtils.closeQuietly(output);
    	 // delete file if allow clear
    	FileUtils.forceDelete(tmpFile);
		LOG.debug("Streaming to output buffer +++ END +++");
		
		
    }
	
	/**
	 * Tries to lookup the parameters on the stack. Will override any existing
	 * parameters
	 * 
	 * @param stack The current value stack
	 */
	protected void resolveParamsFromStack(ValueStack stack, ActionInvocation invocation) {
		//输出数据流需要的参数
		String disposition = stack.findString("contentDisposition");
		if (disposition != null) {
			setContentDisposition(disposition);
		}
		
		if (contentCharSet != null) {
			contentCharSet = conditionalParse(contentCharSet, invocation);
		} else {
			contentCharSet = stack.findString("contentCharSet");
		}
		
		Integer bufferSize = (Integer) stack.findValue("bufferSize", Integer.class);
        if (bufferSize != null) {
            setBufferSize(bufferSize.intValue());
        }
		
		String useThreadPool = stack.findString("useThreadPool");
        if (useThreadPool != null) {
        	setUseThreadPool(Boolean.getBoolean(useThreadPool));
        }
		
        String inputDataModel = stack.findString("inputDataModel");
        if (inputDataModel != null) {
            setInputSheetModel(inputDataModel);
        }
          
        String inputSheetModel = stack.findString("inputSheetModel");
        if (inputSheetModel != null) {
            setInputSheetModel(inputSheetModel);
        }
		
	}

}
