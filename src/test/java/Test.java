import com.github.catvod.api.AliYun;
import com.github.catvod.bean.ali.Data;
import com.github.catvod.net.OkHttp;
import com.github.catvod.spider.Ali;
import com.github.catvod.utils.QRCode;
import com.github.catvod.utils.Swings;
import com.github.catvod.utils.Utils;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.client.utils.DateUtils;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;

public class Test {
    public static void main(String[] args) throws Exception {
        test();
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
            Utils.showDialog(jPanel, "请使用阿里云盘app扫描");
        });
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void AliYunTest() throws Exception {
        Ali ali = new Ali();
        ali.init("");
        String s = ali.detailContent(Arrays.<String>asList("https://www.aliyundrive.com/s/SsotExmDDDu/folder/641d60b407e2b47950a845b1a6d571fb258c563f"));
        System.out.println(s);
    }

    public void proxyTest() throws Exception {
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
        Utils.notify("test");
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
        String s = "https://www.yppan.com";
        System.out.println(Utils.base64Encode(s));
    }

    @org.junit.jupiter.api.Test
    public void matchTest() {
        Matcher matcher = Ali.pattern2.matcher("链接：https://www.aliyundrive.com/s/xt5uhhUJ2ZS");
        System.out.println(matcher.find());
        System.out.println(matcher.group());
    }
}
