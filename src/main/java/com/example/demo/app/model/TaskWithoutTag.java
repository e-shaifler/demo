package com.example.demo.app.model;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

@RequiredArgsConstructor
public class TaskWithoutTag implements TaskModel{
    @Delegate(types = TaskModel.class)
    protected final Task task;

}
