import com.github.catvod.spider.Supjav;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import common.AssertUtil;
import org.junit.jupiter.api.Test;

/**
 * @author heatdesert
 * @date 2024-01-27 12:51
 * @description
 */
public class SupjavTest {
    private static Supjav douban = new Supjav();

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

        String s = douban.categoryContent("popular", "1", false, Maps.newHashMap());
        System.out.println(s);
        AssertUtil.INSTANCE.assertResult(s);

    }

    @Test
    public void detailTest() throws Exception {

        String detail = douban.detailContent(Lists.newArrayList("274533.html"));
        System.out.println(detail);

    }

    @Test
    public void playerTest() throws Exception {
    //{"list":[{"type_name":"无码","vod_id":"274774.html","vod_name":"FC2PPV 4440630 初撮影・顔出し！！3日間限定！！恥ずかしがり屋だけど欲しがりでごめんなさい・・・理性が崩壊し絶叫と共に快楽に堕ちて行く性欲に貪欲で超敏感な身体に2回中出し＆口内射精！！","vod_pic":"https://img.supjav.com/images/2024/05/FC2PPV-4440630.jpg","vod_actor":"","vod_director":"FC2PPV","vod_play_from":"$$$[TV, FST, ST, VOE]","vod_play_url":"$$$[播放$c5904b2353c99688759105641ef07ad5b509f31be223399b691e8c110ce572e7392e9ba549fcb733588724caa455fa30a61d777f1331aff3ee3ab3376ef66d9b9acb183bde233e92936545ab1b621dfb, 播放$b13642e792fcd70d5e0cb8a4b196aa40cf448dbe04641d4e81ed2e52b4dc55ce7f4a59ad0e865f1b4d9e34dbe226cdcc40ec53eb3cf4f17270830acf091765df, 播放$0a6777b5ebd586c4e022f0d28d425b4bc6c3e6386c8f85bf6b84381f4a6baaa18a6538b46749f1e79fcea83893ca1c69022b72cc2952c6f363ea1cdb83b4906b549b8fd2edab26b7049e1116b32c18c9, 播放$3cda8a7304b947f4fce29ed20010713b5b229fdf65f58cd9b6ae863c963731a6ae400d97783fef11414778e14c221ae85b8c9b26fdfbb54fef4cad7eaf8a6b32]"}],"parse":0,"jx":0}
        String palyer = douban.playerContent("TV", "0a30ef80b0b9a7d6ae58e4c438964a4b7af1fe6b287b8162dbb451e616faf88c603a6596e59f542def72e0c610707b08d1cbf622948c291bf38e7d8c759f07c99acb183bde233e92936545ab1b621dfb", null);
        System.out.println(palyer);
        String palyerST = douban.playerContent("ST", "0a6777b5ebd586c4e022f0d28d425b4bc6c3e6386c8f85bf6b84381f4a6baaa18a6538b46749f1e79fcea83893ca1c69022b72cc2952c6f363ea1cdb83b4906b549b8fd2edab26b7049e1116b32c18c9", null);
        System.out.println(palyerST);
        String palyerFST = douban.playerContent("FST", "b13642e792fcd70d5e0cb8a4b196aa40cf448dbe04641d4e81ed2e52b4dc55ce7f4a59ad0e865f1b4d9e34dbe226cdcc40ec53eb3cf4f17270830acf091765df", null);
        System.out.println(palyerFST);
        String palyerVOE = douban.playerContent("VOE", "3cda8a7304b947f4fce29ed20010713b5b229fdf65f58cd9b6ae863c963731a6ae400d97783fef11414778e14c221ae85b8c9b26fdfbb54fef4cad7eaf8a6b32", null);
        System.out.println(palyerVOE);

    }

    @Test
    public void searchTest() throws Exception {

        String detail = douban.searchContent("juq", false);
        System.out.println(detail);

    }

}
