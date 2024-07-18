import cn.hutool.json.JSONUtil;
import com.github.catvod.spider.DaGongRen;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import common.AssertUtil;
import org.junit.jupiter.api.Test;

/**
 * @author heatdesert
 * @date 2024-01-27 12:51
 * @description
 */
public class DaGongRenTest {
    private static DaGongRen douban = new DaGongRen();

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
        System.out.println(JSONUtil.toJsonPrettyStr(JSONUtil.parseObj(s)));
        AssertUtil.INSTANCE.assertResult(s);
    }

    @Test
    public void cateTest() throws Exception {

        //String palyDoc= OkHttp.string("http://www.lzizy9.com/index.php/vod/play/id/79816/sid/1/nid/1.html");

        String s = douban.categoryContent("dianying", "1", false, Maps.newHashMap());
        System.out.print(JSONUtil.toJsonPrettyStr(JSONUtil.parseObj(s)));
        AssertUtil.INSTANCE.assertResult(s);

    }

    @Test
    public void detailTest() throws Exception {

        String detail = douban.detailContent(Lists.newArrayList("126323-1-1.html"));
        System.out.print(JSONUtil.toJsonPrettyStr(JSONUtil.parseObj(detail)));

    }

    @Test
    public void playerTest() throws Exception {

        String palyer = douban.playerContent(null, "126323-2-1.html", null);
        System.out.print(JSONUtil.toJsonPrettyStr(JSONUtil.parseObj(palyer)));

    }

    @Test
    public void searchTest() throws Exception {

        String detail = douban.searchContent("红海", false);
        System.out.print(JSONUtil.toJsonPrettyStr(JSONUtil.parseObj(detail)));

    }

}
