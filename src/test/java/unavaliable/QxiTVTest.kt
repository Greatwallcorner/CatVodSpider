package unavaliable

import com.github.catvod.spider.unavailable.QxiTv
import common.AssertUtil
import common.TestInterface
import org.junit.jupiter.api.Test

class QxiTVTest : TestInterface<QxiTv> {
    override var t: QxiTv
        get() = QxiTv().apply { init() }
        set(value) {}

    @Test
    override fun homeTest() {
        val homeContent = t.homeContent(false)
        println(homeContent)
        AssertUtil.assertResult(homeContent)
    }

    @Test
    override fun cateTest() {
        val categoryContent = t.categoryContent("2", "1", false, hashMapOf())
        println(categoryContent)
        AssertUtil.assertResult(categoryContent)
    }

    @Test
    override fun detailTest() {
        val detailContent = t.detailContent(listOf("/voddetail/867734/"))
        println(detailContent)
        AssertUtil.assertResult(detailContent)
    }

    @Test
    override fun playTest() {
        val playerContent = t.playerContent("", "/vodplay/867734-1-1/", listOf())
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