/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.core.function;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.regex.Matcher;

import javax.validation.ConstraintViolation;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.jamel.dbf.structure.DbfRow;

import com.github.hiwepy.fastxls.core.model.RowMap;
import com.github.hiwepy.fastxls.core.model.RowMapper;
import com.github.hiwepy.fastxls.core.validation.ValidationUtils;

public class DefaultDbfRowMapValidationFunction implements BiFunction<RowMapper<DbfRow, RowMap>, Integer, Set<ConstraintViolation<RowMap>>> {
	
	@Override
	public Set<ConstraintViolation<RowMap>> apply(RowMapper<DbfRow, RowMap> mapper, Integer rowNum) {
		
		// 对转换的数据果进行验证
		Set<ConstraintViolation<RowMap>> constraintViolations = new HashSet<>();
		// 列信息
		List<Map<String, String>> columns = mapper.getColumns();
		// 行数据
		RowMap rowMap = mapper.getMapper();
		// 循环列信息构造列对应的数据
		for (Map<String, String> columnMap : columns) {
			// 列名称
			String column = StringUtils.lowerCase(MapUtils.getString(columnMap, "column"));
			// 列说明
			String label = 	MapUtils.getString(columnMap, "label", "");
			// 数据值
			String value = MapUtils.getString(rowMap, column);
			// 是否必填
			boolean requisite = MapUtils.getBoolean(columnMap, "requisite");
			if(requisite && StringUtils.isBlank(value)) {
				String message = String.format("%s不能为空", label);
				ConstraintViolation<RowMap> violation = ValidationUtils.violationNotNull(message, value);
				if(null != violation) {
					constraintViolations.add(violation);
				}
			}
			// 字段最大长度
			int maxLength =  MapUtils.getIntValue(columnMap, "maxLength");
			ConstraintViolation<RowMap> violation1 = ValidationUtils.violationLength(String.format("%s长度不能大于%s", label, maxLength), value, maxLength);
			if(null != violation1) {
				constraintViolations.add(violation1);
			}
			// 验证规则
			String[] rules = MapUtils.getString(columnMap, "rules", "").split(",");
			for (String rule : rules) {
				
				Matcher matcher = ValidationUtils.digits(rule);
				while (matcher.find()) {
					// digits(min,max)
					int integer = Integer.parseInt(matcher.group(1));
					int fraction = Integer.parseInt(matcher.group(2));
					String message = String.format("%s不是有效的数字", label);
					ConstraintViolation<RowMap> violation = ValidationUtils.violationDigits(message, value, integer, fraction);
					if(null != violation) {
						constraintViolations.add(violation);
					}
				}
				matcher = ValidationUtils.email(rule);
				while (matcher.find()) {
					// email(xxx)
					String message = String.format("%s格式不正确", label);
					ConstraintViolation<RowMap> violation = ValidationUtils.violationEmail(message, value, matcher.group(1));
					if(null != violation) {
						constraintViolations.add(violation);
					}
				}
				matcher = ValidationUtils.max(rule);
				while (matcher.find()) {
					// max(value)
					String message = String.format("%s的值不能大于%s", label, matcher.group(1));
					ConstraintViolation<RowMap> violation = ValidationUtils.violationMax(message, value, Long.valueOf(matcher.group(1)));
					if(null != violation) {
						constraintViolations.add(violation);
					}
				}
				matcher = ValidationUtils.min(rule);
				while (matcher.find()) {
					// min(value)
					String message = String.format("%s的值不能小于%s", label, matcher.group(1));
					ConstraintViolation<RowMap> violation = ValidationUtils.violationMin(message, value, Long.valueOf(matcher.group(1)));
					if(null != violation) {
						constraintViolations.add(violation);
					}
				}
				matcher = ValidationUtils.negative(rule);
				while (matcher.find()) {
					// negative
					String message = String.format("%s的值必须是负数", label);
					ConstraintViolation<RowMap> violation = ValidationUtils.violationNegative(message, value);
					if(null != violation) {
						constraintViolations.add(violation);
					}
				}
				matcher = ValidationUtils.positive(rule);
				while (matcher.find()) {
					// positive
					String message = String.format("%s的值必须是正数", label);
					ConstraintViolation<RowMap> violation = ValidationUtils.violationPositive(message, value);
					if(null != violation) {
						constraintViolations.add(violation);
					}
				}
				matcher = ValidationUtils.positiveOrZero(rule);
				while (matcher.find()) {
					// positiveOrZero
					String message = String.format("%s的值必须是0或正数", label);
					ConstraintViolation<RowMap> violation = ValidationUtils.violationPositiveOrZero(message, value);
					if(null != violation) {
						constraintViolations.add(violation);
					}
				}
				matcher = ValidationUtils.regexp(rule);
				while (matcher.find()) {
					// regexp(正则表达式)
					String message = String.format("%s格式不正确，无法匹配表达式：%s", label, matcher.group(1));
					ConstraintViolation<RowMap> violation = ValidationUtils.violationPattern(message, value, matcher.group(1));
					if(null != violation) {
						constraintViolations.add(violation);
					}
				}
			}
		}
		
		return constraintViolations;
	}

}
