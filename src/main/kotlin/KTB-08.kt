import java.io.File

const val COUNT_OF_ANSWERS = 4
const val CORRECT_ANSWER_NUMBER = 3

fun main() {
    val dictionary = loadDictionary()

    while (true) {
        println("1 - Учить слова\n2 - Статистика\n0 - Выход\n")
        val totalCount = dictionary.size
        val input = readln()
        val notLearnedList = dictionary.filter { it.correctAnswerCount < CORRECT_ANSWER_NUMBER }
        when (input.toIntOrNull()) {
            1 -> {
                if (notLearnedList.isEmpty()) {
                    println("Все слова в словаре выучены")
                    return
                }
                var questionWords = notLearnedList.take(COUNT_OF_ANSWERS)
                val correctWord = questionWords.random()

                while (true) {
                    if ((questionWords.find { it.original == correctWord.original })?.correctAnswerCount == CORRECT_ANSWER_NUMBER) {
                        println("Слово выучено")
                        break
                    }

                    questionWords = questionWords.shuffled()
                    println(
                        "${correctWord.original}: ${
                            questionWords.map { "\n${questionWords.indexOf(it) + 1} - ${it.translate}" }
                                .joinToString("")
                        }\n------------\n0 - Меню"
                    )

                    val correctAnswerId = readln()
                    if (correctAnswerId.toInt() == 0) break

                    if (correctAnswerId.toIntOrNull() in 0..4) {
                        if (correctWord.original == questionWords.get(correctAnswerId.toInt() - 1).original) {
                            println("Правильно!")
                            correctWord.correctAnswerCount++
                            saveDictionary(dictionary)
                        } else println("Не правильно ${correctWord.original} это ${(questionWords.find { it.original == correctWord.original })?.translate}")
                    } else println("Введите число от 0 до 4")
                }
            }

            2 -> {
                if (totalCount != 0) {
                    val learnedCount =
                        dictionary.count { it.correctAnswerCount >= CORRECT_ANSWER_NUMBER }
                    val percent = ((learnedCount.toDouble() / totalCount) * 100).toInt()
                    println("Выучено $learnedCount из $totalCount | $percent%\n")
                } else println("Словарь пуст")
            }

            0 -> return
            else -> println("Введите число 1, 2 или 0")
        }
    }
}

fun loadDictionary(): List<Word> {
    val wordsFile: File = File("words.txt")
    val dictionary = mutableListOf<Word>()
    val lines: List<String> = wordsFile.readLines()

    for (line in lines) {
        val parts = line.split("|")
        val word =
            Word(original = parts[0], translate = parts[1], correctAnswerCount = parts.getOrNull(2)?.toIntOrNull() ?: 0)
        dictionary.add(word)
    }
    return dictionary
}

fun saveDictionary(dictionary: List<Word>): List<Word> {
    val wordsFile: File = File("words.txt")
    val fileList = dictionary.joinToString("\n")
    wordsFile.writeText(fileList)
    return dictionary
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