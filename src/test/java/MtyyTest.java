import cn.hutool.json.JSONUtil;
import com.github.catvod.spider.Liangzi;
import com.github.catvod.spider.Mtyy;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.jupiter.api.Test;

/**
 * @author heatdesert
 * @date 2024-01-27 12:51
 * @description
 */
public class MtyyTest {
    private static Mtyy douban = new Mtyy();

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

        String s = douban.categoryContent("1", "1", false, Maps.newHashMap());
        System.out.println(s);
        AssertUtil.INSTANCE.assertResult(s);

    }

    @Test
    public void detailTest() throws Exception {

        String detail = douban.detailContent(Lists.newArrayList("https://mtyy1.com/voddetail/8529.html"));
        System.out.println(JSONUtil.toJsonPrettyStr(JSONUtil.parseObj(detail)));

    }

    @Test
    public void playerTest() throws Exception {

        String palyer = douban.playerContent(null, "https://mtyy1.com/vodplay/8529-1-8.html", null);
        System.out.println(JSONUtil.toJsonPrettyStr(JSONUtil.parseObj(palyer)));


    }

    @Test
    public void searchTest() throws Exception {

        String detail = douban.searchContent("红海", false);
        System.out.println(detail);

    }

}
