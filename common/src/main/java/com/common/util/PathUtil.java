package com.common.util;


/**
 * 
 *
 * 
 */
public class PathUtil {

    private PathUtil(){}
    
    
    /**
     * 获取根路径下指定路径的绝对路径
     * @param path
     * @return
     */
    public static String getRootPath(String path){
        if(StringUtils.isEmpty(path)){
            path="/";
        }
        return PathUtil.class.getResource(path).getPath();
    }

}
