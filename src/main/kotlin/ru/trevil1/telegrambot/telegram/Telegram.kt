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
    @SerialName("text") val text: String? = null,
    @SerialName("chat") val chat: Chat?,
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

    val trainers = HashMap<Long, LearnWordsTrainer>()

    while (true) {
        Thread.sleep(2000)
        val responseString: String = services.getUpdates(lastUpdateId)
        println(responseString)

        val response: Response = services.json.decodeFromString(responseString)
        if (response.result.isEmpty()) continue
        val sortedUpdates = response.result.sortedBy { it.updateId }
        sortedUpdates.forEach { handleUpdates(it, services.json, services, trainers) }
        lastUpdateId = sortedUpdates.last().updateId + 1
    }
}

fun handleUpdates(
    update: Update,
    json: Json,
    services: TelegramBotService,
    trainers: HashMap<Long, LearnWordsTrainer>
) {
    val chatId: Long = update.message?.chat?.id ?: update.callbackQuery?.message?.chat?.id ?: return
    val text = update.message?.text
    val data = update.callbackQuery?.data

    val trainer = trainers.getOrPut(chatId) { LearnWordsTrainer("$chatId.txt") }

    when {
        text?.lowercase() == START -> {
            services.sendMenu(chatId)
        }

        data?.lowercase() == LEARN_WORD_CLICKED -> {
            checkNextQuestionAndSend(trainer, services, chatId)
        }

        data?.lowercase() == STATISTIC_CLICKED -> {
            services.sendMessage(
                chatId,
                "Выучено слов ${trainer.getStatistics().learnedCount} из ${trainer.getStatistics().total} | ${trainer.getStatistics().percent}%"
            )
        }

        data?.startsWith(CALLBACK_DATA_ANSWER_PREFIX) == true -> {
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

        data?.lowercase() == RESET_CLICKED -> {
            trainer.resetProgress()
            services.sendMessage(chatId, "Прогресс сброшен")
        }
    }
}

fun checkNextQuestionAndSend(
    trainer: LearnWordsTrainer, telegramBotService: TelegramBotService, chatId: Long?
) {
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

