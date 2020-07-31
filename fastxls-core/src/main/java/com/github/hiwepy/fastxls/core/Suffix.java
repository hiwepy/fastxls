package com.github.hiwepy.fastxls.core;


 /**
  * 支持的Excel版本后缀枚举对象
  */
public enum Suffix {

	XLS {
		public String toString() {
			return ".xls";
		}
	},
	XLSX {
		public String toString() {
			return ".xlsx";
		}
	},
	//数据较大时使用此对象
	SXLSX {
		public String toString() {
			return ".xlsx";
		}
	}
	
}



