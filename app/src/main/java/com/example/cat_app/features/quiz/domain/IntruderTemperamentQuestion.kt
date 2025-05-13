package com.example.cat_app.features.quiz.domain

data class IntruderTemperamentQuestion(
    override val id: String,
    override val questionText: String = "Izbaci uljeza!",
    override val imageUrl: String,
    override val choices: List<String>,
    private val correctChoice: String
) : Question {
    override fun score(answer: String) = if (answer == correctChoice) 5 else 0
}
