package unavaliable

import com.github.catvod.spider.unavailable.NG
import common.AssertUtil
import common.TestInterface
import org.junit.jupiter.api.Test

class NgTest:TestInterface<NG> {
    override var t: NG
        get() = NG()
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
        val detailContent = t.detailContent(mutableListOf("253915"))
        println(detailContent)
    }

    @Test
    override fun playTest() {
        val playerContent = t.playerContent("", "http://43.248.129.14:20240/?url=https://svipsvip.ffzyread1.com/20240627/28803_0fc1e40f/index.m3u8", mutableListOf())
//        val playerContent = t.playerContent("", "https://json.oorl.cn/api/svip.php?key=aYqmunbBybpvTu6hnZ&url=https://v.youku.com/v_show/id_XNjIyMjUxMzM2.html", mutableListOf())
        println(playerContent)
    }

    @Test
    override fun searchTest() {
        val searchContent = t.searchContent("阿凡达", false)
        println(searchContent)
        AssertUtil.assertResult(searchContent)
    }
}