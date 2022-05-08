package com.leader.marketcloudapi.util

class UserAuthException : RuntimeException()

class InternalErrorException : RuntimeException {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message)
}
