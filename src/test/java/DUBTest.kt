import cn.hutool.core.lang.Assert
import com.github.catvod.bean.Result
import com.github.catvod.spider.DUB
import common.TestInterface
import org.junit.jupiter.api.Test

class DUBTest:TestInterface<DUB> {
    override var t: DUB
        get() = DUB()
        set(value) {}

    init{
        init()
    }

    @Test
    override fun homeTest() {
        val homeContent = t.homeContent(false)
        println(homeContent)
        val objectFrom = Result.objectFrom(homeContent)
        Assert.isTrue(objectFrom.classes.isNotEmpty())
    }

    @Test
    override fun cateTest() {
        val categoryContent = t.categoryContent("1", "1", false, hashMapOf())
        assert(categoryContent)
    }

    @Test
    override fun detailTest() {
        val detailContent = t.detailContent(mutableListOf("/voddetail/5067.html"))
        assert(detailContent)
    }

    @Test
    override fun playTest() {
        val playerContent = t.playerContent("", "/vodplay/5067-1-1.html", mutableListOf())
        assert(playerContent)
    }

    @Test
    override fun searchTest() {
        val searchContent = t.searchContent("庆余年", false)
        assert(searchContent)
    }
}