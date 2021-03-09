package com.example.demo.app.service;

import com.example.demo.app.model.Tag;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

@ConditionalOnProperty(
        value="mysetting.tagservice.impl",
        havingValue = "jpa-repository")
public interface TagRepository extends CrudRepository<Tag, UUID> {
    Optional<Tag> findByTitle(String title);
}
