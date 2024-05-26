@file:JvmName("AssertUtil")

import com.github.catvod.bean.Result

object AssertUtil {
    fun assertResult(s: String) {
        val result = Result.objectFrom(s)
        assert(result.list.isNotEmpty() || (result.url as List<String>).isNotEmpty())
    }

}