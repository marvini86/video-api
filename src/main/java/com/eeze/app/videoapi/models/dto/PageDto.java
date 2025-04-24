package com.eeze.app.videoapi.models.dto;

public record PageDto(Integer page,
                      Integer size,
                      String sort) {
}
