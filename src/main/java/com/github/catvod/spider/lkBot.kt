package com.github.catvod.spider

import cn.hutool.core.net.URLEncodeUtil
import cn.hutool.core.util.URLUtil
import com.github.catvod.bean.Class
import com.github.catvod.bean.Filter
import com.github.catvod.bean.Result
import com.github.catvod.bean.Vod
import com.github.catvod.bean.Vod.VodPlayBuilder.PlayUrl
import com.github.catvod.crawler.Spider
import com.github.catvod.crawler.SpiderDebug
import com.github.catvod.net.OkHttp
import com.github.catvod.utils.Image
import com.github.catvod.utils.Image.UrlHeaderBuilder
import com.github.catvod.utils.Json
import com.github.catvod.utils.Json.parse
import com.github.catvod.utils.Util
import org.jsoup.Jsoup
import org.jsoup.internal.StringUtil

class IkBot: Spider() {
    private val host = Util.base64Decode("aHR0cHM6Ly92LmlrYW5ib3QuY29tLw==")

    private val detail = Util.base64Decode("YXBpL2dldFJlc04/dmlkZW9JZD0lcyZtdHlwZT0lcyZ0b2tlbj0lcw==")

//    private val tkJsMd = "fc0c1b33cb7815632b5448782aac3d6a"

    private val referer:String = host

    private val home = ""

