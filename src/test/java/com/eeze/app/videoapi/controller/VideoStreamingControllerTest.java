package com.eeze.app.videoapi.controller;

import com.eeze.app.videoapi.models.dto.*;
import com.eeze.app.videoapi.service.VideoStreamingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

class VideoStreamingControllerTest {

    @Mock
    private VideoStreamingService service;
    @InjectMocks
    private VideoStreamingController controller;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllSummarizedVideos_shouldReturnPage() {
        var mockPage = new PageImpl<>(List.of(new SummarizedVideoDto("Title", "Director", "Actor", "Genre", 120)));
        when(service.getAllSummarizedVideos(any())).thenReturn(mockPage);

        var response = controller.getAllSummarizedVideos(1, 10, "title,asc");

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getContent(), hasSize(1));
    }

    @Test
    void getVideo_shouldReturnVideoDto() {
        var dto = new VideoDto("url", "Title", "Synopsis", "Director", "Genre", 120, 2022, "Main Actor", List.of("Actor 1"));
        when(service.getVideo(1L)).thenReturn(dto);

        var response = controller.getVideo(1L);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().title(), equalTo("Title"));
    }

    @Test
    void getVideoStats_shouldReturnStats() {
        var stats = new VideoStatsDto(10, 5);
        when(service.getVideoStats(1L)).thenReturn(stats);

        var response = controller.getVideoStats(1L);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().viewCount(), equalTo(5));
    }

    @Test
    void publish_shouldReturnCreated() {
        var dto = new VideoRequestDto("url", "Title", "Synopsis", "Main Actor", "Director", "Drama", 1000, 2025, Set.of("Actor"));
        doNothing().when(service).saveVideoStreaming(dto);

        var response = controller.publish(dto);

        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
    }

    @Test
    void upload_shouldReturnPath() throws IOException {
        var file = new MockMultipartFile("file", "video.mp4", "video/mp4", "test content".getBytes());
        when(service.uploadFile(file)).thenReturn("uploads/video.mp4");

        var response = controller.upload(file);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), equalTo("uploads/video.mp4"));
    }

    @Test
    void getAllSummarizedVideosByCriteria_shouldReturnPage() {
        var criteria = new VideoSearchCriteriaDto("title", null, null, null, null, null, null, null, 1, 10, "title,asc");
        var mockPage = new PageImpl<>(List.of(new SummarizedVideoDto("Title", "Director", "Actor", "Genre", 100)));
        when(service.getAllSummarizedVideosBySearch(criteria)).thenReturn(mockPage);

        var response = controller.getAllSummarizedVideosByCriteria(criteria);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getContent(), hasSize(1));
    }

    @Test
    void play_shouldReturnVideoUrl() {
        when(service.getVideoUrl(1L)).thenReturn("http://streaming.url");

        var response = controller.play(1L);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), equalTo("http://streaming.url"));
    }

    @Test
    void update_shouldReturnNoContent() {
        var dto = new VideoRequestDto("url", "Title", "Synopsis", "Main Actor", "Director", "Drama", 1000, 2024, Set.of("Actor"));
        doNothing().when(service).updateVideoStreaming(1L, dto);

        var response = controller.update(1L, dto);

        assertThat(response.getStatusCode(), is(HttpStatus.NO_CONTENT));
    }

    @Test
    void delete_shouldReturnNoContent() {
        doNothing().when(service).deleteVideoStreaming(1L);

        var response = controller.delete(1L);

        assertThat(response.getStatusCode(), is(HttpStatus.NO_CONTENT));
    }
}
