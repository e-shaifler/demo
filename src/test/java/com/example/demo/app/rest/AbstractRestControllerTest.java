package com.example.demo.app.rest;

import com.example.demo.app.model.Tag;
import com.example.demo.app.model.Task;
import com.example.demo.app.service.TagService;
import com.example.demo.app.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test_createdb")
public abstract class AbstractRestControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    TagService mockTagService;

    @MockBean
    TaskService mockTaskService;


    Tag createTag(UUID uid, String title){
        Tag tag = new Tag();
        tag.setUid(uid);
        tag.setTitle(title);
        return tag;
    }

    Task createTask(UUID uid, String name, String desc, LocalDate date, Tag tag){
        Task task = new Task();
        task.setUid(uid);
        task.setName(name);
        task.setDesc(desc);
        task.setDate(date);
        task.setTag(tag);
        tag.getTasks().add(task);
        return task;
    }

    UUID uuid(int n2){
        return uuid(0,n2);
    }

    UUID uuid(int n1, int n2){
        return UUID.fromString("10000000-0000-0000-0000-"+n1+"0000000000"+n2);
    }

    protected String dateFormat(LocalDate date) {
        return date.format(DateTimeFormatter.ISO_DATE);
    }

}
