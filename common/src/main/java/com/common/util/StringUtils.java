package com.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StringUtils {
    /**
     * 判断是否为空
     * @param str
     * @return
     */
    public static boolean isEmpty(String...str){
        for (String s : str) {
            if (s==null || "".equals(s.trim())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否为null
     * @param obj
     * @return
     */
    public static boolean isNull(Object...obj){
        for (Object o : obj) {
            if (o==null) {
                return true;
            }
        }
        return false;
    }


    /**
     * 字符串转换long
     * @param list
     * @return
     */
    public static List<Long> str2Long(List<String> list){
        List<Long> tlist = new ArrayList<>();
        for (String obj : list) {
            tlist.add(Long.parseLong(obj));
        }
        return tlist;
    }

    /**
     * 生成uid
     * @return
     */
    public static String getUId(){
        return UUID.randomUUID().toString();
    }

    /**
     * trim
     * @param str
     * @return
     */
//    public static String trimToNull(String str) {
//        return org.apache.commons.lang3.StringUtils.trimToNull(str);
//    }

}
