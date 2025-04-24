package com.eeze.app.videoapi.repository;

import com.eeze.app.videoapi.entity.Actor;
import com.eeze.app.videoapi.entity.VideoStreaming;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class VideoStreamingRepositoryTest {

    @Autowired
    private VideoStreamingRepository videoStreamingRepository;

    @Autowired
    private ActorRepository actorRepository;

    @Test
    public void shouldSaveAndFindVideoStreamingById() {
        // Arrange
        var actor1 = Actor.builder().name("John Doe").build();
        var actor2 = Actor.builder().name("Jane Smith").build();
        actorRepository.save(actor1);
        actorRepository.save(actor2);

        Set<Actor> cast = new HashSet<>();
        cast.add(actor1);
        cast.add(actor2);

        var video = VideoStreaming.builder()
                .title("The Journey")
                .videoUrl("http://videos.com/the-journey")
                .synopsis("An epic adventure across uncharted lands.")
                .director("Michael Johnson")
                .actors(cast)
                .mainActor(actor1)
                .genre("Adventure")
                .duration(120)
                .yearOfRelease(2023)
                .viewCount(1500)
                .impressionCount(7000)
                .active(true)
                .build();

        videoStreamingRepository.save(video);

        // Act
        var found = videoStreamingRepository.findById(video.getId());

        // Assert
        assertTrue(found.isPresent());
        assertEquals("The Journey", found.get().getTitle());
        assertEquals(2, found.get().getActors().size());
        assertEquals("Michael Johnson", found.get().getDirector());
    }
}
