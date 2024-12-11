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
        val detailContent = t.detailContent(mutableListOf("/voddetail/5075.html"))
        assert(detailContent)
    }

    @Test
    override fun playTest() {
        val playerContent = t.playerContent("", "/vodplay/5075-1-1.html", mutableListOf())
        assert(playerContent)
    }

    @Test
    override fun searchTest() {
        val searchContent = t.searchContent("我和我爸的18岁", false)
        assert(searchContent)
    }

    @Test
    fun test(){
        val s = """
            #EXTM3U
            #EXT-X-STREAM-INF:PROGRAM-ID=1,BANDWIDTH=1893000,RESOLUTION=1920x800
            /20241206/OCoW5RVT/hls/index.m3u8?sign=PVsk2rfMzhALh4dgnAfOyUG%2B9%2BfweU4WFP%2FADswYaH8%3D
            
            """.trimIndent()
        val m3uRegx= Regex("/\\d{8}/[A-Za-z0-9]+/hls/index\\.m3u8\\?sign=[A-Za-z0-9+=]+")
        val matchResult = m3uRegx.find(s)
        println(matchResult?.value)
    }
}