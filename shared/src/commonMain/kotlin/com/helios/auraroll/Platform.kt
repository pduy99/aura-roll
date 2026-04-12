package com.helios.auraroll

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform