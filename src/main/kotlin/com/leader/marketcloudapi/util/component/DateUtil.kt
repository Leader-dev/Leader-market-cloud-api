package com.leader.marketcloudapi.util.component

import com.leader.marketcloudapi.util.InternalErrorException
import org.springframework.stereotype.Component
import java.util.*

@Component
class DateUtil {

    fun getCurrentDate() = Date()

    fun getCurrentTime() = Date().time

    fun getCurrentDatePlus(milliseconds: Long) = Date(getCurrentTime() + milliseconds)

    fun getDateBefore(time: Long) = Date(getTimeBefore(time))

    fun getTimeBefore(time: Long) = getCurrentTime() - time

    fun getTodayZero(): Date {
        val calendar = Calendar.getInstance()
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.HOUR_OF_DAY] = 0
        calendar[Calendar.MILLISECOND] = 0
        val todayZero = calendar.timeInMillis
        return Date(todayZero)
    }

    fun assertDateIsAfterNow(thisDate: Date) {
        val currentDate = getCurrentDate()
        if (thisDate.time < currentDate.time) {
            throw InternalErrorException("Invalid date! Must be after current date.")
        }
    }
}