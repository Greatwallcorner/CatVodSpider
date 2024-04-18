import com.github.catvod.spider.PanSearch;
import org.junit.jupiter.api.Test;

public class PanSearchTest {
    private final PanSearch panSearch = new PanSearch();
    @Test
    public void searchTest() throws Exception {
        String s = panSearch.searchContent("阿凡达", false);
        System.out.println(s);
        AssertUtil.INSTANCE.assertResult(s);
    }
}
