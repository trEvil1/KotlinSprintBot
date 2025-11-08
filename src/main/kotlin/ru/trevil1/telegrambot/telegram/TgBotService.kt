package ru.trevil1.telegrambot.telegram

import ru.trevil1.telegrambot.trainer.LearnWordsTrainer
import ru.trevil1.telegrambot.trainer.model.Question
import ru.trevil1.telegrambot.trainer.model.Word
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
const val EXIT_CALLBACK = "exit_callBack"
const val CALLBACK_DATA_ANSWER_PREFIX = "answer_"

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
                                "callback_data": "$LEARN_WORD_CLICKED"
                            },
                            {
                                "text": "Статистика",
                                "callback_data": "$STATISTIC_CLICKED"
                            }
                        ],
                        [
                            {
                                "text": "Выход",
                                "callback_data": "$EXIT_CALLBACK"
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
        val urlSendMessage = "$TELEGRAM_API_URL$botToken/sendMessage?chat_id=$chatId&text=$encoded"
        val request = HttpRequest.newBuilder().uri(URI.create(urlSendMessage)).build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        return response.body()
    }

    fun sendQuestion(chatId: Int, question: Question): String {

        val answers = question.variants.take(4)

        println(answers)
        val answer = answers.mapIndexed { index, word ->
            """[{
            "text": "${word.translate}",
            "callback_data": "${CALLBACK_DATA_ANSWER_PREFIX}${index + 1}"
        }]"""
        }.joinToString()
        val sendQuestionBody =
            """
            {
                "chat_id": $chatId,
                 "text": "${answers.random().original}",
                 "reply_markup": {
                     "inline_keyboard": [
                        $answer                
                     ]
                 }     
            }
        """.trimIndent()
        println(sendQuestionBody)
        val urlSendQuestion = "$TELEGRAM_API_URL$botToken/sendMessage"
        val request = HttpRequest.newBuilder().uri(URI.create(urlSendQuestion))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(sendQuestionBody))
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        return response.body()
    }

    fun checkNextQuestionAndSend(
        trainer: LearnWordsTrainer,
        telegramBotService: TelegramBotService,
        chatId: Int
    ): String {
        val encoded = URLEncoder.encode(
            "Все слова выучены",
            StandardCharsets.UTF_8
        )
        if (trainer.getNextQuestion() == null) {
            val urlSendDone = "$TELEGRAM_API_URL$botToken/sendMessage?chat_id=$chatId&text=$encoded"
            val request = HttpRequest.newBuilder().uri(URI.create(urlSendDone)).build()
            val response = client.send(request, HttpResponse.BodyHandlers.ofString())

            return response.body()
        } else return telegramBotService.sendQuestion(
            chatId,
            Question(trainer.loadDictionary(), trainer.getNextQuestion()?.correctAnswer!!)
        )
    }
}