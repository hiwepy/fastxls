/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.core.function;

import java.util.HashMap;
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

import com.github.hiwepy.fastxls.core.model.ColumnWrapper;
import com.github.hiwepy.fastxls.core.model.RowMap;
import com.github.hiwepy.fastxls.core.model.RowWrapper;
import com.github.hiwepy.fastxls.core.validation.ValidationUtils;

public class DefaultDbfRowValidationFunction<M extends RowMap> implements BiFunction<RowWrapper<DbfRow>, Integer, Set<ConstraintViolation<M>>> {
	
	protected final BiFunction<ColumnWrapper<DbfRow>, String, String> transformer;

	public DefaultDbfRowValidationFunction(
			BiFunction<ColumnWrapper<DbfRow>, String, String> transformer) {
		this.transformer = transformer;
	}
	
	@Override
	public Set<ConstraintViolation<M>> apply(RowWrapper<DbfRow> wrapper, Integer rowNum) {
		
		// 对转换的数据果进行验证
		Set<ConstraintViolation<M>> constraintViolations = new HashSet<>();
		// 转换结果
		Map<String, Object> transMap = new HashMap<String, Object>();
		// 列信息
		List<Map<String, String>> columns = wrapper.getColumns();
		// 行数据
		DbfRow row = wrapper.getRow();
		// 循环处理数据，按需进行数据转换
		for (Map<String, String> columnMap : columns) {
			// 列索引
			int cellnum = MapUtils.getIntValue(columnMap, "cellnum");
			// 列名称
			String column = StringUtils.lowerCase(MapUtils.getString(columnMap, "column"));
			// 列说明
			String label = 	MapUtils.getString(columnMap, "label", "");
			// 数据值
			String value = row.getString(label);
			// 列转换配置
			String transSQL = MapUtils.getString(columnMap, "trans");
			// 尝试进行数据转换
			if(null != getTransformer() && StringUtils.isNotBlank(transSQL)) {
				try {
					ColumnWrapper<DbfRow> colWrapper = new ColumnWrapper<DbfRow>(row, columnMap, new HashMap<>(), rowNum, cellnum);
					value = getTransformer().apply(colWrapper, value);
					// 列转换结果
					transMap.put(column, value);
				} catch (Exception e) {
				}
			}
			// 是否必填
			boolean requisite = MapUtils.getBoolean(columnMap, "requisite");
			if(requisite && StringUtils.isBlank(value)) {
				String message = String.format("%s不能为空", label);
				ConstraintViolation<M> violation = ValidationUtils.violationNotNull(message, value);
				if(null != violation) {
					constraintViolations.add(violation);
				}
			}
			// 字段最大长度
			int maxLength =  MapUtils.getIntValue(columnMap, "maxLength");
			ConstraintViolation<M> violation1 = ValidationUtils.violationLength(String.format("%s长度不能大于%s", label, maxLength), value, maxLength);
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
					ConstraintViolation<M> violation = ValidationUtils.violationDigits(message, value, integer, fraction);
					if(null != violation) {
						constraintViolations.add(violation);
					}
				}
				matcher = ValidationUtils.email(rule);
				while (matcher.find()) {
					// email(xxx)
					String message = String.format("%s格式不正确", label);
					ConstraintViolation<M> violation = ValidationUtils.violationEmail(message, value, matcher.group(1));
					if(null != violation) {
						constraintViolations.add(violation);
					}
				}
				matcher = ValidationUtils.max(rule);
				while (matcher.find()) {
					// max(value)
					String message = String.format("%s的值不能大于%s", label, matcher.group(1));
					ConstraintViolation<M> violation = ValidationUtils.violationMax(message, value, Long.valueOf(matcher.group(1)));
					if(null != violation) {
						constraintViolations.add(violation);
					}
				}
				matcher = ValidationUtils.min(rule);
				while (matcher.find()) {
					// min(value)
					String message = String.format("%s的值不能小于%s", label, matcher.group(1));
					ConstraintViolation<M> violation = ValidationUtils.violationMin(message, value, Long.valueOf(matcher.group(1)));
					if(null != violation) {
						constraintViolations.add(violation);
					}
				}
				matcher = ValidationUtils.negative(rule);
				while (matcher.find()) {
					// negative
					String message = String.format("%s的值必须是负数", label);
					ConstraintViolation<M> violation = ValidationUtils.violationNegative(message, value);
					if(null != violation) {
						constraintViolations.add(violation);
					}
				}
				matcher = ValidationUtils.positive(rule);
				while (matcher.find()) {
					// positive
					String message = String.format("%s的值必须是正数", label);
					ConstraintViolation<M> violation = ValidationUtils.violationPositive(message, value);
					if(null != violation) {
						constraintViolations.add(violation);
					}
				}
				matcher = ValidationUtils.positiveOrZero(rule);
				while (matcher.find()) {
					// positiveOrZero
					String message = String.format("%s的值必须是0或正数", label);
					ConstraintViolation<M> violation = ValidationUtils.violationPositiveOrZero(message, value);
					if(null != violation) {
						constraintViolations.add(violation);
					}
				}
				matcher = ValidationUtils.regexp(rule);
				while (matcher.find()) {
					// regexp(正则表达式)
					String message = String.format("%s格式不正确，无法匹配表达式：%s", label, matcher.group(1));
					ConstraintViolation<M> violation = ValidationUtils.violationPattern(message, value, matcher.group(1));
					if(null != violation) {
						constraintViolations.add(violation);
					}
				}
			}
		}
		
		return constraintViolations;
	}
	
	public BiFunction<ColumnWrapper<DbfRow>, String, String> getTransformer() {
		return transformer;
	}

}
