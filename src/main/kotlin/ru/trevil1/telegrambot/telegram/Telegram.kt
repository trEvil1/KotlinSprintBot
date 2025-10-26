package ru.trevil1.telegrambot.telegram

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

private const val TELEGRAM_API_URL = "https://api.telegram.org/bot"

fun main(args: Array<String>) {

    val botToken = args[0]
    var lastUpdateId = 0
    val lastUpdateIdRegex = "\"update_id\":\\s*(\\d+),".toRegex()
    val messageTextRegex = "\"text\":\"(.+?)\"".toRegex()

    while (true) {
        val updates: String = getUpdates(botToken, lastUpdateId + 1)
        Thread.sleep(2000)
        lastUpdateId = lastUpdateIdRegex.find(updates)?.groups?.get(1)?.value?.toIntOrNull() ?: continue
        val text = messageTextRegex.find(updates)?.groups?.get(1)?.value
        println(lastUpdateId)
        println(text ?: "")
    }
}

fun getUpdates(botToken: String, updateId: Int): String {
    val urlGetUpdate = "$TELEGRAM_API_URL$botToken/getUpdates?offset=$updateId"
    val client = HttpClient.newBuilder().build()
    val requestUpdate = HttpRequest.newBuilder().uri(URI.create(urlGetUpdate)).build()
    val responseUpdate = client.send(requestUpdate, HttpResponse.BodyHandlers.ofString())

    return responseUpdate.body()
}