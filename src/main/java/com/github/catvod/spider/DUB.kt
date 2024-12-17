package com.github.catvod.spider

import cn.hutool.core.codec.Base64
import cn.hutool.core.util.URLUtil
import com.github.catvod.bean.Class
import com.github.catvod.bean.Result
import com.github.catvod.bean.Vod
import com.github.catvod.bean.Vod.VodPlayBuilder.PlayUrl
import com.github.catvod.crawler.Spider
import com.github.catvod.crawler.SpiderDebug
import com.github.catvod.net.OkHttp
import com.github.catvod.utils.Image
import com.github.catvod.utils.Json
import com.github.catvod.utils.Utils
import com.google.common.net.HttpHeaders
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import java.net.URLDecoder
import java.net.URLEncoder

class DUB: Spider() {
    private val host = Utils.base64Decode("aHR0cHM6Ly90di5nYm9rdS5jb20v")

    private val cateFormat = "/vodtype/%s.html" // tid-page
    private val cateFormat2 = "/vodtype/%s-%s.html" // tid-page
    private var referer = Utils.base64Decode("aHR0cHM6Ly93d3cuZHVib2t1LnR2Lw==")
    private val signUrl = Utils.base64Decode("c3RhdGljL3BsYXllci92aWRqczI1LnBocA==")
    private val searchUrl = Utils.base64Decode("L3ZvZHNlYXJjaC8tLS0tLS0tLS0tLS0tLmh0bWw/d2Q9JXMmc3VibWl0PQ==")

//    private val classList = Class.parseFromFormatStr("")
    override fun homeContent(filter: Boolean): String {
//        val result = OkHttp.string("https://tv.gboku.com/vodtype/1.html", Utils.webHeaders("duboku.tv"))
        val result = OkHttp.string("$host/vodtype/2.html", Utils.webHeaders("duboku.tv"))
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
        var url: String
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
                setVodPic(Image.UrlHeaderBuilder(fa.attr("data-original")).referer(referer).build())
                setVodName(fa.attr("title"))
            }
            vodList.add(vod)
        }
        return Result.string(vodList)
    }

    override fun detailContent(ids: MutableList<String>): String {
        val u = "$host${ids[0]}"
        val string = OkHttp.string(u, Utils.webHeaders(referer))
        referer = u
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
        val string =
            OkHttp.string("$host${searchUrl.format(URLEncoder.encode(key, "UTF-8"))}", Utils.webHeaders(referer))
        val document = Jsoup.parse(string)
        val select = document.select("ul#searchList > li")
        val vodList = mutableListOf<Vod>()
        for (element in select) {
            vodList.add(Vod().apply {
                val text = element.select("a.searchKey")
                setVodName(text.text())
                setVodId(text.attr("href"))
                val thumb = element.select(".thumb > a")
                setVodPic(Image.UrlHeaderBuilder(thumb.attr("data-original")).referer(referer).build())
                setVodRemarks(thumb.select(".tag").text())
            })
        }
        return Result.string(vodList)
    }

    override fun searchContent(key: String?, quick: Boolean, pg: String?): String {
        return super.searchContent(key, quick, pg)
    }

    override fun playerContent(flag: String?, id: String, vipFlags: MutableList<String>?): String {
        val string = OkHttp.string("$host$id")
        val regex = Regex("var\\s*player_[a-z]{0,4}\\s*=\\s*([^<]+)")
        val data = regex.find(string)
        var url = ""
        if((data?.groupValues?.size ?: 0) > 0){
            val parse = Json.parse(data!!.groupValues[1])
            val rst = parse.asJsonObject
            val encrypt = rst.get("encrypt").asInt
            url = if(encrypt == 2){
                URLDecoder.decode(Base64.decodeStr(rst.get("url").asString), "UTF-8")
            }else{
                URLDecoder.decode(rst.get("url").asString, "UTF-8")
            }
        }else{
            SpiderDebug.log("DUB 获取播放链接失败 $string")
        }

        val signDocument = OkHttp.string("$host$signUrl", Utils.webHeaders(referer))
        val signRegx = Regex("encodeURIComponent\\s*\\(\\s*[\"']([^\"']+)[\"']\\s*\\)")
        val find = signRegx.find(signDocument)
        if((find?.groupValues?.size ?: 0) > 0){
            val sign = find!!.groupValues[1]
            val m3u = OkHttp.string("$url?sign=${URLEncoder.encode(sign, "UTF-8")}", Utils.webHeaders(host).apply { put(HttpHeaders.HOST, URLUtil.url(url).host) })
            val m3uRegx = Regex("/\\d{8}/[A-Za-z0-9]+/hls/index\\.m3u8\\?sign=[A-Za-z0-9+=%]+")
            val matchResult = m3uRegx.find(m3u)
            if((matchResult?.groupValues?.size ?: 0) > 0){
                val toHttpUrl = URLUtil.url(url)
                return Result.get().url(toHttpUrl.protocol + "://" + toHttpUrl.host + "/" + matchResult!!.value).string()
            }else{
                SpiderDebug.log("DUB 解析m3u地址失败")
            }

            return Result.get().url("$url?sign=${URLEncoder.encode(sign, "UTF-8")}").string()
        }
        SpiderDebug.log("DUB 获取签名失败")
        return Result.error("获取播放链接失败")
    }

}