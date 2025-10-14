const val COUNT_OF_ANSWERS = 4
const val CORRECT_ANSWER_NUMBER = 3

fun main() {

    val trainer = LearnWordsTrainer()

    while (true) {
        println("1 - Учить слова\n2 - Статистика\n0 - Выход\n")
        val totalCount = trainer.dictionary.size
        val input = readln()

        when (input.toIntOrNull()) {
            1 -> {
                while (true) {
                    val question = trainer.getNextQuestion()

                    if (question == null) {
                        println("Все слова в словаре выучены")
                        break
                    }

                    if ((question.variants.find { it.original == question.correctAnswer.original })?.correctAnswerCount == CORRECT_ANSWER_NUMBER) {
                        println("Слово выучено")
                        break
                    }

                    println(
                        "${question.correctAnswer.original}: ${
                            question.variants.map { "\n${question.variants.indexOf(it) + 1} - ${it.translate}" }
                                .joinToString("")
                        }\n------------\n0 - Меню"
                    )

                    val correctAnswerId = readln().toIntOrNull()
                    if (correctAnswerId == 0) break

                    if (correctAnswerId in 0..4) {
                        if (trainer.checkAnswer(correctAnswerId?.minus(1))) {
                            println("Правильно!")
                        } else println("Не правильно ${question.correctAnswer.original} это ${(question.variants.find { it.original == question.correctAnswer.original })?.translate}")
                    } else println("Введите число от 0 до 4")
                }
            }

            2 -> {
                if (totalCount != 0) {
                    val statistics = trainer.getStatistics()
                    println("Выучено ${statistics.learnedCount} из ${statistics.total} | ${statistics.percent}%\n")
                } else println("Словарь пуст")
            }

            0 -> return
            else -> println("Введите число 1, 2 или 0")
        }
    }
}


data class Word(
    val original: String,
    val translate: String,
    var correctAnswerCount: Int
) {
    override fun toString(): String {
        return "$original|$translate|$correctAnswerCount"
    }
}