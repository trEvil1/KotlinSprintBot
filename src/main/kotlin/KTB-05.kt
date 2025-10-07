package org.example

import java.io.File

fun main() {
    val dictionary = loadDictionary()
    while (true) {
        val input = readln()
        when (input.toInt()) {
            1 -> println("Учить слова")
            2 -> println("Статистика")
            0 -> break
            else -> println("Введите число 1, 2 или 0")
        }
    }
}

fun loadDictionary(): List<Word> {
    val wordsFile: File = File("words.txt")
    val dictionary = mutableListOf<Word>()
    val lines: List<String> = wordsFile.readLines()

    for (line in lines) {
        val line = line.split("|")
        val word = Word(original = line[0], translate = line[1], line[2].toInt())
        dictionary.add(word)
    }
    return dictionary
}
