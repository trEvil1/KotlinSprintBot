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

        if (text?.lowercase() == "hello") {
            services.sendMessage(chatId, "Hello")
        }
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

        println(updates)
        println(lastUpdateId)
        println(text ?: "")
    }
}

