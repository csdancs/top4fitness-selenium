package util

import java.util.Properties

object Config {
    val props = Properties().apply {
        val stream = object {}.javaClass.getResourceAsStream("/application.properties")
            ?: error("Could not find application.properties")
        load(stream)
    }

    val username: String = props.getProperty("username")
    val password: String = props.getProperty("password")
    val pageUrl: String = "https://top4fitness.hu/"
}