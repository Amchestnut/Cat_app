package com.example.cat_app.features.quiz.domain

import com.example.cat_app.features.allspecies.domain.Breed
import kotlin.random.Random

object QuestionFactory {

    // Returns 20 questions so ViewModel can use: shuffled().take(20)
    fun fromBreeds(breeds: List<Breed>): List<Question> {
        if (breeds.size < 4)
            return emptyList()

        val rng = Random(System.currentTimeMillis())
        val shuffled = breeds.shuffled(rng)

        // 1) Koja je rasa macke sa slike?
        val breedQuestions = shuffled.map { breed ->
            val wrong = breeds.filterNot { it.id == breed.id }
                .shuffled(rng)
                .take(3)
                .map { it.name }

            val choices = (wrong + breed.name).shuffled(rng)

            ImageToNameQuestion(
                id           = "breed_${breed.id}",
                imageUrl     = breed.imageUrl ?: "",
                choices      = choices,
                correctChoice= breed.name
            )
        }

        // 2) Izbaci uljeza medju temperamentima
        val intruderQuestions = shuffled.map { breed ->
            // temperameni koje rasa ima
            val temps = breed.temperament
                ?.split(",")
                ?.map { it.trim() }
                ?: emptyList()

            // 3 nasumična iz pravih temperamenata
            val actuals = temps.shuffled(rng).take(3)

            // svi ostali temperamenti iz drugih rasa
            val others = breeds.flatMap {
                it.temperament
                    ?.split(",")
                    ?.map(String::trim)
                    ?: emptyList()
            }
                .distinct()
                .filterNot { temps.contains(it) }

            // izabere jednog „uljeza“
            val intruder = others.shuffled(rng).first()
            val choices = (actuals + intruder).shuffled(rng)

            IntruderTemperamentQuestion(
                id            = "temp_${breed.id}",
                imageUrl      = breed.imageUrl ?: "",
                choices       = choices,
                correctChoice = intruder
            )
        }

        return (breedQuestions + intruderQuestions)
    }
}