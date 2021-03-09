package com.example.demo;

import com.example.demo.app.model.listeners.TagListener;
import com.example.demo.app.model.listeners.TaskListener;
import com.example.demo.app.service.TagPersistence;
import com.example.demo.app.service.TagService;
import com.example.demo.app.service.impl.ServiceCacheImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@ConditionalOnProperty(
        value="mysetting.cache",
        havingValue = "on")
@Configuration
public class CacheConfig {

    @Autowired
    TagPersistence tagPersistence;

    @Bean
    @Primary
    public TagService tagService(){
        return new ServiceCacheImpl(tagPersistence);
    }

    @Bean
    public TagListener tagListener(){
        TagListener tagListener = new TagListener();
        tagListener.setServiceCache(tagService());
        return tagListener;
    }

    @Bean
    public TaskListener taskListener(){
        TaskListener taskListener = new TaskListener();
        taskListener.setServiceCache(tagService());
        return taskListener;
    }

}
