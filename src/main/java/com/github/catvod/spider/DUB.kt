import com.github.catvod.bean.Class
import com.github.catvod.bean.Result
import com.github.catvod.bean.Vod
import com.github.catvod.bean.Vod.VodPlayBuilder.PlayUrl
import com.github.catvod.crawler.Spider
import com.github.catvod.net.OkHttp
import com.github.catvod.utils.Utils
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import java.util.HashMap

class DUB: Spider() {
    private val host = Utils.base64Decode("aHR0cHM6Ly90di5nYm9rdS5jb20v")

    private val cateFormat = "/vodtype/%s.html" // tid-page
    private val cateFormat2 = "/vodtype/%s-%s.html" // tid-page
    private var referer = "duboku.tv"

//    private val classList = Class.parseFromFormatStr("")
    override fun homeContent(filter: Boolean): String {
        val result = OkHttp.string("https://tv.gboku.com/vodtype/1.html", Utils.webHeaders("duboku.tv"))
//        val result = OkHttp.string("$host/vodtype/2.html", Utils.webHeaders("duboku.tv"))
        val document = Jsoup.parse(result)
        val select = document.select("ul.nav-list > li")

        val classes = mutableListOf<Class>()
        for (element in select) {
            if("首页" == element.text()) continue
            classes.add(Class(element.select("a").attr("href").split("/").last().split(".").first(), element.text()))
        }
        return Result.string(classes, listOf())
    }

    override fun categoryContent(tid: String?, pg: String?, filter: Boolean, extend: HashMap<String, String>?): String {
        var url = ""
        if(pg == "1"){
            url = cateFormat.format(tid)
        }else{
            url = cateFormat2.format(tid, pg)
        }
        url = "$host$url"
        val string = OkHttp.string(url, Utils.webHeaders(referer))
        referer = url
        val document = Jsoup.parse(string)
        val boxList = document.select(".myui-vodlist__box")
        val vodList = mutableListOf<Vod>()
        for (element in boxList) {
            val vod = Vod()
            val fa = element.select("a").first()!!
            vod.apply {
                setVodRemarks(fa.text())
                setVodId(fa.attr("href"))
                setVodPic(fa.attr("data-original"))
                setVodName(fa.attr("title"))
            }
            vodList.add(vod)
        }
        return Result.string(vodList)
    }

    override fun detailContent(ids: MutableList<String>): String {
        val string = OkHttp.string("$host${ids[0]}", Utils.webHeaders(referer))
        val document = Jsoup.parse(string)
        val detail = document.select(".myui-content__detail")
        val vod = Vod()
        vod.apply {
            setVodName(detail.select(".title").text())
            val thumb = document.select("div.myui-content__thumb > a.myui-vodlist__thumb")
            setVodPic(host + thumb.select("img").attr("src"))
            val list = detail.select("div#rating + p.data")
            val text = list.text().split(" ")

            setVodActor(findPData(detail, "主演"))
            setVodDirector(findPData(detail, "导演"))
            setVodArea(findSome(text, "地区"))
            setVodYear(findSome(text, "年份"))

            val vodPlayBuilder = Vod.VodPlayBuilder()
            val vodList = document.select("ul.myui-content__list a")
            val urlList = mutableListOf<PlayUrl>()
            for (element in vodList) {
                urlList.add(PlayUrl().apply {
                    flag = "B"
                    url = element.attr("href")
                    name = element.text()
                })
            }
            val buildResult = vodPlayBuilder.append("B", urlList).build()
            setVodPlayFrom(buildResult.vodPlayFrom)
            vodPlayUrl = buildResult.vodPlayUrl
        }

        return Result.string(vod)
    }

    private fun findPData(detail: Elements, d:String) = detail.select("p.data:contains($d)").text().replace("$d：", "")

    private fun findSome(list: List<String>, d: String): String{
        for (element in list) {
            if(element.startsWith(d)){
                return element.replace("$d：", "")
            }
        }
        return ""
    }


    override fun searchContent(key: String?, quick: Boolean): String {
        return super.searchContent(key, quick)
    }

    override fun searchContent(key: String?, quick: Boolean, pg: String?): String {
        return super.searchContent(key, quick, pg)
    }

    override fun playerContent(flag: String?, id: String?, vipFlags: MutableList<String>?): String {
        return super.playerContent(flag, id, vipFlags)
    }

}