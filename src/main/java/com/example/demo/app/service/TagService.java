package com.example.demo.app.service;

import com.example.demo.app.model.Tag;
import com.example.demo.app.model.TagWithTasks;

import java.util.Optional;
import java.util.UUID;

public interface TagService {

	Iterable<Tag> findAll();

	Optional<TagWithTasks> getTagWithTasksByUidTag(UUID uidTag);

	Optional<Tag> findByUid(UUID uid);

	Tag save(Tag tag);

	void deleteByUid(UUID uid);

    Optional<Tag> findByTitle(String title);

    Tag create(Tag tag);

}