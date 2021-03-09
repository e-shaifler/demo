package com.example.demo.app.service.impl;

import com.example.demo.app.model.TagWithTasks;
import com.example.demo.app.service.ServiceCache;
import com.example.demo.app.service.TagPersistence;
import com.example.demo.app.service.TagService;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@CacheConfig(cacheNames = "TagWithTasksByUidTag")
@Qualifier("cached")
public class ServiceCacheImpl implements ServiceCache, TagService {

    public ServiceCacheImpl(TagPersistence tagPersistence) {
        this.tagPersistence = tagPersistence;
    }

    interface CachedMethod{
        Optional<TagWithTasks> getTagWithTasksByUidTag(UUID uidTag);
    }
    @Delegate(types = TagService.class, excludes = CachedMethod.class)
    private TagPersistence tagPersistence;

    @Cacheable
    @Override
    public Optional<TagWithTasks> getTagWithTasksByUidTag(UUID uidTag) {
        if(log.isDebugEnabled()){
            log.debug("public Optional<TagWithTasks> ServiceCacheImpl.getTagWithTasksByUidTag({})", uidTag);
        }
        return tagPersistence.getTagWithTasksByUidTag(uidTag);
    }

    @CacheEvict
    @Override
    public void clearCacheByUid(UUID uid) {}
}
