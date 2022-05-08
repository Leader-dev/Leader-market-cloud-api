package com.leader.marketcloudapi.util.component

import org.bson.Document
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse

@Component
class VolatileThreadData {

    private val threadLocal = ThreadLocal<Document>()

    val threadData: Document
        get() {
            return threadLocal.get() ?: kotlin.run {
                val d = Document()
                threadLocal.set(d)
                d
            }
        }

    fun cleanupThreadData() {
        threadLocal.remove()
    }

    operator fun get(key: String): Any? = threadData[key]

    operator fun <T> get(key: String, type: Class<T>): T = threadData[key, type]

    operator fun set(key: String, value: Any?) {
        threadData[key] = value
    }

    fun containsKey(key: String): Boolean = threadData.containsKey(key)

    fun remove(key: String) {
        threadData.remove(key)
    }
}

@Component
class VolatileThreadDataFilter @Autowired constructor(
    private val volatileThreadData: VolatileThreadData
) : Filter {

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        chain.doFilter(request, response)
        volatileThreadData.cleanupThreadData()
    }
}