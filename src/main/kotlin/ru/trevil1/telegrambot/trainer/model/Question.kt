package ru.trevil1.telegrambot.trainer.model

data class Question(
    val variants: List<Word>,
    val correctAnswer: Word
)
