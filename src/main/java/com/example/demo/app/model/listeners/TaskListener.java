package com.example.demo.app.model.listeners;

import com.example.demo.app.model.Task;
import com.example.demo.app.service.ServiceCache;
import com.example.demo.app.service.TagService;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;

@Slf4j
public class TaskListener {

	private static ServiceCache serviceCache;

	public TaskListener(){
		log.debug("public PersistEntityListener()");
	}

	public void setServiceCache(TagService tagService){
		if(tagService instanceof ServiceCache) {
			TaskListener.serviceCache = (ServiceCache)tagService;
		}
	}

	@PostLoad
	public void listenerPostLoad(Task task) {
		log.debug("public void listener@PostLoad({})",task);
	}

	@PrePersist
	public void listenerPrePersist(Task task) {
		log.debug("public void listener@PrePersist({})",task);
	}
	
	@PostPersist
	public void listenerPostPersist(Task task) {
		log.debug("public void listener@PostPersist({})",task);
		clearCache(task);
	}
	
	@PreUpdate
	public void listenerPreUpdate(Task task) {
		log.debug("public void listener@PreUpdate({})",task);
		clearCache(task);
	}
	
	@PostUpdate
	public void listenerPostUpdate(Task task) {
		log.debug("public void listener@PostUpdate({})",task);
	}
	
	@PostRemove
	public void listenerPostRemove(Task task) {
		log.debug("public void listener@PostRemove({})",task);
	}	
	
	@PreRemove
	public void listenerPreRemove(Task task) {
		log.debug("public void listener@PreRemove({})",task);
		clearCache(task);
	}

	private void clearCache(Task task){
		if(serviceCache != null) {
			serviceCache.clearCacheByUid(task.getTag().getUid());
			log.debug("serviceCache.clearCacheByUid(task.getTag().getUid())");
			if(task.getLastUidTag() != null){
				serviceCache.clearCacheByUid(task.getLastUidTag());
				log.debug("serviceCache.clearCacheByUid(task.getLastUidTag())");
			}
		}
	}
}