    private val cateMap = Filter.fromJson(
        Util.base64Decode(
        "ewogICAgImthbmxpc3Qv5YWo6YOoIjogWwogICAgICAgIHsKICAgICAgICAgICAgIm5hbWUiOiAi5YiG57G7IiwKICAgICAgICAgICAgInZhbHVlIjogWwogICAgICAgICAgICAgICAgewogICAgICAgICAgICAgICAgICAgICJ2IjogIiIsCiAgICAgICAgICAgICAgICAgICAgIm4iOiAi5YWo6YOoIgogICAgICAgICAgICAgICAgfSwKICAgICAgICAgICAgICAgIHsKICAgICAgICAgICAgICAgICAgICAidiI6ICJrYW5saXN0L+WJp+aDhSIsCiAgICAgICAgICAgICAgICAgICAgIm4iOiAi5Ymn5oOFIgogICAgICAgICAgICAgICAgfSwKICAgICAgICAgICAgICAgIHsKICAgICAgICAgICAgICAgICAgICAidiI6ICJrYW5saXN0L+aDheaEnyIsCiAgICAgICAgICAgICAgICAgICAgIm4iOiAi5oOF5oSfIgogICAgICAgICAgICAgICAgfSwKICAgICAgICAgICAgICAgIHsKICAgICAgICAgICAgICAgICAgICAidiI6ICJrYW5saXN0L+ayu+aEiCIsCiAgICAgICAgICAgICAgICAgICAgIm4iOiAi5rK75oSIIgogICAgICAgICAgICAgICAgfSwKICAgICAgICAgICAgICAgIHsKICAgICAgICAgICAgICAgICAgICAidiI6ICJrYW5saXN0L+eIseaDhSIsCiAgICAgICAgICAgICAgICAgICAgIm4iOiAi54ix5oOFIgogICAgICAgICAgICAgICAgfSwKICAgICAgICAgICAgICAgIHsKICAgICAgICAgICAgICAgICAgICAidiI6ICJrYW5saXN0L+migeWlliIsCiAgICAgICAgICAgICAgICAgICAgIm4iOiAi6aKB5aWWIgogICAgICAgICAgICAgICAgfSwKICAgICAgICAgICAgICAgIHsKICAgICAgICAgICAgICAgICAgICAidiI6ICJrYW5saXN0L+WWnOWJpyIsCiAgICAgICAgICAgICAgICAgICAgIm4iOiAi5Zac5YmnIgogICAgICAgICAgICAgICAgfSwKICAgICAgICAgICAgICAgIHsKICAgICAgICAgICAgICAgICAgICAidiI6ICJrYW5saXN0L+iOt+WlliIsCiAgICAgICAgICAgICAgICAgICAgIm4iOiAi6I635aWWIgogICAgICAgICAgICAgICAgfSwKICAgICAgICAgICAgICAgIHsKICAgICAgICAgICAgICAgICAgICAidiI6ICJrYW5saXN0LyDnp5HlubsiLAogICAgICAgICAgICAgICAgICAgICJuIjogIuenkeW5uyIKICAgICAgICAgICAgICAgIH0sCiAgICAgICAgICAgICAgICB7CiAgICAgICAgICAgICAgICAgICAgInYiOiAia2FubGlzdC/mvKvlqIEiLAogICAgICAgICAgICAgICAgICAgICJuIjogIua8q+WogSIKICAgICAgICAgICAgICAgIH0sCiAgICAgICAgICAgICAgICB7CiAgICAgICAgICAgICAgICAgICAgInYiOiAia2FubGlzdC/nlJzonJwiLAogICAgICAgICAgICAgICAgICAgICJuIjogIueUnOicnCIKICAgICAgICAgICAgICAgIH0sCiAgICAgICAgICAgICAgICB7CiAgICAgICAgICAgICAgICAgICAgInYiOiAia2FubGlzdC/mgqznlpEiLAogICAgICAgICAgICAgICAgICAgICJuIjogIuaCrOeWkSIKICAgICAgICAgICAgICAgIH0sCiAgICAgICAgICAgICAgICB7CiAgICAgICAgICAgICAgICAgICAgInYiOiAia2FubGlzdC/lirHlv5ciLAogICAgICAgICAgICAgICAgICAgICJuIjogIuWKseW/lyIKICAgICAgICAgICAgICAgIH0sCiAgICAgICAgICAgICAgICB7CiAgICAgICAgICAgICAgICAgICAgInYiOiAia2FubGlzdC/ng6fohJEiLAogICAgICAgICAgICAgICAgICAgICJuIjogIueDp+iEkSIKICAgICAgICAgICAgICAgIH0sCiAgICAgICAgICAgICAgICB7CiAgICAgICAgICAgICAgICAgICAgInYiOiAia2FubGlzdC/lj4vmg4UiLAogICAgICAgICAgICAgICAgICAgICJuIjogIuWPi+aDhSIKICAgICAgICAgICAgICAgIH0KICAgICAgICAgICAgXSwKICAgICAgICAgICAgImtleSI6ICJjYXRlSWQiCiAgICAgICAgfQogICAgXSwKICAgICJiaWxsYm9hcmQiOiBbXSwKICAgICJob3QvaW5kZXgtbW92aWUt54Ot6ZeoIjogWwogICAgICAgIHsKICAgICAgICAgICAgIm5hbWUiOiAi5YiG57G7IiwKICAgICAgICAgICAgInZhbHVlIjogWwogICAgICAgICAgICAgICAgewogICAgICAgICAgICAgICAgICAgICJ2IjogIiIsCiAgICAgICAgICAgICAgICAgICAgIm4iOiAi5YWo6YOoIgogICAgICAgICAgICAgICAgfSwKICAgICAgICAgICAgICAgIHsKICAgICAgICAgICAgICAgICAgICAidiI6ICJob3QvaW5kZXgtbW92aWUt5pyA5pawIiwKICAgICAgICAgICAgICAgICAgICAibiI6ICLmnIDmlrAiCiAgICAgICAgICAgICAgICB9LAogICAgICAgICAgICAgICAgewogICAgICAgICAgICAgICAgICAgICJ2IjogImhvdC9pbmRleC1tb3ZpZS3nu4/lhbgiLAogICAgICAgICAgICAgICAgICAgICJuIjogIue7j+WFuCIKICAgICAgICAgICAgICAgIH0sCiAgICAgICAgICAgICAgICB7CiAgICAgICAgICAgICAgICAgICAgInYiOiAiaG90L2luZGV4LW1vdmllLeixhueTo+mrmOWIhiIsCiAgICAgICAgICAgICAgICAgICAgIm4iOiAi6LGG55Oj6auY5YiGIgogICAgICAgICAgICAgICAgfSwKICAgICAgICAgICAgICAgIHsKICAgICAgICAgICAgICAgICAgICAidiI6ICJob3QvaW5kZXgtbW92aWUt5Ya36Zeo5L2z54mHIiwKICAgICAgICAgICAgICAgICAgICAibiI6ICLlhrfpl6jkvbPniYciCiAgICAgICAgICAgICAgICB9LAogICAgICAgICAgICAgICAgewogICAgICAgICAgICAgICAgICAgICJ2IjogImhvdC9pbmRleC1tb3ZpZS3ljY7or60iLAogICAgICAgICAgICAgICAgICAgICJuIjogIuWNjuivrSIKICAgICAgICAgICAgICAgIH0sCiAgICAgICAgICAgICAgICB7CiAgICAgICAgICAgICAgICAgICAgInYiOiAiaG90L2luZGV4LW1vdmllLeasp+e+jiIsCiAgICAgICAgICAgICAgICAgICAgIm4iOiAi5qyn576OIgogICAgICAgICAgICAgICAgfSwKICAgICAgICAgICAgICAgIHsKICAgICAgICAgICAgICAgICAgICAidiI6ICJob3QvaW5kZXgtbW92aWUt6Z+p5Zu9IiwKICAgICAgICAgICAgICAgICAgICAibiI6ICLpn6nlm70iCiAgICAgICAgICAgICAgICB9LAogICAgICAgICAgICAgICAgewogICAgICAgICAgICAgICAgICAgICJ2IjogImhvdC9pbmRleC1tb3ZpZS3ml6XmnKwiLAogICAgICAgICAgICAgICAgICAgICJuIjogIuaXpeacrCIKICAgICAgICAgICAgICAgIH0sCiAgICAgICAgICAgICAgICB7CiAgICAgICAgICAgICAgICAgICAgInYiOiAiaG90L2luZGV4LW1vdmllLeWKqOS9nCIsCiAgICAgICAgICAgICAgICAgICAgIm4iOiAi5Yqo5L2cIgogICAgICAgICAgICAgICAgfSwKICAgICAgICAgICAgICAgIHsKICAgICAgICAgICAgICAgICAgICAidiI6ICJob3QvaW5kZXgtbW92aWUt5Zac5YmnIiwKICAgICAgICAgICAgICAgICAgICAibiI6ICLllpzliaciCiAgICAgICAgICAgICAgICB9LAogICAgICAgICAgICAgICAgewogICAgICAgICAgICAgICAgICAgICJ2IjogImhvdC9pbmRleC1tb3ZpZS3niLHmg4UiLAogICAgICAgICAgICAgICAgICAgICJuIjogIueIseaDhSIKICAgICAgICAgICAgICAgIH0sCiAgICAgICAgICAgICAgICB7CiAgICAgICAgICAgICAgICAgICAgInYiOiAiaG90L2luZGV4LW1vdmllLeenkeW5uyIsCiAgICAgICAgICAgICAgICAgICAgIm4iOiAi56eR5bm7IgogICAgICAgICAgICAgICAgfSwKICAgICAgICAgICAgICAgIHsKICAgICAgICAgICAgICAgICAgICAidiI6ICJob3QvaW5kZXgtbW92aWUt5oKs55aRIiwKICAgICAgICAgICAgICAgICAgICAibiI6ICLmgqznlpEiCiAgICAgICAgICAgICAgICB9LAogICAgICAgICAgICAgICAgewogICAgICAgICAgICAgICAgICAgICJ2IjogImhvdC9pbmRleC1tb3ZpZS3mgZDmgJYiLAogICAgICAgICAgICAgICAgICAgICJuIjogIuaBkOaAliIKICAgICAgICAgICAgICAgIH0sCiAgICAgICAgICAgICAgICB7CiAgICAgICAgICAgICAgICAgICAgInYiOiAiaG90L2luZGV4LW1vdmllLeaIkOmVvyIsCiAgICAgICAgICAgICAgICAgICAgIm4iOiAi5oiQ6ZW/IgogICAgICAgICAgICAgICAgfSwKICAgICAgICAgICAgICAgIHsKICAgICAgICAgICAgICAgICAgICAidiI6ICJob3QvaW5kZXgtbW92aWUt6LGG55OjdG9wMjUwIiwKICAgICAgICAgICAgICAgICAgICAibiI6ICLosYbnk6N0b3AyNTAiCiAgICAgICAgICAgICAgICB9CiAgICAgICAgICAgIF0sCiAgICAgICAgICAgICJrZXkiOiAiY2F0ZUlkIgogICAgICAgIH0KICAgIF0sCiAgICAiaG90L2luZGV4LXR2LeeDremXqCI6IFsKICAgICAgICB7CiAgICAgICAgICAgICJuYW1lIjogIuWIhuexuyIsCiAgICAgICAgICAgICJ2YWx1ZSI6IFsKICAgICAgICAgICAgICAgIHsKICAgICAgICAgICAgICAgICAgICAidiI6ICIiLAogICAgICAgICAgICAgICAgICAgICJuIjogIuWFqOmDqCIKICAgICAgICAgICAgICAgIH0sCiAgICAgICAgICAgICAgICB7CiAgICAgICAgICAgICAgICAgICAgInYiOiAiaG90L2luZGV4LXR2Lee+juWJpyIsCiAgICAgICAgICAgICAgICAgICAgIm4iOiAi576O5YmnIgogICAgICAgICAgICAgICAgfSwKICAgICAgICAgICAgICAgIHsKICAgICAgICAgICAgICAgICAgICAidiI6ICJob3QvaW5kZXgtdHYt6Iux5YmnIiwKICAgICAgICAgICAgICAgICAgICAibiI6ICLoi7HliaciCiAgICAgICAgICAgICAgICB9LAogICAgICAgICAgICAgICAgewogICAgICAgICAgICAgICAgICAgICJ2IjogImhvdC9pbmRleC10di3pn6nliaciLAogICAgICAgICAgICAgICAgICAgICJuIjogIumfqeWJpyIKICAgICAgICAgICAgICAgIH0sCiAgICAgICAgICAgICAgICB7CiAgICAgICAgICAgICAgICAgICAgInYiOiAiaG90L2luZGV4LXR2LeaXpeWJpyIsCiAgICAgICAgICAgICAgICAgICAgIm4iOiAi5pel5YmnIgogICAgICAgICAgICAgICAgfSwKICAgICAgICAgICAgICAgIHsKICAgICAgICAgICAgICAgICAgICAidiI6ICJob3QvaW5kZXgtdHYt5Zu95Lqn5YmnIiwKICAgICAgICAgICAgICAgICAgICAibiI6ICLlm73kuqfliaciCiAgICAgICAgICAgICAgICB9LAogICAgICAgICAgICAgICAgewogICAgICAgICAgICAgICAgICAgICJ2IjogImhvdC9pbmRleC10di3muK/liaciLAogICAgICAgICAgICAgICAgICAgICJuIjogIua4r+WJpyIKICAgICAgICAgICAgICAgIH0sCiAgICAgICAgICAgICAgICB7CiAgICAgICAgICAgICAgICAgICAgInYiOiAiaG90L2luZGV4LXR2LeaXpeacrOWKqOeUuyIsCiAgICAgICAgICAgICAgICAgICAgIm4iOiAi5pel5pys5Yqo55S7IgogICAgICAgICAgICAgICAgfSwKICAgICAgICAgICAgICAgIHsKICAgICAgICAgICAgICAgICAgICAidiI6ICJob3QvaW5kZXgtdHYt57u86Im6IiwKICAgICAgICAgICAgICAgICAgICAibiI6ICLnu7zoiboiCiAgICAgICAgICAgICAgICB9LAogICAgICAgICAgICAgICAgewogICAgICAgICAgICAgICAgICAgICJ2IjogImhvdC9pbmRleC10di3nuqrlvZXniYciLAogICAgICAgICAgICAgICAgICAgICJuIjogIue6quW9leeJhyIKICAgICAgICAgICAgICAgIH0KICAgICAgICAgICAgXSwKICAgICAgICAgICAgImtleSI6ICJjYXRlSWQiCiAgICAgICAgfQogICAgXQp9"
    ))

//    private val classList = Class.parseFromFormatStr(Utils.base64Decode("55S15b2xPWhvdC9pbmRleC1tb3ZpZS3ng63pl6gm5Ymn6ZuGPWhvdC9pbmRleC10di3ng63pl6gm5qac5Y2VPWJpbGxib2FyZC8qJueJh+WNlT1rYW5saXN0L+WFqOmDqCov"))
    private val classList = Class.parseFromFormatStr(Util.base64Decode("55S15b2xPWhvdC9pbmRleC1tb3ZpZS3ng63pl6gm5Ymn6ZuGPWhvdC9pbmRleC10di3ng63pl6g="))
    private val classMap = classList.mapIndexed{ index, item -> index to item}.toMap()

