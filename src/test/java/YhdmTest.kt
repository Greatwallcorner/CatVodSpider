import com.github.catvod.spider.YHDM
import common.AssertUtil
import common.TestInterface
import org.junit.jupiter.api.Test

class YhdmTest:TestInterface<YHDM> {
    override var t: YHDM = YHDM().apply { init() }

    @Test
    override fun homeTest() {
        val homeContent = t.homeContent(false)
        println(homeContent)
        AssertUtil.assertResult(homeContent)
    }

    override fun cateTest() {
        TODO("Not yet implemented")
    }

    @Test
    override fun detailTest() {
        val detailContent = t.detailContent(listOf("/video/9167.html"))
        println(detailContent)
        AssertUtil.assertResult(detailContent)
    }

    @Test
    override fun playTest() {
        val playerContent = t.playerContent("", "/video/9167.html", listOf())
        println(playerContent)
        AssertUtil.assertResult(playerContent)
    }

    override fun searchTest() {
        TODO("Not yet implemented")
    }
}