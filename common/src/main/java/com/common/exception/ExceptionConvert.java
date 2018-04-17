package com.common.exception;

import com.common.util.StringUtils;
import org.elasticsearch.ElasticsearchStatusException;

/**
 * http ES api 异常转换为 自定义异常类
 *
 *
 * @create 2017-11-07 11:02.
 */
public class ExceptionConvert {

    public static void convertHttpException(ElasticsearchStatusException e){
        String message = e.getDetailedMessage();
        if (!StringUtils.isEmpty(message)){
            if(message.contains("index_not_found_exception")){
                throw new ESIndexNotFoundException("index_not_found_exception");
            }else if (message.contains("strict_dynamic_mapping_exception")){
                throw new ESMappingNotFoundException("strict_dynamic_mapping_exception");
            }else{
                throw e;
            }
        }
    }



}
