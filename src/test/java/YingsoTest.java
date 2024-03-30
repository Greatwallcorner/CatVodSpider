import com.github.catvod.spider.Yingso;
import org.junit.jupiter.api.Test;

public class YingsoTest {
    private static Yingso instance = new Yingso();

    @Test
    public void searchTest() throws Exception {
        String s = instance.searchContent("阿凡达", true);
        System.out.println(s);
        AssertUtil.INSTANCE.assertResult(s);
    }


}
