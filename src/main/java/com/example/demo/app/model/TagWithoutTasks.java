package com.example.demo.app.model;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

@RequiredArgsConstructor
public class TagWithoutTasks implements TagModel {
    @Delegate(types = TagModel.class)
    protected final Tag tag;
}
