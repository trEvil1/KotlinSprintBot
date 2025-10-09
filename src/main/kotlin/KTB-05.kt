package org.example

import java.io.File

fun main() {
    val dictionary = loadDictionary()
    while (true) {
        println("1 - Учить слова\n2 - Статистика\n0 - Выход")
        val input = readln()
        when (input.toIntOrNull()) {
            1 -> println("Учить слова")
            2 -> println("Статистика")
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
