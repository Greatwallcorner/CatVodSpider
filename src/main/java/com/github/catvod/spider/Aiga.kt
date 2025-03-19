package com.github.catvod.spider

import cn.hutool.crypto.digest.MD5
import com.github.catvod.bean.Result
import com.github.catvod.crawler.Spider
import com.github.catvod.net.OkHttp
import com.github.catvod.utils.Json
import com.github.catvod.utils.Util
import org.jsoup.Jsoup

class Aiga:Spider() {
    private val host = "https://aigua.tv/"
    private val home = "/video/index"
    var a: String = "https://tvapi211.magicetech.com/"
    var b: String = "hr_1_1_0/apptvapi/web/index.php"

    override fun homeContent(filter: Boolean): String {
        val get = Jsoup.connect(host).get()
//        val post = OkHttp.post("$a$b/video/index", Json.toJson(commonParam()))
        val string = OkHttp.string(host, Util.webHeaders(host))
//        val parse = Jsoup.parse(string)
//        val movList = parse.select("div.video-box-new")
//        val vodList = mutableListOf<Vod>()
//        movList.forEach {
//            val v = Vod().apply {
//                val a = it.select("a[class=Movie-name02]")
//                setVodName(a.text())
//                setVodId(a.attr("href"))
//                setVodPic(it.select("img.Movie-img").attr("src"))
//            }
//            vodList.add(v)
//        }
        return Result.string(listOf())
    }

    private fun commonParam():Map<String, String>{
        var map:MutableMap<String, String> = Json.parseSafe<MutableMap<String, String>>("{\n" +
                "    \"debug\":\"1\",\n" +
                "\"appId\":\"1\",\n" +
                "\"osType\":\"3\",\n" +
                "\"product\":\"4\",\n" +
                "\"sysVer\":\"30\",\n" +
//                "\"token\": \"\",\n" +
                "\"udid\":\"0A2233445566\",\n" +
                "\"ver\":\"1.1.0\",\n" +
                "\"packageName\":\"com.gzsptv.gztvvideo\",\n" +
                "\"marketChannel\":\"tv\"" +
//                ",\n" +
//                "\"authcode\": \"\"\n" +
                "\n" +
                "}", MutableMap::class.java)
        map["time"] = (System.currentTimeMillis() / 1000).toString()
        getSign(map)
        return map
    }

    private fun getSign(map:MutableMap<String, String>):String{
        val mutableListOf = mutableListOf<String>()
        map.entries.forEach{
            mutableListOf.add("${it.key}=${it.value}")
        }
        mutableListOf.sort()
        val buildString = buildString {
            append("jI7POOBbmiUZ0lmi")
            mutableListOf.forEachIndexed { i, o ->
                if (i == 0) append(o)
                else append("&$o")
            }
            append("D9ShYdN51ksWptpkTu11yenAJu7Zu3cR")
        }
        val digestHex = MD5.create().digestHex(buildString)
        map["sign"] = digestHex
        return digestHex
    }

    override fun categoryContent(tid: String?, pg: String?, filter: Boolean, extend: HashMap<String, String>?): String {
        return super.categoryContent(tid, pg, filter, extend)
    }

    override fun detailContent(ids: MutableList<String>?): String {
        return super.detailContent(ids)
    }

    override fun searchContent(key: String?, quick: Boolean): String {
        return super.searchContent(key, quick)
    }

    override fun playerContent(flag: String?, id: String?, vipFlags: MutableList<String>?): String {
        return super.playerContent(flag, id, vipFlags)
    }
}