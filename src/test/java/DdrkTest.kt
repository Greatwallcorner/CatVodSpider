import com.github.catvod.spider.Ddrk
import common.AssertUtil
import common.TestInterface
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class DdrkTest:TestInterface<Ddrk> {
    override var t: Ddrk = Ddrk().apply { init(" {\"site\":\"https://ddys.info/\"}") }

    @Test
    override fun homeTest() {
        val homeContent = t.homeContent(false)
        println(homeContent)
        AssertUtil.assertResult(homeContent)
    }

    @Test
    override fun cateTest() {
        val categoryContent = t.categoryContent("https://ddys.pro/category/movie/", "1", false, hashMapOf())
        println(categoryContent)
        AssertUtil.assertResult(categoryContent)
    }

    @Test
    override fun detailTest() {
        val detailContent = t.detailContent(listOf("https://ddys.pro/medalist/"))
        println(detailContent)
        AssertUtil.assertResult(detailContent)
    }

    @Test
    override fun playTest() {
        val playerContent = t.playerContent(
            "",
            "http://127.0.0.1:9978/proxy?do=proxy&url=aHR0cHM6Ly92LmRkeXMucHJvL3YvQW5pbWUvTWVkYWxpc3QvTWVkYWxpc3RfUzAxRTAxLm1wNA==&header=eyJPcmlnaW4iOiJodHRwczovL2RkeXMucHJvIiwiQWNjZXB0IjoiKi8qIiwiQ29ubmVjdGlvbiI6ImtlZXAtYWxpdmUiLCJVc2VyLUFnZW50IjoiTW96aWxsYS81LjAgKFdpbmRvd3MgTlQgMTAuMDsgV2luNjQ7IHg2NCkgQXBwbGVXZWJLaXQvNTM3LjM2IChLSFRNTCwgbGlrZSBHZWNrbykgQ2hyb21lLzExNy4wLjAuMCBTYWZhcmkvNTM3LjM2IiwiUmVmZXJlciI6Imh0dHBzOi8vZGR5cy5wcm8ifQ==",
            mutableListOf()
        )
        println(playerContent)
        AssertUtil.assertResult(playerContent)
    }

    @Test
    override fun searchTest() {
        val searchContent = t.searchContent("阿凡达", false)
        println(searchContent)
        AssertUtil.assertResult(searchContent)
    }

    companion object {
        @JvmStatic
        @BeforeAll
        fun initI(): Unit {

        }
    }
}