package com.example.demo.app.service;

import com.example.demo.app.model.Task;

import java.util.Optional;
import java.util.UUID;


public interface TaskService {

    Optional<Task> findByUid(UUID uid);

    Iterable<Task> findAllWithoutTag();

    Iterable<Task> findWithoutTagByUidTag(UUID uidTag);

    Iterable<Task> findAllWithTag();

    Task save(Task task);

	void deleteByUid(UUID uid);

}