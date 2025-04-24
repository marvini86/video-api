package com.eeze.app.videoapi.service;

import com.eeze.app.videoapi.entity.Actor;
import com.eeze.app.videoapi.entity.VideoStreaming;
import com.eeze.app.videoapi.exceptions.DataNotFound;
import com.eeze.app.videoapi.models.dto.*;
import com.eeze.app.videoapi.repository.ActorRepository;
import com.eeze.app.videoapi.repository.VideoStreamingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import org.springframework.mock.web.MockMultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class VideoStreamingServiceTest {

    @Mock private VideoStreamingRepository videoStreamingRepository;
    @Mock private ActorRepository actorRepository;

    @InjectMocks private VideoStreamingService service;

    private VideoStreaming mockVideo;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockVideo = VideoStreaming.builder()
                .id(1L)
                .title("Test Movie")
                .director("John Doe")
                .genre("Action")
                .duration(120)
                .yearOfRelease(2023)
                .synopsis("Test Synopsis")
                .videoUrl("http://video.url")
                .mainActor(Actor.builder().name("Main Actor").build())
                .actors(Set.of(Actor.builder().name("Actor 1").build()))
                .viewCount(5)
                .impressionCount(10)
                .active(true)
                .build();
    }

    @Test
    void getAllSummarizedVideos_shouldReturnPagedResults() {
        PageDto pageDto = new PageDto(1, 10, "title,asc");
        when(videoStreamingRepository.findAllByActive(eq(true), any())).thenReturn(new PageImpl<>(List.of(mockVideo)));

        var result = service.getAllSummarizedVideos(pageDto);

        assertThat(result.getContent(), hasSize(1));
        assertThat(result.getContent().get(0).title(), equalTo("Test Movie"));
    }


    @Test
    void getAllSummarizedVideosBySearch_shouldReturnFilteredPage() {
        VideoSearchCriteriaDto criteria = new VideoSearchCriteriaDto(
                "test title",
                "john doe",
                "some synopsis",
                "actor 1",
                "main actor",
                2023,
                "action",
                120,
                1,
                10,
                "title,asc"
        );

        when(videoStreamingRepository.findAllBySearch(
                eq("test title"),
                eq("john doe"),
                eq("some synopsis"),
                eq("actor 1"),
                eq("main actor"),
                eq("action"),
                eq(2023),
                eq(120),
                any()
        )).thenReturn(new PageImpl<>(List.of(mockVideo)));

        var result = service.getAllSummarizedVideosBySearch(criteria);

        assertThat(result.getContent(), hasSize(1));
        assertThat(result.getContent().get(0).title(), equalTo("Test Movie"));
        assertThat(result.getContent().get(0).director(), equalTo("John Doe"));
    }


    @Test
    void getVideo_shouldReturnDtoAndIncrementImpression() {
        when(videoStreamingRepository.findById(1L)).thenReturn(Optional.of(mockVideo));

        var result = service.getVideo(1L);

        assertThat(result.title(), equalTo("Test Movie"));
        verify(videoStreamingRepository, times(1)).save(any());
    }

    @Test
    void getVideo_shouldThrowWhenNotFound() {
        when(videoStreamingRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(DataNotFound.class, () -> service.getVideo(99L));
    }

    @Test
    void getVideoUrl_shouldReturnUrlAndIncrementView() {
        when(videoStreamingRepository.findById(1L)).thenReturn(Optional.of(mockVideo));

        var url = service.getVideoUrl(1L);

        assertThat(url, equalTo("http://video.url"));
        verify(videoStreamingRepository).save(any());
    }

    @Test
    void getVideoStats_shouldReturnStats() {
        when(videoStreamingRepository.findById(1L)).thenReturn(Optional.of(mockVideo));

        var stats = service.getVideoStats(1L);

        assertThat(stats.viewCount(), equalTo(5));
        assertThat(stats.impressionCount(), equalTo(10));
    }

    @Test
    void deleteVideoStreaming_shouldDeactivateVideo() {
        when(videoStreamingRepository.findById(1L)).thenReturn(Optional.of(mockVideo));

        service.deleteVideoStreaming(1L);

        var captor = ArgumentCaptor.forClass(VideoStreaming.class);
        verify(videoStreamingRepository).save(captor.capture());

        assertThat(captor.getValue().getActive(), is(false));
    }

    @Test
    void deleteVideoStreaming_shouldThrowIfNotFound() {
        when(videoStreamingRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(DataNotFound.class, () -> service.deleteVideoStreaming(99L));
    }

    @Test
    void saveVideoStreaming_shouldSaveVideoWithActors() {
        VideoRequestDto requestDto = new VideoRequestDto(
                "http://video.url",
                "Test Movie",
                "Test Synopsis",
                "John Doe",
                "Director",
                "Action",
                1000,
                2023,
                Set.of("Actor 1", "Actor 2")
        );

        var mainActor = Actor.builder().name("John Doe").build();
        var actor1 = Actor.builder().name("Actor 1").build();
        var actor2 = Actor.builder().name("Actor 2").build();

        var allActors = new HashSet<>(Set.of(mainActor, actor1, actor2));

        when(actorRepository.findByName(anyString())).thenReturn(Optional.of(mainActor));
        when(actorRepository.saveAll(any())).thenReturn(new ArrayList<>(allActors));

        service.saveVideoStreaming(requestDto);

        var captor = ArgumentCaptor.forClass(VideoStreaming.class);
        verify(videoStreamingRepository).save(captor.capture());

        var saved = captor.getValue();
        assertThat(saved.getTitle(), equalTo("Test Movie"));
        assertThat(saved.getActors(), hasSize(3));
        assertThat(saved.getMainActor().getName(), equalTo("John Doe"));
        assertThat(saved.getActive(), is(true));
        assertThat(saved.getViewCount(), equalTo(0));
        assertThat(saved.getImpressionCount(), equalTo(0));
    }


    @Test
    void updateVideoStreaming_shouldUpdateAllFields() {
        var id = 1L;

        var requestDto = new VideoRequestDto(
                "http://new.video.url",
                "Updated Title",
                "Updated Synopsis",
                "Updated Main Actor",
                "Updated Director",
                "Drama",
                1000,
                2024,
                Set.of("Actor 1", "Actor 2")
        );


        var existingVideo = VideoStreaming.builder()
                .id(id)
                .title("Old Title")
                .videoUrl("old.url")
                .mainActor(Actor.builder().name("Old Actor").build())
                .actors(new HashSet<>())
                .build();

        when(videoStreamingRepository.findById(id)).thenReturn(Optional.of(existingVideo));

        var updatedMainActor = Actor.builder().name("Updated Main Actor").build();
        when(actorRepository.findByName("Updated Main Actor")).thenReturn(Optional.of(updatedMainActor));
        when(actorRepository.saveAll(any())).thenAnswer(invocation -> new ArrayList<>(invocation.getArgument(0)));

        service.updateVideoStreaming(id, requestDto);

        verify(videoStreamingRepository).save(existingVideo);

        assertThat(existingVideo.getTitle(), equalTo("Updated Title"));
        assertThat(existingVideo.getVideoUrl(), equalTo("http://new.video.url"));
        assertThat(existingVideo.getSynopsis(), equalTo("Updated Synopsis"));
        assertThat(existingVideo.getDirector(), equalTo("Updated Director"));
        assertThat(existingVideo.getGenre(), equalTo("Drama"));
        assertThat(existingVideo.getDuration(), equalTo(1000));
        assertThat(existingVideo.getYearOfRelease(), equalTo(2024));
        assertThat(existingVideo.getMainActor().getName(), equalTo("Updated Main Actor"));
        assertThat(existingVideo.getActors(), hasSize(2));
    }


    @Test
    void uploadFile_shouldSaveAndReturnPath() throws Exception {
        // Arrange
        String fileName = "video.mp4";
        byte[] content = "mock content".getBytes();
        var mockFile = new MockMultipartFile("file", fileName, "video/mp4", content);

        var mockUploadDir = Path.of("uploads");

        try (var filesMock = mockStatic(Files.class);
             var uuidMock = mockStatic(UUID.class)) {

                filesMock.when(() -> Files.exists(mockUploadDir)).thenReturn(false);
                filesMock.when(() -> Files.createDirectories(mockUploadDir)).thenReturn(mockUploadDir);

                service.uploadFile(mockFile);

                filesMock.verify(() -> Files.createDirectories(mockUploadDir));
        }
    }

}
