package com.example.demo.app.model;

public interface TaskModel {
    java.util.UUID getUid();

    String getName();

    String getDesc();

    java.time.LocalDate getDate();
}
