import com.github.catvod.utils.Utils;
import org.junit.jupiter.api.Test;

import java.util.Base64;

public class Util {
    @Test
    public void strEncode(){
        String s = "https://yingso.fun:3001/";
        System.out.println(Utils.base64Encode(s));
    }

    @Test
    public void StrDecode(){
        String s = "";
        System.out.println(Utils.base64Decode(s));
    }
}
