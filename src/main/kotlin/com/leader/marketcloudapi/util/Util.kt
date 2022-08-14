package com.leader.marketcloudapi.util

fun <T> requireArgument(value: T?, name: String): T {
    return value ?: throw InternalErrorException("Require argument $name.")
}

fun <T> T?.isRequiredArgument(name: String): T {
    return requireArgument(this, name)
}

fun <T> T.oneMinus(other: T): T? {
    return if (this == other) {
        null
    } else {
        this
    }
}

fun <T> T.setMinus(other: T): List<T> {
    return if (this == other) {
        listOf()
    } else {
        listOf(this)
    }
}

fun <T> List<T>.setMinus(other: List<T>): List<T> {
    return this.filter { !other.contains(it) }
}
