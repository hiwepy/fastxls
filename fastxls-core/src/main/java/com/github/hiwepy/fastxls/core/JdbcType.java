/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.core;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.RowId;
import java.sql.SQLXML;
import java.sql.Struct;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * https://www.cnblogs.com/cunkouzh/p/5504052.html
 * https://www.cnblogs.com/shishm/archive/2012/01/30/2332142.html
 * https://www.cnblogs.com/yadongliang/p/7727930.html
 * @author <a href="https://github.com/hiwepy">wandl</a>
 * @since 2020-01-08
 */
public enum JdbcType {

	/*
	 * This is added to enable basic support for the ARRAY data type - but a custom type handler is still required
	 */
	ARRAY(Types.ARRAY , Array.class),
	BIT(Types.BIT , Boolean.class),
	TINYINT(Types.TINYINT , Short.class),
	SMALLINT(Types.SMALLINT , Integer.class),
	INTEGER(Types.INTEGER , Integer.class),
	BIGINT(Types.BIGINT , Long.class),
	FLOAT(Types.FLOAT , Float.class),
	REAL(Types.REAL , Float.class),
	DOUBLE(Types.DOUBLE , Double.class),
	NUMERIC(Types.NUMERIC , BigDecimal.class, "NUMERIC", "NUMBER"),
	DECIMAL(Types.DECIMAL , BigDecimal.class),
	CHAR(Types.CHAR, String.class),
	VARCHAR(Types.VARCHAR , String.class, "VARCHAR", "VARCHAR2"),
	LONGVARCHAR(Types.LONGVARCHAR , String.class),
	DATE(Types.DATE, Timestamp.class),
	TIME(Types.TIME , Time.class),
	TIMESTAMP(Types.TIMESTAMP , Timestamp.class, "TIMESTAMP", "TIMESTAMP WITH TZ", "TIMESTAMP WITH LOCAL TZ"),
	BINARY(Types.BINARY , Byte[].class, "BINARY", "BINARY_DOUBLE", "BINARY_FLOAT"),
	VARBINARY(Types.VARBINARY , Byte[].class),
	LONGVARBINARY(Types.LONGVARBINARY , Byte[].class),
	NULL(Types.NULL , String.class),
	OTHER(Types.OTHER , String.class),
	BLOB(Types.BLOB , Blob.class),
	CLOB(Types.CLOB ,  Clob.class),
	BOOLEAN(Types.BOOLEAN , Boolean.class),
	CURSOR(-10 ,String.class), // Oracle
	UNDEFINED(Integer.MIN_VALUE + 1000 , String.class),
	NVARCHAR(Types.NVARCHAR , String.class, "NVARCHAR", "NVARCHAR2"), // JDK6
	NCHAR(Types.NCHAR , String.class), // JDK6
	NCLOB(Types.NCLOB , NClob.class), // JDK6
	STRUCT(Types.STRUCT , Struct.class),
	JAVA_OBJECT(Types.JAVA_OBJECT ,String.class),
	DISTINCT(Types.DISTINCT , String.class),
	REF(Types.REF , Ref.class),
	DATALINK(Types.DATALINK , String.class),
	ROWID(Types.ROWID , RowId.class, "ROWID", "UROWID"), // JDK6
	LONGNVARCHAR(Types.LONGNVARCHAR ,String.class), // JDK6
	SQLXML(Types.SQLXML , SQLXML.class), // JDK6
	DATETIMEOFFSET(-155 ,String.class), // SQL Server 2008
	TIME_WITH_TIMEZONE(Types.TIME_WITH_TIMEZONE ,String.class), // JDBC 4.2 JDK8
	TIMESTAMP_WITH_TIMEZONE(Types.TIMESTAMP_WITH_TIMEZONE ,String.class); // JDBC 4.2 JDK8
	
	private final int TYPE_CODE;
	private final Type type;
	private final String[] names;

	private static Map<Integer, JdbcType> codeLookup = new HashMap<>();

	static {
		for (JdbcType type : JdbcType.values()) {
			codeLookup.put(type.TYPE_CODE, type);
		}
	}

	JdbcType(int code, Type type, String... names) {
		this.TYPE_CODE = code;
		this.type = type;
		this.names = ArrayUtils.isEmpty(names) ? new String[] {this.name()} : names;
	}

	public static JdbcType fromCode(int code) {
		return codeLookup.get(code);
	}

	public static JdbcType fromString(String type) {

		for (JdbcType jdbcType : JdbcType.values()) {
			if (jdbcType.name().equalsIgnoreCase(type) || Stream.of(jdbcType.getNames())
					.filter(name -> StringUtils.equalsIgnoreCase(name, type)).count() > 0) {
				return jdbcType;
			}
		}
		throw new RuntimeException("JdbcType " + type + " is not supported!");
	}

	public Type type() {
		return type;
	}

	public int getCode() {
		return TYPE_CODE;
	}
	
	public String[] getNames() {
		return names;
	}

	@Override
	public String toString() {
		return type.getTypeName();
	}

}
