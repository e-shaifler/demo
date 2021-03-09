package com.example.demo.app.service.impl;

import com.example.demo.app.model.Tag;
import com.example.demo.app.model.TagWithTasks;
import com.example.demo.app.model.Task;
import com.example.demo.app.service.TagPersistence;
import com.example.demo.app.service.TagRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@ConditionalOnProperty(
		value="mysetting.tagservice.impl",
		havingValue = "jpa-repository")
@Service
@Slf4j
public class TagServiceImplData implements TagPersistence {

	private final TagRepository tagRepository;

	public TagServiceImplData(TagRepository tagRepository) {
		this.tagRepository = tagRepository;
	}

	@Override
	public Iterable<Tag> findAll() {
		return tagRepository.findAll();
	}

	@Transactional(readOnly = true)
	@Override
	public Optional<TagWithTasks> getTagWithTasksByUidTag(UUID uidTag) {
		Optional<Tag> optionalTag = tagRepository.findById(uidTag);
		if(optionalTag.isEmpty()){
			return Optional.empty();
		}
		Tag tag = optionalTag.get();
		Iterable<Task> tasks = tag.getTasks().stream().collect(Collectors.toList()); //fix lazy
		return Optional.of(new TagWithTasks(tag, tasks));
	}

	@Override
	public Optional<Tag> findByUid(UUID uid) {
		return tagRepository.findById(uid);
	}

	@Override
	public Optional<Tag> findByTitle(String title) {
		return tagRepository.findByTitle(title);
	}

	@Override
	public Tag create(Tag tag) {
		return tagRepository.save(tag);
	}

	@Override
	public Tag save(Tag tag) {
		return tagRepository.save(tag);
	}

	@Override
	public void deleteByUid(UUID uid) {
		tagRepository.deleteById(uid);
	}

}
