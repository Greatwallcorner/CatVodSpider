@file:JvmName("AssertUtil")

import cn.hutool.core.collection.CollectionUtil
import com.github.catvod.bean.Result
import com.google.common.collect.Collections2
import org.apache.commons.lang3.StringUtils
import java.util.Collections

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