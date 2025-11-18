package ru.trevil1.telegrambot.telegram

import ru.trevil1.telegrambot.trainer.LearnWordsTrainer

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
                    "Выучено слов ${statistic.learnedCount} из ${statistic.total} | ${statistic.percent}%"
                )
            }

            data?.startsWith(CALLBACK_DATA_ANSWER_PREFIX) == true -> {
                val userAnswerIndex = data.substringAfter(CALLBACK_DATA_ANSWER_PREFIX).toInt() - 1
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
    trainer: LearnWordsTrainer,
    telegramBotService: TelegramBotService,
    chatId: Int
) {
    val question = trainer.generateAndGetNextQuestion()
    if (question == null) {
        telegramBotService.sendMessage(
            chatId = chatId,
            message = "Все слова выучены"
        )
    } else {
        telegramBotService.sendQuestion(
            chatId = chatId,
            question = question
        )
    }
}

