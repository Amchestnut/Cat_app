package com.example.cat_app.quiz_package.questions

// Iz koje zemlje potice rasa <name> ?
data class OriginQuestion(
    override val id: String,
    val breedName: String,
    override val choices: List<String>,          // zemlje
    private val correctChoice: String,
) : Question {
    override fun score(answer: String) = if (answer == correctChoice) 5 else 0
}
