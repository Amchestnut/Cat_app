package com.example.cat_app.quiz_package.questions

// koja je ovo rasa macke?  image + 4 imena
data class ImageToNameQuestion(
    override val id: String,
    val imageUrl: String,
    override val choices: List<String>,
    private val correctChoice: String,
) : Question {
    override fun score(answer: String) = if (answer == correctChoice) 5 else 0
}
