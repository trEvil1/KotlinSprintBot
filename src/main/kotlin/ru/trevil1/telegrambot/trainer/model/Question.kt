package ru.trevil1.telegrambot.trainer.model

import ru.trevil1.telegrambot.trainer.model.Word

data class Question(
    val variants: List<Word>,
    val correctAnswer: Word
)
