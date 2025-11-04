package ru.trevil1.telegrambot.telegram

import java.net.http.HttpClient

fun main(args: Array<String>) {

    val services = TelegramBotService(args[0])

    var lastUpdateId = 0

    val lastUpdateIdRegex = "\"update_id\":\\s*(\\d+),".toRegex()
    val messageTextRegex = "\"text\":\"(.+?)\"".toRegex()
    val chatIdRegex = ",\"chat\":\\{\"id\":(.+?),".toRegex()

    while (true) {

        val updates: String = services.getUpdates(lastUpdateId + 1)
        Thread.sleep(2000)
        lastUpdateId = lastUpdateIdRegex.find(updates)?.groups?.get(1)?.value?.toIntOrNull() ?: continue
        val chatId = chatIdRegex.find(updates)?.groups?.get(1)?.value?.toIntOrNull() ?: continue
        val text = messageTextRegex.find(updates)?.groups?.get(1)?.value

        if (text == "Hello") {
            services.sendMessage(chatId, text)
        }

        println(updates)
        println(lastUpdateId)
        println(text ?: "")
    }
}
