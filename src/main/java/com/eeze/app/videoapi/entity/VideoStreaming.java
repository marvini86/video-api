package com.eeze.app.videoapi.entity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "video_streaming")
public class VideoStreaming {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String videoUrl;
    private String title;
    private String synopsis;
    private String director;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "video_streaming_cast",
            joinColumns = @JoinColumn(name = "video_streaming_id"),
            inverseJoinColumns = @JoinColumn(name = "cast_id"))
    private Set<Actor> actors;

    @ManyToOne
    @JoinColumn(name = "main_actor_id")
    private Actor mainActor;
    private String genre;
    private Integer duration;
    private Integer yearOfRelease;
    private Integer viewCount;
    private Integer impressionCount;
    private Boolean active;

}
