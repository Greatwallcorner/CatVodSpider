import com.github.catvod.spider.YpPan;
import com.google.common.collect.Lists;
import common.AssertUtil;
import org.junit.jupiter.api.Test;

public class YpPanTest {
    private static YpPan instance = new YpPan();

    @Test
    public void searchTest() throws Exception {
        String s = instance.searchContent("惜花芷", false);
        System.out.println(s);
        AssertUtil.INSTANCE.assertResult(s);
    }

    @Test
    public void detailTest() throws Exception {
        String s = instance.detailContent(Lists.newArrayList("https://www.yppan.com/archives/45299"));
        System.out.println(s);
        AssertUtil.INSTANCE.assertResult(s);
    }
}
