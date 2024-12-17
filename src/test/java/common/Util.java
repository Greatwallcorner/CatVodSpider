package common;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HtmlUtil;
import com.github.catvod.utils.Utils;
import org.apache.commons.codec.digest.DigestUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;

import java.io.File;

public class Util {
    @Test
    public void strEncode(){
        String s = "/vodsearch/-------------.html?wd=%s&submit=";
        System.out.println(Utils.base64Encode(s));
    }

    @Test
    public void strDecode(){
        String s = "YzNSaGRHbGpMM0JzWVhsbGNpOTJhV1JxY3pJMUxuQm9jQT09";
        System.out.println(Utils.base64Decode(s));
    }


    @Test
    public void md5Test(){
        String s = "<script>\n" +
                "    function _0xf746(_0xbb40c4,_0x1cb776){const _0x45e084=_0x45e0();return _0xf746=function(_0xf74696,_0x4d32af){_0xf74696=_0xf74696-0x1a8;let _0xcbfa28=_0x45e084[_0xf74696];return _0xcbfa28;},_0xf746(_0xbb40c4,_0x1cb776);}function _0x45e0(){const _0x58b10c=['1580630GngmmA','117uvwflw','join','current_id','565448Apkhig','23092JwmytW','707152yowhOv','getElementById','855936CGaczt','length','2966831GCGpvn','611266nfcTEf','value','substring'];_0x45e0=function(){return _0x58b10c;};return _0x45e0();}(function(_0x27923d,_0x43d7fc){const _0x439396=_0xf746,_0x30f164=_0x27923d();while(!![]){try{const _0xa560eb=-parseInt(_0x439396(0x1b4))/0x1+parseInt(_0x439396(0x1ad))/0x2+-parseInt(_0x439396(0x1b1))/0x3*(-parseInt(_0x439396(0x1b5))/0x4)+-parseInt(_0x439396(0x1b0))/0x5+parseInt(_0x439396(0x1aa))/0x6+parseInt(_0x439396(0x1ac))/0x7+parseInt(_0x439396(0x1a8))/0x8;if(_0xa560eb===_0x43d7fc)break;else _0x30f164['push'](_0x30f164['shift']());}catch(_0x3ae316){_0x30f164['push'](_0x30f164['shift']());}}}(_0x45e0,0x4a3d9));function get_tks(){const _0xf07220=_0xf746;let _0x35162d=document[_0xf07220(0x1a9)](_0xf07220(0x1b3))[_0xf07220(0x1ae)],_0xf25678=document['getElementById']('e_token')[_0xf07220(0x1ae)];if(!_0x35162d||!_0xf25678)return;let _0x3882a3=_0x35162d['length'],_0x52a097=_0x35162d[_0xf07220(0x1af)](_0x3882a3-0x4,_0x3882a3),_0x2d9d1b=[];for(let _0x570711=0x0;_0x570711<_0x52a097[_0xf07220(0x1ab)];_0x570711++){let _0x23e537=parseInt(_0x52a097[_0x570711]),_0x48b93d=_0x23e537%0x3+0x1;_0x2d9d1b[_0x570711]=_0xf25678[_0xf07220(0x1af)](_0x48b93d,_0x48b93d+0x8),_0xf25678=_0xf25678[_0xf07220(0x1af)](_0x48b93d+0x8,_0xf25678[_0xf07220(0x1ab)]);}v_tks=_0x2d9d1b[_0xf07220(0x1b2)]('');}get_tks();\n" +
                "</script>";
        String s1 = DigestUtils.md5Hex(s.getBytes());
        System.out.println(s1);
    }

    @Test
    public void decodeTest(){
        String s = "JTY4JTc0JTc0JTcwJTczJTNBJTJGJTJGJTc0JTMyJTMwJTZEJTJFJTZFJTYyJTZGJTZCJTc1JTJFJTYzJTZGJTZEJTJGJTMyJTMwJTMyJTM0JTMxJTMyJTMwJTM2JTJGJTRGJTQzJTZGJTU3JTM1JTUyJTU2JTU0JTJGJTY5JTZFJTY0JTY1JTc4JTJFJTZEJTMzJTc1JTM4";
        String s1 = Base64.decodeStr(s);
        System.out.println(s1);
    }
    @Test
    public void parseTest(){
        String s = "#EXTM3U\n" +
                "#EXT-X-STREAM-INF:PROGRAM-ID=1,BANDWIDTH=1893000,RESOLUTION=1920x800\n" +
                "/20241206/OCoW5RVT/hls/index.m3u8?sign=PVsk2rfMzhALh4dgnAfOyUG%2B9%2BfweU4WFP%2FADswYaH8%3D\n";
        String s1 = Base64.decodeStr(s);
        System.out.println(s1);
    }

    @Test
    public void htmlEncode(){
        String html = FileUtil.readString(new File("E:\\Archives\\compose-mutiplatform-workspace\\Test\\https __www.yjys.me_.htm"), "utf-8");
        Document document = Jsoup.parse(html);
        Elements pre = document.select("pre");
        String s = pre.toString();
//        String s = "这个世界需要超级英雄&hellip;&hellip; 黑亚当降临。<br>　　黑亚当（道恩&middot;强森\n" +
//                "                饰）被赋予了古代诸神无所不能的力量，但很快就被封印。近五千年后，他终于重见天日，并将在现代世界以独一无二的方式行使正义。";
//        System.out.println(HtmlUtil.cleanHtmlTag(s));
        System.out.println(HtmlUtil.unescape(HtmlUtil.unwrapHtmlTag(HtmlUtil.removeHtmlAttr(s, "span", "a"), "span", "a")));
    }



    @Test
    public void strEncode1(){
        String s = "&lt;<span class=\"start-tag\">p</span> <span class=\"attribute-name\">class</span>=\"<a class=\"attribute-value\">mb-0 text-muted</a>\"&gt;";
        String s1 = HtmlUtil.removeAllHtmlAttr(s, "span", "a");
        String s2 = HtmlUtil.unwrapHtmlTag(s1, "span", "a");
        System.out.println(HtmlUtil.unescape(HtmlUtil.removeHtmlTag(s2, "span")));
    }

    @Test
    public void StrDecode(){
        String s = "";
        System.out.println(Utils.base64Decode(s));
    }
}
