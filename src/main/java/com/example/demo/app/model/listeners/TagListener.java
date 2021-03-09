package com.example.demo.app.model.listeners;

import javax.persistence.*;

import com.example.demo.app.model.Tag;
import com.example.demo.app.service.ServiceCache;
import com.example.demo.app.service.TagService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TagListener {

	private static ServiceCache serviceCache;

	public TagListener(){
		log.debug("public PersistEntityListener()");
	}

	public void setServiceCache(TagService tagService){
		if(tagService instanceof ServiceCache) {
			TagListener.serviceCache = (ServiceCache)tagService;
		}
	}

	@PostLoad
	public void listenerPostLoad(Tag tag) {
		log.debug("public void listener@PostLoad({})",tag);
	}

	@PrePersist
	public void listenerPrePersist(Tag tag) {
		log.debug("public void listener@PrePersist({})",tag);
	}
	
	@PostPersist
	public void listenerPostPersist(Tag tag) {
		log.debug("public void listener@PostPersist({})",tag);
		clearCache(tag);
	}
	
	@PreUpdate
	public void listenerPreUpdate(Tag tag) {
		log.debug("public void listener@PreUpdate({})",tag);
		clearCache(tag);
	}
	
	@PostUpdate
	public void listenerPostUpdate(Tag tag) {
		log.debug("public void listener@PostUpdate({})",tag);
	}
	
	@PostRemove
	public void listenerPostRemove(Tag tag) {
		log.debug("public void listener@PostRemove({})",tag);
	}	
	
	@PreRemove
	public void listenerPreRemove(Tag tag) {
		log.debug("public void listener@PreRemove({})",tag);
		clearCache(tag);
	}

	private void clearCache(Tag tag){
		if(serviceCache != null) {
			serviceCache.clearCacheByUid(tag.getUid());
			log.debug("serviceCache.clearCacheByUid(tag.getUid())");
		}
	}
}
