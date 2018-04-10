package com.common.util.serializer;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;

/**
 *
 */
public class LocalDateTimeSerializer implements ObjectSerializer {
	/**
	 * 日期
	 */
    @Override
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
        if (object == null) {
            serializer.getWriter().writeNull();
            return;
        }

        LocalDateTime date = (LocalDateTime) object;

        String text = date.toString();
        serializer.write(text);
    }
}
