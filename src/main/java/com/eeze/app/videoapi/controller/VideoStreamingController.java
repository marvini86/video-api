package com.eeze.app.videoapi.controller;


import com.eeze.app.videoapi.models.dto.*;
import com.eeze.app.videoapi.service.VideoStreamingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Controller that handles video streaming requests
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/videos")
public class VideoStreamingController {

    private final VideoStreamingService videoStreamingService;

    /**
     * get all summarized videos
     * @param page
     * @param size
     * @param sort
     * @return
     */
    @GetMapping
    public ResponseEntity<Page<SummarizedVideoDto>> getAllSummarizedVideos(@RequestParam(defaultValue = "1") int page,
                                                                          @RequestParam(defaultValue = "10") int size,
                                                                          @RequestParam(defaultValue = "title,asc") String sort) {
        return ResponseEntity.ok(videoStreamingService.getAllSummarizedVideos(new PageDto(page, size, sort)));
    }

    /**
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<VideoDto> getVideo(@PathVariable Long id) {
        return ResponseEntity.ok(videoStreamingService.getVideo(id));
    }


    /**
     * get video stats
     * @param id
     * @return
     */
    @GetMapping("/{id}/stats")
    public ResponseEntity<VideoStatsDto> getVideoStats(@PathVariable Long id) {
        return ResponseEntity.ok(videoStreamingService.getVideoStats(id));
    }

    /**
     *
     * @param request
     * @return
     */
    @PostMapping
    public ResponseEntity publish(@RequestBody VideoRequestDto request) {
        videoStreamingService.saveVideoStreaming(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     *
     * @param file
     * @return
     * @throws IOException
     */
    @PostMapping("/upload")
    public ResponseEntity upload(@RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(videoStreamingService.uploadFile(file));
    }

    /**
     * search videos by criteria
     * @param criteria
     * @return
     */
    @PostMapping("/search")
    public ResponseEntity<Page<SummarizedVideoDto>> getAllSummarizedVideosByCriteria(@RequestBody VideoSearchCriteriaDto criteria) {
        return ResponseEntity.ok(videoStreamingService.getAllSummarizedVideosBySearch(criteria));
    }

    /**
     *
     * @param id
     * @return
     */
    @PostMapping("/{id}/play")
    public ResponseEntity play(@PathVariable Long id) {
        return ResponseEntity.ok(videoStreamingService.getVideoUrl(id));
    }

    /**
     * update video
     * @param id
     * @param request
     * @return
     */
    @PutMapping("/{id}")
    public ResponseEntity update(@PathVariable Long id, @RequestBody VideoRequestDto request) {
        videoStreamingService.updateVideoStreaming(id, request);
        return ResponseEntity.noContent().build();
    }

    /**
     * delete video
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        videoStreamingService.deleteVideoStreaming(id);
        return ResponseEntity.noContent().build();
    }

}
