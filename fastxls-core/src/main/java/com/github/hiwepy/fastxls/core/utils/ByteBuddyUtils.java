/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.core.utils;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.hiwepy.fastxls.core.JdbcType;
import com.github.hiwepy.fastxls.core.model.RowMap;
import com.github.hiwepy.fastxls.core.validation.ValidationAnnotationUtils;
import com.github.hiwepy.fastxls.core.validation.ValidationUtils;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.NamingStrategy;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.dynamic.DynamicType.Builder.FieldDefinition.Optional.Valuable;
import net.bytebuddy.utility.RandomString;

@SuppressWarnings("unchecked")
public class ByteBuddyUtils {

	protected Logger LOG = LoggerFactory.getLogger(getClass());
	protected static RandomString randomString = new RandomString(8);
	protected static final String PREFIX = "com.github.hiwepy.fastxls.bytebuddy.";
	
	public static <M extends RowMap> Class<M> dynamicDtoClass(Class<M> superClass, List<Map<String, String>> columns) {
		
		// 动态构建Vo对象
		Builder<M> builder = new ByteBuddy().with(new NamingStrategy.AbstractBase() {
			
			@Override
			protected String name(TypeDescription typeDescription) {
				return PREFIX + typeDescription.getSimpleName() + "$" + randomString.nextString();
			}
			
		})
		// 继承父类
		.subclass(superClass);

		// 循环列信息构造列对应的数据
		for (Map<String, String> columnMap : columns) {
			// 列名称
			String column = StringUtils.lowerCase(MapUtils.getString(columnMap, "column"));
			// 列说明
			String label = 	MapUtils.getString(columnMap, "label", "");
			// 字段类型
			String type = MapUtils.getString(columnMap, "type");
			JdbcType jdbcType = JdbcType.fromString(type);
			// 生成列对应的Vo字段
			Valuable<M> valuable = builder.defineField(column, jdbcType.type(), Modifier.PUBLIC);
			// 是否必填
			boolean requisite = MapUtils.getBoolean(columnMap, "requisite");
			if(requisite && String.class.isAssignableFrom(jdbcType.type().getClass())) {
				// 字段添加@NotNull注解
				String message = String.format("%s不能为空", label);
				valuable.annotateType(ValidationAnnotationUtils.annotNotNull(message));
			}
			// 字段最大长度
			int maxLength =  MapUtils.getIntValue(columnMap, "maxLength");
			// 字段添加@Length注解
			valuable.annotateType(ValidationAnnotationUtils.annotLength(String.format("%s长度不能大于%s", label, maxLength), maxLength));
			// 验证规则
			String[] rules = MapUtils.getString(columnMap, "rules", "").split(",");
			for (String rule : rules) {
				
				Matcher matcher = ValidationUtils.digits(rule);
				while (matcher.find()) {
					// digits(min,max)
					int integer = Integer.parseInt(matcher.group(1));
					int fraction = Integer.parseInt(matcher.group(2));
					String message = String.format("%s不是有效的数字", label);
					valuable.annotateType(ValidationAnnotationUtils.annotDigits(message, integer, fraction));
				}
				matcher = ValidationUtils.email(rule);
				while (matcher.find()) {
					// email(xxx)
					String message = String.format("%s格式不正确", label);
					valuable.annotateType(ValidationAnnotationUtils.annotEmail(message, matcher.group(1)));
				}
				matcher = ValidationUtils.max(rule);
				while (matcher.find()) {
					// max(value)
					String message = String.format("%s的值不能大于%s", label, matcher.group(1));
					valuable.annotateType(ValidationAnnotationUtils.annotMax( message , Long.valueOf(matcher.group(1))));
				}
				matcher = ValidationUtils.min(rule);
				while (matcher.find()) {
					// min(value)
					String message = String.format("%s的值不能小于%s", label, matcher.group(1));
					valuable.annotateType(ValidationAnnotationUtils.annotMin( message , Long.valueOf(matcher.group(1))));
				}
				matcher = ValidationUtils.negative(rule);
				while (matcher.find()) {
					// negative
					String message = String.format("%s的值必须是负数", label);
					valuable.annotateType(ValidationAnnotationUtils.annotNegative(message));
				}
				matcher = ValidationUtils.positive(rule);
				while (matcher.find()) {
					// positive
					String message = String.format("%s的值必须是正数", label);
					valuable.annotateType(ValidationAnnotationUtils.annotPositive(message));
				}
				matcher = ValidationUtils.positiveOrZero(rule);
				while (matcher.find()) {
					// positiveOrZero
					String message = String.format("%s的值必须是0或正数", label);
					valuable.annotateType(ValidationAnnotationUtils.annotPositiveOrZero(message));
				}
				matcher = ValidationUtils.regexp(rule);
				while (matcher.find()) {
					// regexp(正则表达式)
					String message = String.format("%s格式不正确，无法匹配表达式：%s", label, matcher.group(1));
					valuable.annotateType(ValidationAnnotationUtils.annotPattern(message, matcher.group(1)));
				}
			}
			builder = valuable;
		}
		// 构建对象类型
		return (Class<M>) builder.make().load(superClass.getClassLoader()).getLoaded();
	}
	
}
