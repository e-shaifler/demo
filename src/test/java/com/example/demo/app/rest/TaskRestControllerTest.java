package com.example.demo.app.rest;

import com.example.demo.app.model.Tag;
import com.example.demo.app.model.Task;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TaskRestControllerTest extends AbstractRestControllerTest{

    @DisplayName("GET /tasks <OK>")
    @Test
    @Order(1)
    void getTaskAllWithTagTest() throws Exception {
        Tag tag = createTag(uuid(0), "tag 0");
        LocalDate date = LocalDate.of(2020,10,11);
        List<Task> tasks = new ArrayList<>();
        for(int j = 0; j < 3; j++) {
            String inx =  "0 " + j;
            tasks.add(createTask(uuid(0,j), "name " + inx, "desc " + inx, date, tag));
        }

        when(mockTaskService.findAllWithTag()).thenReturn(tasks);

        mockMvc.perform(get("/tasks"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$.[0].uid", startsWith("10000000-0000-0000-0000-00000000000")))
                .andExpect(jsonPath("$.[0].name", startsWith("name 0")))
                .andExpect(jsonPath("$.[0].desc", startsWith("desc 0")))
                .andExpect(jsonPath("$.[0].date", is("2020-10-11")))
                .andExpect(jsonPath("$.[0].uidTag", is("10000000-0000-0000-0000-000000000000")))
                .andExpect(jsonPath("$.[0].size()", is(5)))

                .andExpect(jsonPath("$.[1].uid", startsWith("10000000-0000-0000-0000-00000000000")))
                .andExpect(jsonPath("$.[1].name", startsWith("name 0")))
                .andExpect(jsonPath("$.[1].desc", startsWith("desc 0")))
                .andExpect(jsonPath("$.[1].date", is("2020-10-11")))
                .andExpect(jsonPath("$.[1].uidTag", is("10000000-0000-0000-0000-000000000000")))
                .andExpect(jsonPath("$.[1].size()", is(5)))

                .andExpect(jsonPath("$.[2].uid", startsWith("10000000-0000-0000-0000-00000000000")))
                .andExpect(jsonPath("$.[2].name", startsWith("name 0")))
                .andExpect(jsonPath("$.[2].desc", startsWith("desc 0")))
                .andExpect(jsonPath("$.[2].date", is("2020-10-11")))
                .andExpect(jsonPath("$.[2].uidTag", is("10000000-0000-0000-0000-000000000000")))
                .andExpect(jsonPath("$.[2].size()", is(5)));

        verify(mockTaskService, Mockito.times(1)).findAllWithTag();
    }

    @DisplayName("POST /task update <OK>")
    @Test
    @Order(2)
    void createOrUpdateTagTest1() throws Exception {
        final Tag tag = createTag(uuid(1), "tag 1111");
        final Task task11 = createTask(uuid(1,1), "name 1","desc 1",LocalDate.of(2020,6,4),tag);
        final Task task12 = createTask(uuid(1,1), "name 111","desc 111",LocalDate.of(2020,6,4),tag);

        when(mockTagService.findByUid(uuid(1))).thenReturn(Optional.of(tag));
        when(mockTaskService.findByUid(uuid(1,1))).thenReturn(Optional.of(task11));
        when(mockTaskService.save(task12)).thenReturn(task12);
        mockMvc.perform(post("/task")
                .param("uid",task12.getUid().toString())
                .param("name",task12.getName())
                .param("desc",task12.getDesc())
                .param("date",dateFormat(task12.getDate()))
                .param("tag.uid",task12.getTag().getUid().toString()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uid", is(task12.getUid().toString())))
                .andExpect(jsonPath("$.name", is(task12.getName())))
                .andExpect(jsonPath("$.desc", is(task12.getDesc())))
                .andExpect(jsonPath("$.date", is(dateFormat(task12.getDate()))))
                .andExpect(jsonPath("$.tag.uid", is(task12.getTag().getUid().toString())))
                .andExpect(jsonPath("$.tag.title", is(task12.getTag().getTitle())));

    }

    @DisplayName("POST /task update (not exists tag) <CONFLICT>")
    @Test
    @Order(3)
    void createOrUpdateTagTest2() throws Exception {
        final Tag tag = createTag(uuid(2), "tag 2222");
        final Task task21 = createTask(uuid(2,2), "name 2","desc 2",LocalDate.of(2020,6,4),tag);
        final Task task22 = createTask(uuid(2,2), "name 222","desc 222",LocalDate.of(2020,6,4),tag);

        when(mockTagService.findByUid(uuid(2))).thenReturn(Optional.empty());
        when(mockTaskService.findByUid(uuid(2,2))).thenReturn(Optional.of(task21));
        mockMvc.perform(post("/task")
                .param("uid",task22.getUid().toString())
                .param("name",task22.getName())
                .param("desc",task22.getDesc())
                .param("date",dateFormat(task22.getDate()))
                .param("tag.uid",task22.getTag().getUid().toString()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(String.valueOf(HttpStatus.CONFLICT.value()))))
                .andExpect(jsonPath("$.message", containsString(uuid(2).toString())));;

    }

    @DisplayName("POST /task update (not exists task) <CONFLICT>")
    @Test
    @Order(4)
    void createOrUpdateTagTest3() throws Exception {
        final Tag tag = createTag(uuid(3), "tag 3333");
        final Task task32 = createTask(uuid(3,3), "name 333","desc 333",LocalDate.of(2020,6,4),tag);

        when(mockTagService.findByUid(uuid(3))).thenReturn(Optional.of(tag));
        when(mockTaskService.findByUid(uuid(3,3))).thenReturn(Optional.empty());
        mockMvc.perform(post("/task")
                .param("uid",task32.getUid().toString())
                .param("name",task32.getName())
                .param("desc",task32.getDesc())
                .param("date",dateFormat(task32.getDate()))
                .param("tag.uid",task32.getTag().getUid().toString()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(String.valueOf(HttpStatus.CONFLICT.value()))))
                .andExpect(jsonPath("$.message", containsString(uuid(3,3).toString())));;

    }

    @DisplayName("POST /task update (validation failed) <BAD REQUEST>")
    @Test
    @Order(5)
    void createOrUpdateTagTest4() throws Exception {
        final Tag tag = createTag(uuid(4), "tag 4444");
        final Task task42 = createTask(uuid(4,4), "na","de",LocalDate.of(2020,6,4),tag);

        mockMvc.perform(post("/task")
                .param("uid",task42.getUid().toString())
                .param("name",task42.getName())
                .param("desc",task42.getDesc())
                .param("date",dateFormat(task42.getDate()))
                .param("tag.uid",task42.getTag().getUid().toString()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(String.valueOf(HttpStatus.BAD_REQUEST.value()))));
    }

    @DisplayName("POST /task create <CREATED>")
    @Test
    @Order(6)
    void createOrUpdateTagTest5() throws Exception {
        final Tag tag = createTag(uuid(5), "tag 5555");
        final Task task51 = createTask(null, "name 555","desc 555",LocalDate.of(2020,6,4),tag);
        final Task task52 = createTask(uuid(1,1), "name 555","desc 555",LocalDate.of(2020,6,4),tag);

        when(mockTagService.findByUid(uuid(5))).thenReturn(Optional.of(tag));
        when(mockTaskService.save(task51)).thenReturn(task52);
        mockMvc.perform(post("/task")
                .param("name",task51.getName())
                .param("desc",task51.getDesc())
                .param("date",dateFormat(task51.getDate()))
                .param("tag.uid",task51.getTag().getUid().toString()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.uid", is(task52.getUid().toString())))
                .andExpect(jsonPath("$.name", is(task52.getName())))
                .andExpect(jsonPath("$.desc", is(task52.getDesc())))
                .andExpect(jsonPath("$.date", is(dateFormat(task52.getDate()))))
                .andExpect(jsonPath("$.tag.uid", is(task52.getTag().getUid().toString())))
                .andExpect(jsonPath("$.tag.title", is(task52.getTag().getTitle())));

    }

    @DisplayName("DELETE /task/{uid} <NO CONTENT>")
    @Test
    @Order(7)
    void deleteTest() throws Exception {
        doNothing().when(mockTaskService).deleteByUid(uuid(6,6));
        mockMvc.perform(delete("/task/" + uuid(6,6).toString()))
                .andExpect(status().isNoContent());

        verify(mockTaskService, Mockito.times(1)).deleteByUid(uuid(6,6));
    }

}