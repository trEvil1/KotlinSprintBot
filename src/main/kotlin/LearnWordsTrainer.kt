import java.io.File

data class Statistics(
    val learnedCount: Int,
    val percent: Int,
    val total: Int
)

data class Question(
    val variants: List<Word>,
    val correctAnswer: Word
)


class LearnWordsTrainer {
    private var question: Question? = null
    val dictionary = loadDictionary()
    fun loadDictionary(): List<Word> {
        val wordsFile: File = File("words.txt")
        val dictionary = mutableListOf<Word>()
        val lines: List<String> = wordsFile.readLines()

        for (line in lines) {
            val parts = line.split("|")
            val word =
                Word(
                    original = parts[0],
                    translate = parts[1],
                    correctAnswerCount = parts.getOrNull(2)?.toIntOrNull() ?: 0
                )
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

    fun getStatistics(): Statistics {
        val total = dictionary.size
        val learnedCount = dictionary.count { it.correctAnswerCount >= CORRECT_ANSWER_NUMBER }
        val percent = ((learnedCount.toDouble() / total) * 100).toInt()
        return Statistics(learnedCount, total, percent)
    }

    fun getNextQuestion(): Question? {
        val notLearnedList = dictionary.filter { it.correctAnswerCount < CORRECT_ANSWER_NUMBER }
        if (notLearnedList.isEmpty()) return null
        val questionWords = notLearnedList.take(COUNT_OF_ANSWERS).shuffled()
        val correctWord = questionWords.random()
        question = Question(
            variants = questionWords,
            correctAnswer = correctWord
        )
        return question
    }

    fun checkAnswer(userAnswerIndex: Int?): Boolean {
        return question?.let {
            val correctAnswerId = it.variants.indexOf(it.correctAnswer)
            if (userAnswerIndex == correctAnswerId) {
                it.correctAnswer.correctAnswerCount++
                saveDictionary(dictionary)
                println("Правильно!")
                true
            } else {
                false
            }
        }?:false

    }
}