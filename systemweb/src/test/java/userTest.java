import com.db.model.User;
import com.db.service.inf.iuserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring-*.xml"})
public class userTest {
    @Autowired
    private iuserService Service;

    @Test
    public void testdemo(){
        User user = Service.checklogin("123", "123");
        System.out.println(user.getId());
    }
}
