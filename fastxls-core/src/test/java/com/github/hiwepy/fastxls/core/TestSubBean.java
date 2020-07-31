package com.github.hiwepy.fastxls.core;

import com.github.hiwepy.fastxls.core.annotation.Column;
import com.github.hiwepy.fastxls.core.annotation.SubRow;

@SubRow(description="此注解的作用是对此对象进行标记，意思Excel的一行（作为子行的时候使用）")
public class TestSubBean {
	
	@Column(index=1,key="NAME")
	private String name;
	
	@Column(index=2,key="SB")
	private String sb;
	
	@Column(index=3,key="ZHCJ")
	private String zhcj;
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSb() {
		return sb;
	}

	public void setSb(String sb) {
		this.sb = sb;
	}

	public String getZhcj() {
		return zhcj;
	}

	public void setZhcj(String zhcj) {
		this.zhcj = zhcj;
	}

}
