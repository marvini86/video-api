package com.eeze.app.videoapi.repository;

import com.eeze.app.videoapi.entity.VideoStreaming;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VideoStreamingRepository extends JpaRepository<VideoStreaming, Long> {

    Page<VideoStreaming> findAllByActive(Boolean active, Pageable pageable);


    @Query("""
            SELECT v FROM VideoStreaming v
             JOIN FETCH v.actors a
             JOIN FETCH v.mainActor ma
            WHERE v.active = true
              AND (:year IS NULL OR v.yearOfRelease = :year)
              AND (:genre IS NULL OR LOWER(v.genre) = LOWER(:genre))
              AND (:duration IS NULL OR v.duration = :duration)
              AND (:title IS NULL OR LOWER(v.title) LIKE LOWER(CONCAT('%', :title, '%')))
              AND (:director IS NULL OR LOWER(v.director) LIKE LOWER(CONCAT('%', :director, '%')))
              AND (:synopsis IS NULL OR LOWER(v.synopsis) LIKE LOWER(CONCAT('%', :synopsis, '%')))
              AND (:actor IS NULL OR LOWER(a.name) LIKE LOWER(CONCAT('%', :actor, '%')))
              AND (:mainActor IS NULL OR LOWER(ma.name) LIKE LOWER(CONCAT('%', :mainActor, '%')))
    """)
    Page<VideoStreaming> findAllBySearch(@Param("title") String title, @Param("director") String director, @Param("synopsis") String synopsis, @Param("actor") String actor, @Param("mainActor") String mainActor, @Param("genre") String genre, @Param("year") Integer yearOfRelease, @Param("duration") Integer duration, Pageable pageable);

    @Query("SELECT v.videoUrl FROM VideoStreaming v WHERE v.active = true AND v.id = ?1")
    String getVideoUrl(Long id);

}
