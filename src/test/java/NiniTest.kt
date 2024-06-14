import com.github.catvod.spider.NiNi
import org.junit.jupiter.api.Test
import java.security.Security

class NiniTest {
    private val nini:NiNi = NiNi()

    init {
        nini.init()
    }

    @Test
    fun homeTest(){
        val homeContent = nini.homeContent(false)
        println(homeContent)
        AssertUtil.assertResult(homeContent)
    }
    @Test
    fun homeVodTest(){
        val homeContent = nini.homeVideoContent()
        println(homeContent)
        AssertUtil.assertResult(homeContent)
    }

    @Test
    fun cateTest(){
//        nini.categoryContent("")
    }

    @Test
    fun detailTest(){
        val detailContent = nini.detailContent(listOf("WYK1NxUrYHNxbdL7SlusOSDVCFUkD0du"))
        println(detailContent)
        AssertUtil.assertResult(detailContent)
    }

    @Test
    fun playTest(){
        val playerContent = nini.playerContent("", "WYK1NxUrYHNxbdL7SlusOSDVCFUkD0du_rsAr9R5W9huUOppHxNo9E9diwBkrZRrc", listOf())
        println(playerContent)
        AssertUtil.assertResult(playerContent)
    }

    @Test
    fun searchTest(){
        val searchContent = nini.searchContent("阿凡达", false)
        println(searchContent)
        AssertUtil.assertResult(searchContent)
    }

}