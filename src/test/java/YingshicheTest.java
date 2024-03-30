import com.github.catvod.spider.unavailable.Yingshiche;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class YingshicheTest {
    private static Yingshiche instance;

    @BeforeAll
    public static void init() throws Exception {
        instance = new Yingshiche();
        instance.init();
    }

    @Test
    public void homeTest() throws Exception {
        String s = instance.homeContent(false);
        System.out.println(s);
        AssertUtil.INSTANCE.assertResult(s);
    }
}
