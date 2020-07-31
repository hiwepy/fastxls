package com.github.hiwepy.fastxls.core;

import java.util.List;

import com.github.hiwepy.fastxls.core.annotation.Column;
import com.github.hiwepy.fastxls.core.annotation.Row;
import com.github.hiwepy.fastxls.core.annotation.SubColumns;
/*
导出实现一行不变的导出
一行对应多个字行的导出
   --
-- -- --
   --

      --
-- -- --
      --

--
-- -- -- 
--

市场信息采集系统 MICS
Market information collection system

async
sync
dynamic

数据同步 DataSync
auto-sync:"false" 	自动同步
interval		自动同步间隔时间
lazy-sync:"false"	是否延时同步
delay:""		延时同步时间

Data Synchronism 
synchronize

系统维护：
	系统权限维护(角色权限组件)
	基础代码维护(基础代码组件)
	日志信息(日志信息组件)
	数据同步(数据同步组件)



市场调查管理
 
  调查问卷管理
  
  如此的几个注解添加后 可以实现表格任意区域值与java对象的相互转换
  
  */
@Row(description="此注解的作用是对此对象进行标记，意思Excel的一行（非子行的时候使用）")
public class TestBean {
	
	@Column(index=1,key="NAME")
	private String name;
	
	@Column(index=2,key="SB")
	private String sb;
	
	@SubColumns(firstCol=3,lastCol=6)
	private List<TestSubBean> jtcyList;
	
	@Column(index=7,key="ZHCJ")
	private String zhcj;
	
	@SubColumns(firstCol=8,lastCol=10)
	private List<TestSubBean> ahxx;

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

	public List<TestSubBean> getJtcyList() {
		return jtcyList;
	}

	public void setJtcyList(List<TestSubBean> jtcyList) {
		this.jtcyList = jtcyList;
	}

	public List<TestSubBean> getAhxx() {
		return ahxx;
	}

	public void setAhxx(List<TestSubBean> ahxx) {
		this.ahxx = ahxx;
	}

	
}
