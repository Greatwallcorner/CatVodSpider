package com.github.catvod.spider

import cn.hutool.core.net.URLEncodeUtil
import cn.hutool.crypto.digest.DigestUtil
import com.github.catvod.bean.Class
import com.github.catvod.bean.Filter
import com.github.catvod.bean.Result
import com.github.catvod.bean.Vod
import com.github.catvod.bean.Vod.VodPlayBuilder.PlayUrl
import com.github.catvod.crawler.Spider
import com.github.catvod.crawler.SpiderDebug
import com.github.catvod.net.OkHttp
import com.github.catvod.utils.Json
import com.github.catvod.utils.ProxyVideo
import com.github.catvod.utils.Util
import com.google.common.net.HttpHeaders
import com.google.gson.JsonParser
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken


class NG: Spider() {
    private var COMMON_URL: String = Util.base64Decode("aHR0cDovL3lzLmNoYW5nbWVuZ3l1bi5jb20=")
    private val FIND_VIDEO_VOD_LIST: String = "/api.php/provide/vod_list"
    private val FIND_CLASSIFICATION: String = "/api.php/provide/home_nav"
    private val VIDEO_DETAIL: String = "/api.php/provide/vod_detail"
    private val SEARCH_SEARCH: String = "/api.php/provide/search_result"


    private val classList = mutableListOf<Class>()
    private val filters = LinkedHashMap<String, MutableList<Filter>>()


    private fun getParams(): Map<String, String> {
        val hashMap: HashMap<String, String> = java.util.HashMap()
        hashMap["devices"] = "android"
        hashMap["deviceModel"] = "ASUS_I003DD"
        hashMap["deviceBrand"] = "ASUS"
        hashMap["deviceVersion"] = "9"
        hashMap["deviceScreen"] = "2340*1080"
        hashMap["appVersionCode"] = "9"
        hashMap["appVersionName"] = "1.0.9"
        hashMap["time"] = (System.currentTimeMillis() / 1000).toString()
        hashMap["imei"] = ""
        hashMap["app"] = "ylys"
        return hashMap
    }
    
    fun getHeaders(): Map<String, String> {
        val headers = HashMap<String, String>()
        val currentTimeMillis = System.currentTimeMillis()
        headers.put("timeMillis", currentTimeMillis.toString())
        headers.put(
            "sign",
            DigestUtil.md5Hex(Util.base64Decode("I3VCRnN6ZEVNMG9MMEpSbkA=")+"$currentTimeMillis")
        )
        return headers
    }

    override fun homeContent(filter: Boolean): String {
        if(classList.isEmpty()){
            val string = OkHttp.string("$COMMON_URL$FIND_CLASSIFICATION", getParams(), getHeaders())
            val filterList = JsonParser.parseString(string).asJsonArray
            for ((index, jsonElement) in filterList.withIndex()) {
                if(index == 0) continue
                val obj = jsonElement.asJsonObject
                val id = obj.get("id").asInt
                val name = obj.get("name").asString
                val clazz = Class(id.toString(), name)
                classList.add(clazz)
//                val msgs = obj.get("msg").asJsonArray
//                if(msgs.isEmpty){
//                    continue
//                }
//                for (msg in msgs) {
//                    val msgObj = msg.asJsonObject
//                    val dataArray = msgObj.get("data").asJsonArray
//                    val list = mutableListOf<Filter.Value>()
//                    for ((index, jsonElement1) in dataArray.withIndex()) {
//                        if(index == 0) continue
//                        list.add(Filter.Value(jsonElement1.asString, jsonElement1.asString))
//                    }
//
//                    if(filters[id.toString()].isNullOrEmpty()){
//                        filters[id.toString()] = mutableListOf(Filter(msgObj.get("name").asString, dataArray.get(0).asString, list))
//                    }else{
//                        filters[id.toString()]!!.add(Filter(msgObj.get("name").asString, dataArray.get(0).asString, list))
//                    }
//                }
            }
        }
        return Result.string(classList, filters)
    }

    override fun categoryContent(
        tid: String,
        pg: String,
        filter: Boolean,
        extend: java.util.HashMap<String, String>
    ): String {
        val params = getParams().toMutableMap()
        extend.forEach { (t, u) ->
            params[t] = URLEncodeUtil.encode(u)
        }
        params["page"] = pg
        params["id"] = tid
        val string = OkHttp.string("$COMMON_URL$FIND_VIDEO_VOD_LIST", params, getHeaders())
        val type = object : TypeToken<Rst<It>>() {}.type
        val resp = Json.parseSafe<Rst<It>>(string, type)
        var vodList = listOf<Vod>()
        if(resp.isSuccess()){
            vodList = resp.list.toVodList()
        }else{
            SpiderDebug.log("ng cate error: $string")
        }
        return Result.string(classList, vodList, filters)
    }

