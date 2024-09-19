import com.github.catvod.spider.WuMingMusic;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import common.AssertUtil;
import org.junit.jupiter.api.Test;

/**
 * @author heatdesert
 * @date 2024-01-27 12:51
 * @description
 */
public class WuMingMusicTest {
    private static WuMingMusic douban = new WuMingMusic();

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

        String s = douban.categoryContent("love", "1", false, Maps.newHashMap());
        System.out.println(s);
        AssertUtil.INSTANCE.assertResult(s);

    }

    @Test
    public void detailTest() throws Exception {

        String detail = douban.detailContent(Lists.newArrayList("http://www.mvmp3.com/sing/70ec1f2e0383021891a716060cc6eeae.html"));
        System.out.println(detail);
        String detail2 = douban.detailContent(Lists.newArrayList("http://www.mvmp3.com/mp4/54340c3ab942a50df0d9b49f202f6623.html"));
        System.out.println(detail2);


    }

    @Test
    public void playerTest() throws Exception {

        String palyer = douban.playerContent(null, "http://www.mvmp3.com/sing/70ec1f2e0383021891a716060cc6eeae.html", null);
        System.out.println(palyer);
        String palyer2 = douban.playerContent(null, "http://www.mvmp3.com/mp4/54340c3ab942a50df0d9b49f202f6623.html", null);
        System.out.println(palyer2);

    }

    @Test
    public void searchTest() throws Exception {

        String detail = douban.searchContent("周杰伦", false);
        System.out.println(detail);

    }

}
