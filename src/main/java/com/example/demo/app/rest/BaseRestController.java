package com.example.demo.app.rest;

import com.example.demo.app.model.Tag;
import com.example.demo.app.rest.exception.BaseLogicRestException;
import com.example.demo.app.service.TagService;
import com.example.demo.app.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;

import java.text.MessageFormat;

public abstract class BaseRestController {

    protected TaskService taskService;

    protected TagService tagService;

    @Autowired
    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }
    @Autowired
    public void setTagService(TagService tagService) {
        this.tagService = tagService;
    }

    protected void checkExistsTag(Tag tag) {
        if(tagService.findByUid(tag.getUid()).isEmpty()){
            throw new BaseLogicRestException(
                    MessageFormat.format("Не существует тега {1} с uid = {0}",
                            tag.getUid().toString(), tag.getTitle()),
                    HttpStatus.CONFLICT);
        }
    }
}
