import com.github.catvod.spider.NCat;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import common.AssertUtil;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class NCatTest {
    private static NCat nCat = new NCat();

    static {
        try {
            nCat.init();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void homeTest() throws Exception {
        String s = nCat.homeContent(false);
        System.out.println(s);
        AssertUtil.INSTANCE.assertResult(s);
    }

    @Test
    public void cateTest() throws Exception {
        String s = nCat.categoryContent("1", "1", false, Maps.newHashMap());
        System.out.println(s);
        AssertUtil.INSTANCE.assertResult(s);
    }

    @Test
    public void detailTest() throws Exception {
        String s = nCat.detailContent(Arrays.asList("240633.html"));
        System.out.println(s);
        AssertUtil.INSTANCE.assertResult(s);
    }

    @Test
    public void searchTest() throws Exception {
        String s = nCat.searchContent("阿凡达", false);
        System.out.println(s);
        AssertUtil.INSTANCE.assertResult(s);
    }

    @Test
    public void playTest() throws Exception {
        String s = nCat.playerContent("", "257902-4-1191383.html", Lists.newArrayList());
        System.out.println(s);
        AssertUtil.INSTANCE.assertResult(s);
    }
}
