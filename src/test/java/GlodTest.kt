import com.github.catvod.spider.Glod
import common.AssertUtil
import common.TestInterface
import org.junit.jupiter.api.Test

class GlodTest() :TestInterface<Glod> {
    override var t: Glod = Glod()
    @Test
    override fun homeTest() {
        val homeContent = t.homeContent(false)
        println(homeContent)
        AssertUtil.assertResult(homeContent)
    }

    @Test
    override fun cateTest() {
        val categoryContent = t.categoryContent("1", "1", false, hashMapOf())
        println(categoryContent)
        AssertUtil.assertResult(categoryContent)
    }

    @Test
    override fun detailTest() {
        val detailContent = t.detailContent(mutableListOf("133698"))
        println(detailContent)
        AssertUtil.assertResult(detailContent)
    }

    @Test
    override fun playTest() {
        val playerContent = t.playerContent("", "135328/1176989", mutableListOf())
        println(playerContent)
        AssertUtil.assertResult(playerContent)
    }

    @Test
    override fun searchTest() {
        val searchContent = t.searchContent("阿凡达", false)
        println(searchContent)
        AssertUtil.assertResult(searchContent)
    }
}