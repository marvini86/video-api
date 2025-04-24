package com.eeze.app.videoapi.controller;

import com.eeze.app.videoapi.models.dto.VideoDto;
import com.eeze.app.videoapi.models.dto.VideoRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = "server.port=8080"
)
public class VideoStreamingControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api/v1/videos";
    }

    @Test
    public void shouldCreateAndRetrieveVideo() {
        // Arrange
        var request = new VideoRequestDto(
                "http://example.com/the-matrix",
                "The Matrix",
                "A computer hacker learns the world is a simulation.",
                "Lana Wachowski",
                "Director",
                "Sci-fi",
                136,
                1999,
                Set.of("Actor 1", "Actor 2"));

        // Act - Create
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<VideoRequestDto> entity = new HttpEntity<>(request, headers);

        var postResponse = restTemplate.postForEntity(getBaseUrl(), entity, Void.class);
        assertEquals(HttpStatus.CREATED, postResponse.getStatusCode());

        // Act - Retrieve specific video by ID
        ResponseEntity<VideoDto> getResponse = restTemplate.getForEntity(getBaseUrl() + "/" + 1, VideoDto.class);
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());

        var retrieved = getResponse.getBody();
        assertNotNull(retrieved);
        assertEquals("The Matrix", retrieved.title());
        assertEquals("Director", retrieved.director());
    }
}
