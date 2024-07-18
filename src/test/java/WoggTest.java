import com.github.catvod.spider.Wogg;
import common.AssertUtil;
import org.junit.jupiter.api.BeforeAll;

public class WoggTest {
    private static Wogg wogg;

    @BeforeAll
    public static void init() throws Exception {
        wogg = new Wogg();
        wogg.init("{}");
    }

    @org.junit.jupiter.api.Test
    public void homeContentTest() {
        String s1 = wogg.homeContent(true);
        System.out.println(s1);
        AssertUtil.INSTANCE.assertResult(s1);
    }

    @org.junit.jupiter.api.Test
    public void woggTest() throws Exception {
        Wogg wogg = new Wogg();
        wogg.init();
        String s = wogg.searchContent("宁安如梦", false);
//        String s = wogg.detailContent(Arrays.asList("/index.php/voddetail/81931.html"));
//        String s = wogg.playerContent("代理普畫#01", "yJcwweiN61T+656edff2c63533be753d4d90b5aac9f14495c882", Lists.newArrayList());
//        System.out.println(OkHttp.newCall("http://127.0.0.1:9978/proxy?do=ali&type=video&cate=open&shareId=5CNb1zzo7z9&fileId=6572dcc537aa2f73e51e499d9a14280cdcda9eaa").body());
        System.out.println(s);
        AssertUtil.INSTANCE.assertResult(s);
    }
}
