import cn.hutool.json.JSONUtil;
import com.github.catvod.spider.Zxzj;
import com.github.catvod.utils.ProxyVideo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import common.AssertUtil;
import okhttp3.Response;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author heatdesert
 * @date 2024-01-27 12:51
 * @description
 */
public class ZxzjTest {
    private static Zxzj douban = new Zxzj();

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

        String s = douban.categoryContent("/list/1.html", "1", false, Maps.newHashMap());
        System.out.println(s);
        AssertUtil.INSTANCE.assertResult(s);

    }

    @Test
    public void detailTest() throws Exception {

        String detail = douban.detailContent(Lists.newArrayList("/detail/4465.html"));
        System.out.println(detail);

    }

    @Test
    public void playerTest() throws Exception {

        String palyer = douban.playerContent(null, "/video/4441-1-2.html", null);
        System.out.println(palyer);


    }

    @Test
    public void searchTest() throws Exception {

        String detail = douban.searchContent("红", false);
        System.out.println(detail);

    }

    @Test
    public void proxy() throws Exception {
        Map<String, String> header = new HashMap<>();

        header.put("Accept", "*/*");
        header.put("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8,zh-TW;q=0.7,de;q=0.6");
        header.put("Cache-Control", "no-cache");
        header.put("Connection", "keep-alive");
        header.put("Pragma", "no-cache");
      //  header.put("Host", "https://media-gzga-fy-home.gz9oss.ctyunxs.cn");

        header.put("Sec-Fetch-Dest", "video");
        header.put("Sec-Fetch-Mode", "no-cors");
        header.put("Sec-Fetch-Site", "cross-site");
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36");
        /*header.put("sec-ch-ua", "\"Chromium\";v=\"124\", \"Google Chrome\";v=\"124\", \"Not-A.Brand\";v=\"99\"");
        header.put("sec-ch-ua-mobile", "?0");
        header.put("sec-ch-ua-platform", "\"Windows\"");*/


//proxy url��https://cloud189-shh2-corp.oos-sh2.ctyunapi.cn/CORPCLOUD/317b4623-78ee-464d-8edf-074bd63524db.mp4?response-content-disposition=attachment%3Bfilename%3D%22J%E5%93%81%E5%9F%BA%E8%80%81%E4%BC%B4S01E01.mp4%22%3Bfilename*%3DUTF-8%27%27J%25E5%2593%2581%25E5%259F%25BA%25E8%2580%2581%25E4%25BC%25B4S01E01.mp4&x1719211-amz-CLIENTNETWORK=UNKNOWN&x-amz-CLOUDTYPEIN=CORP&x-amz-CLIENTTYPEIN=UNKNOWN&Signature=9ByMzICcxAk22J0sCg5NU37jrh0%3D&AWSAccessKeyId=e975956edda0be55c086&Expires=1719222468&x-amz-limitrate=102400&response-content-type=video/mp4&x-amz-FSIZE=328098705&x-amz-UID=10000000883250&x-amz-UFID=31418316925376437 headers{"sec-fetch-mode":"no-cors","sec-fetch-site":"cross-site","User-Agent":"Mozilla/5.0 (iPhone; CPU iPhone OS 16_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/100.0.4896.77 Mobile/15E148 Safari/604.1","Connection":"keep-alive","sec-fetch-dest":"video","Cache-Control":"max-age\u003d0","sec-ch-ua":"\"Chromium\";v\u003d\"124\", \"Google Chrome\";v\u003d\"124\", \"Not-A.Brand\";v\u003d\"99\"","sec-ch-ua-mobile":"?0","sec-ch-ua-platform":"\"Windows\"","Upgrade-Insecure-Requests":"1","Accept":"text/html,application/xhtml+xml,application/xml;q\u003d0.9,image/avif,image/webp,image/apng,*/*;q\u003d0.8,application/signed-exchange;v\u003db3;q\u003d0.7","Sec-Fetch-Site":"none","Sec-Fetch-Mode":"navigate","Sec-Fetch-User":"?1","Sec-Fetch-Dest":"document","Accept-Encoding":"gzip, deflate, br, zstd","Accept-Language":"zh-CN,zh;q\u003d0.9,en;q\u003d0.8,zh-TW;q\u003d0.7,de;q\u003d0.6","Cookie":"locale\u003dzh-cn"}
       Response response= ProxyVideo.proxyResponse("https://media-gzga-fy-home.gz9oss.ctyunxs.cn/FAMILYCLOUD/710fca7b-9b7c-476d-9a1d-aaa342647335.mp4?response-content-disposition=attachment%3Bfilename%3D%22%E6%AF%95%E4%B8%9A01.mp4%22%3Bfilename*%3DUTF-8%27%27%25E6%25AF%2595%25E4%25B8%259A01.mp4&x-amz-CLIENTNETWORK=UNKNOWN&x-amz-CLOUDTYPEIN=CORP&x-amz-CLIENTTYPEIN=UNKNOWN&Signature=iTPxWWMfoAracnVBDit/iS3hXMs%3D&AWSAccessKeyId=0Lg7dAq3ZfHvePP8DKEU&Expires=1719219616&x-amz-limitrate=102400&response-content-type=video/mp4&x-amz-FSIZE=1134121207&x-amz-UID=10000001365083&x-amz-UFID=31418316933734819",header);
        System.out.println(JSONUtil.toJsonPrettyStr(response));
    }

}
