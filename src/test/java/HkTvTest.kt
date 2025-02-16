import com.github.catvod.spider.HkTv
import common.AssertUtil
import common.TestInterface
import org.junit.jupiter.api.Test

class HkTvTest: TestInterface<HkTv> {
    override var t: HkTv = HkTv().apply { init() }

    @Test
    override fun homeTest() {
        val homeContent = t.homeContent(false)
        println(homeContent)
        AssertUtil.assertResult(homeContent)
    }


    @Test
    override fun cateTest() {
        val categoryContent = t.categoryContent("1", "1", true, hashMapOf())
        println(categoryContent)
        AssertUtil.assertResult(categoryContent)
    }

    @Test
    override fun detailTest() {
        val detailContent = t.detailContent(mutableListOf("191567.html"))
        println(detailContent)
        AssertUtil.assertResult(detailContent)
    }

    @Test
    override fun playTest() {
        val playerContent = t.playerContent("", "191567/sid/1/nid/4.html", listOf())
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