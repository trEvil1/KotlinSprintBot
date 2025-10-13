import java.io.File

const val CORRECT_ANSWER_NUMBER = 3
const val COUNT_OF_ANSWERS = 4

fun main() {
    val dictionary = loadDictionary()

    while (true) {
        println("1 - Учить слова\n2 - Статистика\n0 - Выход\n")
        val input = readln()
        val totalCount = dictionary.size
        when (input.toInt()) {
            1 -> {
                val notLearnedList = dictionary.filter { it.correctAnswerCount < CORRECT_ANSWER_NUMBER }
                if (notLearnedList.isEmpty()) {
                    println("Все слова в словаре выучены")
                    return
                }
                val questionWords = notLearnedList.take(COUNT_OF_ANSWERS).shuffled()
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
    val original: String,
    val translate: String,
    var correctAnswerCount: Int
)