    override fun detailContent(ids: MutableList<String>): String {
        val map = getParams().toMutableMap()
        map["id"] = ids[0]
        val string = OkHttp.string("$COMMON_URL$VIDEO_DETAIL", map, getHeaders())
        val type = object : TypeToken<Rst<Dt>>() {}.type
        val dt = Json.parseSafe<Rst<Dt>>(string, type)
        if(!dt.isSuccess()){
            SpiderDebug.log("ng detail err: ${dt.msg}")
            return Result.error(dt.msg)
        }
        return Result.string(dt.data.toVod().apply { setVodId(ids[0]) })
    }

    override fun playerContent(flag: String, id: String, vipFlags: MutableList<String>): String {
        val string = OkHttp.string(id)
        val type = object : TypeToken<Rst<PlayRst>>() {}.type
        val rst = Json.parseSafe<Rst<PlayRst>>(string, type)
        if(!rst.isSuccess()){
            SpiderDebug.log("play err： ${rst.msg}")
            return Result.error(rst.msg)
        }
        val filter = rst.data.header.filter { it.key.equals(HttpHeaders.USER_AGENT, true) }
        return Result.get().url(ProxyVideo.buildCommonProxyUrl(rst.data.url, filter)).string()
    }

    override fun searchContent(key: String, quick: Boolean): String {
        val params = getParams().toMutableMap()
        params["video_name"] = URLEncodeUtil.encode(key)
        val string = OkHttp.string("$COMMON_URL$SEARCH_SEARCH", params, getHeaders())
        val type = object : TypeToken<Rst<List<SearchRst>>>() {}.type
        val rst = Json.parseSafe<Rst<List<SearchRst>>>(string, type)
        if(!rst.isSuccess()){
            SpiderDebug.log("ng search error:${rst.msg}")
            return Result.error(rst.msg)
        }
        return Result.string(rst.data[0].toVodList())
    }


    data class Rst<T>(
        val code:Int,
        val msg:String,
        val limit:String,
        val pagecount:Int,
        val total:Int,
        val list:List<T>,
        val data:T
    ){
        fun isSuccess(): Boolean {
            return code == 1
        }
    }


    data class It(
        val id:Int,
        val img:String,
        val name:String,
        val score:String,
        val msg:String,
    ){
        fun toVod(): Vod {
            val vod = Vod()
            vod.setVodId(id.toString())
            vod.setVodName(name)
            vod.setVodRemarks(score)
            vod.setVodPic(img)
            return vod
        }
    }

    data class Dt(
        val name:String,
        val year:String,
        val score: String,
        val hits:Int,
        val type:String,
        val img:String,
        val info:String,
        @SerializedName("total_count")
        val totalCount:Int,
        @SerializedName("player_info")
        val playerInfo:List<DtIt>
    ){
        fun toVod(): Vod {
            val vod = Vod()
            vod.setVodId(name+score)
            vod.setVodName(name)
            vod.setVodPic(img)
            vod.setVodTag(type)
            vod.setVodRemarks(year)
            vod.vodContent = info
            val vodPlayBuilder = Vod.VodPlayBuilder()
            for (info in playerInfo) {
                val playUrlList = mutableListOf<PlayUrl>()
                for (vtInfo in info.videoInfo) {
                    val playUrl = PlayUrl()
                    playUrl.url = vtInfo.url[0]
                    playUrl.name = vtInfo.name
                    playUrl.flag = info.show
                    playUrlList.add(playUrl)
                }
                vodPlayBuilder.append(info.show, playUrlList)
            }
            val build = vodPlayBuilder.build()
            vod.setVodPlayFrom(build.vodPlayFrom)
            vod.vodPlayUrl = build.vodPlayUrl
            return vod
        }
    }

    data class DtIt(
        val id:Int,
        val from:String,
        val show:String,
        @SerializedName("url_count")
        val urlCount:Int,
        @SerializedName("video_info")
        val videoInfo: List<VtInfo>
    )

    data class VtInfo(
        val id:Int,
        val name:String,
        val pic:String,
        val url:List<String>
    )

    private fun List<It>.toVodList(): MutableList<Vod> {
        val list = mutableListOf<Vod>()
        for (it in this) {
            list.add(it.toVod())
        }
        return list
    }

    data class PlayRst(
        val url:String,
        val header: Map<String, String>
    )

    data class SearchRst(
        val id: Int,
        val name:String,
        val data:List<SearchRstItem>
    ){
        fun toVodList(): MutableList<Vod> {
            val list = mutableListOf<Vod>()
            for (datum in data) {
                list.add(datum.toVOd())
            }
            return list
        }
    }

    data class SearchRstItem(
        val id:Int,
        val type:Int,
        @SerializedName("video_name")
        val videoName:String,
        val qingxidu:String,
        val img:String,
        val director:String,
        @SerializedName("main_actor")
        val mainActor:String,
        val category: String
    ){
        fun toVOd():Vod{
            val vod = Vod()
            vod.setVodId(id.toString())
            vod.setVodTag(qingxidu)
            vod.setVodPic(img)
            vod.setVodRemarks(category)
            vod.setVodName(videoName)
            vod.setVodActor(mainActor)
            return vod
        }
    }

}