import cn.hutool.json.JSONUtil;
import com.github.catvod.spider.JustLive;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import common.AssertUtil;
import org.junit.jupiter.api.Test;

/**
 * @author heatdesert
 * @date 2024-01-27 12:51
 * @description
 */
public class JustLiveTest {
    private static JustLive douban = new JustLive();

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

        String s = douban.categoryContent("网游", "1", false, Maps.newHashMap());
        System.out.print(JSONUtil.toJsonPrettyStr(JSONUtil.parseObj(s)));
        AssertUtil.INSTANCE.assertResult(s);

    }

    @Test
    public void detailTest() throws Exception {

        String detail = douban.detailContent(Lists.newArrayList("platform=douyu&roomId=1126960"));
        System.out.print(JSONUtil.toJsonPrettyStr(JSONUtil.parseObj(detail)));

    }

    @Test
    public void playerTest() throws Exception {

        String palyer = douban.playerContent(null, "https://tc-tct.douyucdn2.cn/dyliveflv1/1126960ri4dbIhGl.flv?wsAuth=3da61cdcc32e36ca4b72122caefdc6c9&token=web-h5-0-1126960-97b8314bb93dc18a2e17eafc14048b9d856b5578d09767c3&logo=0&expire=0&did=10000000000000000000000000001501&pt=2&st=0&sid=394241764&mcid2=0&origin=tct&mix=0&isp=", null);
        System.out.print(JSONUtil.toJsonPrettyStr(JSONUtil.parseObj(palyer)));

    }

    @Test
    public void searchTest() throws Exception {

        String detail = douban.searchContent("雨神", false);
        System.out.print(JSONUtil.toJsonPrettyStr(JSONUtil.parseObj(detail)));

    }

}
