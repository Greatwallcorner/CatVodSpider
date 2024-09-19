import com.github.catvod.spider.Bili
import common.AssertUtil
import org.junit.jupiter.api.Test

class BiliTest {
    private val bili: Bili = Bili()

    init {
        bili.init("{\n" +
                "        \"json\": \"https://gh-proxy.com/https://raw.githubusercontent.com/gaotianliuyun/gao/master/json/bili.json\",\n" +
                "        \"type\": \"帕梅拉#太极拳#广场舞#演唱会\",\n" +
                "        \"cookie\": \"\"\n" +
                "      }")
    }
    @Test
    fun homeTest(){
        val homeContent = bili.homeContent(false)
        println(homeContent)
//        common.AssertUtil.assertResult(homeContent)
    }

    @Test
    fun homeContentTest(){
        val homeVideoContent = bili.homeVideoContent()
        println(homeVideoContent)
        AssertUtil.assertResult(homeVideoContent)
    }

    @Test
    fun searchTest(){
        val searchContent = bili.searchContent("青蛇", false)
        println(searchContent)
        AssertUtil.assertResult(searchContent)
    }

    @Test
    fun detailTest(){
        val detailContent = bili.detailContent(listOf("BV1aw4m117kR@1103835840"))
        println(detailContent)
        AssertUtil.assertResult(detailContent)
    }

    @Test
    fun playTest(){
        val playerContent = bili.playerContent("B站", "1103835840+1528096265+32:16+清晰 480P:流畅 360P", listOf())
        println(playerContent)
        AssertUtil.assertResult(playerContent)
    }

    @Test
    fun proxyTest(){
        val proxy = Bili.proxy(mutableMapOf("aid" to "884193221", "cid" to "221860775", "qn" to "16", "type" to "mpd"))
        println(proxy)
    }
}