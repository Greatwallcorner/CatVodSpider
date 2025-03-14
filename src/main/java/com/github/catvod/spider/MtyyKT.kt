package com.github.catvod.spider

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.unit.dp
import cn.hutool.core.util.RandomUtil
import cn.hutool.json.JSONUtil
import com.github.catvod.crawler.SpiderDebug
import com.github.catvod.net.OkHttp
import com.github.catvod.utils.DialogUtil
import com.github.catvod.utils.Util
import com.google.common.collect.ImmutableMap
import okhttp3.Response
import java.util.concurrent.atomic.AtomicReference


class MtyyKT {


    companion object {
        private val siteUrl = "https://mtyy2.com"

        @JvmStatic
        fun init(): String {
            val res = OkHttp.newCall("${MtyyKT.siteUrl}/vodsearch/-------------.html?wd=1", Util.webHeaders(siteUrl))
            var session = "";
            if (res.isSuccessful) {
                val c = res.headers[com.google.common.net.HttpHeaders.SET_COOKIE]
                val split = c?.split(";")
                session = if ((split?.size ?: 0) > 1) split?.get(0) ?: "" else c ?: ""

            } else {
                SpiderDebug.log("麦田初始化失败：$res")
            }
            SpiderDebug.log("麦田 init session:" + session)
            return session
        }

        @JvmStatic
        fun verify(searchUrl: String, string: AtomicReference<String>) {

            val map = getVerifyCodePic(searchUrl)
            val session = map.get("session") as String
            val resp = map.get("pic") as ByteArray
            if (resp.isNotEmpty()) {
                DialogUtil.showDialog(
                    content = {
                        verifyCode(resp, searchUrl, onClose = { DialogUtil.close() }, onConfirm = {
                            SpiderDebug.log("麦田 code confirm start")
                            DialogUtil.close()
                            SpiderDebug.log("麦田 verify url:" + "$siteUrl/index.php/ajax/verify_check?type=search&verify=$it")

                            val res = verifying(searchUrl, session, it)
                            if (JSONUtil.parseObj(res).getInt("code") == 1) {
                               /* val html = OkHttp.string(searchUrl, Utils.webHeaders(searchUrl, session))
                                SpiderDebug.log("搜索结果：$html")*/

                                string.set(
                                    session
                                )
                            } else {
                                SpiderDebug.log("麦田验证：" + JSONUtil.parseObj(res).getStr("msg"))
                                Util.notify(JSONUtil.parseObj(res).getStr("msg"));
                            }
                            SpiderDebug.log("麦田 code confirm end")
                        }, session)
                    }, "麦田验证"
                )
            } else {
                SpiderDebug.log("麦田 获取验证码失败")
            }
        }

        public fun verifying(
            url: String, session: String, it: String
        ): String? {
            val header = Util.webHeaders(
                url, session
            )
            header["X-Requested-With"] = "XMLHttpRequest"
            SpiderDebug.log(
                "麦田 verify header:" + JSONUtil.toJsonPrettyStr(
                    header
                ),
            )
            val res = OkHttp.post(
                "$siteUrl/index.php/ajax/verify_check?type=search&verify=$it", "", header
            ).body

            SpiderDebug.log("麦田验证结果：$res")

            return res;
        }


        public fun getVerifyCodePic(url: String): ImmutableMap<String, Any> {
            val codeUrl = "${siteUrl}/verify/index.html?r=0.6416516521737"+RandomUtil.randomInt(0,9)
            var resp: Response? = null
            try {
                val header = Util.webHeaders(url)
                header["Sec-Fetch-Dest"] = "image";
                header["Sec-Fetch-Mode"] = "no-cors";
                header["Sec-Fetch-Site"] = "same-origin";
                resp = OkHttp.newCall(codeUrl, Util.webHeaders(url))
                var session = "";
                if (resp.isSuccessful) {
                    val c = resp.headers[com.google.common.net.HttpHeaders.SET_COOKIE]
                    val split = c?.split(";")
                    session = if ((split?.size ?: 0) > 1) split?.get(0) ?: "" else c ?: ""
                    SpiderDebug.log("麦田session：" + session)
                }
                return ImmutableMap.of<String, Any>("pic", resp?.body?.bytes() ?: byteArrayOf(), "session", session);
            } finally {
                resp?.close()
            }
        }

        public fun getVerifyCodePic(url: String, session: String): ByteArray {
            val codeUrl = "${siteUrl}/verify/index.html?"
            var resp: Response? = null
            try {
                resp = OkHttp.newCall(codeUrl, Util.webHeaders(url, session))
                return resp?.body?.bytes() ?: byteArrayOf()
            } finally {
                resp?.close()
            }
        }

        @Composable
        fun verifyCode(ins: ByteArray, url: String, onClose: () -> Unit, onConfirm: (String) -> Unit, session: String) {
            var stream by remember { mutableStateOf(ins) }
            var value by remember { mutableStateOf("") }
            var bitmap: ImageBitmap = loadImageBitmap(stream.inputStream())
            DisposableEffect(stream) {
                SpiderDebug.log("stream change " + stream.size)
                if (stream.isNotEmpty()) {
                    bitmap = loadImageBitmap(stream.inputStream())
                }
                onDispose { }
            }
            Box(Modifier.padding(8.dp)) {
                Column(modifier = Modifier.width(350.dp)) {
                    Row(modifier = Modifier.padding(vertical = 5.dp)) {
                        Image(
                            bitmap,
                            contentDescription = "verifyCode",
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier.clickable {
                                val response = getVerifyCodePic(url, session)
                                if (response.isNotEmpty()) {
                                    SpiderDebug.log("麦田 请求成功")
                                    stream = response
                                }
                            }.padding(end = 10.dp).size(width = 150.dp, height = 50.dp)
                        )
                        TextField(value, onValueChange = {
                            SpiderDebug.log("麦田 text change: " + it)
                            value = it
                        })
                    }
                    Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                        Button(onClick = { onClose() }) {
                            Text("取消")
                        }
                        Button(onClick = { onConfirm(value) }) {
                            Text("确认")
                        }
                    }
                }
            }
        }
    }

    /* companion object {


         private val host = Utils.base64Decode("aHR0cHM6Ly93d3cueWp5czAxLmNvbS8=")

         private val classList =
             Class.parseFromFormatStr(Utils.base64Decode("5Yqo5L2cPS9zL2Rvbmd6dW8m54ix5oOFPS9zL2FpcWluZybllpzliac9L3MveGlqdSbnp5Hlubs9L3Mva2VodWFuJuaBkOaAlj0vcy9rb25nYnUm5oiY5LqJPS9zL3poYW56aGVuZybmrabkvqA9L3Mvd3V4aWEm6a2U5bm7PS9zL21vaHVhbibliafmg4U9L3MvanVxaW5nJuWKqOeUuz0vcy9kb25naHVhJuaDiuaCmj0vcy9qaW5nc29uZyYzRD0vcy8zRCbngb7pmr49L3MvemFpbmFuJuaCrOeWkT0vcy94dWFueWkm6K2m5YyqPS9zL2ppbmdmZWkm5paH6Im6PS9zL3dlbnlpJumdkuaYpT0vcy9xaW5nY2h1biblhpLpmak9L3MvbWFveGlhbibniq/nvao9L3MvZmFuenVpJue6quW9lT0vcy9qaWx1JuWPpOijhT0vcy9ndXpodWFuZyblpYflubs9L3MvcWlodWFuJuWbveivrT0vcy9ndW95dSbnu7zoibo9L3Mvem9uZ3lpJuWOhuWPsj0vcy9saXNoaSbov5Dliqg9L3MveXVuZG9uZybljp/liJvljovliLY9L3MveXVhbmNodWFuZybnvo7liac9L3MvbWVpanUm6Z+p5YmnPS9zL2hhbmp1JuWbveS6p+eUteinhuWJpz0vcy9ndW9qdSbml6Xliac9L3MvcmlqdSboi7Hliac9L3MveWluZ2p1JuW+t+WJpz0vcy9kZWp1JuS/hOWJpz0vcy9lanUm5be05YmnPS9zL2JhanUm5Yqg5YmnPS9zL2ppYWp1Juilv+WJpz0vcy9zcGFuaXNoJuaEj+Wkp+WIqeWJpz0vcy95aWRhbGlqdSbms7Dliac9L3MvdGFpanUm5riv5Y+w5YmnPS9zL2dhbmd0YWlqdSbms5Xliac9L3MvZmFqdSbmvrPliac9L3MvYW9qdQ=="))

         private val ad = Utils.base64Decode("enp6eno=")

         private var reference = host
         fun proxyLocal(params: MutableMap<String, String>): Array<Any> {
             var url = Utils.base64Decode(params["url"] ?: "")
             val id = Utils.base64Decode(params["id"] ?: "")
             val cookie = Utils.base64Decode(params["session"] ?: "")
             val ref = Utils.base64Decode(params["ref"] ?: "")
             if (url.contains("god")) {
                 val t = Date().time
                 val p = mutableMapOf("t" to t, "sg" to getSg(id, t.toString()), "verifyCode" to "888")
                 val body = OkHttp.post("${host}god", Json.toJson(p), Utils.webHeaders(host, cookie)).body
                 SpiderDebug.log("DB god req:$body")
                 url = Json.get().toJsonTree(body).asJsonObject.get("url").asString
             }
             return ProxyVideo.ProxyRespBuilder.redirect(url)
         }

         fun getSg(id: String, time: String): String {
             val s = "$id-$time"
             val digest = DigestUtil.md5Hex(s).substring(IntRange(0, 15)).toByteArray()
             val aes = AES(Mode.ECB, Padding.PKCS5Padding, digest)
             return base64ToHex(Base64.encode(aes.encrypt(s.toByteArray())))
         }

         private fun base64ToHex(s: String): String {
             val decodedBytes = Base64.decode(s)
             val hexString = StringBuilder()
             for (byte in decodedBytes) {
                 val hex = Integer.toHexString(0xFF and byte.toInt())
                 if (hex.length == 1) {
                     hexString.append('0')
                 }
                 hexString.append(hex)
             }
             return hexString.toString().uppercase()
         }
     }*/
}