package com.ericwafula.rickandmorty.data.mappers

import com.ericwafula.rickandmorty.data.model.Character
import com.ericwafula.rickandmorty.datasources.remote.characters.dto.CharacterDto

/**
 * Converts a wire [CharacterDto] into the data layer's [Character] model. The
 * one boundary that decouples the data layer from the JSON shape — a field
 * change on the DTO ripples no further than here. Total and pure: the nested
 * origin/location objects are flattened to their names and the episode URLs are
 * reduced to a count.
 */
internal fun CharacterDto.toData() = Character(
    id = id,
    name = name,
    status = status,
    species = species,
    type = type,
    gender = gender,
    image = image,
    origin = origin.name,
    location = location.name,
    episodeCount = episode.size,
)
