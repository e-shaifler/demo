package com.example.demo.app.service.impl;

import com.example.demo.app.model.Task;
import com.example.demo.app.service.TaskService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional
public class TaskServiceImpl implements TaskService {

	@PersistenceContext
	private EntityManager em;

	@Transactional(readOnly = true)
	@Override
	public Optional<Task> findByUid(UUID uid) {
		Objects.requireNonNull(uid);
		Task task = em.find(Task.class, uid);
		return Optional.ofNullable(task);
	}

	@Transactional(readOnly = true)
	@Override
	public Iterable<Task> findAllWithoutTag() {
		return em.createNamedQuery(Task.FIND_ALL_WITHOUT_TAG, Task.class).getResultList();
	}

	@Transactional(readOnly = true)
	@Override
	public Iterable<Task> findAllWithTag(){
		return em.createNamedQuery(Task.FIND_ALL_WITH_TAG, Task.class).getResultList();
	}

	@Transactional(readOnly = true)
	@Override
	public Iterable<Task> findWithoutTagByUidTag(UUID uidTag) {
		Objects.requireNonNull(uidTag);
		TypedQuery<Task> typedQuery = em.createNamedQuery(Task.FIND_WITHOUT_TAG_BY_UID_TAG, Task.class);
		typedQuery.setParameter("uidTag", uidTag);
		return typedQuery.getResultList();
	}

	@Override
	public Task save(Task task){
		return em.merge(task);
	}

	@Override
	public void deleteByUid(UUID uid){
		Objects.requireNonNull(uid);
		Task task = em.find(Task.class, uid);
		if(task != null) {
			em.remove(task);
		}
	}
}
