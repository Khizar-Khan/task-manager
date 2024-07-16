package com.example.task_manager.controller;

import com.example.task_manager.model.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping( "/tasks" )
public class TaskController
{
    @Autowired
    private ReactiveMongoTemplate reactiveMongoTemplate;

    @PostMapping
    public Mono<Task> createTask( @RequestBody Task task )
    {
        return reactiveMongoTemplate.save( task );
    }

    @GetMapping
    public Flux<Task> getAllTasks()
    {
        return reactiveMongoTemplate.findAll( Task.class );
    }

    @GetMapping( "/{id}" )
    public Mono<ResponseEntity<Task>> getTaskById( @PathVariable( "id" ) String id )
    {
        return reactiveMongoTemplate.findById( id, Task.class )
                .map( ResponseEntity::ok )
                .defaultIfEmpty( ResponseEntity.notFound().build() );
    }

    @PutMapping( "/{id}" )
    public Mono<Task> updateTask( @PathVariable( "id" ) String id, @RequestBody Task task )
    {
        return reactiveMongoTemplate.findById( id, Task.class )
                .flatMap( existingTask ->
                {
                    existingTask.setTitle( task.getTitle() );
                    existingTask.setDescription( task.getDescription() );
                    existingTask.setSubTasks( task.getSubTasks() );
                    return reactiveMongoTemplate.save( existingTask );
                } );
    }

    @DeleteMapping( "/{id}" )
    public Mono<Void> deleteTask( @PathVariable( "id" ) String id )
    {
        Criteria criteria = Criteria.where( "id" ).is( id );
        Query query = Query.query( criteria );
        return reactiveMongoTemplate.remove( query, Task.class ).then();
    }

    @DeleteMapping( "/{id}/{subId}" )
    public Mono<Void> deleteSubTask( @PathVariable( "id" ) String id, @PathVariable( "subId" ) String subId )
    {
        return reactiveMongoTemplate.findById( id, Task.class )
                .flatMap( task ->
                {
                    removeSubTaskById( task, subId );
                    return reactiveMongoTemplate.save( task );
                } )
                .then();
    }

    private void removeSubTaskById( Task task, String subId )
    {
        if( task.getSubTasks() != null )
        {
            task.getSubTasks().removeIf( subTask -> subTask.getId().equals( subId ) );
            task.getSubTasks().forEach( subTask -> removeSubTaskById( subTask, subId ) );
        }
    }
}
