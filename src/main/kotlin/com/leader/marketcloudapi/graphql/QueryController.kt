package com.leader.marketcloudapi.graphql

import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.stereotype.Controller

@Controller
@SchemaMapping(typeName = "Query")
class QueryController {

    @SchemaMapping
    fun currentAgent() = true

    @SchemaMapping
    fun agent() = true

    @SchemaMapping
    fun organization() = true

    @SchemaMapping
    fun project() = true
}