import com.github.catvod.spider.IkBot
import org.junit.jupiter.api.Test

class IkBotTest {
    private val ikbot = IkBot()

    init {
        ikbot.init()
    }

    @Test
    fun homeSearch(){
        val homeContent = ikbot.homeContent(false)
        println(homeContent)
        AssertUtil.assertResult(homeContent)
    }

    @Test
    fun cateTest(){
        val cateContent = ikbot.categoryContent("hot/index-movie-热门", "1", false, hashMapOf())
        println(cateContent)
        AssertUtil.assertResult(cateContent)
    }

    @Test
    fun detailTest(){
        val detailContent = ikbot.detailContent(listOf("/play/844070").toMutableList())
        println(detailContent)
        AssertUtil.assertResult(detailContent)
    }

    @Test
    fun searchTest(){
        val searchContent = ikbot.searchContent("泪之女王", false)
        println(searchContent)
        AssertUtil.assertResult(searchContent)
    }

    @Test
    fun tkTest(){
        val tk = ikbot.getTk("851741", "caec5afd02y100cbfc55xqe1a59569vj84587d34")
        println(tk)


    }

}