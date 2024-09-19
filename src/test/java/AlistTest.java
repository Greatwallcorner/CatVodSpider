import com.github.catvod.bean.Result;
import com.github.catvod.bean.alist.Drive;
import com.github.catvod.spider.AList;
import common.AssertUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;

public class AlistTest {
    private static AList aList;

    @BeforeAll
    public static void init() {
        aList = new AList();
        aList.init("https://gh-proxy.com/https://raw.githubusercontent.com/FongMi/CatVodSpider/main/json/alist.json");
    }

    @Test
    public void home() throws Exception {
        String s = aList.homeContent(false);
        System.out.println(s);
        Result result = Result.objectFrom(s);
        assert !result.getClasses().isEmpty();
    }

    @Test
    public void cate() throws Exception {
        String s = aList.categoryContent("小雅", "1", false, new HashMap<>());
        System.out.println(s);
        AssertUtil.INSTANCE.assertResult(s);
    }

    @Test
    public void detail() throws Exception {
        String s = aList.detailContent(Arrays.asList("小雅/元数据"));
        System.out.println(s);
        AssertUtil.INSTANCE.assertResult(s);
    }

    @Test
    public void parseTest() {
        String s = "{\n" +
                "  \"vodPic\": \"https://x.imgs.ovh/x/2023/09/05/64f680bb030b4.png\",\n" +
                "  \"drives\": [\n" +
                "    {\n" +
                "      \"name\": \"小雅\",\n" +
                "      \"server\": \"http://alist.xiaoya.pro\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"觸光\",\n" +
                "      \"server\": \"https://pan.ichuguang.com\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"一只魚\",\n" +
                "      \"server\": \"https://vtok.pp.ua/\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"七米藍\",\n" +
                "      \"server\": \"https://al.chirmyram.com\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"神族九帝\",\n" +
                "      \"server\": \"https://alist.shenzjd.com\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"梓澪\",\n" +
                "      \"server\": \"https://zi0.cc\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"範本\",\n" +
                "      \"server\": \"https://one.fongmi.com\",\n" +
                "      \"search\": true,\n" +
                "      \"hidden\": false,\n" +
                "      \"login\": {\n" +
                "        \"username\": \"fongmi\",\n" +
                "        \"password\": \"fongmi\"\n" +
                "      },\n" +
                "      \"params\": [\n" +
                "        {\n" +
                "          \"path\": \"/安齋拉拉\",\n" +
                "          \"pass\": \"18181818\"\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        Drive drive = Drive.objectFrom(s);
        System.out.println(drive);
    }
}