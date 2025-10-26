package ru.trevil1.telegrambot.trainer.model

data class Word(
    val original: String,
    val translate: String,
    var correctAnswerCount: Int
) {
    override fun toString(): String {
        return "$original|$translate|$correctAnswerCount"
    }
}
