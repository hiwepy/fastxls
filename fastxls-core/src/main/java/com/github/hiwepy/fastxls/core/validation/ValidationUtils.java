/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.core.validation;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.ConstraintViolation;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.github.hiwepy.fastxls.core.model.RowMap;

public class ValidationUtils {

	private static Pattern PATTERN_DIGITS = Pattern.compile("(?:(?:digits\\()([\\d]*?),([\\\\d]*?)(?:\\)))+", Pattern.CASE_INSENSITIVE);
	private static Pattern PATTERN_EMAIL = Pattern.compile("(?:(?:email\\()([\\S]*?)(?:\\)))+", Pattern.CASE_INSENSITIVE);
	private static Pattern PATTERN_MIN = Pattern.compile("(?:(?:min\\()([\\d]*?)(?:\\)))+", Pattern.CASE_INSENSITIVE);
	private static Pattern PATTERN_MAX = Pattern.compile("(?:(?:max\\()([\\d]*?)(?:\\)))+", Pattern.CASE_INSENSITIVE);
	private static Pattern PATTERN_REGEXP = Pattern.compile("(?:(?:regexp\\()([\\S]*?)(?:\\)))+", Pattern.CASE_INSENSITIVE);
	private static Pattern PATTERN_NEGATIVE = Pattern.compile("(?:(?:negative))+", Pattern.CASE_INSENSITIVE);
	private static Pattern PATTERN_POSITIVE = Pattern.compile("(?:(?:positive))+", Pattern.CASE_INSENSITIVE);
	private static Pattern PATTERN_POSITIVEORZERO = Pattern.compile("(?:(?:positiveOrZero))+", Pattern.CASE_INSENSITIVE);
	
	public static Matcher digits(String input) {
		return PATTERN_DIGITS.matcher(input);
	}
	
	public static Matcher email(String input) {
		return PATTERN_EMAIL.matcher(input);
	}
	
	public static Matcher min(String input) {
		return PATTERN_MIN.matcher(input);
	}
	
	public static Matcher max(String input) {
		return PATTERN_MAX.matcher(input);
	}
	
	public static Matcher regexp(String input) {
		return PATTERN_REGEXP.matcher(input);
	}
	
	public static Matcher negative(String input) {
		return PATTERN_NEGATIVE.matcher(input);
	}
	
	public static Matcher positive(String input) {
		return PATTERN_POSITIVE.matcher(input);
	}
	
	public static Matcher positiveOrZero(String input) {
		return PATTERN_POSITIVEORZERO.matcher(input);
	}
	
	public static <M extends RowMap> ConstraintViolation<M> violationDigits(String message, String input, int integer, int fraction) {
		 return NumberUtils.isDigits(input) ? null : new DefaultConstraintViolation<M>(input, message);
	}
	
	public static <M extends RowMap> ConstraintViolation<M> violationNotBlank(String message, String input) {
		return StringUtils.isNotBlank(input) ? null : new DefaultConstraintViolation<M>(input, message);
	}
	
	public static <M extends RowMap> ConstraintViolation<M> violationNotEmpty(String message, String input) {
		return StringUtils.isNotEmpty(input) ? null: new DefaultConstraintViolation<M>(input, message);
	}
	
	public static <M extends RowMap> ConstraintViolation<M> violationNotNull(String message, String input) {
		return null != input ? null: new DefaultConstraintViolation<M>(input, message);
	}

	public static <M extends RowMap> ConstraintViolation<M> violationEmail(String message, String input, String regexp) {
		Pattern pattern = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE);
		return pattern.matcher(input).find() ? null : new DefaultConstraintViolation<M>(input, message);
	}
	  
	public static <M extends RowMap> ConstraintViolation<M> violationLength(String message, String input, int max) {
		return StringUtils.length(input) <= max ? null : new DefaultConstraintViolation<M>(input, message);
	} 
	
	public static <M extends RowMap> ConstraintViolation<M> violationMax(String message, String input, long value) {
		return Long.parseLong(input) <= value ? null : new DefaultConstraintViolation<M>(input, message);
	} 
	
	public static <M extends RowMap> ConstraintViolation<M> violationMin(String message, String input, long value) {
		return Long.parseLong(input) >= value ? null : new DefaultConstraintViolation<M>(input, message);
	}
	
	public static <M extends RowMap> ConstraintViolation<M> violationNegative(String message, String input) {
		// -1, 0, or 1 as this BigDecimal is numericallyless than, equal to, or greater than val.
		int compareTo = new BigDecimal(input).compareTo(BigDecimal.ZERO);
		return compareTo < 0 ? null : new DefaultConstraintViolation<M>(input, message);
	}
	 
	public static <M extends RowMap> ConstraintViolation<M> violationPositive(String message, String input) {
		// -1, 0, or 1 as this BigDecimal is numericallyless than, equal to, or greater than val.
				int compareTo = new BigDecimal(input).compareTo(BigDecimal.ZERO);
				return compareTo > 0 ? null : new DefaultConstraintViolation<M>(input, message);
	}
	
	public static <M extends RowMap> ConstraintViolation<M> violationPositiveOrZero(String message, String input) {
		// -1, 0, or 1 as this BigDecimal is numericallyless than, equal to, or greater than val.
		int compareTo = new BigDecimal(input).compareTo(BigDecimal.ZERO);
		return compareTo >= 0 ? null : new DefaultConstraintViolation<M>(input, message);
	}
	
	public static <M extends RowMap> ConstraintViolation<M> violationPattern(String message, String input, String regexp) {
		Pattern pattern = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE);
		return pattern.matcher(input).find() ? null : new DefaultConstraintViolation<M>(input, message);
	}

}
