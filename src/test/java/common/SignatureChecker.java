package common;

import com.github.catvod.net.OkHttp;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

public class SignatureChecker {
    public static void printMethodSignatures(Class<?> clazz) {
        for (Method method : clazz.getDeclaredMethods()) {
            System.out.println(method.toString());
        }
    }

    @Test
    public void test() {
        printMethodSignatures(OkHttp.class);
    }
}
