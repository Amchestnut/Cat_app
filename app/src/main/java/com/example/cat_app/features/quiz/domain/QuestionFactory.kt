package com.example.cat_app.features.quiz.domain

import com.example.cat_app.features.allspecies.domain.Breed
import kotlin.random.Random

object QuestionFactory {

    // Returns 20 questions so ViewModel can use: shuffled().take(20)
    fun fromBreeds(breeds: List<Breed>): List<Question> {
        val breedWithImages = breeds.filterNot { it.imageUrl.isNullOrBlank() }

        if (breedWithImages.size < 4)
            return emptyList()

        val rng = Random(System.currentTimeMillis())
        val shuffledBreeds = breedWithImages.shuffled(rng)

        // 1) Koja je rasa macke sa slike?
        val breedQuestions = shuffledBreeds.map { breed ->
            val wrong = breeds.filterNot { it.id == breed.id }
                .shuffled(rng)
                .take(3)
                .map { it.name }

            val allChoices = (wrong + breed.name).shuffled(rng)

            ImageToNameQuestion(
                id           = "breed_${breed.id}",
                imageUrl     = breed.imageUrl ?: "",
                choices      = allChoices,
                correctChoice= breed.name
            )
        }

        // 2) Izbaci uljeza medju temperamentima
        val intruderQuestions = shuffledBreeds.map { breed ->
            // temperameni koje ova rasa ima
            val breedTemperament = breed.temperament
                ?.split(",")
                ?.map { it.trim() }
                ?: emptyList()

            // svi ostali temperamenti iz drugih rasa
            val allOtherTemperaments = breeds.flatMap {
                it.temperament
                    ?.split(",")
                    ?.map(String::trim)
                    ?: emptyList()
            }
                .distinct()
                .filterNot { breedTemperament.contains(it) }

            // 3 nasumična temperamenta iz odabrane rase
            val actuals = breedTemperament
                .shuffled(rng)
                .take(3)
                .let { list ->
                    if (list.size < 3)
                        list + allOtherTemperaments.shuffled(rng).filterNot(list::contains).take(3 - list.size)
                    else
                        list
                }

            // 1 nasumican „uljez“ temperament
            val intruder = allOtherTemperaments.shuffled(rng).first()
            val finalChoices = (actuals + intruder).shuffled(rng)

            IntruderTemperamentQuestion(
                id            = "temp_${breed.id}",
                imageUrl      = breed.imageUrl ?: "",
                choices       = finalChoices,
                correctChoice = intruder
            )
        }

        return (breedQuestions + intruderQuestions)
    }
}