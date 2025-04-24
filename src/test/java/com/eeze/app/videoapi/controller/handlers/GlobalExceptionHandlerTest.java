package com.eeze.app.videoapi.controller.handlers;

import com.eeze.app.videoapi.exceptions.DataNotFound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleDataNotFound_shouldReturnNotFoundResponse() {
        // Arrange
        var ex = new DataNotFound("Video not found");

        // Act
        var response = handler.handleDataNotFound(ex);

        // Assert
        assertThat(response.getStatusCode(), is(HttpStatus.NOT_FOUND));
        assertThat(response.getBody().message(), equalTo("Video not found"));
    }

    @Test
    void handleGeneralException_shouldReturnInternalServerErrorResponse() {
        // Arrange
        var ex = new Exception("Unexpected error");

        // Act
        var response = handler.handleGeneralException(ex);

        // Assert
        assertThat(response.getStatusCode(), is(HttpStatus.INTERNAL_SERVER_ERROR));
        assertThat(response.getBody().message(), equalTo("Unexpected error"));
    }
}
