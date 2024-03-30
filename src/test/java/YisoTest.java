import com.github.catvod.spider.YiSo;
import org.junit.jupiter.api.Test;

import java.util.UUID;

/**
 * @author heatdesert
 * @date 2024-01-27 12:11
 * @description
 */
public class YisoTest {
    @Test
    public void search() throws Exception {
        YiSo yiSo = new YiSo();
        yiSo.init();
        String s = yiSo.searchContent("宁安如梦", false);
        System.out.println(s);
        AssertUtil.INSTANCE.assertResult(s);
    }

    public void uuidTest() {
        System.out.println(UUID.randomUUID().toString());
    }
}
