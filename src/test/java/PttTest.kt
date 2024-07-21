import com.github.catvod.spider.PTT
import common.TestInterface
import org.junit.jupiter.api.Test

class PttTest : TestInterface<PTT> {

    override var t: PTT = PTT()
    override fun init() {
        t.init()
    }


    @Test
    override fun homeTest() {
    }

    override fun searchTest() {

    }

    @Test
    override fun cateTest() {
        val categoryContent = t.categoryContent("1", "1", false, hashMapOf())
        assert(categoryContent)
    }

    @Test
    override fun detailTest() {
        val detailContent = t.detailContent(listOf("143878"))
        assert(detailContent)
    }

    @Test
    override fun playTest() {
        val playerContent = t.playerContent("", "143878/1/45", listOf())
        assert(playerContent)
    }
}