package com.example.demo.app.rest;

import com.example.demo.app.model.Task;
import com.example.demo.app.model.TaskWithTag;
import com.example.demo.app.rest.exception.BaseLogicRestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.MessageFormat;
import java.util.UUID;

@RestController
@Slf4j
@Validated
public class TaskRestController extends BaseRestController{

    @GetMapping(path="/tasks", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Iterable<Task> getTaskAllWithTag(){
        return taskService.findAllWithTag();
    }

    @PostMapping(path = "/task", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TaskWithTag> createOrUpdateTag(@Valid Task task) {
        if(log.isDebugEnabled())
            log.debug(task.toString());
        checkExistsTag(task.getTag());
        if(task.getUid() != null){
            checkExistsTask(task);
            return new ResponseEntity<>(new TaskWithTag(taskService.save(task)), HttpStatus.OK);
        }
        return new ResponseEntity<>(new TaskWithTag(taskService.save(task)), HttpStatus.CREATED);
    }

    @DeleteMapping(path="/task/{uid}")
    @ResponseStatus(code=HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("uid") UUID uid) {
        taskService.deleteByUid(uid);
    }

    protected void checkExistsTask(Task task) {
        if(taskService.findByUid(task.getUid()).isEmpty()) {
            throw new BaseLogicRestException(
                    MessageFormat.format("Не существует задачи с uid = {0}",
                            task.getUid().toString()),
                    HttpStatus.CONFLICT);
        }
    }
}
