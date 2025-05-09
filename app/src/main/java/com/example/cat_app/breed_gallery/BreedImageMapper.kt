package com.example.cat_app.breed_gallery

fun BreedImageDTO.toDomain(breedId: String) = BreedImage(
    imageId = id,
    breedId  = breedId,
    url      = url,
    width    = width,
    height   = height
)

fun BreedImage.toEntity(): BreedImageEntity =
    BreedImageEntity(
        breedId = this.breedId,
        imageId = this.imageId,
        url     = this.url,
        width   = this.width,
        height  = this.height
    )


fun BreedImageEntity.toDomain(): BreedImage =
    BreedImage(
        imageId = this.imageId,
        breedId  = this.breedId,
        url       = this.url,
        width     = this.width,
        height    = this.height
    )