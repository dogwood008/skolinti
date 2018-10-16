package com.dogwood008.kotlinrxsample

class Slack (text: String, channel: String? = null,
             username: String? = null, icon_emoji: String? = null){
    val text: String = text
    val channel: String = channel ?: ""
    val username: String = username ?: "webhookbot"
    val icon_emoji: String = icon_emoji ?: ":ghost:"
}
