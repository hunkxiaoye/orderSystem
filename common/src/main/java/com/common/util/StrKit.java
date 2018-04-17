package com.common.util;

import java.util.Iterator;

public class StrKit {
    public static final String empty = "";
    public static final String newLine = System.getProperty("line.separator");

    public StrKit() {
    }

    public static String firstCharToLowerCase(String str) {
        char firstChar = str.charAt(0);
        if(firstChar >= 65 && firstChar <= 90) {
            char[] arr = str.toCharArray();
            arr[0] = (char)(arr[0] + 32);
            return new String(arr);
        } else {
            return str;
        }
    }

    public static String firstCharToUpperCase(String str) {
        char firstChar = str.charAt(0);
        if(firstChar >= 97 && firstChar <= 122) {
            char[] arr = str.toCharArray();
            arr[0] = (char)(arr[0] - 32);
            return new String(arr);
        } else {
            return str;
        }
    }

    public static boolean isBlank(String str) {
        return str == null || str.isEmpty();
    }

    public static boolean isBlank(Object str) {
        return str == null || str instanceof String && ((String)str).isEmpty();
    }

    public static boolean notBlank(String str) {
        return str != null && !str.isEmpty();
    }

    public static boolean notBlank(String... strings) {
        if(strings == null) {
            return false;
        } else {
            String[] var1 = strings;
            int var2 = strings.length;

            for(int var3 = 0; var3 < var2; ++var3) {
                String str = var1[var3];
                if(str == null || str.isEmpty()) {
                    return false;
                }
            }

            return true;
        }
    }

    public static <T> String ArrayToString(Iterable<T> inputs, String separator) {
        if(inputs == null) {
            return "";
        } else {
            StringBuilder sb = new StringBuilder();

            Object input;
            for(Iterator var3 = inputs.iterator(); var3.hasNext(); sb.append(input.toString())) {
                input = var3.next();
                if(sb.length() > 0) {
                    sb.append(separator);
                }
            }

            return sb.toString();
        }
    }
}

