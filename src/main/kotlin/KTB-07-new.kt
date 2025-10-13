import java.io.File

const val CORRECT_ANSWER_NUMBER = 3

fun main() {
    val dictionary = loadDictionary()
    val totalCount = dictionary.size
    while (true) {
        println("1 - Учить слова\n2 - Статистика\n0 - Выход\n")
        val input = readln()
        when (input.toInt()) {
            1 -> {
                val notLearnedList = mutableListOf<Word>()
                for (i in dictionary) {
                    if (i.correctAnswerCount != CORRECT_ANSWER_NUMBER) {
                        notLearnedList.add(i)
                    }
                }
                if (notLearnedList.isEmpty()) {
                    println("Все слова в словаре выучены")
                    return
                }
                val questionWords = notLearnedList.take(4).shuffled()
                val correctAnswer = questionWords.random().original
                var count = 1
                println("$correctAnswer: ${questionWords.map { "\n${count++} - ${it.translate}" }.joinToString("")}")
                readln()
            }

            2 -> {
                if (totalCount != 0) {
                    val learnedCount = dictionary.count { it.correctAnswerCount >= CORRECT_ANSWER_NUMBER }
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

data class Word(
    val original: String, val translate: String, var correctAnswerCount: Int
)