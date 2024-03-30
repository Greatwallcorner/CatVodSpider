import com.github.catvod.spider.Zhaozy;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ZhaozyTest {
    private static Zhaozy instance;

    @BeforeAll
    public static void init() {
        instance = new Zhaozy();
        instance.init("$$$fanty$$$qqq111");
    }

    @Test
    public void searchTest() throws Exception {
        String s = instance.searchContent("阿凡达", false);
        System.out.println(s);
        AssertUtil.INSTANCE.assertResult(s);
    }
}
