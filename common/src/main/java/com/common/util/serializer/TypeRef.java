package com.common.util.serializer;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 *
 * @param <T>
 */
public class TypeRef<T> {

    private final Type type;

    private String refClassName;

    public  static final Type LIST_STRING = new TypeRef<List<String>>() {}.getType();

    protected TypeRef(){
        Type superClass = getClass().getGenericSuperclass();
        type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
        refClassName = type.getTypeName();
    }

    public Type getType() {
        return type;
    }

    public String getRefClassName(){
        return refClassName;
    }


}
