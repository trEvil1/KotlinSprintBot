import java.io.File

fun main() {
    val dictionary = loadDictionary()
    val totalCount = dictionary.lastIndex + 1
    while (true) {
        println("1 - Учить слова\n2 - Статистика\n0 - Выход")
        val input = readln()
        when (input.toInt()) {
            1 -> println("Учить слова")
            2 ->
            {
                val learnedCount = (dictionary.filter { it.correctAnswerCount >= 3 }).lastIndex + 1
                val percent = ((learnedCount.toDouble() / totalCount) * 100).toInt()
                println("Выучено $learnedCount из $totalCount | $percent%\n")
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
        val word = Word(original = parts[0], translate = parts[1], parts[2].toInt())
        dictionary.add(word)
    }
    return dictionary
}

data class Word(
    val original: String,
    val translate: String,
    var correctAnswerCount: Int
)
