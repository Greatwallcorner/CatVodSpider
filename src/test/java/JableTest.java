import com.github.catvod.spider.Jable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import common.AssertUtil;
import org.junit.jupiter.api.Test;

/**
 * @author heatdesert
 * @date 2024-01-27 12:51
 * @description
 */
public class JableTest {
    private static Jable douban = new Jable();

    static {
        try {
            douban.init();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void homeTest() throws Exception {
        String s = douban.homeContent(false);
        System.out.println(s);
        AssertUtil.INSTANCE.assertResult(s);
    }

    @Test
    public void cateTest() throws Exception {

        //String palyDoc= OkHttp.string("http://www.lzizy9.com/index.php/vod/play/id/79816/sid/1/nid/1.html");

        String s = douban.categoryContent("sex-only", "1", false, Maps.newHashMap());
        System.out.println(s);
        AssertUtil.INSTANCE.assertResult(s);

    }

    @Test
    public void detailTest() throws Exception {

        String detail = douban.detailContent(Lists.newArrayList("juq-705"));
        System.out.println(detail);

    }

    @Test
    public void playerTest() throws Exception {

        String palyer = douban.playerContent(null, "https://adoda-smart-coin.mushroomtrack.com/hls/Q66PjZ9NrbPnfjTRBUDI4Q/1716393109/41000/41839/41839.m3u8", null);
        System.out.println(palyer);

    }

    @Test
    public void searchTest() throws Exception {

        String detail = douban.searchContent("juq", false);
        System.out.println(detail);

    }

}
