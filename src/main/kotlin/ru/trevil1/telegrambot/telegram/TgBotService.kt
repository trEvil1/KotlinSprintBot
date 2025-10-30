package ru.trevil1.telegrambot.telegram

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

private const val TELEGRAM_API_URL = "https://api.telegram.org/bot"

class TelegramBotService(val botToken: String) {

    private val client = HttpClient.newBuilder().build()

    fun getUpdates(updateId: Int): String {
        val urlGetUpdate = "$TELEGRAM_API_URL$botToken/getUpdates?offset=$updateId"
        val requestUpdate = HttpRequest.newBuilder().uri(URI.create(urlGetUpdate)).build()
        val responseUpdate = client.send(requestUpdate, HttpResponse.BodyHandlers.ofString())

        return responseUpdate.body()
    }

    fun sendMessage(chatId: Int, message: String): String {
        val hello = ",\"text\":\"Hello\""
        if (hello in message) {
            val text = "Hello"
            val urlSendMessage = "$TELEGRAM_API_URL$botToken/sendMessage?chat_id=$chatId&text=$text"
            val request = HttpRequest.newBuilder().uri(URI.create(urlSendMessage)).build()
            val response = client.send(request, HttpResponse.BodyHandlers.ofString())

            return response.body()
        } else {
            return ""
        }
    }
}