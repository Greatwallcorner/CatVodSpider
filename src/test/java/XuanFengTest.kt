import com.github.catvod.spider.XuanFeng
import common.TestInterface
import org.junit.jupiter.api.Test

class XuanFengTest:TestInterface<XuanFeng> {
    override var t: XuanFeng = XuanFeng()

    @Test
    override fun homeTest() {
        val homeContent = t.homeContent(false)
        assert(homeContent)
    }

    @Test
    override fun cateTest() {
        val categoryContent = t.categoryContent("/tag/%e5%8a%a8%e4%bd%9c%e7%89%87/1", "1", false, hashMapOf())
        assert(categoryContent)
    }

    @Test
    override fun detailTest() {
        val detailContent = t.detailContent(listOf("/video/A2KcAnaiCABmA9eeTYL2Vj"))
        assert(detailContent)
    }

    @Test
    override fun playTest() {
        val playerContent = t.playerContent("", "/video/A2KcAnaiCABmA9eeTYL2Vj", listOf())
        assert(playerContent)
    }

    @Test
    override fun searchTest() {
        val searchContent = t.searchContent("阿凡达", false)
        assert(searchContent)
    }
}