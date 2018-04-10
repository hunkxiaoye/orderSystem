package com.common.Elasticsearch;

import com.common.Elasticsearch.meta.*;
import com.common.exception.ESIndexNotFoundException;
import com.common.exception.ExceptionConvert;
import com.common.func.RFunc;
import com.common.util.*;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.*;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.NumericMetricsAggregation;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class esSearchUtil {
    private  final Logger log = LoggerFactory.getLogger(esSearchUtil.class);
    @Autowired
    private esClientUtil esClientUtil;
    /**
     * 查询
     * @param clazz
     * @param clusterName
     * @param <T>
     * @return
     */

    public  <T extends BaseModel> Map<String, Object> getById(Class<T> clazz, String id, String clusterName){
        if (clazz == null || StrKit.isBlank(id)){
            throw new RuntimeException("查询的id不可以为空");
        }
        Mapper.EntityInfo info = Mapper.getEntityInfo(clazz);
        ElasticIndex index = info.getIndex();
        GetRequest req = new GetRequest(index.getIndexName(), index.getIndexType(), id);
        GetResponse response = exce(clusterName,client -> client.get(req));
        return response.getSource();
    }

    /**
     *
     * @param clazz
     * @param params
     * @param sort
     * @param filterQuery
     * @param nestParams
     * @param nestFilterString
     * @param pageIndex
     * @param pageSize
     * @param <T>
     * @return
     */
    public  <T extends BaseModel> SearchResult<T> query(Class<T> clazz,
                                                               Map<String,Object> params,
                                                               Map<String, SortOrder> sort,
                                                               String filterQuery,
                                                               Map<String,Map<String,Object>> nestParams,
                                                               Map<String,String> nestFilterString,
                                                               int pageIndex, int pageSize,String clusterName) {
        return query(clazz,"", params, sort, filterQuery, nestParams, nestFilterString,null, pageIndex, pageSize,clusterName);
    }



    /**
     * 根据条件分页查询
     * @param clazz
     * @param params
     * @param sort
     * @param filterQuery
     * @param nestParams
     *   Map<String,Map<String,Object>> nest = new HashMap<>();
     *   Map<String,Object> nestarr = new HashMap<>();
     *   nestarr.put("arr.zhangsan","iszhangsan");
     *   nestarr.put("arr",nests);
     *
     *   Map<String,Object> nestadd = new HashMap<>();
     *   nestadd.put("add.age","9");
     *   nestadd.put("add",nestadd);
     *
     *
     * @param nestFilterString
     *   String nestedString = "arr.age:[ 3 TO 5 ]";
     *   String nestedString1 = "arr.age:[ * TO 5 ]";
     *   String nestedString2 = "arr.age:[ 3 TO * ]";
     *   String nestedString4 = "arr.age:{ 3 TO 5 }";
     *   String nestedString5 = "arr.age:{ * TO 5 }";
     *   String nestedString6 = "arr.age:{ 3 TO * }";
     *   String nestedString7 = "arr.age:8 OR arr.name:zhangsan";
     *
     *
     *
     * @param pageIndex
     * @param pageSize
     * @param <T>
     *
     * @return
     */
    public  <T extends BaseModel> SearchResult<T> query(Class<T> clazz,
                                                               String suffix,
                                                               Map<String,Object> params,
                                                               Map<String, SortOrder> sort,
                                                               String filterQuery,
                                                               Map<String,Map<String,Object>> nestParams,
                                                               Map<String,String> nestFilterString,
                                                               IndexAgg indexAgg,
                                                               int pageIndex,int pageSize,String clusterName) {
        if(nestFilterString != null){
            log.info("nestFilterString :{}",FastJsonUtil.bean2Json(nestFilterString));
        }
        if (nestParams != null){
            log.info("nestParams :{}",FastJsonUtil.bean2Json(nestParams));
        }
        Mapper.EntityInfo info = Mapper.getEntityInfo(clazz);
        ElasticIndex index = info.getIndex();
        SearchRequest request = new SearchRequest(index.getIndexName()+(suffix==null?"":suffix));
        request.types(index.getIndexType());
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.from((pageIndex - 1) * pageSize);
        builder.size(pageSize);

        //查询条件
        BoolQueryBuilder booleanQueryBuilder = QueryBuilders.boolQuery();
        if (params !=null && !params.isEmpty()){
            String query = queryStr(params);
            booleanQueryBuilder.must(QueryBuilders.queryStringQuery(query));
        }
        //查询条件
        if (!StrKit.isBlank(filterQuery)){
            booleanQueryBuilder.must(QueryBuilders.queryStringQuery(filterQuery));
        }
        //nested 查询条件
        if (nestParams !=null && !nestParams.isEmpty()){
            nestParams.entrySet().forEach(nest ->{
                String query = queryStr(nest.getValue());
                NestedQueryBuilder nestedQueryBuilder =
                        QueryBuilders.nestedQuery(nest.getKey(), QueryBuilders.queryStringQuery(query), ScoreMode.None);
                booleanQueryBuilder.must(nestedQueryBuilder);
            });
        }
        //nested 查询条件
        if (nestFilterString !=null && !nestFilterString.isEmpty()){
            nestFilterString.entrySet().forEach(nest ->{
                NestedQueryBuilder nestedQueryBuilder =
                        QueryBuilders.nestedQuery(nest.getKey(), QueryBuilders.queryStringQuery(nest.getValue()),ScoreMode.None);
                booleanQueryBuilder.must(nestedQueryBuilder);
            });
        }
        //添加聚合字段
        if (indexAgg!=null){
            List<AbstractAggregationBuilder> builders = getAgg(indexAgg);
            if (builders != null && !builders.isEmpty()){
                for (AbstractAggregationBuilder b : builders) {
                    builder.aggregation(b);
                }
            }
        }
        //排序
        if (sort !=null && !sort.isEmpty()){
            sort(builder ,sort);
        }
//        builder.setPostFilter(booleanQueryBuilder);
        builder.query(booleanQueryBuilder);
        request.source(builder);
        SearchResponse response =null;
        try {
            response =exce(clusterName,client -> client.search(request));
        }catch (ESIndexNotFoundException e){}
        return getResult(clazz,response,indexAgg);
    }

    //排序
    private  void sort(SearchSourceBuilder builder , Map<String, SortOrder> sort){
        sort.entrySet().forEach(s -> builder.sort(s.getKey(),s.getValue()));
    }

    /**
     * 添加聚合字段
     * @param indexAgg
     * @return
     */
    private  List<AbstractAggregationBuilder> getAgg(IndexAgg indexAgg){
        if (indexAgg == null){
            return null;
        }
        List<AbstractAggregationBuilder> list = new ArrayList<>();
        Set<String> fields = indexAgg.getAggregation();
        if (!fields.isEmpty()){
            for (String f : fields) {
                TermsAggregationBuilder term = AggregationBuilders.terms(f).field(f);
                list.add(term);
            }
        }
        Map<String, IndexAgg.Func[]> groupAgg = indexAgg.getGroupAgg();
        if (groupAgg != null && !groupAgg.isEmpty()) {
            for (Map.Entry<String, IndexAgg.Func[]> entry : groupAgg.entrySet()) {
                String field = entry.getKey();
                IndexAgg.Func[] value = entry.getValue();
                for (IndexAgg.Func func : value) {
                    addGroup(list, func, field);
                }
            }
        }
        return list;
    }

    private  List<AbstractAggregationBuilder> addGroup(List<AbstractAggregationBuilder> list, IndexAgg.Func func, String field) {
        field = field.replace(func.name(), "");
        if (func.equals(IndexAgg.Func.MAX)) {

            list.add(AggregationBuilders.max(func.name() + field).field(field));
        } else if (func.equals(IndexAgg.Func.MIN)) {
            list.add(
                    AggregationBuilders.min(func.name() + field).field(field)
            );
        } else if (func.equals(IndexAgg.Func.SUM)) {
            list.add(
                    AggregationBuilders.sum(func.name() + field).field(field)
            );
        } else if (func.equals(IndexAgg.Func.COUNT)) {
            list.add(
                    AggregationBuilders.count(func.name() + field).field(field)
            );
        } else if (func.equals(IndexAgg.Func.AVG)) {
            list.add(
                    AggregationBuilders.avg(func.name() + field).field(field)
            );
        } else {
            throw new RuntimeException("不支持该类型的聚合");
        }
        return list;

    }

    /**
     * 把Map参数转换为ES查询的字符串
     * @param params
     * @return
     */
    private  String queryStr(Map<String,Object> params){
        StringBuilder qs = new StringBuilder();
        for (Map.Entry<String, Object> m : params.entrySet()) {
            Object o = m.getValue();
            String key = m.getKey();
            if (StrKit.isBlank(o)) continue;
            if(o.getClass().isArray()){
                Object[] values =(Object[])o;
                if (values.length == 0) continue;
                if (qs.length()>0){
                    qs.append(" AND ");
                }
                qs.append("( ");
                for (int i = 0; i < values.length; i++) {
                    if (i > 0) qs.append(" OR ");
                    qs.append(key + ":" + values[i]);
                }
                qs.append(")");
            } else {
                if (qs.length() != 0){
                    qs.append(" AND ");
                }
                qs.append(key+":"+o);
            }
        }
//        params.entrySet().forEach(m ->{
//
//        });
        return qs.toString();
    }

    /**
     * 根据条件分页查询
     * @param clazz
     * @param params
     * @param sort
     * @param pageIndex
     * @param pageSize
     * @param <T>
     * @return
     */
    public  <T extends BaseModel> SearchResult<T> query(Class<T> clazz,
                                                               Map<String,Object> params,
                                                               Map<String, SortOrder> sort,
                                                               int pageIndex,int pageSize,String clusterName) {
        return query(clazz, params, sort, null, null, null, pageIndex, pageSize,clusterName);
    }

    /**
     * 根据条件分页查询
     * @param clazz
     * @param params  params:
     * @param sort
     * @param pageIndex
     * @param pageSize
     * @param filterQuery date:{ 2016-09-10 TO 2016-09-12 } date:[ 3 TO 7 ]   date:[ * TO 7 ] date:[ 3 TO * ] AND (status : ( 2 OR 3 OR 4))
     * @param <T>
     * @return
     */
    public  <T extends BaseModel> SearchResult<T> query(Class<T> clazz,
                                                               Map<String,Object> params,
                                                               Map<String, SortOrder> sort,
                                                               String filterQuery,
                                                               int pageIndex,int pageSize,String clusterName) {
        return query(clazz, params, sort, filterQuery, null, null, pageIndex, pageSize,clusterName);
    }


    /**
     * 根据条件分页查询
     * @param clazz
     * @param params  params:
     * @param sort
     * @param pageIndex
     * @param pageSize
     * @param filterQuery date:{ 2016-09-10 TO 2016-09-12 } date:[ 3 TO 7 ]   date:[ * TO 7 ] date:[ 3 TO * ] AND (status : ( 2 OR 3 OR 4))
     * @param <T>
     * @return
     */
    public  <T extends BaseModel> SearchResult<T> query(Class<T> clazz,
                                                               Map<String,Object> params,
                                                               Map<String, SortOrder> sort,
                                                               String filterQuery,
                                                               IndexAgg indexAgg,
                                                               int pageIndex,int pageSize,String clusterName) {
        return query(clazz,null, params, sort, filterQuery, null, null,indexAgg, pageIndex, pageSize,clusterName);
    }

    /**
     * 获取结果
     * @param response
     * @return
     */
    private  <T extends BaseModel> SearchResult<T> getResult(Class<T> clazz,SearchResponse response,IndexAgg indexAgg){
        SearchResult<T> result = new SearchResult<>();
        if (response == null){
            return result;
        }
        //设置查询总条数
        result.setSearchCount(Integer.valueOf(String.valueOf(response.getHits().getTotalHits())));
        //设置fqResult
        SearchHit[] hits = response.getHits().getHits();
        List<T> searchList= new ArrayList<>();
        for (SearchHit doc : hits) {
            T t = Utils.json2Bean(doc.getSourceAsString(), clazz);
            searchList.add(t);
        }
        result.setSearchList(searchList);
        //设置aggResult
        if (indexAgg !=null){
            result.setAggResult(aggResult(response, indexAgg));
        }
        //设置groupResult
        if (indexAgg !=null){
            result.setGroupResult(groupResult(response, indexAgg));
        }
        //设置scrollId
        if (!StringUtils.isEmpty(response.getScrollId())){
            result.setScrollId(response.getScrollId());
        }
        return result;
    }


    private  Map<String,Map<Object,Long>> aggResult(SearchResponse response,IndexAgg indexAgg){
        Map<String,Map<Object,Long>> aggResult = new HashMap<>();
        Set<String> fields = indexAgg.getAggregation();
        for (String f : fields) {
            Terms term = response.getAggregations().get(f);
            Map<Object,Long> res = new HashMap<>();
            for (Terms.Bucket b : term.getBuckets()) {
                res.put(b.getKey(),b.getDocCount());
            }
            aggResult.put(f,res);
        }
        return aggResult;
    }

    private  Map<String,Map<IndexAgg.Func,Object>> groupResult(SearchResponse response,IndexAgg group){
        Aggregations agg = response.getAggregations();
        Map<String, IndexAgg.Func[]> groupAgg = group.getGroupAgg();
        if (groupAgg != null && !groupAgg.isEmpty()) {
            Map<String, Map<IndexAgg.Func, Object>> aggGroup = new HashMap<>();
            for (Map.Entry<String, IndexAgg.Func[]> entry : groupAgg.entrySet()) {
                String field = entry.getKey();
                IndexAgg.Func[] value = entry.getValue();
                Map<IndexAgg.Func, Object> m = new HashMap<>();
                for (IndexAgg.Func func : value) {
                    NumericMetricsAggregation.SingleValue groupValue =
                            agg.get(func.name() + field.replace(func.name(), ""));
                    m.put(func, groupValue.value());
                }
                aggGroup.put(field, m);
            }
            return aggGroup;
        }
        return null;
    }

    public  <T extends BaseModel> SearchResult<T>  scrollQuery(Class<T> clazz,
                                                                      String suffix,
                                                                      String scrollId,
                                                                      Map<String,Object> params,
                                                                      Map<String, SortOrder> sort,
                                                                      String filterQuery,
                                                                      Map<String,Map<String,Object>> nestParams,
                                                                      Map<String,String> nestFilterString,
                                                                      IndexAgg indexAgg,
                                                                      int pageSize,
                                                                      String clusterName){

        return scrollQuery(clazz,suffix,scrollId,params,sort,filterQuery,nestParams,nestFilterString,indexAgg,pageSize,0,clusterName);

    }


    /**
     * 用于全量查询索引使用
     * @param clazz
     * @param suffix
     * @param scrollId
     * @param params
     * @param filterQuery
     * @param pageSize
     * @param sessionTimeOut scrollId 过期时间 毫秒
     * @param <T>
     * @return
     */
    public  <T extends BaseModel> SearchResult<T>  scrollQuery(Class<T> clazz,
                                                                      String suffix,
                                                                      String scrollId,
                                                                      Map<String,Object> params,
                                                                      Map<String, SortOrder> sort,
                                                                      String filterQuery,
                                                                      Map<String,Map<String,Object>> nestParams,
                                                                      Map<String,String> nestFilterString,
                                                                      IndexAgg indexAgg,
                                                                      int pageSize,
                                                                      int sessionTimeOut,String clusterName){
        if(nestFilterString != null){
            log.info("nestFilterString :{}",FastJsonUtil.bean2Json(nestFilterString));
        }
        if (nestParams != null){
            log.info("nestParams :{}",FastJsonUtil.bean2Json(nestParams));
        }
        if (sessionTimeOut == 0){
            sessionTimeOut = 60000;
        }
        Mapper.EntityInfo info = Mapper.getEntityInfo(clazz);
        ElasticIndex index = info.getIndex();
        TimeValue timeValue = new TimeValue(sessionTimeOut, TimeUnit.MILLISECONDS);
        SearchRequest request = new SearchRequest(index.getIndexName() + (suffix == null ? "" : suffix));
        request.types(index.getIndexType());
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.size(pageSize);
        request.scroll(new Scroll(timeValue));
        SearchResponse response=null;
        if (StringUtils.isEmpty(scrollId)){
            //查询条件
            BoolQueryBuilder booleanQueryBuilder = QueryBuilders.boolQuery();
            if (params !=null && !params.isEmpty()){
                String query = queryStr(params);
                booleanQueryBuilder.must(QueryBuilders.queryStringQuery(query));
            }
            //查询条件
            if (!StrKit.isBlank(filterQuery)){
                booleanQueryBuilder.must(QueryBuilders.queryStringQuery(filterQuery));
            }

            //查询条件
            if (!StrKit.isBlank(filterQuery)){
                booleanQueryBuilder.must(QueryBuilders.queryStringQuery(filterQuery));
            }
            //nested 查询条件
            if (nestParams !=null && !nestParams.isEmpty()){
                nestParams.entrySet().forEach(nest ->{
                    String query = queryStr(nest.getValue());
                    NestedQueryBuilder nestedQueryBuilder =
                            QueryBuilders.nestedQuery(nest.getKey(), QueryBuilders.queryStringQuery(query),ScoreMode.None);
                    booleanQueryBuilder.must(nestedQueryBuilder);
                });
            }
            //nested 查询条件
            if (nestFilterString !=null && !nestFilterString.isEmpty()){
                nestFilterString.entrySet().forEach(nest ->{
                    NestedQueryBuilder nestedQueryBuilder =
                            QueryBuilders.nestedQuery(nest.getKey(), QueryBuilders.queryStringQuery(nest.getValue()),ScoreMode.None);
                    booleanQueryBuilder.must(nestedQueryBuilder);
                });
            }
            //添加聚合字段
            if (indexAgg!=null){
                List<AbstractAggregationBuilder> builders = getAgg(indexAgg);
                if (builders != null && !builders.isEmpty()){
                    for (AbstractAggregationBuilder b : builders) {
                        builder.aggregation(b);
                    }
                }
            }
            //排序
            if (sort !=null && !sort.isEmpty()){
                sort(builder ,sort);
            }
            try {
                response =exce(clusterName,client -> client.search(request));
            }catch (ESIndexNotFoundException e){}

        }else {
            SearchScrollRequest req = new SearchScrollRequest(scrollId);
            req.scroll(timeValue);
            try {
                response =exce(clusterName,client -> client.searchScroll(req));
            }catch (ESIndexNotFoundException e){}
        }
        return getResult(clazz,response,null);
    }

    /**
     * 用于全量查询索引使用
     * @param clazz
     * @param suffix
     * @param scrollId
     * @param params
     * @param filterQuery
     * @param pageSize
     * @param <T>
     * @return
     */
    public  <T extends BaseModel> SearchResult<T>  scrollQuery(Class<T> clazz,
                                                                      String suffix,
                                                                      String scrollId,
                                                                      Map<String,Object> params,
                                                                      String filterQuery,
                                                                      int pageSize,String clusterName){

        return scrollQuery(clazz,suffix,scrollId,params,null,filterQuery,null,null,null,pageSize,0,clusterName);
    }

    /**
     * 用于全量查询索引使用
     * @param clazz
     * @param suffix
     * @param scrollId
     * @param params
     * @param filterQuery
     * @param pageSize
     * @param sessionTimeOut 毫秒
     * @param <T>
     * @return
     */
    public  <T extends BaseModel> SearchResult<T>  scrollQuery(Class<T> clazz,
                                                                      String suffix,
                                                                      String scrollId,
                                                                      Map<String,Object> params,
                                                                      String filterQuery,
                                                                      int pageSize,int sessionTimeOut,String clusterName){

        return scrollQuery(clazz,suffix,scrollId,params,null,filterQuery,null,null,null,pageSize,sessionTimeOut,clusterName);
    }



    /**
     * 用于全量查询索引使用
     * @param clazz
     * @param params
     * @param filterQuery
     * @param pageSize
     * @param <T>
     * @return
     */
    public  <T extends BaseModel, U extends BaseModel> MultiSearchResult<T,U> multiIndexQuery(
            Class<T> clazz,
            Class<U> clazu,
            Map<String,Object> params,
            Map<String, SortOrder> sort,
            String filterQuery,
            int pageIndex,
            int pageSize,String clusterName){
        Mapper.EntityInfo info = Mapper.getEntityInfo(clazz);
        Mapper.EntityInfo infou = Mapper.getEntityInfo(clazu);
        ElasticIndex indext = info.getIndex();
        ElasticIndex indexu = infou.getIndex();
        SearchRequest request = new SearchRequest(indext.getIndexName(),indexu.getIndexName());
        request.types(indext.getIndexType(),indexu.getIndexType());
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.from((pageIndex - 1) * pageSize);
        builder.size(pageSize);

        //查询条件
        BoolQueryBuilder booleanQueryBuilder = QueryBuilders.boolQuery();
        if (params !=null && !params.isEmpty()){
            String query = queryStr(params);
            booleanQueryBuilder.must(QueryBuilders.queryStringQuery(query));
        }
        //查询条件
        if (!StrKit.isBlank(filterQuery)){
            booleanQueryBuilder.must(QueryBuilders.queryStringQuery(filterQuery));
        }
        //排序
        if (sort !=null && !sort.isEmpty()){
            sort(builder ,sort);
        }
//        builder.setPostFilter(booleanQueryBuilder);
        builder.query(booleanQueryBuilder);
        SearchResponse response =null;
        try {
            response =exce(clusterName,client -> client.search(request));
        }catch (ESIndexNotFoundException e){}
        MultiSearchResult<T,U> result = new MultiSearchResult<>();
        if (response == null){
            return result;
        }
        SearchHit[] hits = response.getHits().getHits();
        List<T> searchTList= new ArrayList<>();
        List<U> searchUList= new ArrayList<>();
        for (SearchHit hit : hits) {
            String index = hit.getIndex();
            if (index.equals(indext.getIndexName())){
                T t = Utils.json2Bean(hit.getSourceAsString(), clazz);
                searchTList.add(t);
            }else{
                U u = Utils.json2Bean(hit.getSourceAsString(), clazu);
                searchUList.add(u);
            }
        }
        result.setSearchListT(searchTList);
        result.setSearchListU(searchUList);
        result.setSearchCount(response.getHits().getTotalHits());
        return result;
    }


    public  void clearScrollId(String scrollId,String clusterName){
        if (StringUtils.isEmpty(scrollId)){
            return;
        }
        ClearScrollRequest request = new ClearScrollRequest();
        request.addScrollId(scrollId);
        ClearScrollResponse response = exce(clusterName, client-> client.clearScroll(request));
        if(response.isSucceeded()){
            log.debug("释放scrollId 成功！");
        }
    }

    /**
     * 统一处理 增删改 请求，统一处理异常
     * @param action
     */
    private  <R extends ActionResponse> R  exce(String clusterName,RFunc<RestHighLevelClient,ActionResponse> action){
        RestHighLevelClient client = esClientUtil.getClient(clusterName);
        try {
            return (R)action.accept(client);
        } catch (ElasticsearchStatusException e) {
            ExceptionConvert.convertHttpException(e);
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
    
}
