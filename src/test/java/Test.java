import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.EscapeUtil;
import com.github.catvod.api.AliYun;
import com.github.catvod.bean.ali.Data;
import com.github.catvod.net.OkHttp;
import com.github.catvod.spider.Ali;
import com.github.catvod.utils.QRCode;
import com.github.catvod.utils.Swings;
import com.github.catvod.utils.Util;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {
    public static void main(String[] args) throws Exception {
//        proxyTest();
        parse();
    }

    public static void parse(){
        String s = FileUtil.readString(new File("E:\\Archives\\compose-mutiplatform-workspace\\Test\\https __www.yjys.me_.htm"), "utf-8");
        Document document = Jsoup.parse(s);
        Elements pre = document.select("pre");
        String s1 = EscapeUtil.unescapeHtml4(pre.toString());
        System.out.println(s1);
    }

    public void dialogTest() {
//        Utils.showToast("ttttttest 我的");
        String json = OkHttp.string("https://passport.aliyundrive.com/newlogin/qrcode/generate.do?appName=aliyun_drive&fromSite=52&appName=aliyun_drive&appEntrance=web&isMobile=false&lang=zh_CN&returnUrl=&bizParams=&_bx-v=2.2.3");
        Data data = Data.objectFrom(json).getContent().getData();
        SwingUtilities.invokeLater(() -> {
            BufferedImage image = QRCode.getBitmap(data.getCodeContent(), 350, 2);
            JPanel jPanel = new JPanel();
            jPanel.setSize(Swings.dp2px(240), Swings.dp2px(240));
            jPanel.add(new JLabel(new ImageIcon(image)));
            Util.showDialog(jPanel, "请使用阿里云盘app扫描");
        });
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @org.junit.jupiter.api.Test
    public void test2() throws Exception {
        String input = "const playSource = {\n" +
                "    src: \"https://vip.ffzy-video.com/20250220/12513_38da0530/index.m3u8\",\n" +
                "    type: \"application/x-mpegURL\",\n" +
                "};";

        // Java 需要双反斜杠进行转义
        String regex = "playSource\\s*=\\s*\\{[^{}]*\\}";

        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            System.out.println("匹配结果: " + matcher.group());
        } else {
            System.out.println("未匹配到目标对象");
        }
    }

    public static void AliYunTest() throws Exception {
        Ali ali = new Ali();
        ali.init("");
        String s = ali.detailContent(Arrays.<String>asList("https://www.alipan.com/s/GKwvRJbZ71F"));
        System.out.println(s);
    }

    public static void proxyTest() throws Exception {
        // do=ali&type=video&cate=open&shareId=d9BiD8gfWpu&fileId=65654cf96f0e3f3f4dda456186b92bb63cb64f42
        HashMap<String, String> map = Maps.newHashMap();
        map.put("type", "video");
        map.put("cate", "open");
        map.put("shareId", "d9BiD8gfWpu");
        map.put("fileId", "65654cf96f0e3f3f4dda456186b92bb63cb64f42");

        Object[] proxy = Ali.proxy(map);
        System.out.println(proxy);
    }

    public void inputTest() {
//        String t = Utils.ShowInputDialog("请输入Token");
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//        System.out.println(t);
        String extend = "{\"filter\":\"https://fm.t4tv.hz.cz/json/wogg.json\"}";
        JsonObject asJsonObject = JsonParser.parseString(extend).getAsJsonObject();
        System.out.println();

    }

    public void notifyTest() {
        Util.notify("test");
        try {
            Thread.sleep(50000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void test() throws Exception {
        AliYun aliYun = new AliYun();
        //"http://127.0.0.1:9978/proxy?do=ali&type=video&cate=open&shareId=5CNb1zzo7z9&fileId=6572dcc537aa2f73e51e499d9a14280cdcda9eaa"
        HashMap<String, String> maps = new HashMap<>();
        maps.put("cate", "share");
        maps.put("shareId", "yJcwweiN61T");
        maps.put("fileId", "656edff2c63533be753d4d90b5aac9f14495c882");
//        Object[] objects = aliYun.proxyVideo(maps);
        String s = aliYun.getShareDownloadUrl("yJcwweiN61T", "656edff2c63533be753d4d90b5aac9f14495c882");
        System.out.println(s);
        Runtime.getRuntime().exec("\"E:\\Program File\\Tools\\Scoop\\apps\\potplayer\\current\\PotPlayerMini64.exe\"", new String[]{s});
        try {
            Thread.sleep(50000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @org.junit.jupiter.api.Test
    public void strTest() {
        String s = "{\"kanlist/全部\":[{\"name\":\"分类\",\"value\":[{\"v\":\"\",\"n\":\"全部\"},{\"v\":\"kanlist/剧情\",\"n\":\"剧情\"},{\"v\":\"kanlist/情感\",\"n\":\"情感\"},{\"v\":\"kanlist/治愈\",\"n\":\"治愈\"},{\"v\":\"kanlist/爱情\",\"n\":\"爱情\"},{\"v\":\"kanlist/颁奖\",\"n\":\"颁奖\"},{\"v\":\"kanlist/喜剧\",\"n\":\"喜剧\"},{\"v\":\"kanlist/获奖\",\"n\":\"获奖\"},{\"v\":\"kanlist/ 科幻\",\"n\":\"科幻\"},{\"v\":\"kanlist/漫威\",\"n\":\"漫威\"},{\"v\":\"kanlist/甜蜜\",\"n\":\"甜蜜\"},{\"v\":\"kanlist/悬疑\",\"n\":\"悬疑\"},{\"v\":\"kanlist/励志\",\"n\":\"励志\"},{\"v\":\"kanlist/烧脑\",\"n\":\"烧脑\"},{\"v\":\"kanlist/友情\",\"n\":\"友情\"}],\"key\":\"cateId\"}],\"billboard\":[],\"hot/index-movie-热门\":[{\"name\":\"分类\",\"value\":[{\"v\":\"\",\"n\":\"全部\"},{\"v\":\"hot/index-movie-最新\",\"n\":\"最新\"},{\"v\":\"hot/index-movie-经典\",\"n\":\"经典\"},{\"v\":\"hot/index-movie-豆瓣高 分\",\"n\":\"豆瓣高分\"},{\"v\":\"hot/index-movie-冷门佳片\",\"n\":\"冷 门佳片\"},{\"v\":\"hot/index-movie-华语\",\"n\":\"华语\"},{\"v\":\"hot/index-movie-欧美\",\"n\":\"欧美\"},{\"v\":\"hot/index-movie-韩国\",\"n\":\"韩国\"},{\"v\":\"hot/index-movie-日本\",\"n\":\"日本\"},{\"v\":\"hot/index-movie-动作\",\"n\":\"动作\"},{\"v\":\"hot/index-movie-喜剧\",\"n\":\"喜剧\"},{\"v\":\"hot/index-movie-爱情\",\"n\":\"爱情\"},{\"v\":\"hot/index-movie-科幻\",\"n\":\"科幻\"},{\"v\":\"hot/index-movie-悬疑\",\"n\":\"悬疑\"},{\"v\":\"hot/index-movie-恐怖\",\"n\":\"恐怖\"},{\"v\":\"hot/index-movie-成长\",\"n\":\"成长\"},{\"v\":\"hot/index-movie-豆瓣top250\",\"n\":\"豆瓣top250\"}],\"key\":\"cateId\"}],\"hot/index-tv-热门\":[{\"name\":\"分类\",\"value\":[{\"v\":\"\",\"n\":\"全部\"},{\"v\":\"hot/index-tv-美剧\",\"n\":\"美剧\"},{\"v\":\"hot/index-tv-英剧\",\"n\":\"英剧\"},{\"v\":\"hot/index-tv-韩剧\",\"n\":\"韩剧\"},{\"v\":\"hot/index-tv-日剧\",\"n\":\"日剧\"},{\"v\":\"hot/index-tv-国产剧\",\"n\":\"国产剧\"},{\"v\":\"hot/index-tv-港剧\",\"n\":\"港剧\"},{\"v\":\"hot/index-tv-日本动画\",\"n\":\"日本动画\"},{\"v\":\"hot/index-tv-综艺\",\"n\":\"综艺\"},{\"v\":\"hot/index-tv-纪录片\",\"n\":\"纪录片\"}],\"key\":\"cateId\"}]}\n";
        System.out.println(Util.base64Encode(s));
    }

    @org.junit.jupiter.api.Test
    public void matchTest() {
        Matcher matcher = Ali.pattern2.matcher("链接：https://www.aliyundrive.com/s/xt5uhhUJ2ZS");
        System.out.println(matcher.find());
        System.out.println(matcher.group());
    }
}
