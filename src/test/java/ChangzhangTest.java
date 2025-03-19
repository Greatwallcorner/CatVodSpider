import com.github.catvod.spider.ChangZhang;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import common.AssertUtil;
import org.junit.jupiter.api.Test;

/**
 * @author heatdesert
 * @date 2024-01-27 12:51
 * @description
 */
public class ChangzhangTest {
    private static ChangZhang douban = new ChangZhang();

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

        String s = douban.categoryContent("/zuixindianying", "1", false, Maps.newHashMap());
        System.out.println(s);
        AssertUtil.INSTANCE.assertResult(s);

    }

    @Test
    public void detailTest() throws Exception {

        String detail = douban.detailContent(Lists.newArrayList("https://www.czzyvideo.com/movie/20460.html"));
        System.out.println(detail);

    }

    @Test
    public void playerTest() throws Exception {

        String palyer = douban.playerContent(null, "https://www.czzyvideo.com/v_play/bXZfMjAyOTQtbm1fMQ==.html", null);
//        String palyer = douban.playerContent(null, "https://www.czzyvideo.com/v_play/bXZfMTc1ODAtbm1fMQ==.html", null);
//        String palyer = douban.playerContent(null, "https://www.czzyvideo.com/v_play/bXZfODE1OC1ubV8x.html", null);
        System.out.println(palyer);


    }

    @Test
    public void searchTest() throws Exception {

        String detail = douban.searchContent("红", false);
        System.out.println(detail);

    }

}
