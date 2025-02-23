import cn.hutool.crypto.Mode
import cn.hutool.crypto.Padding
import cn.hutool.crypto.digest.DigestUtil
import cn.hutool.crypto.symmetric.AES
import com.github.catvod.bean.Class
import com.github.catvod.net.OkHttp
import com.github.catvod.spider.BD
import common.AssertUtil
import org.jsoup.Jsoup
import org.junit.jupiter.api.Test
import kotlin.io.encoding.ExperimentalEncodingApi

class DBTest {
    private val bd: BD = BD()

    init {
        bd.init("aHR0cHM6Ly92LnhseXMubHRkLnVhLw==")
    }

    @Test
    fun homeTest() {
        val homeContent = bd.homeContent(false)
        println(homeContent)
        AssertUtil.assertResult(homeContent)
    }

    @Test
    fun detailTest() {
        val detailContent = bd.detailContent(mutableListOf("/kehuan/24802.htm"))
        println(detailContent)
        AssertUtil.assertResult(detailContent)
    }

    @Test
    fun cateTest(){
        val categoryContent = bd.categoryContent("/s/dongzuo", "1", false, hashMapOf())
        println(categoryContent)
        AssertUtil.assertResult(categoryContent)
    }

    @Test
    fun playerTest() {
//        val playerContent = bd.playerContent("", "/play/24839-0.htm", mutableListOf())
//        val playerContent = bd.playerContent("", "/play/24802-0.htm", mutableListOf())
        val playerContent = bd.playerContent("", "/play/24867-0.htm", mutableListOf())
        println(playerContent)
        AssertUtil.assertResult(playerContent)
    }

    @OptIn(ExperimentalEncodingApi::class)
    @Test
    fun signTest() {
        val s = "178215-1715182172769"
        val digest = DigestUtil.md5Hex(s).substring(IntRange(0, 15)).toByteArray()
//        val digest = md5Hex.substring(0, 16).toByteArray()
//        val digest = MD5.create().digest(s, "utf-8").slice(IntRange(0, 16)).toByteArray()
        // 52E61163D2560C4FFE9F79BBA6C3B4A13EE429D21C8513D9368C4BDEB4B7B852
        val aes = AES(Mode.ECB, Padding.PKCS5Padding, digest)
//        println(base64ToHex(Base64.encode(aes.encrypt(s.toByteArray()))))
//        println(aes.encryptHex(digest).uppercase())
    }

    @Test
    fun classTest() {
        val string = OkHttp.string("")
        val select = Jsoup.parse(string)
        val nav = select.select("li.nav-item > a.nav-link")
        val li = mutableListOf<Class>()
        for (element in nav) {
            val id = element.attr("href")
            val text = element.select(".nav-link-title").text()
            val clazz = Class(id, text)
            li.add(clazz)
        }
        val item = select.select(".dropdown-menu .dropdown-item")
        for (element in item) {
            val id = element.attr("href")
            val name = element.text()
            val clazz = Class(id, name)
            li.add(clazz)
        }
        println(Class.listToFormatStr(li))
    }

    @Test
    fun code() {
        val s = "-"
        println(s[0].code)
    }
}