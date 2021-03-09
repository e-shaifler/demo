package com.example.demo.app.service.impl;

import com.example.demo.app.model.Tag;
import com.example.demo.app.model.TagWithTasks;
import com.example.demo.app.model.Task;
import com.example.demo.app.service.ServiceCache;
import com.example.demo.app.service.TagPersistence;
import com.example.demo.app.service.TagService;
import com.example.demo.app.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlGroup;

import java.lang.reflect.Proxy;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Интеграционный тест кэша: JPA EntityListeners & Spring Cache")
@SpringBootTest(classes = {CacheTest.ProxyCacheBeanPostProcessor.class},
    properties = {"mysetting.cache=on"})
@ActiveProfiles("test_createdb")

@SqlGroup({
        @Sql(value = "classpath:db/insert.sql",
                config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "classpath:db/delete.sql",
                config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
})
public class CacheTest{
    @Autowired
    TagService tagService;

    @Autowired
    TaskService taskService;

    @Autowired
    ProxyCacheBeanPostProcessor proxyCacheBeanPostProcessor;

    @BeforeEach
    void resetCnt(){
        ServiceCache serviceCache = (ServiceCache) tagService;
        serviceCache.clearCacheByUid(uuid(1));
        serviceCache.clearCacheByUid(uuid(2));
        ProxyCacheBeanPostProcessor.cntInvoke = 0;
    }

    @DisplayName("После первого вызова метода с конкретный параметром UidTag, последующие должны кэшироваться")
    @Test
    void should_cache(){
        final Optional<TagWithTasks> optTagExist11 = tagService.getTagWithTasksByUidTag(uuid(1));
        assertAll(
                ()->assertTrue(optTagExist11.isPresent()),
                ()->assertEquals(1,ProxyCacheBeanPostProcessor.cntInvoke)
        );
        final Optional<TagWithTasks> optTagExist12 = tagService.getTagWithTasksByUidTag(uuid(1));
        assertAll(
                ()->assertTrue(optTagExist12.isPresent()),
                ()->assertTrue(optTagExist12.get() == optTagExist11.get()),
                ()->assertEquals(1,ProxyCacheBeanPostProcessor.cntInvoke)
        );


        final Optional<TagWithTasks> optTagExist21 = tagService.getTagWithTasksByUidTag(uuid(2));
        assertAll(
                ()->assertTrue(optTagExist21.isPresent()),
                ()->assertEquals(2,ProxyCacheBeanPostProcessor.cntInvoke)
        );
        final Optional<TagWithTasks> optTagExist22 = tagService.getTagWithTasksByUidTag(uuid(2));
        assertAll(
                ()->assertTrue(optTagExist22.isPresent()),
                ()->assertTrue(optTagExist22.get() == optTagExist21.get()),
                ()->assertEquals(2,ProxyCacheBeanPostProcessor.cntInvoke)
        );

        final Optional<TagWithTasks> optTagNoExist91 = tagService.getTagWithTasksByUidTag(uuid(9));
        assertAll(
                ()->assertTrue(optTagNoExist91.isEmpty()),
                ()->assertEquals(3,ProxyCacheBeanPostProcessor.cntInvoke)
        );
        final Optional<TagWithTasks> optTagNoExist92 = tagService.getTagWithTasksByUidTag(uuid(9));
        assertAll(
                ()->assertTrue(optTagNoExist92.isEmpty()),
                ()->assertEquals(3,ProxyCacheBeanPostProcessor.cntInvoke)
        );

    }

    @DisplayName("Кэш с ключом uid должен сбрасываться при изменении/удалении tag.uid")
    @Test
    void should_clear_cache__change_tag(){
        final Optional<TagWithTasks> optTagExist11 = tagService.getTagWithTasksByUidTag(uuid(1));
        assertEquals(1,ProxyCacheBeanPostProcessor.cntInvoke);
        final Optional<TagWithTasks> optTagExist21 = tagService.getTagWithTasksByUidTag(uuid(2));
        assertEquals(2,ProxyCacheBeanPostProcessor.cntInvoke);
        tagService.getTagWithTasksByUidTag(uuid(1));
        tagService.getTagWithTasksByUidTag(uuid(1));

        Tag tag = tagService.findByUid(uuid(1)).get();
        tag.setTitle(tag.getTitle() + " изм");
        tagService.save(tag);

        final Optional<TagWithTasks> optTagExist12 = tagService.getTagWithTasksByUidTag(uuid(1));
        assertAll(
                ()-> assertEquals(3,ProxyCacheBeanPostProcessor.cntInvoke),
                ()-> assertTrue(optTagExist12.isPresent()),
                ()-> assertFalse(optTagExist11.get().equals(optTagExist12.get())));

        final Optional<TagWithTasks> optTagExist22 = tagService.getTagWithTasksByUidTag(uuid(2));
        assertEquals(3,ProxyCacheBeanPostProcessor.cntInvoke);

        tagService.deleteByUid(uuid(2));

        final Optional<TagWithTasks> optTagNoExist23 = tagService.getTagWithTasksByUidTag(uuid(2));
        assertAll(
                ()-> assertEquals(4,ProxyCacheBeanPostProcessor.cntInvoke),
                ()-> assertTrue(optTagExist21.get() == optTagExist22.get()),
                ()-> assertTrue(optTagNoExist23.isEmpty()));

    }

    @DisplayName("Кэш с ключом uid должен сбрасываться при изменении/удалении task.tag.uid")
    @Test
    void should_clear_cache__change_task(){
        final Optional<TagWithTasks> optTagExist11 = tagService.getTagWithTasksByUidTag(uuid(1));
        assertEquals(1,ProxyCacheBeanPostProcessor.cntInvoke);
        final Optional<TagWithTasks> optTagExist21 = tagService.getTagWithTasksByUidTag(uuid(2));
        assertEquals(2,ProxyCacheBeanPostProcessor.cntInvoke);
        tagService.getTagWithTasksByUidTag(uuid(1));
        tagService.getTagWithTasksByUidTag(uuid(1));

        Task task = taskService.findByUid(uuid(1,1)).get();

        task.setDesc(task.getDesc() + " изм");
        task = taskService.save(task);
        final Optional<TagWithTasks> optTagExist12 = tagService.getTagWithTasksByUidTag(uuid(1));
        assertAll(
                ()-> assertEquals(3,ProxyCacheBeanPostProcessor.cntInvoke),
                ()-> assertTrue(optTagExist12.isPresent()),
                ()-> assertFalse(optTagExist12.get().equals(optTagExist11.get())));

        task = taskService.save(task);
        tagService.getTagWithTasksByUidTag(uuid(1));
        assertEquals(3,ProxyCacheBeanPostProcessor.cntInvoke);

        tagService.getTagWithTasksByUidTag(uuid(2));
        assertEquals(3,ProxyCacheBeanPostProcessor.cntInvoke);

        task.setTag(tagService.findByUid(uuid(2)).get());
        task = taskService.save(task);

        final Optional<TagWithTasks> optTagExist13 = tagService.getTagWithTasksByUidTag(uuid(1));
        assertAll(
                ()-> assertEquals(4,ProxyCacheBeanPostProcessor.cntInvoke),
                ()-> assertFalse(optTagExist12.get().equals(optTagExist13.get())));
        final Optional<TagWithTasks> optTagExist22 = tagService.getTagWithTasksByUidTag(uuid(2));
        assertAll(
                ()-> assertEquals(5,ProxyCacheBeanPostProcessor.cntInvoke),
                ()-> assertFalse(optTagExist21.get().equals(optTagExist22.get())));

        taskService.deleteByUid(task.getUid());
        final Optional<TagWithTasks> optTagExist23 = tagService.getTagWithTasksByUidTag(uuid(2));
        assertAll(
                ()-> assertEquals(6,ProxyCacheBeanPostProcessor.cntInvoke),
                ()-> assertFalse(optTagExist22.get().equals(optTagExist23.get())));


        task = new Task();
        task.setName("name");
        task.setDesc("desc");
        task.setDate(LocalDate.now());
        task.setTag(tagService.findByUid(uuid(2)).get());
        taskService.save(task);
        final Optional<TagWithTasks> optTagExist24 = tagService.getTagWithTasksByUidTag(uuid(2));
        assertAll(
                ()-> assertEquals(7,ProxyCacheBeanPostProcessor.cntInvoke),
                ()-> assertFalse(optTagExist23.get().equals(optTagExist24.get())));
    }


    private UUID uuid(int n2){
        return uuid(0, n2);
    }
    private UUID uuid(int n1, int n2){
        return UUID.fromString("86bcfc7f-1000-1000-1000-0000000000"+ n1 + ""+ n2);
    }

    @Slf4j
    @TestConfiguration
    static class ProxyCacheBeanPostProcessor implements BeanPostProcessor{
        static int cntInvoke;

        private Map<String,Class> map = new HashMap<>();

        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            Class<?> beanClass = bean.getClass();
            if(Arrays.stream(beanClass.getInterfaces()).anyMatch(i -> i == TagPersistence.class)){
                log.info(bean.getClass().getName());
                map.put(beanName, beanClass);
            }
            return bean;
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            Class beanClass = map.get(beanName);
            if (beanClass != null){
                return Proxy.newProxyInstance(beanClass.getClassLoader(),
                        beanClass.getInterfaces(), (proxy, method, args) -> {
                            if("getTagWithTasksByUidTag".equals(method.getName())){
                                cntInvoke++;
                            }
                            return method.invoke(bean, args);
                        }
                );
            }
            return bean;
        }
    }

}
