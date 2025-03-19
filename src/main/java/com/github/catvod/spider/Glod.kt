package com.github.catvod.spider

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
import com.github.catvod.utils.Util
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import org.jsoup.Jsoup
import java.util.*

class Glod:Spider() {
    private val host = Util.base64Decode("aHR0cHM6Ly93d3cuY2Zrajg2LmNvbS8=")

    private val detailUrl = host + "api/mw-movie/anonymous/video/detail?id=%s"

    private val epUrl = "/api/mw-movie/anonymous/v1/video/episode/url?clientType=1&id=%s&nid=%s"

    private val deviceId = cn.hutool.core.lang.UUID.randomUUID().toString()

    private val classList = Class.parseFromFormatStr("电影=1&电视剧=2&综艺=3&动漫=4")

    private var cookie = ""

    private val key = "cb808529bae6b6be45ecfab29a4889bc"

    override fun init() {
        cookie = generateWafCookie()
    }

    override fun homeContent(filter: Boolean): String {
        val string = OkHttp.string(host, Util.webHeaders("https://www.bing.com"))
        val vodList = parseFromJson(string, "home")
//        val vodList = parseVodList(string)
        return Result.string(classList, vodList)
    }

    fun generateWafCookie(): String {
        val uuidPart1 = UUID.randomUUID().toString()
        val uuidPart2 = UUID.randomUUID().toString().replace("-", "").substring(0, 24)
        return "$uuidPart1$uuidPart2"
    }

    override fun detailContent(ids: MutableList<String>): String {
        val times = Date().time.toString()
        val string = OkHttp.string(detailUrl.format(ids[0]), genHeaders("id=${ids[0]}&key=${key}&t=${times}", times))
        val detail = Json.parse(string)
        val data = detail.asJsonObject.get("data").asJsonObject
        val vod = Vod(ids[0], data.getString("vodName"), data.getString("vodPic"), data.getString("vodClass"))
        vod.setVodActor(data.getString("vodActor"))
        vod.setVodDirector(data.getString("vodDirector"))
        vod.vodContent = data.getString("vodContent")

        val linkList = data.get("episodeList").asJsonArray

        val playUrlList = mutableListOf<PlayUrl>()
        for (element in linkList) {
            val u = element.asJsonObject.getString("nid")
            val n = element.asJsonObject.getString("name")
            playUrlList.add(PlayUrl().also {
                it.name = n
                it.url = ids[0] + "/" +u
            })
        }

        val buildResult = VodPlayBuilder().append("glod", playUrlList).build()

        vod.setVodYear(data.getString("vodYear"))
        vod.setVodPlayFrom(buildResult.vodPlayFrom)
        vod.vodPlayUrl = buildResult.vodPlayUrl
        return Result.string(vod)
    }

    fun JsonObject.getString(name:String):String{
        return this.get(name).asString
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
        val i = list[0]
        val nid = list[1]
        val time = Date().time.toString()
        val webHeaders = genHeaders("clientType=1&id=${i}&nid=${nid}&key=${key}&t=${time}", time)
        val string = OkHttp.string(host + String.format(epUrl, i , nid), webHeaders)
        val parse = Json.parse(string).asJsonObject
        if(parse.get("code").asInt != 200){
            SpiderDebug.log("glod 获取播放链接失败:$string")
            return Result.error("获取播放链接失败")
        }
        val url = parse.get("data").asJsonObject.get("playUrl").asString
        val content = OkHttp.string(url, webHeaders)
        return Result.get().url(url/*ProxyVideo.buildCommonProxyUrl(url, webHeaders)*/).string()
    }

    private fun genHeaders(signKey:String, time:String = Date().time.toString()): HashMap<String, String>? {
        val webHeaders = Util.webHeaders(host)
        val sign = genSign(signKey)
        webHeaders["t"] = time
        webHeaders["deviceId"] = deviceId
        webHeaders["Sign"] = sign
        return webHeaders
    }

    private fun genSign(key: String): String {
        return DigestUtil.sha1Hex(
            DigestUtil.md5Hex(key)
        )
    }

    override fun categoryContent(tid: String, pg: String, filter: Boolean, extend: HashMap<String, String>): String {
        val url = "$host/type/$tid"
        val string = OkHttp.string(url, Util.webHeaders(host))
        val vodList = parseFromJson(string, "cate")
        return Result.string(classList, vodList)
    }

    override fun searchContent(key: String, quick: Boolean): String {
        val string = OkHttp.string("${host}vod/search/$key", Util.webHeaders(host))
        val vodList = parseFromJson(string, "search")
        return Result.string(vodList)
    }

    private fun parseFromJson(string: String, type: String): List<Vod> {
        val vodList = mutableListOf<Vod>()
        val parse = Jsoup.parse(string)
        val select = parse.select("script")
        val data = select.find {
            it.html().contains("操作成功")
        }
        if(data == null) {
            SpiderDebug.log("glod 找不到json")
            return vodList
        }
        val json = data.html().replace("self.__next_f.push(", "").replace(")", "")

        val gson = Json.parse(json).asJsonArray.get(1).asString.replace("6:", "")
        val resp =
            Json.parse(gson).asJsonArray.get(3).asJsonObject
        if(type == "home"){
            val element = resp.get("children").asJsonArray.get(3).asJsonObject.get("data").asJsonObject.get("data")
            var vList = element.asJsonObject.get("homeNewMoviePageData").asJsonObject.get("list").asJsonArray
            getVodList(vList, vodList)
            vList = element.asJsonObject.get("homeBroadcastPageData").asJsonObject.get("list").asJsonArray
            getVodList(vList, vodList)
            vList = element.asJsonObject.get("homeManagerPageData").asJsonObject.get("list").asJsonArray
            getVodList(vList, vodList)
            vList = element.asJsonObject.get("newestTvPageData").asJsonObject.get("list").asJsonArray
            getVodList(vList, vodList)
            vList = element.asJsonObject.get("newestCartoonPageData").asJsonObject.get("list").asJsonArray
            getVodList(vList, vodList)
        }else if(type == "cate"){
            for (jsonElement in resp.get("children").asJsonArray.get(3).asJsonObject.get("data").asJsonArray) {
                val objList = jsonElement.asJsonObject.get("vodList").asJsonObject.get("list").asJsonArray
                getVodList(objList, vodList)
            }
        }else if(type == "search"){
            val asJsonArray = resp.get("data").asJsonObject.get("data").asJsonObject.get("result").asJsonObject.get("list").asJsonArray
            getVodList(asJsonArray, vodList)
        }
        return vodList
    }

    private fun getVodList(
        objList: JsonArray,
        vodList: MutableList<Vod>
    ) {
        for (oj in objList) {
            val obj = oj.asJsonObject
            val v = Vod()
            v.setVodId(obj.get("vodId").asString)
            v.setVodName(obj.get("vodName").asString)
            //            v.setVodActor(obj.get("vodActor").asString)
            v.setVodRemarks(obj.get("vodScore").asString)
            v.setVodPic(obj.get("vodPic").asString)
            vodList.add(v)
        }
    }

    private fun parseVodList(string: String): MutableList<Vod> {
        val parse = Jsoup.parse(string)
        val list = parse.select("div.content-card")
        val vodList = mutableListOf<Vod>()
        for (element in list) {
            val id = element.select("a").attr("href")
            val title = element.select("div.info-title-box > div.title").text()
            val score = element.select("div.bottom div[class^=score]").text()
            val img = element.select("img").attr("srcset")
            vodList.add(Vod(id, title, host + img, score))
        }
        return vodList
    }
}