package org.example

import java.io.File

fun main() {
    val wordsFile: File = File("words.txt")
    wordsFile.createNewFile()
    val dictionary = mutableListOf<Word>()
    val lines: List<String> = wordsFile.readLines()

    for (line in lines) {
        val line = line.split("|")
        val word = Word(original = line[0], translate = line[1], correctAnswer = line[2])
        if (line[2] == "1") {
            word.correctAnswer = "1"
        } else word.correctAnswer = "0"
        dictionary.add(word)
    }
    println(dictionary)
}

data class Word(
    val original: String,
    val translate: String,
    var correctAnswer: String
)