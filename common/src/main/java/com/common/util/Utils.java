package com.common.util;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.common.util.serializer.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Created by jinweile on 2016/1/21.
 */
public class Utils {

    private static SerializeConfig serializeConfig = new SerializeConfig();

    private static ParserConfig parserConfig = new ParserConfig();

    static{
        serializeConfig.put(LocalDateTime.class, new LocalDateTimeSerializer());
        serializeConfig.put(Date.class, new DateSerializer());
        serializeConfig.put(LocalDate.class, new LocalDateSerializer());

        parserConfig.putDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
        parserConfig.putDeserializer(Date.class, new LocalDateTimeDeserializer());
        parserConfig.putDeserializer(LocalDate.class, new LocalDateDeserializer());
    }




    private Utils(){}

    /**
     * json序列化(包括LocalDateTime转换成utc时间格式)
     * @param object
     * @return
     */
    public static String toJson(Object object){
        return JSON.toJSONString(object, serializeConfig);
    }

    /**
     * json反序列化(包括utc时间转换成LocalDateTime)
     * @param json
     * @param typeRef
     * @return
     */
    public static <T> T parseObject(String json, TypeRef<T> typeRef){
        return JSON.parseObject(json, typeRef.getType(), parserConfig, JSON.DEFAULT_PARSER_FEATURE);
    }

    /**
     * 转义检索关键字
     * @param s
     * @return
     */
    public static String escapeQueryChars(String s) {
    	if (s == null) {
			return null;
		}
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            // These characters are part of the query syntax and must be escaped
            if (c == '\\' || c == '+' || c == '-' || c == '!'  || c == '(' || c == ')' || c == ':'
                    || c == '^' || c == '[' || c == ']' || c == '\"' || c == '{' || c == '}' || c == '~'
                    || c == '*' || c == '?' || c == '|' || c == '&'  || c == ';' || c == '/' || c == ' '
                    || Character.isWhitespace(c)) {
                sb.append('\\');
            }
            sb.append(c);
        }
        return sb.toString();
    }

    /**
     * 字符转义
     * @param s
     * @return
     */
    public static String[] escapeQueryChars(String ...s){
    	if (s == null) {
			return null;
		}
    	String[] values = new String[s.length];
    	for (int i = 0; i < s.length; i++) {
    		String str = escapeQueryChars(s[i]);
    		values[i]=str;
		}
    	return values;
    }

    /**
     * 字符串反序列化为指定对象
     * @param jsonStr
     * @param objClass
     * @param <T>
     * @return
     */
    public static <T> T json2Bean(String jsonStr, Class<T> objClass) {
        return JSON.parseObject(jsonStr, objClass, parserConfig, JSON.DEFAULT_PARSER_FEATURE);
    }

    /**
     * bean2Map
     * @param t
     * @param <T>
     * @return
     */
    public static <T> Map<String,Object> bean2Map(T t){
        String s = toJson(t);
        return parseHashMap(s);
    }

    /**
     * 序列化成map
     * @param json
     * @return
     */
    public static Map<String, Object> parseHashMap(String json){
        Map<String,Object> map = parseObject(json, new TypeRef<HashMap<String,Object>>() {});
        removeNullValue(map);
        return map;
    }

    public static List<Map<String, Object>> parseListHashMap(String json){
        List<Map<String, Object>> docs = Utils.parseObject(json, new TypeRef<List<Map<String, Object>>>(){});
        for (Map<String, Object> map : docs) {
            removeNullValue(map);
        }
        return docs;
    }

    private static Map<String, Object> removeNullValue(Map<String, Object> map) {
        Set<String> keys = new HashSet<>();
        if (map != null && !map.isEmpty() && map.containsValue(null)){
            map.entrySet().forEach(entry -> {
                if (entry.getValue() == null){
                    keys.add(entry.getKey());
                }
            });
            if (!keys.isEmpty()){
                for (String key : keys) {
                    map.remove(key);
                }
            }
        }
        return map;
    }

}
