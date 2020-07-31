/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.poi;

import java.math.BigDecimal;

import org.apache.poi.ss.util.CellReference;

public class Test {

	
	public static void main(String[] args) {
		

        // 四舍五入
        long round = Math.round(1.499);
        long round2 = Math.round(1.5);
        System.out.println(round);
        System.out.println(round2);

        // 向上取整
        int s = (int) Math.ceil(1.1);
        System.out.println(s);

        // 向下取整
        System.out.println(Math.floor(1.6));
        
        System.out.println(900 % 500);
        
        int sheetIndex = (int) Math.floor(new BigDecimal((1 + 1) * 100).divide( new BigDecimal(500)).floatValue());
        
		System.out.println(sheetIndex);
		
		String colName = CellReference.convertNumToColString(10);
		System.out.println(colName);
		System.out.println(String.format("hidden%s%s%s%s", colName, 1 , colName, 50));
		System.out.println(String.format("hidden!$%s$%s:$%s$%s", colName, 1 , colName, 50));
		
	}
	
}
