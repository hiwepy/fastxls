package com.github.hiwepy.fastxls.core.interceptor.comparator;

import java.util.Comparator;

import com.github.hiwepy.fastxls.core.interceptor.Interceptor;

/**
 * 拦截器集合按索引排序
 */
@SuppressWarnings("rawtypes")
public class IndexComparator implements Comparator<Interceptor> {
	
	@Override
	public int compare(Interceptor i1, Interceptor i2) {
		if ((i1.getOrder() > i2.getOrder())) {
			return 1;
		} else if ((i1.getOrder() == i2.getOrder())) {
			return 0;
		} else if ((i1.getOrder() < i2.getOrder())) {
			return -1;
		}
		return 0;
	}

}