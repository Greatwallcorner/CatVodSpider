import com.github.catvod.spider.Xb6v
import common.TestInterface
import org.junit.jupiter.api.Test

class Xb6vTest: TestInterface<Xb6v> {
    override var t: Xb6v
        get() = Xb6v()
        set(value) {}

    @Test
    override fun homeTest() {
        val homeContent = t.homeContent(false)
        assert(homeContent)
    }

    @Test
    override fun cateTest() {
        val categoryContent = t.categoryContent("/xijupian/", "1", false, hashMapOf())
        assert(categoryContent)
    }

    @Test
    override fun detailTest() {
        val detailContent = t.detailContent(listOf("/xijupian/25112.html"))
        assert(detailContent)
    }

    override fun playTest() {
    }

    @Test
    override fun searchTest() {
        val searchContent = t.searchContent("阿凡达", false)
        assert(searchContent)
    }
}