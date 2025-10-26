package ru.trevil1.telegrambot.console

import ru.trevil1.telegrambot.trainer.COUNT_OF_ANSWERS
import ru.trevil1.telegrambot.trainer.LearnWordsTrainer

fun main() {

    val trainer = try {
        LearnWordsTrainer()
    } catch (e: Exception) {
        println("Невозможно загрузить словарь")
        return
    }

    while (true) {
        println("1 - Учить слова\n2 - Статистика\n0 - Выход\n")
        val input = readln()

        when (input.toIntOrNull()) {
            1 -> {
                while (true) {
                    val question = trainer.getNextQuestion()

                    if (question == null) {
                        println("Все слова в словаре выучены")
                        break
                    }

                    println(
                        question.variants.joinToString(
                            prefix = "${question.correctAnswer.original}:",
                            separator = "\n",
                            postfix = "\n------------\n0 - Меню"
                        ) { "\n${question.variants.indexOf(it) + 1} - ${it.translate}" }
                    )

                    val correctAnswerId = readln().toIntOrNull()
                    if (correctAnswerId == 0) break

                    if (correctAnswerId in 0..COUNT_OF_ANSWERS) {
                        if (trainer.checkAnswer(correctAnswerId?.minus(1))) {
                            println("Правильно!")
                        } else println(
                            "Не правильно " +
                                    "${question.correctAnswer.original} " + "это ${question.correctAnswer.translate}"
                        )
                    } else println("Введите число от 0 до ${COUNT_OF_ANSWERS}")
                }
            }

            2 -> {
                val statistics = trainer.getStatistics()
                println("Выучено ${statistics.learnedCount} из ${statistics.total} | ${statistics.percent}%\n")
            }

            0 -> return
            else -> println("Введите число 1, 2 или 0")
        }
    }
}
