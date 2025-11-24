package ru.trevil1.telegrambot.telegram

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import ru.trevil1.telegrambot.trainer.model.Question
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@Serializable
data class SendMessageRequest(
    @SerialName("chat_id") val chatId: Long?,
    @SerialName("reply_markup") val replyMarkup: ReplyMarkup? = null,
    @SerialName("text") val text: String
)

@Serializable
data class ReplyMarkup(
    @SerialName("inline_keyboard") val inlineKeyboard: List<List<InlineKeyBoard>>,
)

@Serializable
data class InlineKeyBoard(
    @SerialName("callback_data") val callbackData: String,
    @SerialName("text") val text: String,
)

private const val TELEGRAM_API_URL = "https://api.telegram.org/bot"
const val LEARN_WORD_CLICKED = "learn_words_clicked"
const val START = "/start"
const val STATISTIC_CLICKED = "statistics_clicked"
const val EXIT_CALLBACK = "exit_callBack"
const val CALLBACK_DATA_ANSWER_PREFIX = "answer_"
const val RESET_CLICKED = "reset_clicked"

class TelegramBotService(val botToken: String) {
    private val client = HttpClient.newBuilder().build()
    val json = Json {
        ignoreUnknownKeys = true
    }

    fun getUpdates(updateId: Long): String {
        val urlGetUpdate = "$TELEGRAM_API_URL$botToken/getUpdates?offset=$updateId"
        val requestUpdate = HttpRequest.newBuilder().uri(URI.create(urlGetUpdate)).build()
        val responseUpdate = client.send(requestUpdate, HttpResponse.BodyHandlers.ofString())

        return responseUpdate.body()
    }

    fun sendMenu(chatId: Long?): String {

        val urlSendMessage = "$TELEGRAM_API_URL$botToken/sendMessage"

        val requestBody = SendMessageRequest(
            chatId = chatId,
            text = "Основное меню",
            replyMarkup = ReplyMarkup(
                listOf(
                    listOf(
                        InlineKeyBoard(callbackData = LEARN_WORD_CLICKED, text = "Изучать слова"),
                        InlineKeyBoard(callbackData = STATISTIC_CLICKED, text = "Статистика"),
                    ),
                    listOf(
                        InlineKeyBoard(callbackData = RESET_CLICKED, text = "Сбросить прогресс"),
                    )
                )
            )
        )
        val requestBodyString = json.encodeToString(requestBody)

        val request = HttpRequest.newBuilder().uri(URI.create(urlSendMessage))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBodyString))
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }

    fun sendMessage(chatId: Long?, message: String): String {
        val urlSendMessage = "$TELEGRAM_API_URL$botToken/sendMessage"
        val requestBody = SendMessageRequest(
            chatId = chatId,
            text = message,
        )
        val requestBodyString = json.encodeToString(requestBody)
        val request = HttpRequest.newBuilder().uri(URI.create(urlSendMessage))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBodyString))
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }

    fun sendQuestion(chatId: Long?, question: Question): String {
        val requestBody = SendMessageRequest(
            chatId = chatId,
            text = question.correctAnswer.original,
            replyMarkup = ReplyMarkup(
                listOf(question.variants.mapIndexed { index, word ->
                    InlineKeyBoard(
                        text = word.translate, callbackData = "$CALLBACK_DATA_ANSWER_PREFIX$index"
                    )
                })
            )
        )
        val requestBodyString = json.encodeToString(requestBody)

        val urlSendQuestion = "$TELEGRAM_API_URL$botToken/sendMessage"
        val request = HttpRequest.newBuilder().uri(URI.create(urlSendQuestion))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBodyString))
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        return response.body()
    }
}
