package com.github.hiwepy.fastxls.core;

import java.util.List;

import com.github.hiwepy.fastxls.core.annotation.Rows;
import com.github.hiwepy.fastxls.core.annotation.Sheet;
import com.github.hiwepy.fastxls.core.annotation.Title;
@Sheet(name="test-sheet-2012-01")
public class SheetBean {
	
	@Title("2012-2013----66565656")
	private String title;
	
	@Rows(startRow=1)
	private List<TestBean> data;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<TestBean> getData() {
		return data;
	}

	public void setData(List<TestBean> data) {
		this.data = data;
	}
	
	
	
	
}
