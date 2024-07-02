import cn.hutool.json.JSONUtil;
import com.github.catvod.spider.AppYsV2;
import com.github.catvod.spider.DaGongRen;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.jupiter.api.Test;


public class AppYsV2Test {
    private static AppYsV2 douban = new AppYsV2();

    static {
        try {
            douban.init("http://106.52.241.21:83/api.php/app/");
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

        String s = douban.categoryContent("27", "1", false, Maps.newHashMap());
        System.out.print(JSONUtil.toJsonPrettyStr(JSONUtil.parseObj(s)));
        AssertUtil.INSTANCE.assertResult(s);

    }

    @Test
    public void detailTest() throws Exception {

        String detail = douban.detailContent(Lists.newArrayList("1630"));
        System.out.print(JSONUtil.toJsonPrettyStr(JSONUtil.parseObj(detail)));

    }

    @Test
    public void playerTest() throws Exception {

        String palyer = douban.playerContent("zzdj", "https://douyin.zzdj.cc/douyin/20220531/1734772864059551744.mp4", null);
        System.out.print(JSONUtil.toJsonPrettyStr(JSONUtil.parseObj(palyer)));

    }

    @Test
    public void searchTest() throws Exception {

        String detail = douban.searchContent("总", false);
        System.out.print(JSONUtil.toJsonPrettyStr(JSONUtil.parseObj(detail)));

    }

}
