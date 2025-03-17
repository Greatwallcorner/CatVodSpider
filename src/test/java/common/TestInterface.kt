package common

import com.github.catvod.crawler.Spider

interface TestInterface<T:Spider> {
    var t:T

    fun init(){
        t.init()
    }

    fun homeTest()

    fun cateTest()

    fun detailTest()

    fun playTest()

    fun searchTest()

//    fun playerContentTest()

    fun assert(s:String){
        println(s)
        AssertUtil.assertResult(s)
    }
}