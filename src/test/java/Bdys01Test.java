import com.github.catvod.spider.Bdys01;
import com.github.catvod.spider.Douban;
import com.github.catvod.spider.Liangzi;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Bdys01Test {
    private static Bdys01 douban = new Bdys01();

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
        System.out.println("homeTest--"+s);
        AssertUtil.INSTANCE.assertResult(s);
    }

    @Test
    public void cateTest() throws Exception {

        //String palyDoc= OkHttp.string("http://www.lzizy9.com/index.php/vod/play/id/79816/sid/1/nid/1.html");

        String s = douban.categoryContent("0", "1", true, Maps.newHashMap());
        System.out.println("cateTest--"+s);
        AssertUtil.INSTANCE.assertResult(s);

    }

    @Test
    public void detailTest() throws Exception {

        String detail = douban.detailContent(Lists.newArrayList("/kehuan/24802.htm;jsessionid=ukDN-L4VK3uuz0mKd2PVWqRRoR0tbSxK3j9sjUTZ"));
        System.out.println("detailTest--"+detail);

    }

    @Test
    public void playerTest() throws Exception {

        String palyer = douban.playerContent(null, "/play/24802-0.htm", null);
        System.out.println("playerTest--"+palyer);


    }

    @Test
    public void searchTest() throws Exception {

        String detail = douban.searchContent("红海", false);
        System.out.println(detail);

    }
}