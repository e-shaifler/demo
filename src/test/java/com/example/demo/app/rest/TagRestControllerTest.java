package com.example.demo.app.rest;

import com.example.demo.app.model.Tag;
import com.example.demo.app.model.TagWithTasks;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.Optional;

import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TagRestControllerTest extends AbstractRestControllerTest {


    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @DisplayName("GET /tag/{uid} <OK>")
    @Test
    @Order(1)
    void getTagWithTasksTest1() throws Exception {
        Tag tag = createTag(uuid(0), "tag 0");
        LocalDate date = LocalDate.of(2020,10,11);
        for(int j = 0; j < 3; j++) {
            String inx =  "0 " + j;
            createTask(uuid(0,j), "name " + inx, "desc " + inx, date, tag);
        }
        TagWithTasks tagWithTasks = new TagWithTasks(tag,tag.getTasks());
        when(mockTagService.getTagWithTasksByUidTag(uuid(0))).thenReturn(Optional.of(tagWithTasks));

        mockMvc.perform(get("/tag/" + uuid(0)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uid", is("10000000-0000-0000-0000-000000000000")))
                .andExpect(jsonPath("$.title", is("tag 0")))
                .andExpect(jsonPath("$.tasks", hasSize(3)))
                .andExpect(jsonPath("$.tasks[0].uid", startsWith("10000000-0000-0000-0000-00000000000")))
                .andExpect(jsonPath("$.tasks[0].name", startsWith("name 0")))
                .andExpect(jsonPath("$.tasks[0].desc", startsWith("desc 0")))
                .andExpect(jsonPath("$.tasks[0].date", is("2020-10-11")))
                .andExpect(jsonPath("$.tasks[0].size()", is(4)))

                .andExpect(jsonPath("$.tasks[1].uid", startsWith("10000000-0000-0000-0000-00000000000")))
                .andExpect(jsonPath("$.tasks[1].name", startsWith("name 0")))
                .andExpect(jsonPath("$.tasks[1].desc", startsWith("desc 0")))
                .andExpect(jsonPath("$.tasks[1].date", is("2020-10-11")))
                .andExpect(jsonPath("$.tasks[1].size()", is(4)))

                .andExpect(jsonPath("$.tasks[2].uid", startsWith("10000000-0000-0000-0000-00000000000")))
                .andExpect(jsonPath("$.tasks[2].name", startsWith("name 0")))
                .andExpect(jsonPath("$.tasks[2].desc", startsWith("desc 0")))
                .andExpect(jsonPath("$.tasks[2].date", is("2020-10-11")))
                .andExpect(jsonPath("$.tasks[2].size()", is(4)));
        verify(mockTagService, Mockito.times(1)).getTagWithTasksByUidTag(uuid(0));
    }

    @DisplayName("GET /tag/{uid} <NOT FOUND>")
    @Test
    @Order(2)
    void getTagWithTasksTest2() throws Exception {
        when(mockTagService.getTagWithTasksByUidTag(uuid(1))).thenReturn(Optional.empty());
        mockMvc.perform(get("/tag/" + uuid(1)))
                .andExpect(status().isNotFound());
        verify(mockTagService, Mockito.times(1)).getTagWithTasksByUidTag(uuid(1));
    }

    @DisplayName("POST /tag update <OK>")
    @Test
    @Order(3)
    void createOrUpdateTagTest1() throws Exception {
        final Tag tag21 = createTag(uuid(2), "tag 2");
        final Tag tag22 = createTag(uuid(2), "tag 2222");
        when(mockTagService.findByUid(uuid(2))).thenReturn(Optional.of(tag21));
        when(mockTagService.save(tag22)).thenReturn(tag22);
        mockMvc.perform(post("/tag/")
                .param("uid",uuid(2).toString())
                .param("title","tag 2222"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uid", is(uuid(2).toString())))
                .andExpect(jsonPath("$.title", is("tag 2222")));

        verify(mockTagService, Mockito.times(1)).findByUid(uuid(2));
        verify(mockTagService, Mockito.times(1)).save(tag22);
    }

    @DisplayName("POST /tag update(no exists) <CONFLICT>")
    @Test
    @Order(4)
    void createOrUpdateTagTest2() throws Exception {
        when(mockTagService.findByUid(uuid(3))).thenReturn(Optional.empty());
        mockMvc.perform(post("/tag/")
                .param("uid",uuid(3).toString())
                .param("title","tag 3333"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(String.valueOf(HttpStatus.CONFLICT.value()))))
                .andExpect(jsonPath("$.message", containsString(uuid(3).toString())));

        verify(mockTagService, Mockito.times(1)).findByUid(uuid(3));
    }

    @DisplayName("POST /tag create <OK>")
    @Test
    @Order(5)
    void createOrUpdateTagTest3() throws Exception {
        final Tag tag41 = createTag(null, "tag 4444");
        final Tag tag42 = createTag(uuid(4), "tag 4444");

        when(mockTagService.create(tag41)).thenReturn(tag42);
        mockMvc.perform(post("/tag/")
                .param("title",tag41.getTitle()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.uid", is(tag42.getUid().toString())))
                .andExpect(jsonPath("$.title", is(tag42.getTitle())));

        verify(mockTagService, Mockito.times(1)).create(tag41);
    }

    @DisplayName("POST /tag create (validation failed) <BAD REQUEST>")
    @Test
    @Order(6)
    void createOrUpdateTagTest4() throws Exception {
        mockMvc.perform(post("/tag/")
                .param("title","t"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(String.valueOf(HttpStatus.BAD_REQUEST.value()))));
    }

    @DisplayName("DELETE /tag/{uid} <NO CONTENT>")
    @Test
    @Order(7)
    void deleteTest() throws Exception {
        doNothing().when(mockTagService).deleteByUid(uuid(5));
        mockMvc.perform(delete("/tag/" + uuid(5).toString()))
                .andExpect(status().isNoContent());

        verify(mockTagService, Mockito.times(1)).deleteByUid(uuid(5));
    }

}