package ru.trevil1.telegrambot.telegram

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import ru.trevil1.telegrambot.trainer.LearnWordsTrainer

@Serializable
data class Update(
    @SerialName("update_id") val updateId: Long,
    @SerialName("message") val message: Message? = null,
    @SerialName("callback_query") val callbackQuery: CallbackQuery? = null,
)

@Serializable
data class Response(
    @SerialName("result") val result: List<Update>
)

@Serializable
data class Message(
    @SerialName("text") val text: String,
    @SerialName("chat") val chat: Chat,
)

@Serializable
data class CallbackQuery(
    @SerialName("data") val data: String? = null,
    @SerialName("message") val message: Message? = null,
)

@Serializable
data class Chat(
    @SerialName("id") val id: Long
)


fun main(args: Array<String>) {

    val services = TelegramBotService(args[0])

    var lastUpdateId = 0L


    val trainer = LearnWordsTrainer()
    val statistic = trainer.getStatistics()
    while (true) {
        Thread.sleep(2000)
        val responseString: String = services.getUpdates(lastUpdateId)
        println(responseString)

        val response: Response = services.json.decodeFromString(responseString)
        val updates = response.result
        val firstUpdate = updates.firstOrNull() ?: continue
        val updateId = firstUpdate.updateId
        lastUpdateId = updateId + 1


        val chatId: Long? = firstUpdate.message?.chat?.id ?: firstUpdate.callbackQuery?.message?.chat?.id
        val text = firstUpdate.message?.text
        val data = firstUpdate.callbackQuery?.data

        when {
            text?.lowercase() == START -> {
                services.sendMenu(chatId)
            }

            data?.lowercase() == LEARN_WORD_CLICKED -> {
                checkNextQuestionAndSend(trainer, services, chatId)
            }

            data?.lowercase() == STATISTIC_CLICKED -> {
                services.sendMessage(
                    chatId, "Выучено слов ${statistic.learnedCount} из ${statistic.total} | ${statistic.percent}%"
                )
            }

            data?.startsWith(CALLBACK_DATA_ANSWER_PREFIX) == true -> {
                println(responseString)
                val userAnswerIndex = data.substringAfter(CALLBACK_DATA_ANSWER_PREFIX).toInt()
                println(trainer.question?.correctAnswer)
                val isCorrect = trainer.checkAnswer(userAnswerIndex)
                if (isCorrect) {
                    services.sendMessage(chatId, "Правильно!")
                } else {
                    val correct = trainer.question?.correctAnswer?.original
                    val translate = trainer.question?.correctAnswer?.translate
                    services.sendMessage(chatId, "Неправильно! $correct - это $translate")
                }
                checkNextQuestionAndSend(trainer, services, chatId)
            }
        }
    }
}

fun checkNextQuestionAndSend(
    trainer: LearnWordsTrainer, telegramBotService: TelegramBotService, chatId: Long?
) {
    val json = Json {
        ignoreUnknownKeys = true
    }
    val question = trainer.generateAndGetNextQuestion()
    if (question == null) {
        telegramBotService.sendMessage(
            chatId = chatId, message = "Все слова выучены",
        )
    } else {
        telegramBotService.sendQuestion(
            chatId = chatId, question = question,
        )
    }
}

