import com.github.catvod.spider.QiLeSo;
import com.github.catvod.spider.Yingso;
import common.AssertUtil;
import org.junit.jupiter.api.Test;

public class QiLeSoTest {
    private static QiLeSo instance = new QiLeSo();

    @Test
    public void searchTest() throws Exception {
        String s = instance.searchContent("阿凡达", true);
        System.out.println(s);
        AssertUtil.INSTANCE.assertResult(s);
    }


}
