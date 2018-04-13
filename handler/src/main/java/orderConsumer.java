import com.common.Elasticsearch.EsWriteUtils;
import com.common.Elasticsearch.esSearchUtil;
import com.common.Elasticsearch.meta.SearchResult;
import com.common.constantCode;
import com.common.kafka.AbstractConsumer;
import com.common.kafka.annotation.KafkaConf;
import com.db.model.orderInfo;
import com.db.service.inf.iorderInfoService;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@KafkaConf(topic = "order_create", groupid = "admin", threads = 1)
public class orderConsumer extends AbstractConsumer<orderInfo> {
    protected static final Logger log = LoggerFactory.getLogger(orderConsumer.class);
    @Autowired
    private EsWriteUtils esWriteUtils;
    @Autowired
    private iorderInfoService service;
    protected boolean process(orderInfo msg) {

        try {
            //插入数据库
            addorder(msg);
            //添加es索引
            esWriteUtils.addIndex(constantCode.getClusterName(),msg);
        } catch (Exception e) {
            log.error("错误："+e);
        }
        return false;
    }


    private Integer addorder(orderInfo model){

           return service.add(model);
    }
}
