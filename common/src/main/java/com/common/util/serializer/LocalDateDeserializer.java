package com.common.util.serializer;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONToken;
import com.alibaba.fastjson.parser.deserializer.AbstractDateDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 *
 */
public class LocalDateDeserializer extends AbstractDateDeserializer {
	
	Logger logger = LoggerFactory.getLogger(LocalDateDeserializer.class);
	/**
	 * 日期转换
	 */
	@Override
	protected <T> T cast(DefaultJSONParser parser, Type clazz, Object fieldName, Object value) {
		if (value == null) {
			return null;
		}

		if (value instanceof String) {
			String strVal = (String) value;
			if (strVal.length() == 0) {
				return null;
			}

			LocalDateTime localDateTime = null;
			try {
				if (strVal.length()<11){
					LocalDate localDate = LocalDate.parse(strVal);
					localDateTime = localDate.atStartOfDay();
				}else{
					localDateTime = LocalDateTime.parse(strVal);
				}
			} catch (Exception e) {
				localDateTime =LocalDateTime.parse(strVal.replace("Z",""));
			}
			LocalDate date = LocalDate.of(localDateTime.getYear(), localDateTime.getMonth(),
					localDateTime.getDayOfMonth());
			return (T) date;
		}

		throw new JSONException("parse error");
	}

	@Override
	public int getFastMatchToken() {
		return JSONToken.LITERAL_INT;
	}

}
