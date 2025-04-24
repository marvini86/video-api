package com.eeze.app.videoapi.repository;

import com.eeze.app.videoapi.entity.Actor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ActorRepository extends JpaRepository<Actor, Long> {

    Optional<Actor> findByName(String name);
    List<Actor> findByNameIn(Set<String> name);


}
