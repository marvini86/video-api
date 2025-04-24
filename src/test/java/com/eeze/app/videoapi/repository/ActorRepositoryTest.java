package com.eeze.app.videoapi.repository;

import com.eeze.app.videoapi.entity.Actor;
import com.eeze.app.videoapi.entity.VideoStreaming;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ActorRepositoryTest {

    @Autowired
    private ActorRepository actorRepository;

    @Autowired
    private VideoStreamingRepository videoStreamingRepository;

    @Test
    public void shouldSaveAndFindActorById() {
        // Arrange
        var actor = Actor.builder()
                .name("Emma Watson")
                .build();

        actorRepository.save(actor);

        // Act
        var found = actorRepository.findById(actor.getId());

        // Assert
        assertTrue(found.isPresent());
        assertEquals("Emma Watson", found.get().getName());
    }

    @Test
    public void shouldAssociateActorWithVideos() {
        // Arrange
        var actor = Actor.builder().name("Chris Evans").build();
        actorRepository.save(actor);

        var video = VideoStreaming.builder()
                .title("Heroic Tales")
                .videoUrl("http://videos.com/heroic-tales")
                .synopsis("A story of courage and honor.")
                .director("Steven Grant")
                .actors(Set.of(actor))
                .mainActor(actor)
                .genre("Action")
                .duration(110)
                .yearOfRelease(2022)
                .viewCount(2000)
                .impressionCount(8000)
                .active(true)
                .build();

        videoStreamingRepository.save(video);

        // Act
        var found = actorRepository.findById(actor.getId());

        // Assert
        assertTrue(found.isPresent());
    }
}
