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

    override fun detailTest() {
        TODO("Not yet implemented")
    }

    override fun playTest() {
        TODO("Not yet implemented")
    }

    override fun searchTest() {
        TODO("Not yet implemented")
    }
}