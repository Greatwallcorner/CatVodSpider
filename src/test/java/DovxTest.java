import com.github.catvod.spider.unavailable.Dovx;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class DovxTest {
    @Test
    public void test() throws Exception {
        Dovx dovx = new Dovx();
        dovx.init();
        String s = dovx.searchContent("恋爱冲绳", false);
        System.out.println(s);
        AssertUtil.INSTANCE.assertResult(s);
    }

    public void detailTest() throws Exception {
        Dovx dovx = new Dovx();
        dovx.init();
        // 阿里 分享被取消 {"code":"ShareLink.Cancelled","message":"share_link is cancelled by the creator","requestId":"0a00800117066076490395587eaea9"}
        String s = dovx.detailContent(Arrays.asList("https://www.aliyundrive.com/s/8ZB9jovGN1x"));
        // {"expire_time":"2024-01-30T11:46:55.056Z","expires_in":7200,"share_token":"eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJjdXN0b21Kc29uIjoie1wiZG9tYWluX2lkXCI6XCJiajI5XCIsXCJzaGFyZV9pZFwiOlwiMlVpdHJXakRSQmdcIixcImNyZWF0b3JcIjpcIjY4M2E3ODg0YWJjZDQzNmI5OThhMjQyYTllMjA4NWNhXCIsXCJ1c2VyX2lkXCI6XCJhbm9ueW1vdXNcIn0iLCJjdXN0b21UeXBlIjoic2hhcmVfbGluayIsImV4cCI6MTcwNjYxNTIxNSwiaWF0IjoxNzA2NjA3OTU1fQ.Mo1N0OST4mmT9LuPf7SbNVElV75X0zA3ix4N6pGrb1RZIDlJlAs1gB70DF_gFQp7XzlcBNmGpe4Wy-VuqX3s6ADUwdJs6vKBSip7O1lMSbiqw8vrLuSrSUsIBYu1nT8_VyeKMY0BKb8yea8oEPY87i4BfgfqdQjRJf_DKL-2W_M"}
//        String s = dovx.detailContent(Arrays.asList("https://www.aliyundrive.com/s/2UitrWjDRBg"));
        System.out.println(s);
    }
}
