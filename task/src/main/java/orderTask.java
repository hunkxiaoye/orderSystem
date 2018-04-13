import com.db.model.orderInfo;
import com.db.service.inf.iorderInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class orderTask {

    private static final Logger log = LoggerFactory.getLogger(orderTask.class);
    @Autowired
    private iorderInfoService service;

    @Scheduled(fixedDelay = 1000 * 60 * 15)
    public void orderTimeOut() {
        log.info("取消超时订单开始");

        List<orderInfo> list = service.findByStatus(0);
        for (int i = 0; i < list.size(); i++) {
            service.update(list.get(i));

        }
        log.info("取消超时订单结束");
    }
}