    override fun homeContent(filter: Boolean): String {
        val string = OkHttp.string(host, Util.webHeaders(host))
        val vodList = parseVod(string)
        return Result.string(classList, vodList.toList(), cateMap)
    }

    private fun parseVod(string: String): MutableSet<Vod> {
        val select = Jsoup.parse(string).select("div > a.item")
        val vodList = mutableSetOf<Vod>()
        for (element in select) {
            val id = element.attr("href")
            val item = element.select("div > img")
            val name = item.attr("alt")
            val image = item.attr("data-src")
            val imageUrl = UrlHeaderBuilder(image).referer(host).userAgent(Util.CHROME).build()
            vodList.add(Vod(id, name, imageUrl))
        }
        return vodList
    }

    private fun cateParseVod(string: String, tid: String, ref:String): MutableSet<Vod>{
        if("billboard" == tid){
            val vodList = mutableSetOf<Vod>()
            val elements = Jsoup.parse(string).select("div#video-list")
            for (element in elements) {
                val list = element.select("div.item-root > a")
                for (item in list) {
                    val id = item.attr("href")
                    val a = item.select("img")
                    val name = a.attr("alt")
                    val pic = a.attr("data-src")
                    val vod = Vod(id, name, Image.UrlHeaderBuilder(pic).referer(ref).userAgent(Util.CHROME).build())
                    vodList.add(vod)
                }
            }
            return vodList.toMutableSet()
        }else{
            return parseVod(string)
        }
    }

