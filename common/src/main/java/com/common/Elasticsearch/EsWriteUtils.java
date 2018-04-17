package com.common.Elasticsearch;

import com.alibaba.fastjson.JSON;
import com.common.Elasticsearch.meta.BaseModel;
import com.common.Elasticsearch.meta.ElasticIndex;
import com.common.exception.ESIndexNotFoundException;
import com.common.exception.ESMappingNotFoundException;
import com.common.exception.ExceptionConvert;
import com.common.func.Func;
import com.common.util.Mapper;
import com.common.util.StrKit;
import com.common.util.StringUtils;
import com.common.util.Utils;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.*;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class EsWriteUtils {
    private static final Logger log = LoggerFactory.getLogger(EsWriteUtils.class);
    @Autowired
    private esClientUtil esClientUtil;
    private static volatile Map<String, String> indexMap = new HashMap<>();



    public boolean isExist(Mapper.EntityInfo info,String clusterName){

        boolean exist =false;
        ElasticIndex index = info.getIndex();

        Response response = null;
        try {
            response = esClientUtil.getrestClient(clusterName)
                    .performRequest("HEAD","/"+index.getIndexName(), Collections.emptyMap());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (response.getStatusLine().getReasonPhrase().equals("OK")){
              exist =true;
        }
        return exist;

    }
    /**
     * 单个添加
     *
     * @param model
     * @param <T>
     */
    public <T extends BaseModel> void addIndex(String clusterName, T model) throws Exception{
        if (model == null) {
            throw new RuntimeException("添加的索引对象不可以为空");
        }
        Mapper.EntityInfo info = Mapper.getEntityInfo(model.getClass());
        try {
            if (isExist(info, clusterName)){
                add(clusterName, model, info);
            }else {
                addCore(info, clusterName);
                add(clusterName, model, info);
            }
        }catch (ESMappingNotFoundException e) {
            log.debug("新字段的mapping未找到，则去新建索引:{}", e);
            addFieldMapping(info, clusterName);
            add(clusterName, model, info);
        }
    }

    public <T extends BaseModel> void add(String clusterName, T model, Mapper.EntityInfo info) {
        Object id = model.getId();
        if (StrKit.isBlank(id)) {
            throw new RuntimeException("主键值不可为空");
        }
        ElasticIndex index = info.getIndex();
        IndexRequest request = new IndexRequest(index.getIndexName(), index.getIndexType(), id.toString())
                .setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE)
                .source(Utils.toJson(model),XContentType.JSON);
        exce(clusterName, client -> client.index(request));
    }

    /**
     * 单个更新
     *
     * @param clusterName
     * @param model
     * @param <T>
     */
    public <T extends BaseModel> void updateIndex(String clusterName, T model) {
        if (model == null) {
            throw new RuntimeException("添加的索引对象不可以为空");
        }
        Mapper.EntityInfo info = Mapper.getEntityInfo(model.getClass());
        try {
            if(isExist(info, clusterName)) {
                update(clusterName, model, info);
            }else {
                addCore(info, clusterName);
                update(clusterName, model, info);
            }
        }catch (ESMappingNotFoundException e) {
            log.debug("新字段的mapping未找到，则去新建索引:{}", e);
            addFieldMapping(info, clusterName);
            update(clusterName, model, info);
        }
    }

    public <T extends BaseModel> void update(String clusterName, T model, Mapper.EntityInfo info) {
        Object id = model.getId();
        if (StrKit.isBlank(id)) {
            throw new RuntimeException("主键值不可为空");
        }
        ElasticIndex index = info.getIndex();
        UpdateRequest updateRequest = new UpdateRequest(index.getIndexName(),
                index.getIndexType(),
                id.toString())
                .setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE)
                .doc(Utils.toJson(model))
                .retryOnConflict(10)
                .docAsUpsert(true);
        exce(clusterName, client -> client.update(updateRequest));

    }

    /**
     * 单个删除
     *
     * @param clusterName

     */
    public  <T extends BaseModel> void deleteIndex(String clusterName,Class<T> clazz,String id) {
        if (clazz == null || StrKit.isBlank(id)) {
            throw new RuntimeException("删除的索引对象不可以为空");
        }
        Mapper.EntityInfo info = Mapper.getEntityInfo(clazz);
        ElasticIndex index = info.getIndex();
        DeleteRequest request = new DeleteRequest(index.getIndexName(), index.getIndexType(), id);
        request.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
        exce(clusterName, client -> client.delete(request));
    }

    /**
     * 批量添加
     *
     * @param list
     * @param <T>
     */
    public  <T extends BaseModel> void addIndexList(String clusterName, List<T> list) {
        if (list.isEmpty()){
            throw new RuntimeException("批量添加索引的列表不可以为空");
        }
        Mapper.EntityInfo info = Mapper.getEntityInfo(list.get(0).getClass());
        try {
            if(isExist(info, clusterName)) {
                addList(clusterName,list,info);
            }else {
                addCore(info, clusterName);
                addList(clusterName,list,info);
            }
        }catch (ESMappingNotFoundException e){
            log.debug("新字段的mapping未找到，则去新建索引:{}",e);
            addFieldMapping(info,clusterName);
            addList(clusterName,list,info);
        }

    }

    public <T extends BaseModel> BulkResponse addList(String clusterName, List<T> list,Mapper.EntityInfo info) {
        RestHighLevelClient client = esClientUtil.getClient(clusterName);
        ElasticIndex index = info.getIndex();
        BulkRequest request = new BulkRequest();
        list.forEach(obj ->{
            Object id = obj.getId();
            if (StrKit.isBlank(id)){
                throw new RuntimeException("主键值不可为空");
            }
            IndexRequest indexBuilder = new IndexRequest(index.getIndexName(),index.getIndexType(),id.toString());
            indexBuilder.source(Utils.toJson(obj), XContentType.JSON);
            request.add(indexBuilder);
        });
        request.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
        BulkResponse response;
        try {
            response = client.bulk(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        processException(response);
        return response;

    }

    /**
     * 批量更新索引
     * @param list
     * @param <T>
     */
    public  <T extends BaseModel> void updateIndexList(String clusterName,List<T> list){
        if (list.isEmpty()){
            throw new RuntimeException("批量更新索引的列表不可以为空");
        }
        retryUpdateIndexList(list,true,clusterName);

    }
    private  <T extends BaseModel> void retryUpdateIndexList(List<T> list,boolean fresh,String clusterName){
        Mapper.EntityInfo info = Mapper.getEntityInfo(list.get(0).getClass());
        try {
            if(isExist(info, clusterName)) {
                updateList(clusterName,list,fresh,info);
            }else {
                addCore(info, clusterName);
                updateList(clusterName,list,fresh,info);
            }
        }catch (ESMappingNotFoundException e){
            log.debug("新字段的mapping未找到，则去新建索引:{}",e);
            addFieldMapping(info,clusterName);
            updateList(clusterName,list,fresh,info);
        }
    }

    /**
     * 批量更新索引
     * @param list
     * @param <T>
     */
    public  <T extends BaseModel> void updateIndexListNoRefresh(List<T> list,String clusterName){
        retryUpdateIndexList(list,false,clusterName);
    }


    /**
     * 批量更新
     *
     * @param clusterName
     * @param list
     * @param <T>
     */
    public <T extends BaseModel> BulkResponse updateList(String clusterName, List<T> list,boolean fresh,Mapper.EntityInfo info){
        RestHighLevelClient client = esClientUtil.getClient(clusterName);
        BulkRequest request = new BulkRequest();
        ElasticIndex index = info.getIndex();
        list.forEach(obj ->{
            Object id = obj.getId();
            if (StrKit.isBlank(id)){
                throw new RuntimeException("主键值不可为空");
            }
            UpdateRequest update = new UpdateRequest(index.getIndexName(),
                    index.getIndexType(),
                    id.toString());
            update.doc(Utils.toJson(obj));
            update.docAsUpsert(true);
            update.retryOnConflict(10);
            request.add(update);
        });
        request.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
        BulkResponse response;
        try {
            response = client.bulk(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        processException(response);
        return response;
    }

    /**
     * 批量删除
     *
     * @param clusterName
     * @param ids
     */
    public  <T extends BaseModel> void deleteIndexList(Class<T> clazz,List<String> ids,String clusterName){
        if (clazz == null || ids.isEmpty()){
            throw new RuntimeException("批量删除索引的列表不可以为空");
        }
        Mapper.EntityInfo info = Mapper.getEntityInfo(clazz);
        RestHighLevelClient client = esClientUtil.getClient(clusterName);
        BulkRequest request = new BulkRequest();
        ElasticIndex index =info.getIndex();
        ids.forEach(id ->{
            DeleteRequest delete = new DeleteRequest(index.getIndexName(),index.getIndexType(),id);
            request.add(delete);
        });
        request.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
        BulkResponse response;
        try {
            response = client.bulk(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        processException(response);
    }



    /**
     * 添加索引core
     *
     * @param info
     */
    private void addCore(Mapper.EntityInfo info, String clusterName) {
        try {
            RestClient client = esClientUtil.getrestClient(clusterName);
            ElasticIndex index = info.getIndex();
            int shards = Integer.parseInt(esClientUtil.getshards());
            int replicas = Integer.parseInt(esClientUtil.getreplicas());
            int maxresult = Integer.parseInt(esClientUtil.getmaxresult());
            Map<String, Object> settings = new HashMap<>();
            settings.put("number_of_shards", shards);
            settings.put("number_of_replicas", replicas);
            settings.put("max_result_window", maxresult);
            log.debug("index : {} , shards :{} ,replicas :{} ", index.getIndexName(), shards, replicas);
            try (NStringEntity entity = new NStringEntity(Utils.toJson(settings), ContentType.APPLICATION_JSON);
                 NStringEntity mapping = new NStringEntity(Utils.toJson(info.getMappings()), ContentType.APPLICATION_JSON)) {
                client.performRequest("PUT", index.getIndexName(), Collections.emptyMap(), entity);
                client.performRequest("POST", index.getIndexName() + "/" + index.getIndexType() + "/_mapping", Collections.emptyMap(), mapping);
                indexMap.put(index.getIndexName(),index.getIndexName());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (UnknownHostException e) {
        }
    }

    /**
     * 添加新加字段的mapping
     *
     * @param info
     */
    private void addFieldMapping(Mapper.EntityInfo info, String clusterName) {
        try {

            RestClient client = esClientUtil.getrestClient(clusterName);
            ElasticIndex index = info.getIndex();
            try (NStringEntity mapping = new NStringEntity(JSON.toJSONString(info.getMappings()), ContentType.APPLICATION_JSON)) {
                client.performRequest("POST", index.getIndexName() + "/" + index.getIndexType() + "/_mapping", Collections.emptyMap(), mapping);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (UnknownHostException e) {
        }
    }


    /**
     * 处理异常
     *
     * @param response
     */
    private static void processException(BulkResponse response) {
        if (response.hasFailures()) {
            for (BulkItemResponse res : response) {
                if (res.isFailed()) {
                    String failureMessage = res.getFailureMessage();
                    if (StringUtils.isEmpty(failureMessage)) {
                        throw new RuntimeException(res.getFailure().getCause());
                    }
                    if (failureMessage.contains("index_not_found_exception")) {
                        throw new ESIndexNotFoundException("不存在该索引");
                    } else if (failureMessage.contains("strict_dynamic_mapping_exception")) {
                        throw new ESMappingNotFoundException();
                    } else {
                        throw new RuntimeException(res.getFailure().getCause());
                    }
                }
            }
        }
    }

    /**
     * 统一处理 增删改 请求，统一处理异常
     *
     * @param clusterName
     * @param action
     */
    private void exce(String clusterName, Func<RestHighLevelClient> action) {
        RestHighLevelClient client = esClientUtil.getClient(clusterName);
        try {
            action.accept(client);
        } catch (ElasticsearchStatusException e) {
            ExceptionConvert.convertHttpException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
