package com.example.demo.app.model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;

public class TagWithTasks extends TagWithoutTasks {
    protected final Iterable<Task> tasks;

    public TagWithTasks(Tag tag, Iterable<Task> tasks){
        super(tag);
        this.tasks = tasks;
    }

    @JsonIgnoreProperties({"tag","uidTag"})
    public Iterable<Task> getTasks(){
        return tasks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TagWithTasks)) return false;
        TagWithTasks that = (TagWithTasks) o;
        return Objects.equals(tasks, that.tasks) && Objects.equals(tag, that.tag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tasks, tag);
    }
}
