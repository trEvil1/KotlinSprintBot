package ru.trevil1.telegrambot.telegram

import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets

private const val TELEGRAM_API_URL = "https://api.telegram.org/bot"
const val LEARN_WORD_CLICKED = "learn_words_clicked"
const val START = "/start"
const val STATISTIC_CLICKED = "statistics_clicked"

class TelegramBotService(val botToken: String) {

    private val client = HttpClient.newBuilder().build()

    fun getUpdates(updateId: Int): String {
        val urlGetUpdate = "$TELEGRAM_API_URL$botToken/getUpdates?offset=$updateId"
        val requestUpdate = HttpRequest.newBuilder().uri(URI.create(urlGetUpdate)).build()
        val responseUpdate = client.send(requestUpdate, HttpResponse.BodyHandlers.ofString())

        return responseUpdate.body()
    }

    fun sendMenu(chatId: Int): String {

        val urlSendMessage = "$TELEGRAM_API_URL$botToken/sendMessage"
        val sendMenuBody = """
            {
                "chat_id": $chatId,
                "text": "Основное меню",
                "reply_markup": {
                    "inline_keyboard": [
                        [
                            {
                                "text": "Изучить слова",
                                "callback_data": $LEARN_WORD_CLICKED
                            },
                            {
                                "text": "Статистика",
                                "callback_data": $STATISTIC_CLICKED
                            }
                        ]
                    ]
                }
            }
                """.trimIndent()

        val request = HttpRequest.newBuilder().uri(URI.create(urlSendMessage))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(sendMenuBody))
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        return response.body()
    }

    fun sendMessage(chatId: Int, message: String): String {
        val encoded = URLEncoder.encode(
            message,
            StandardCharsets.UTF_8
        )
        println(encoded)
        val urlSendMessage = "$TELEGRAM_API_URL$botToken/sendMessage?chat_id=$chatId&text=$encoded"
        val request = HttpRequest.newBuilder().uri(URI.create(urlSendMessage)).build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        return response.body()
    }
}