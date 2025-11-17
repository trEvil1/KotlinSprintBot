package ru.trevil1.telegrambot.telegram

import ru.trevil1.telegrambot.trainer.LearnWordsTrainer
import ru.trevil1.telegrambot.trainer.model.Question

fun main(args: Array<String>) {

    val services = TelegramBotService(args[0])

    var lastUpdateId = 0

    val lastUpdateIdRegex = "\"update_id\":\\s*(\\d+),".toRegex()
    val messageTextRegex = "\"text\":\"(.+?)\"".toRegex()
    val chatIdRegex = ",\"chat\":\\{\"id\":(.+?),".toRegex()
    val dataRegex = "\"data\":\"(.+?)\"".toRegex()

    val trainer = LearnWordsTrainer()

    while (true) {
        val statistic = trainer.getStatistics()
        val updates: String = services.getUpdates(lastUpdateId + 1)
        Thread.sleep(2000)
        lastUpdateId = lastUpdateIdRegex.find(updates)?.groups?.get(1)?.value?.toIntOrNull() ?: continue
        val chatId = chatIdRegex.find(updates)?.groups?.get(1)?.value?.toIntOrNull() ?: continue
        val text = messageTextRegex.find(updates)?.groups?.get(1)?.value
        val data = dataRegex.find(updates)?.groups?.get(1)?.value

        if (text?.lowercase() == START) {
            services.sendMenu(chatId)
        }
        if (data?.lowercase() == LEARN_WORD_CLICKED) {
            services.checkNextQuestionAndSend(trainer, services, chatId)
        }
        if (data?.lowercase() == STATISTIC_CLICKED) {
            services.sendMessage(
                chatId,
                "Выучено слов ${statistic.learnedCount} из ${statistic.total} | ${statistic.percent}%"
            )
        }

        if (data?.startsWith(CALLBACK_DATA_ANSWER_PREFIX) == true) {
            val userAnswerIndex = data.substringAfter(CALLBACK_DATA_ANSWER_PREFIX).toInt() - 1
            println(trainer.question?.correctAnswer)
            if (trainer.question != null) {
                val isCorrect = trainer.checkAnswer(userAnswerIndex)

                if (isCorrect) {
                    services.sendMessage(chatId, "Правильно!")
                } else {
                    val correct = trainer.question?.correctAnswer?.original
                    val translate = trainer.question?.correctAnswer?.translate
                    services.sendMessage(chatId, "Неправильно! $correct - это $translate")
                }
                services.checkNextQuestionAndSend(trainer, services, chatId)
            }
        }
    }
}

