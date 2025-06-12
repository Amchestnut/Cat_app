package com.example.cat_app.features.quiz.domain

data class IntruderTemperamentQuestion(
    override val id: String,
    override val questionText: String = "Izbaci uljeza!",
    override val imageUrl: String,
    override val choices: List<String>,
    override val correctChoice: String
) : Question
