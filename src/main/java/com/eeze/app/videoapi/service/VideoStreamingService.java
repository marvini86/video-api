package com.eeze.app.videoapi.service;


import com.eeze.app.videoapi.entity.Actor;
import com.eeze.app.videoapi.entity.VideoCount;
import com.eeze.app.videoapi.entity.VideoStreaming;
import com.eeze.app.videoapi.exceptions.DataNotFound;
import com.eeze.app.videoapi.models.dto.*;
import com.eeze.app.videoapi.repository.ActorRepository;
import com.eeze.app.videoapi.repository.VideoStreamingRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


/**
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VideoStreamingService {

    private final VideoStreamingRepository videoStreamingRepository;
    private final ActorRepository actorRepository;
    private static final String UPLOAD_DIR = "uploads";


    /**
     * get all summarized videos
     * @param pageDto
     * @return
     */
    public Page<SummarizedVideoDto> getAllSummarizedVideos(PageDto pageDto) {

        log.info("Getting all active summarized videos");

        var pageable = PageRequest.of(
                pageDto.page() - 1,
                pageDto.size(),
                parseSort(pageDto.sort())
        );

        var videoPage = videoStreamingRepository.findAllByActive(true, pageable);
        var list = videoPage.stream()
                .map(videoStreaming -> new SummarizedVideoDto(videoStreaming.getTitle(), videoStreaming.getDirector(), videoStreaming.getMainActor().getName(), videoStreaming.getGenre(), videoStreaming.getDuration()))
                .toList();


        new PageImpl<>(list, pageable, videoPage.getTotalElements());

        return new PageImpl<>(list, pageable, videoPage.getTotalElements());
    }

    /**
     * get all summarized videos by search criteria
     * @param criteria
     * @return
     */
    public Page<SummarizedVideoDto> getAllSummarizedVideosBySearch(VideoSearchCriteriaDto criteria) {

        log.info("Getting all active summarized videos by search criteria: {}", criteria);

        var pageable = PageRequest.of(
                criteria.page() - 1,
                criteria.size(),
                parseSort(criteria.sort())
        );

        var page = videoStreamingRepository.findAllBySearch(
                null != criteria.title() ? criteria.title().toLowerCase() : null,
                null != criteria.director() ? criteria.director().toLowerCase() : null,
                null != criteria.synopsis() ? criteria.synopsis().toLowerCase() : null,
                null != criteria.actor() ? criteria.actor().toLowerCase() : null,
                null != criteria.mainActor() ? criteria.mainActor().toLowerCase() : null,
                null != criteria.genre() ? criteria.genre().toLowerCase() : null,
                criteria.yearOfRelease(),
                criteria.duration(),
                pageable);

        var list = page.stream()
                .map(videoStreaming -> new SummarizedVideoDto(videoStreaming.getTitle(), videoStreaming.getDirector(), videoStreaming.getMainActor().getName(), videoStreaming.getGenre(), videoStreaming.getDuration()))
                .toList();

        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    /**
     * get video
     * @param id
     * @return
     */
    public VideoDto getVideo(Long id) {

        log.info("Loading video: {}", id);

        var videoStreaming = videoStreamingRepository.findByIdAndActive(id, true);

        if (videoStreaming.isEmpty()) {
            log.error("Video streaming not found: {}", id);
            throw new DataNotFound("Video streaming not found");
        }

        var video = videoStreaming.get();

        incrementCounts(video, VideoCount.IMPRESSION_COUNT);

        return new VideoDto(video.getVideoUrl(), video.getTitle(), video.getSynopsis(), video.getDirector(), video.getGenre(), video.getDuration(), video.getYearOfRelease(), video.getMainActor().getName(), video.getActors().stream().map(Actor::getName).toList());
    }


    /**
     * get video url
     * @param id
     * @return
     */
    public String getVideoUrl(Long id) {
        var videoStreaming = videoStreamingRepository.findByIdAndActive(id, true);

        if (videoStreaming.isEmpty()) {
            log.error("Video streaming not found: {}", id);
            throw new DataNotFound("Video streaming not found");
        }

        var video = videoStreaming.get();

        incrementCounts(video, VideoCount.VIEW_COUNT);

        return video.getVideoUrl();
    }


    /**
     * get video stats
     * @param id
     * @return
     */
    public VideoStatsDto getVideoStats(Long id) {
        var videoStreaming = videoStreamingRepository.findByIdAndActive(id, true);

        if (videoStreaming.isEmpty()) {
            log.error("Video streaming not found: {}", id);
            throw new DataNotFound("Video streaming not found");
        }

        var video = videoStreaming.get();

        return new VideoStatsDto(video.getImpressionCount(), video.getViewCount());
    }


    /**
     * save video streaming
     * @param videoStreaming
     */
    @Transactional
    public void saveVideoStreaming(VideoRequestDto videoStreaming) {
        log.info("Saving video streaming: {}", videoStreaming);

        var mainActor = Actor.builder()
                .name(videoStreaming.mainActor())
                .build();

        var actors = videoStreaming.actors().stream()
                .map( actor -> Actor.builder()
                        .name(actor)
                        .build())
                .collect(Collectors.toSet());

        actors.add(mainActor);

        var savedActors = saveActors(actors);
        var mainActorEntity = actorRepository.findByName(videoStreaming.mainActor()).get();

        var videoStreamingEntity = VideoStreaming.builder()
                .videoUrl(videoStreaming.videoUrl())
                .title(videoStreaming.title())
                .synopsis(videoStreaming.synopsis())
                .director(videoStreaming.director())
                .genre(videoStreaming.genre())
                .duration(videoStreaming.duration())
                .actors(savedActors)
                .mainActor(mainActorEntity)
                .yearOfRelease(videoStreaming.yearOfRelease())
                .active(true)
                .viewCount(0)
                .impressionCount(0)
                .build();


        videoStreamingRepository.save(videoStreamingEntity);
    }


    /**
     * update video streaming
     * @param id
     * @param videoStreaming
     */
    @Transactional
    public void updateVideoStreaming(Long id, VideoRequestDto videoStreaming) {
        log.info("Updating video streaming: {}", videoStreaming);

        var videoStreamingEntity = videoStreamingRepository.findByIdAndActive(id, true);

        if (videoStreamingEntity.isEmpty()) {
            log.error("Video streaming not found: {}", id);
            throw new DataNotFound("Video streaming not found");
        }

        var videoStreamingEntityToUpdate = videoStreamingEntity.get();

        if (videoStreaming.mainActor() != null) {
            var mainActor = Actor.builder()
                    .name(videoStreaming.mainActor())
                    .build();
            var mainActorEntity = actorRepository.findByName(videoStreaming.mainActor()).get();
            videoStreamingEntityToUpdate.setMainActor(mainActorEntity);
        }

        if (videoStreaming.actors() != null) {
            var actors = videoStreaming.actors().stream()
                    .map(actor -> Actor.builder()
                            .name(actor)
                            .build())
                    .collect(Collectors.toSet());
            var savedActors = saveActors(actors);
            videoStreamingEntityToUpdate.setActors(savedActors);
        }

        if (videoStreaming.videoUrl() != null) {
            videoStreamingEntityToUpdate.setVideoUrl(videoStreaming.videoUrl());
        }

        if (videoStreaming.title() != null) {
            videoStreamingEntityToUpdate.setTitle(videoStreaming.title());
        }

        if (videoStreaming.synopsis() != null) {
            videoStreamingEntityToUpdate.setSynopsis(videoStreaming.synopsis());
        }

        if (videoStreaming.director() != null) {
            videoStreamingEntityToUpdate.setDirector(videoStreaming.director());
        }

        if (videoStreaming.genre() != null) {
            videoStreamingEntityToUpdate.setGenre(videoStreaming.genre());
        }

        if (videoStreaming.duration() != null) {
            videoStreamingEntityToUpdate.setDuration(videoStreaming.duration());
        }

        if (videoStreaming.yearOfRelease() != null) {
            videoStreamingEntityToUpdate.setYearOfRelease(videoStreaming.yearOfRelease());
        }

        videoStreamingRepository.save(videoStreamingEntityToUpdate);
    }


    /**
     * delete video streaming
     * @param id
     */
    @Transactional
    public void deleteVideoStreaming(Long id) {
        log.info("Deleting video streaming: {}", id);

        var videoStreamingEntity = videoStreamingRepository.findByIdAndActive(id, true);

        if (videoStreamingEntity.isEmpty()) {
            log.error("Video streaming not found: {}", id);
            throw new DataNotFound("Video streaming not found");
        }

        var videoStreaming = videoStreamingEntity.get();

        videoStreaming.setActive(false);
        videoStreamingRepository.save(videoStreaming);
    }


    /**
     * upload file
     * @param file
     * @return
     * @throws IOException
     */
    public String uploadFile(MultipartFile file) throws IOException {
        log.info("Uploading file with metadata: {}", file);

        var uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        var originalFilename = file.getOriginalFilename();
        var fileExtension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
        var uniqueFilename = UUID.randomUUID() + fileExtension;

        var filePath = uploadPath.resolve(uniqueFilename);

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return filePath.toString();
    }



    /**
     * save actors
     * @param actors
     * @return
     */
    private Set<Actor> saveActors(Set<Actor> actors) {
        var actorsEntities = actors.stream()
                .map(actor ->  {
                    var actorEntity = actorRepository.findByName(actor.getName());
                    if (actorEntity.isEmpty()) {
                        return Actor.builder()
                                .name(actor.getName())
                                .build();
                    }
                    return actorEntity.get();
                })
                .toList();

        return new HashSet<Actor>(actorRepository.saveAll(actorsEntities));
    }


    /**
     * increment counts
     * @param videoStreaming
     * @param videoCount
     */
    private void incrementCounts(VideoStreaming videoStreaming, VideoCount videoCount) {

        switch (videoCount) {
            case VIEW_COUNT -> videoStreaming.setViewCount(videoStreaming.getViewCount() + 1);
            case IMPRESSION_COUNT -> videoStreaming.setImpressionCount(videoStreaming.getImpressionCount() + 1);
        }

        videoStreamingRepository.save(videoStreaming);
    }


    /**
     * parse sort
     * @param sortStr
     * @return
     */
    private Sort parseSort(String sortStr) {
        if (sortStr == null || sortStr.isBlank()) {
            return Sort.by("title").ascending();
        }
        var parts = sortStr.split(",");
        return Sort.by(Sort.Direction.fromString(parts[1]), parts[0]);
    }

}
