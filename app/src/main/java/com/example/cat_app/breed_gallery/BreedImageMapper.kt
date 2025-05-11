package com.example.cat_app.breed_gallery


/*
Znaci razmisli logicno kako tece ceo flow:
Sa API-a dobijem podatke, koje pretvaram u DTO, i sada imam DTO

1) DTO pretvaram u DOMAIN da bi napravio model, jer DTO je "temporary"
2) DOMAIN pretvaram u ENTITY  ako hocu da popunjavam lokalnu room bazu
3) ENTITY -> DOMAIN ako hocu da """citam iz baze"""

TLDR:
DTO  ->  DOMAIN   ->   ENTITY
DTO su api podaci pretvoreni u data transfer object
 */

// Pretvara DTO u DOMAIN
fun BreedImageDTO.toDomain(breedId: String) =
    BreedImage(
    imageId = id,
    breedId  = breedId,
    url      = url,
    width    = width,
    height   = height
)

// Pretvara DOMAIN u ENTITET
fun BreedImage.toEntity(): BreedImageEntity =
    BreedImageEntity(
        breedId = this.breedId,
        imageId = this.imageId,
        url     = this.url,
        width   = this.width,
        height  = this.height
    )

// Pretvara ENTITET u DOMAIN
fun BreedImageEntity.toDomain(): BreedImage =
    BreedImage(
        imageId = this.imageId,
        breedId  = this.breedId,
        url       = this.url,
        width     = this.width,
        height    = this.height
    )