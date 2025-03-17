import cn.hutool.json.JSONUtil
import com.github.catvod.spider.Dm84
import com.google.common.collect.Lists
import com.google.common.collect.Maps
import common.AssertUtil
import common.TestInterface
import org.junit.jupiter.api.Test
import kotlin.io.encoding.Base64


class Dm84Test : TestInterface<Dm84> {
    override var t: Dm84 = Dm84()

    init {
        t.init()
    }

    @Test
    override fun homeTest() {
        val homeContent = t.homeContent(false)
        println(homeContent)
        AssertUtil.assertResult(homeContent)
    }

    @Test
    override fun cateTest() {
        val categoryContent = t.categoryContent("3", "1", false, Maps.newHashMap())
        println(categoryContent)
        AssertUtil.assertResult(categoryContent)
    }

    @Test
    override fun detailTest() {
        val detailContent = t.detailContent(Lists.newArrayList("5367.html"))
        println(detailContent)
        AssertUtil.assertResult(detailContent)
    }

    @Test
    override fun playTest() {
        val playerContent = t.playerContent("", "/p/5367-1-1.html", Lists.newArrayList())
        println(playerContent)
        AssertUtil.assertResult(playerContent)
    }

//    @Test
//    override fun playerContentTest() {
//
//    }

    @Test
    override fun searchTest() {
        val searchContent = t.searchContent("火影", false)
        println(searchContent)
        AssertUtil.assertResult(searchContent)
    }

    @Test
    fun decodeTest(){
        val s = "M08wZE8wTzMxME9vMG8wTzA1TzBmTzBPNTAyT29DY08yNU8wZk8wTzUyOTBPbzBvME8wNjY4OTVPMGZPME81MTAwT28wbzBPMDYyT29DY08yMDI3M08wZE8wTzM3M08wZE8wTzMxTzBiTzAwMTg5MTEz";
        val decodeStr = cn.hutool.core.codec.Base64.decodeStr(s)
        val decodeString = t.decodeString(decodeStr)
        assert(decodeString.equals("d1af0cf29a6689f10a6c027d7db89113"))
    }
}