package com.eeze.app.videoapi.models.dto;

import java.util.Set;

public record VideoRequestDto(String videoUrl, String title, String synopsis, String mainActor, String director, String genre, Integer duration, Integer yearOfRelease, Set<String> actors) {
}
