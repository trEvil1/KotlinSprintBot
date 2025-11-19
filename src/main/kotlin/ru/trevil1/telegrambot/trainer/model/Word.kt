package ru.trevil1.telegrambot.trainer.model

import kotlinx.serialization.Serializable

@Serializable
data class Word(
    val original: String,
    val translate: String,
    var correctAnswerCount: Int
) {
    override fun toString(): String {
        return "$original|$translate|$correctAnswerCount"
    }
}
