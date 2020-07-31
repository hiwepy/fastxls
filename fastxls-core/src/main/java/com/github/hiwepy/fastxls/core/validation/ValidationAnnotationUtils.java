/*
 * Copyright (c) 2018, vindell (https://github.com/vindell).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.github.hiwepy.fastxls.core.validation;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Negative;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import org.apache.commons.lang3.ArrayUtils;
import org.hibernate.validator.constraints.Length;
import org.springframework.util.StringUtils;

import net.bytebuddy.description.annotation.AnnotationDescription;

public class ValidationAnnotationUtils {

	
	/**
	 * 构造 @Digits 注解
	 * @param message 			: Error Message.
	 * @param integer 			: maximum number of integral digits accepted for this number
	 * @param fraction 			: maximum number of fractional digits accepted for this number
	 * @see {@link @Digits}
	 * @return
	 */
	public static AnnotationDescription annotDigits(String message, int integer, int fraction) {
		return AnnotationDescription.Builder.ofType(Digits.class)
				.define("message", StringUtils.hasText(message) ? message : "")
				.define("integer", integer)
				.define("fraction", fraction)
				.build();
	}
	
	/**
	 * 构造 @NotBlank 注解
	 * @param message 			: Error Message.
	 * @see {@link @NotBlank}
	 * @return
	 */
	public static AnnotationDescription annotNotBlank(String message) {
		return AnnotationDescription.Builder.ofType(NotBlank.class)
				.define("message", StringUtils.hasText(message) ? message : "")
				.build();
	}
	
	/**
	 * 构造 @NotEmpty 注解
	 * @param message 			: Error Message.
	 * @see {@link @NotEmpty}
	 * @return
	 */
	public static AnnotationDescription annotNotEmpty(String message) {
		return AnnotationDescription.Builder.ofType(NotEmpty.class)
				.define("message", StringUtils.hasText(message) ? message : "")
				.build();
	}
	
	/**
	 * 构造 @NotNull 注解
	 * @param message 			: Error Message.
	 * @see {@link @NotNull}
	 * @return
	 */
	public static AnnotationDescription annotNotNull(String message) {
		return AnnotationDescription.Builder.ofType(NotNull.class)
				.define("message", StringUtils.hasText(message) ? message : "")
				.build();
	}

	/**
	 * 构造 @Email 注解
	 * @param message 			: Error Message.
	 * @param regexp 			: The regular expression the annotated element must match. The default is any string ('.*')
	 * @see {@link @Email}
	 * @return
	 */
	public static AnnotationDescription annotEmail(String message, String regexp) {
		return AnnotationDescription.Builder.ofType(Email.class)
				.define("message", StringUtils.hasText(message) ? message : "")
				.define("regexp", StringUtils.hasText(regexp) ? regexp : ".*")
				.build();
	}
	
	/**
	 * 构造 @Email 注解
	 * @param message 			: Error Message.
	 * @param regexp 			: The regular expression the annotated element must match. The default is any string ('.*')
	 * @param flags 		: Corresponds to the `produces` field of the operations under this resource.
	 * @see {@link @Email}
	 * @return
	 */
	public static AnnotationDescription annotEmail(String message, String regexp, Pattern.Flag[] flags) {
		return AnnotationDescription.Builder.ofType(Email.class)
				.define("message", StringUtils.hasText(message) ? message : "")
				.defineEnumerationArray("flags", Pattern.Flag.class, ArrayUtils.isEmpty(flags) ? new Pattern.Flag[] {} : flags)
				.define("regexp", StringUtils.hasText(regexp) ? regexp : ".*")
				.build();
	}
	
	/**
	 * 构造 @Length 注解
	 * @param message 			: Error Message.
	 * @param max 				: maximum number of length
	 * @see {@link @Length}
	 * @return
	 */
	public static AnnotationDescription annotLength(String message, int max) {
		return AnnotationDescription.Builder.ofType(Length.class)
				.define("message", StringUtils.hasText(message) ? message : "")
				.define("max", max)
				.build();
	} 
	
	/**
	 * 构造 @Length 注解
	 * @param message 			: Error Message.
	 * @param min 				: maximum number of length
	 * @param max 				: maximum number of length
	 * @see {@link @Length}
	 * @return
	 */
	public static AnnotationDescription annotLength(String message, int min, int max) {
		return AnnotationDescription.Builder.ofType(Length.class)
				.define("message", StringUtils.hasText(message) ? message : "")
				.define("min", Math.max(min, 0))
				.define("max", max)
				.build();
	} 
	
	/**
	 * 构造 @Max 注解
	 * @param message 			: Error Message.
	 * @param value 				: value the element must be lower or equal to
	 * @see {@link @Max}
	 * @return
	 */
	public static AnnotationDescription annotMax(String message, long value) {
		return AnnotationDescription.Builder.ofType(Max.class)
				.define("message", StringUtils.hasText(message) ? message : "")
				.define("value", Math.max(value, 0))
				.build();
	} 
	
	/**
	 * 构造 @Min 注解
	 * @param message 			: Error Message.
	 * @param value 			: value the element must be higher or equal to
	 * @see {@link @Min}
	 * @return
	 */
	public static AnnotationDescription annotMin(String message, long value) {
		return AnnotationDescription.Builder.ofType(Min.class)
				.define("message", StringUtils.hasText(message) ? message : "")
				.define("value", Math.max(value, 0))
				.build();
	} 
	
	/**
	 * 构造 @Negative 注解
	 * @param message 			: Error Message.
	 * @see {@link @Negative}
	 * @return
	 */
	public static AnnotationDescription annotNegative(String message) {
		return AnnotationDescription.Builder.ofType(Negative.class)
				.define("message", StringUtils.hasText(message) ? message : "")
				.build();
	} 
	
	/**
	 * 构造 @Positive 注解
	 * @param message 			: Error Message.
	 * @see {@link @Positive}
	 * @return
	 */
	public static AnnotationDescription annotPositive(String message) {
		return AnnotationDescription.Builder.ofType(Positive.class)
				.define("message", StringUtils.hasText(message) ? message : "")
				.build();
	} 
	
	/**
	 * 构造 @PositiveOrZero 注解
	 * @param message 			: Error Message.
	 * @see {@link @PositiveOrZero}
	 * @return
	 */
	public static AnnotationDescription annotPositiveOrZero(String message) {
		return AnnotationDescription.Builder.ofType(PositiveOrZero.class)
				.define("message", StringUtils.hasText(message) ? message : "")
				.build();
	}
	
	/**
	 * 构造 @Pattern 注解
	 * @param message 			: Error Message.
	 * @param regexp 			: The regular expression to match
	 * @see {@link @Pattern}
	 * @return
	 */
	public static AnnotationDescription annotPattern(String message, String regexp) {
		return AnnotationDescription.Builder.ofType(Pattern.class)
				.define("message", StringUtils.hasText(message) ? message : "")
				.define("regexp", StringUtils.hasText(regexp) ? regexp : ".*")
				.build();
	}
	
	/**
	 * 构造 @Pattern 注解
	 * @param message 			: Error Message.
	 * @param regexp 			: The regular expression to match
	 * @param flags 			: The array of {@code Flag}s considered when resolving the regular expression.
	 * @see {@link @Pattern}
	 * @return
	 */
	public static AnnotationDescription annotPattern(String message, String regexp, Pattern.Flag[] flags) {
		return AnnotationDescription.Builder.ofType(Pattern.class)
				.define("message", StringUtils.hasText(message) ? message : "")
				.defineEnumerationArray("flags", Pattern.Flag.class, ArrayUtils.isEmpty(flags) ? new Pattern.Flag[] {} : flags)
				.define("regexp", StringUtils.hasText(regexp) ? regexp : ".*")
				.build();
	}
	
	
	/**
	 * 构造 @ApiImplicitParams 注解
	 * @param summary	: 接口概述
	 * @param notes		: 接口注意事项
	 * @return
	public static AnnotationDescription annotApiImplicitParams(MvcParam<?>... params) {
		AnnotationDescription[] paramAnnots = new AnnotationDescription[params.length];
		for (int i = 0; i < params.length; i++) {
			paramAnnots[i] = annotApiImplicitParam(params[i]);
		}
		AnnotationDescription.Builder builder = AnnotationDescription.Builder.ofType(ApiImplicitParams.class)
				.defineAnnotationArray("value", TypeDescription.ForLoadedType.of(ApiImplicitParam.class), paramAnnots);
		return	builder.build();
	}
	
	public static AnnotationDescription annotApiImplicitParam(MvcParam<?> param) {
		String paramType = "query";
		switch (param.getFrom()) {
			case PATH: {
				paramType = "path";
			};break;
			case BODY: {
				paramType = "body";
			};break;
			case HEADER: {
				paramType = "header";
			};break;
			case PARAM: {
				paramType = "query";
			};break;
			default: {
				paramType = "form";
			};break;
		}
		return AnnotationDescription.Builder.ofType(ApiImplicitParam.class)
				.define("paramType", paramType)
				.define("name", param.getName())
				.define("value", "Param " + param.getName())
				.define("dataType", param.getType().getName())
				.define("dataTypeClass", param.getType())
				.define("defaultValue", StringUtils.hasText(param.getDef()) ? param.getDef() : "")
				//.define("allowableValues", StringUtils.hasText(notes) ? notes : "")
				.define("required", param.isRequired())
				.build();
	}*/
	
}