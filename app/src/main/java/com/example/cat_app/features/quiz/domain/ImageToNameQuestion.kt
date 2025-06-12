package com.example.cat_app.features.quiz.domain

// koja je ovo rasa macke?  image + 4 imena
data class ImageToNameQuestion(
    override val id: String,
    override val questionText: String = "Koja je rasa mačke sa slike?",
    override val imageUrl: String,
    override val choices: List<String>,
    override val correctChoice: String
) : Question
