import com.github.catvod.api.AliYun
import org.junit.jupiter.api.Test

class AliTest {
    @Test
    fun proxyVideo(){
        // share: http://127.0.0.1:9978/proxy?do=ali&type=video&cate=preview&shareId=AcUnFNQ6eHa&fileId=663f374b1431770341834ebda9b06ad84b218387&templateId=HD
//        val string =
//            OkHttp.string("http://127.0.0.1:9978/proxy?do=ali&type=video&cate=preview&shareId=AcUnFNQ6eHa&fileId=663f374b1431770341834ebda9b06ad84b218387&templateId=HD")
//        println(string)
        val aliYun = AliYun()

        //"http://127.0.0.1:9978/proxy?do=ali&type=video&cate=open&shareId=5CNb1zzo7z9&fileId=6572dcc537aa2f73e51e499d9a14280cdcda9eaa"
        val maps = HashMap<String, String>()
        maps["cate"] = "preview"
        maps["shareId"] = "AcUnFNQ6eHa"
        maps["fileId"] = "663f374b1431770341834ebda9b06ad84b218387"
        maps["templateId"] = "HD"
        val objects = aliYun.proxyVideo(maps)


        //        String s = aliYun.getShareDownloadUrl("yJcwweiN61T", "656edff2c63533be753d4d90b5aac9f14495c882");
//        System.out.println(s);
//        Runtime.getRuntime().exec("\"E:\\Program File\\Tools\\Scoop\\apps\\potplayer\\current\\PotPlayerMini64.exe\"", new String[]{s});
        try {
            Thread.sleep(50000)
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        }
    }

    @Test
    fun m3uTest(){
        val s = "http://127.0.0.1:9978/proxy?do=ali&type=video&cate=m3u8&shareId=AcUnFNQ6eHa&fileId=663f374b1684a0b156604b859f2045ffec346fa2&templateId=LD&mediaId=280"
        val aliYun = AliYun()

        val maps = HashMap<String, String>()
        maps["cate"] = "m3u8"
        maps["shareId"] = "AcUnFNQ6eHa"
        maps["fileId"] = "663f374b1684a0b156604b859f2045ffec346fa2"
        maps["templateId"] = "LD"

        val proxyVideo = aliYun.proxyVideo(maps)

        try {
            Thread.sleep(50000)
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        }

    }
}