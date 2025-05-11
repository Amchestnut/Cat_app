package com.example.cat_app.quiz_package.questions

import com.example.cat_app.domain.Breed
import kotlin.random.Random

object QuestionFactory {

    // Returns (potentially >20) questions so ViewModel can `shuffled().take(20)`
    fun fromBreeds(breeds: List<Breed>): List<Question> {
        if (breeds.size < 4) return emptyList()  // defensive

        val rng = Random(System.currentTimeMillis())
        val shuffled = breeds.shuffled(rng)

        val imageQuestions = shuffled.take(10).map { breed ->
            val wrong = breeds.filterNot { it.id == breed.id }
                .shuffled(rng)
                .take(3)
                .map { it.name }
            ImageToNameQuestion(
                id = "img_${breed.id}",
                imageUrl = breed.imageUrl ?: "",
                choices  = (wrong + breed.name).shuffled(rng),
                correctChoice = breed.name
            )
        }

        val originQuestions = shuffled.take(5).map { breed ->
            val wrong = breeds.asSequence()
                .mapNotNull { it.origin }
                .filterNot { it == breed.origin }
                .distinct()
                .shuffled(rng)
                .take(3)
                .toList()
            OriginQuestion(
                id = "org_${breed.id}",
                breedName = breed.name,
                choices   = (wrong + (breed.origin ?: "Unknown")).shuffled(rng),
                correctChoice = breed.origin ?: "Unknown"
            )
        }

        val lifeSpanQuestions = shuffled.take(5).map { breed ->
            // Parse "12 - 15" → "12-15"
            val correct = breed.lifeSpan?.replace(" ", "") ?: "??"
            val wrong = generateWrongLifespans(correct).take(3)
            LifeSpanQuestion(
                id = "life_${breed.id}",
                breedName = breed.name,
                choices   = (wrong + correct).shuffled(rng),
                correctChoice = correct
            )
        }

        return (imageQuestions + originQuestions + lifeSpanQuestions)
    }

    /** Very naïve – bumps both ends of the interval by ±2 */
    private fun generateWrongLifespans(correct: String): List<String> {
        val (lo, hi) = correct.split("-", limit = 2)
            .mapNotNull { it.toIntOrNull() }
            .let { if (it.size == 2) it else return emptyList() }

        return listOf(
            "${lo - 2}-${hi - 2}",
            "${lo + 1}-${hi + 1}",
            "${lo + 3}-${hi + 3}"
        )
    }
}