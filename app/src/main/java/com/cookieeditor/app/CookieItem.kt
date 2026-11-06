package com.cookieeditor.app

data class CookieItem(
    val name: String,
    val value: String,
    val domain: String,
    val path: String = "/"
)
