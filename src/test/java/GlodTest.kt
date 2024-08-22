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
        val detailContent = t.detailContent(mutableListOf("/detail/125013"))
        println(detailContent)
        AssertUtil.assertResult(detailContent)
    }

    @Test
    override fun playTest() {
        val playerContent = t.playerContent("", "/vod/play/125013/1/1048499", mutableListOf())
        println(playerContent)
        AssertUtil.assertResult(playerContent)
    }

    override fun searchTest() {
        TODO("Not yet implemented")
    }
}