/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.core.model;

import java.io.Serializable;

/**
 * 	键值对模型对象
 */
@SuppressWarnings("serial")
public class PairModel implements Cloneable, Serializable, Comparable<PairModel> {
	
	/**
	 * 数据键
	 */
	protected String key;
	/**
	 * 数据值
	 */
	protected String value;

	public PairModel() {

	}

	public PairModel(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String toString() {
		return "key:" + key + " value:" + value;
	}

	@Override
	public int compareTo(PairModel o) {
		return this.key.compareTo(o.key);
	}
}