    override fun categoryContent(tid: String, pg: String, filter: Boolean, extend: HashMap<String, String>): String {
        val url:String
        if(filter){
            val cateId = extend["cateId"]
            if(cateId.isNullOrBlank()) return ""
            url = getUrl(pg, cateId)
            val string = OkHttp.string(url, Util.webHeaders(url))
            val vodList = cateParseVod(string, tid, url)
            return Result.string(classList, vodList.toList(), cateMap)
        }else{
            url = getUrl(pg, tid)
            val string = OkHttp.string(url, Util.webHeaders(url))
            val vodList = cateParseVod(string, tid, url)
            return Result.string(classList, vodList.toList(), cateMap)
        }
    }

    private fun getUrl(pg: String, tid: String):String {
        val url:String = if (pg == "1" /*&& classMap[3]!!.typeId != tid*/) {
            "$host${URLEncodeUtil.encode(tid)}.html"
        } else {
            "$host${URLEncodeUtil.encode(tid)}-p-$pg.html"
        }
        return url
    }

    override fun detailContent(ids: MutableList<String>): String {
        val id = ids[0]
        val url = URLUtil.completeUrl(host, id)
        val string = OkHttp.string(url, Util.webHeaders(host))
        val parse = Jsoup.parse(string)
        val cid = parse.select("input#current_id").attr("value")
        val tk = parse.select("input#e_token").attr("value")
        val ty = parse.select("input#mtype").attr("value")

        val info = parse.select("div.result-info > .item-root")
        val img = info.select("img").attr("data-src")
        val select = info.select("div.detail > .meta")
        val name = select[0].text()
        val remark = select[1].text()
        val year = select[2].text()
        val zone = select[3].text()
        val actor = select[4].text()

        val t = getTk(cid, tk)
        val video = id.split("/").last()
        val vd = OkHttp.string(String.format(host + detail, video, ty, t), Util.webHeaders(url))
        SpiderDebug.log("Ik 请求返回: $vd")
        val r = Json.parseSafe<Rst>(vd, Rst::class.java)
        if(r.state != 1){
            SpiderDebug.log("Ik 请求失败")
            return ""
        }
        val vodPlayBuilder = Vod.VodPlayBuilder()
        with(vodPlayBuilder){
            for (item in r.data.list) {
                val playUrl = item.getPlayUrl()
                append(playUrl[0].flag, playUrl)
            }
        }
        val buildResult = vodPlayBuilder.build()
        val vod = Vod(id, name, Image.UrlHeaderBuilder(img).userAgent(Util.CHROME).referer(url).build(), remark)
        vod.let {
            it.setVodYear(year)
            it.setVodActor(actor)
            it.setVodArea(zone)
            it.setVodPlayFrom(buildResult.vodPlayFrom)
            it.vodPlayUrl = buildResult.vodPlayUrl
        }
        return Result.string(vod)
    }

