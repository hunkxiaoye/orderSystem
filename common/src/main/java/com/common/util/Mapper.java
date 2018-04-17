package com.common.util;

import com.common.Elasticsearch.annotation.ESColumn;
import com.common.Elasticsearch.annotation.ESIndex;
import com.common.Elasticsearch.annotation.ESType;
import com.common.Elasticsearch.annotation.SearchId;
import com.common.Elasticsearch.enums.ESDataType;
import com.common.Elasticsearch.enums.Indexed;
import com.common.Elasticsearch.enums.Store;
import com.common.Elasticsearch.meta.ElasticIndex;
import com.common.Elasticsearch.meta.FieldProperties;
import com.common.Elasticsearch.meta.MappingProperties;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Mapper {
    public static EntityInfo getEntityInfo(Class<?> clazz) {
        Map<String, Object> properties = getProperties(clazz,null,null);
        ElasticIndex index = getIndex(clazz);
        String idColumn = getIdColumn(clazz,null);
        EntityInfo info = new EntityInfo(clazz,properties,index,idColumn);
        return info;
    }

    private static Map<String,Object> getProperties(Class<?> clazz,Class<?> subClazz,Map<String, Object> prop){
        Field[] fields = clazz.getDeclaredFields();
        if (prop == null){
            prop = new HashMap<>();
        }
        for (Field f : fields) {


            String javaType = f.getType().getTypeName();
            //判断是不是泛型类
            if(f.getType().equals(Object.class)){
                Type type = subClazz.getGenericSuperclass();
                if (type instanceof ParameterizedType){
                    Type trueType = ((ParameterizedType)type).getActualTypeArguments()[0];
                    javaType = trueType.getTypeName();
                }
            }
            //判断是不是数组
            Class<?> componentType = f.getType().getComponentType();
            if (componentType != null){
                javaType =componentType.getTypeName();
            }
            //判断是不是List集合
            Class<?> tt = f.getType();
            if (tt.isAssignableFrom(List.class) && !tt.equals(Object.class)){
                Type t = f.getGenericType();
                Type actrualType = ((ParameterizedType) t).getActualTypeArguments()[0];
                if (isPrimetive( (Class<?>) actrualType)){
                    javaType = actrualType.getTypeName();
                }else {
                    Map<String,Object> obj = getSub((Class<?>)actrualType);
                    prop.put(f.getName(),obj);
                    continue;
                }
            }

            //判断是不是Set集合
            if (tt.isAssignableFrom(Set.class) && !tt.equals(Object.class)){
                Type t = f.getGenericType();
                Type actrualType = ((ParameterizedType) t).getActualTypeArguments()[0];
                if (isPrimetive( (Class<?>) actrualType)){
                    javaType = actrualType.getTypeName();
                }else {
                    Map<String,Object> obj = getSub((Class<?>)actrualType);
                    prop.put(f.getName(),obj);
                    continue;
                }
            }

            //判断是不是自定义类
            if (!isPrimetive(f.getType())){
                Map<String,Object> obj = getSub(f.getType());
                prop.put(f.getName(),obj);
                continue;
            }
            FieldProperties fprop = new FieldProperties();
            ESColumn column = f.getAnnotation(ESColumn.class);
            if (column != null) {
                fprop.setStore(column.store().equals(Store.STORE));
                if (f.getType() == String.class) {
                    fprop.setIndex(column.indexed().getName());
                }else {
                    fprop.setIndex(Indexed.INDEXED.getName());
                }
            } else {
                fprop.setStore(true);
                fprop.setIndex(Indexed.INDEXED.getName());
            }
            fprop.setType(ESDataType.getEsType(javaType));
//            if ("java.time.LocalDate".equals(javaType)){
//                fprop.setFormat("yyyy-MM-dd");
//            }
//            if ("java.time.LocalDateTime".equals(javaType)){
//                fprop.setFormat("yyyy-MM-dd HH:mm:ss");
//            }
            prop.put(f.getName(),fprop);
        }
        //判断有没有父类
        if (clazz.getSuperclass() != Object.class) {
            return getProperties(clazz.getSuperclass(),clazz,prop);
        }
        return prop;
    }


    private static Map<String,Object> getSub(Class<?> clazz){
        Map<String,Object> obj = new HashMap<>();
        Map<String, Object> subProp = getProperties(clazz,null,null);
        obj.put("type", ESDataType.NESTED.getEsType());
        obj.put("properties",subProp);
        return obj;
    }

    private static String getIdColumn(Class<?> clazz ,Boolean isId) {
        if (isId == null){
            isId = false;
        }
        //获取索引主键字段
        Field[] fields = clazz.getDeclaredFields();
        for(Field f : fields){
            if (f.getAnnotation(SearchId.class)!=null){
                return f.getName();
            }
            if (f.getName().equalsIgnoreCase("id")){
                isId = true;
            }
        }
        if (clazz.getSuperclass() != Object.class) {
            return getIdColumn(clazz.getSuperclass(),isId);
        }
        if (isId){
            return "id";
        }
        throw new RuntimeException("没有找到主键");
    }


    /**
     * 判断当前类是不是java基础类
     * @param clazz
     * @return
     */
    private static boolean isPrimetive(Class<?> clazz){
        return clazz.getClassLoader() == null;
    }

    /**
     * 获取索引的名称 和 类型
     * @param clazz
     * @return
     */
    private static ElasticIndex getIndex(Class<?> clazz){
        ElasticIndex index = new ElasticIndex();
        //获取索引名
        ESIndex indexName = clazz.getAnnotation(ESIndex.class);
        if (indexName == null || StrKit.isBlank(indexName.value())){
            index.setIndexName(clazz.getSimpleName().toLowerCase());
        }else{
            index.setIndexName(indexName.value().toLowerCase());
        }
        //获取索引type
        ESType type = clazz.getAnnotation(ESType.class);
        if (type == null || StrKit.isBlank(type.value())){
            index.setIndexType(index.getIndexName());
        }else {
            index.setIndexType(type.value().toLowerCase());
        }
        return index;
    }

    public static class EntityInfo {

        public EntityInfo(){}

        MappingProperties mappings;

        String idColumn;


        ElasticIndex index;



        public EntityInfo(Class<?> clazz,Map<String, Object> properties,ElasticIndex index, String idColumn){
            this.mappings = new MappingProperties(properties);
            this.index = index;
            this.idColumn = idColumn;
        }

        public MappingProperties getMappings() {
            return mappings;
        }

        public void setMappings(MappingProperties mappings) {
            this.mappings = mappings;
        }



        public ElasticIndex getIndex() {
            return index;
        }

        public void setIndex(ElasticIndex index) {
            this.index = index;
        }

        public String getIdColumn() {
            return idColumn;
        }

        public void setIdColumn(String idColumn) {
            this.idColumn = idColumn;
        }
    }
}
