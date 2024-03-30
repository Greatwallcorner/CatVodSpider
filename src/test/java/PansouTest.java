import com.github.catvod.spider.PanSou;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * @author heatdesert
 * @date 2024-01-20 21:54
 * @description
 */
public class PansouTest {

    private static PanSou panSou;

    @BeforeAll
    public static void init() throws Exception {
        panSou = new PanSou();
        panSou.init();
    }

    @Test
    public void searchTest() throws Exception {
        String s = panSou.searchContent("花青歌", false);
        System.out.println(s);
        AssertUtil.INSTANCE.assertResult(s);
    }

    @Test
    public void detailTest() throws Exception {
        String s = panSou.detailContent(Arrays.asList("/s/pev3ilDFYKMy8rqzEwV1fgnwgtxSm"));
        System.out.println(s);
    }
}
