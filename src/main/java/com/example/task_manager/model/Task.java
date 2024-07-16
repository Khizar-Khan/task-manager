package com.example.task_manager.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.UUID;

@Document( collection = "tasks" )
public class Task
{
    @Id
    private String id;
    private String title;
    private String description;
    private List<Task> subTasks;

    public Task( String title, String description, List<Task> subTasks )
    {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.description = description;
        this.subTasks = subTasks;
    }

    // Getters and Setters
    public String getId()
    {
        return id;
    }

    public void setId( String id )
    {
        this.id = id;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle( String title )
    {
        this.title = title;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public List<Task> getSubTasks()
    {
        return subTasks;
    }

    public void setSubTasks( List<Task> subTasks )
    {
        this.subTasks = subTasks;
    }
}