    fun getTk(cid:String, tk:String):String{
        var t = tk
        val len = cid.length
        val lastFourBit = cid.substring(len - 4, len)
        val array = mutableListOf<String>()
        for (i in 0 until lastFourBit.length) {
            val rst = lastFourBit[i].code % 3 + 1
            array.add(i, t.substring(rst, rst + 8))
            t = t.substring(rst + 8, t.length)
        }
        return array.joinToString("")
    }

    override fun searchContent(key: String, quick: Boolean): String {
        val url = "${host}search?q=${URLEncodeUtil.encode(key)}"
        val string = OkHttp.string(url, Util.webHeaders(host))
        val parse = Jsoup.parse(string)
        val items = parse.select("div.media")
        val vodList = mutableListOf<Vod>()
        for (item in items) {
            val head = item.select(".media-heading > a")
            val id = head.attr("href")
            val name = head.text()
            val pic = item.select("div > a > img").attr("data-src")
            vodList.add(Vod(id, name, Image.UrlHeaderBuilder(pic).userAgent(Util.CHROME).referer(url).build()))
        }
        return Result.string(vodList)
    }

    override fun playerContent(flag: String, id: String, vipFlags: MutableList<String>): String {
        SpiderDebug.log("play flag:"+flag + "id: "+id)
//        val array = arrayOf<Any>(
//            200,
//            "application/vnd.apple.mpegurl",
//            id
//            )
        return Result.get().url(id).header(Util.webHeaders(host)).string()
    }
}

data class Rst(
    val state:Int,
    val message: String,
    val data: D
)

data class D(
    val list: List<RstItem>
)

data class RstItem(
    val siteId:Int,
    val id:Int,
    val resData:String
)

fun RstItem.getPlayUrl():List<PlayUrl>{
    val list = mutableListOf<PlayUrl>()
    val parse = parse(resData)
    if(parse.isJsonArray){
        for (jsonElement in parse.asJsonArray) {
            val obj = jsonElement.asJsonObject
            val f = obj.get("flag").asString
            val s = obj.get("url")
            if(StringUtil.isBlank(s.asString)) continue
            val epList = s.asString.split("#")
            for (ep in epList) {
                val split = ep.split("$")
                if(split.size <= 1){
                    continue
                }
                val playUrl = PlayUrl()
                playUrl.flag = f
                playUrl.name = split[0]
                playUrl.url = split[1]
                list.add(playUrl)
            }
        }
    }
    return list
}

data class DataUrl(
    val flag:String,
    val url:String // name$url
)