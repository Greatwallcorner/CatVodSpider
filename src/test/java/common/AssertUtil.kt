@file:JvmName("common.AssertUtil")

package common

import cn.hutool.core.collection.CollectionUtil
import com.github.catvod.bean.Result
import org.apache.commons.lang3.StringUtils

object AssertUtil {
    fun assertResult(s: String) {
        val result = Result.objectFrom(s)
        if(result.url != null){
            if(result.url is String){
                assert(StringUtils.isNotBlank(result.url as String))
            }else if(result.url is List<*>){
                assert(CollectionUtil.isNotEmpty(result.url as List<*>))
            }
        }else{
            assert(result.list.isNotEmpty())
        }
    }

}