package com.eeze.app.videoapi.models.dto;

public record VideoSearchCriteriaDto(String title, String director, String synopsis, String actor, String mainActor, Integer yearOfRelease, String genre, Integer duration, Integer page,
                                     Integer size,
                                     String sort) {
}
