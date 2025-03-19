import com.github.catvod.spider.Aiga
import common.TestInterface
import org.junit.jupiter.api.Test

class AigaTest:TestInterface<Aiga> {
    override var t: Aiga = Aiga()

    @Test
    override fun homeTest() {
        val homeContent = t.homeContent(false)
        assert(homeContent)
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