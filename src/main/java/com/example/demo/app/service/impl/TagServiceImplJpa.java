package com.example.demo.app.service.impl;

import com.example.demo.app.model.Tag;
import com.example.demo.app.model.TagWithTasks;
import com.example.demo.app.model.Task;
import com.example.demo.app.service.TagPersistence;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@ConditionalOnProperty(value="mysetting.tagservice.impl",
		havingValue = "jpa")
@Repository
@Transactional
public class TagServiceImplJpa implements TagPersistence {
	
	@PersistenceContext
	private EntityManager em;

	@Transactional(readOnly = true)
	@Override
	public Iterable<Tag> findAll() {
		return em.createNamedQuery(Tag.FIND_ALL, Tag.class).getResultList();
	}

	@Transactional(readOnly = true)
	@Override
	public Optional<TagWithTasks> getTagWithTasksByUidTag(UUID uidTag) {
	/*	Tag tag = em.find(Tag.class, uidTag);
	*/
		TypedQuery<Tag> typedQuery = em.createNamedQuery(Tag.FIND_WITH_TASKS_BY_UID, Tag.class);
		typedQuery.setParameter("uid", uidTag);
		Tag tag;
		try {
			tag = typedQuery.getSingleResult();
		} catch (javax.persistence.NoResultException e) {
			return Optional.empty();
		}
		Iterable<Task> tasks = tag.getTasks();
		return Optional.of(new TagWithTasks(tag, tasks));
	}

	@Transactional(readOnly = true)
	@Override
	public Optional<Tag> findByUid(UUID uid) {
		Objects.requireNonNull(uid);
		Tag tag = em.find(Tag.class, uid);
		return Optional.ofNullable(tag);		
	}

	@Transactional(readOnly = true)
	@Override
	public Optional<Tag> findByTitle(String title) {
		Objects.requireNonNull(title);
		TypedQuery<Tag> typedQuery = em.createNamedQuery(Tag.FIND_BY_TITLE, Tag.class);
		typedQuery.setParameter("title", title);
		return typedQuery.getResultStream().findFirst();
	}

	@Override
	public Tag create(Tag tag) {
		em.persist(tag);
		return tag;		
	}

	@Override
	public Tag save(Tag tag) {
		return em.merge(tag);		
	}

	@Override
	public void deleteByUid(UUID uid) {
		Objects.requireNonNull(uid);
		Tag tag = em.find(Tag.class, uid);
		if(tag != null) {
			em.remove(tag);
		}
	}
}
