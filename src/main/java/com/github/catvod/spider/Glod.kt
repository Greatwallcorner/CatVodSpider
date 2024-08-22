package com.github.catvod.spider

import cn.hutool.core.date.DateUtil
import cn.hutool.crypto.digest.DigestUtil
import com.github.catvod.bean.Class
import com.github.catvod.bean.Result
import com.github.catvod.bean.Vod
import com.github.catvod.bean.Vod.VodPlayBuilder
import com.github.catvod.bean.Vod.VodPlayBuilder.PlayUrl
import com.github.catvod.crawler.Spider
import com.github.catvod.crawler.SpiderDebug
import com.github.catvod.net.OkHttp
import com.github.catvod.utils.Json
import com.github.catvod.utils.Utils
import org.jsoup.Jsoup
import java.util.*

class Glod:Spider() {
    private val host = "https://www.cfkj86.com/"

    private val epUrl = "/api/mw-movie/anonymous/v1/video/episode/url?id=%s&nid=%s"

    private val deviceId = cn.hutool.core.lang.UUID.randomUUID().toString()

    private val classList = Class.parseFromFormatStr("电影=1&电视剧2&综艺=3&动漫=4")

    override fun homeContent(filter: Boolean): String {
        val string = OkHttp.string(host, Utils.webHeaders("https://www.bing.com"))
        val parse = Jsoup.parse(string)
        val list = parse.select("div.content-card")
        val vodList = mutableListOf<Vod>()
        for (element in list) {
            val id = element.select("a").attr("href")
            val title = element.select("div.info-title-box > div.title").text()
            val score = element.select("div.bottom div[class^=score]").text()
            val img = element.select("img").attr("src")
            vodList.add(Vod(id, title, host + img, score))
        }
        return Result.string(classList, vodList)
    }

    override fun detailContent(ids: MutableList<String>): String {
        val url = host + ids[0]
        val string = OkHttp.string(url, Utils.webHeaders(host))
        val parse = Jsoup.parse(string)
        val name = parse.select("h1.title").text()
        val img = parse.select("div[class^=detail__CardImg] img").attr("src")
        val tag = parse.select("div.tags > a.tag").eachText().joinToString(" ")
        val vod = Vod(ids[0], name, img, tag)
        val director = parse.select("div.director")
        val d = director[0].select("a").text()
        val actor = director[1].select("a").eachText().joinToString(" ")
        vod.setVodActor(actor)
        vod.setVodDirector(d)
        val desc = parse.select("div.intro div.wrapper_more_text").text()
        vod.vodContent = desc

        val link = parse.select("div.listitem > a")
        val u = link.attr("href")
        val n = link.text()

        val buildResult = VodPlayBuilder().append("glod", listOf(PlayUrl().also {
            it.url = u
            it.name = n
        })).build()

        val time = parse.select("div.item:contains(上映时间)").select(".item-top").text()
        vod.setVodYear(DateUtil.parse(time, "yyyy-MM-dd").toString("yyyy"))
        vod.setVodPlayFrom(buildResult.vodPlayFrom)
        vod.vodPlayUrl = buildResult.vodPlayUrl
        return Result.string(vod)
    }

    /**
     * 请求头
     * t 时间戳
     * sign 签名
     * deviceId
     * authorization 空的
     * 还有cookie
     */
    override fun playerContent(flag: String, id: String, vipFlags: MutableList<String>): String {
        val list = id.split("/")
        val i = list[3]
        val nid = list[5]
        val webHeaders = Utils.webHeaders(host)
        val time = Date().time.toString()
        val sign = DigestUtil.sha1Hex(
            DigestUtil.md5Hex("id=${i}&nid=${nid}&key=cb808529bae6b6be45ecfab29a4889bc&t=${time}")
        )
        webHeaders["t"] = time
        webHeaders["deviceId"] = deviceId
        webHeaders["Sign"] = sign

        val string = OkHttp.string(host + String.format(epUrl, i , nid), webHeaders)
        val parse = Json.parse(string).asJsonObject
        if(parse.get("code").asInt != 200){
            SpiderDebug.log("glod 获取播放链接失败:$string")
            return Result.error("获取播放链接失败")
        }
        val url = parse.get("data").asJsonObject.get("playUrl").asString
        return Result.get().url(url).string()
    }

    override fun categoryContent(tid: String, pg: String, filter: Boolean, extend: HashMap<String, String>): String {
        val url = "$host/type/$tid"
        val string = OkHttp.string(url, Utils.webHeaders(host))
        val parse = Jsoup.parse(string)
        val select = parse.select("script")
        val data = select.find {
            it.html().contains("操作成功")
        }
        val json = data?.html()?.replace("self.__next_f.push(", "")?.replace(")", "")

        val gson = Json.parse(json).asJsonArray.get(1).asString.replace("6:", "")
        val element = Json.parse(gson).asJsonArray.get(3)
        return ""

    }
}