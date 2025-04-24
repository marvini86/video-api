package com.eeze.app.videoapi.models.dto;

import java.util.List;

public record VideoDto(String videoUrl, String title, String synopsis, String director, String genre, int duration, int yearOfRelease, String mainActor, List<String> actors) {
}
