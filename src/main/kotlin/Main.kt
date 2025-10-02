package org.example

import java.io.File

fun main() {
    val wordsFile: File = File("words.txt")
    wordsFile.createNewFile()
    wordsFile.writeText("hello привет\n")
    wordsFile.appendText("cat кошка\n")
    wordsFile.appendText("dog собака\n")
    println(wordsFile.readLines().map { it + "\n" }.joinToString(""))
}