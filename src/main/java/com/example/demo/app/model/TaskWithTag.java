package com.example.demo.app.model;


public class TaskWithTag extends TaskWithoutTag{
    public TaskWithTag(Task task) {
        super(task);
    }

    public TagWithoutTasks getTag() {
        return new TagWithoutTasks(task.getTag());
    }
}
