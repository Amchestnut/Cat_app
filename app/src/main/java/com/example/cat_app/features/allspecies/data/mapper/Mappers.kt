package com.example.cat_app.features.allspecies.data.mapper

import com.example.cat_app.features.allspecies.data.remote.BreedDTO
import com.example.cat_app.features.allspecies.data.local.BreedEntity
import com.example.cat_app.features.allspecies.domain.Breed

fun BreedDTO.toEntity(): BreedEntity {
    return BreedEntity(
        id = id,
        name            = name,
        temperament     = temperament,
        origin          = origin,
        description     = description,
        imageUrl        = image?.url,
        lifeSpan        = lifeSpan,
        childFriendly   = childFriendly,
        dogFriendly     = dogFriendly,
        healthIssues    = healthIssues,
        sheddingLevel   = sheddingLevel,
        strangerFriendly = strangerFriendly,
        vocalisation    = vocalisation,
        height          = height,
        altNames        = altNames,
        weightMetric    = weight?.metric,
        weightImperial  = weight?.imperial,
        adaptability    = adaptability,
        affectionLevel  = affectionLevel,
        energyLevel     = energyLevel,
        intelligence    = intelligence,
        socialNeeds     = socialNeeds,
        grooming        = grooming,
        rare            = rare == 1,
        wikipediaUrl    = wikipediaUrl,
    )
}


fun BreedEntity.toDomain() : Breed {
    return Breed(
        id              = id,
        name            = name,
        temperament     = temperament,
        origin          = origin,
        description     = description,
        imageUrl        = imageUrl,
        lifeSpan        = lifeSpan,
        childFriendly   = childFriendly,
        dogFriendly     = dogFriendly,
        healthIssues    = healthIssues,
        sheddingLevel   = sheddingLevel,
        strangerFriendly = strangerFriendly,
        vocalisation    = vocalisation,
        height          = height,
        altNames        = altNames,
        weightMetric    = weightMetric,
        weightImperial  = weightImperial,
        adaptability    = adaptability,
        affectionLevel  = affectionLevel,
        energyLevel     = energyLevel,
        intelligence    = intelligence,
        socialNeeds     = socialNeeds,
        grooming        = grooming,
        rare            = rare,
        wikipediaUrl    = wikipediaUrl,
    )
}

