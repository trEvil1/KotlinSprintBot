package org.example

import java.io.File

fun main() {
    val wordsFile: File = File("words.txt")
    wordsFile.createNewFile()
    val dictionary = mutableListOf<Word>()
    val lines: List<String> = wordsFile.readLines()
    var answer = 0
    var word = Word("", "", 0)

    for (line in lines) {
        val line = line.split("|")
        word = word.copy(original = line[0], translate = line[1])
        println(line[0] + " перевод")
        val translate = readln()
        if (translate == word.translate) {
            word.correctAnswer = 1
            answer += 1
        } else word.correctAnswer = 0
        dictionary.add(word)
    }
    println(dictionary)
}

data class Word(
    val original: String,
    val translate: String?,
    var correctAnswer: Int
)