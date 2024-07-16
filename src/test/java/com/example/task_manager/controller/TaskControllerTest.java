package com.example.task_manager.controller;

import com.example.task_manager.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@WebFluxTest( TaskController.class )
class TaskControllerTest
{
    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ReactiveMongoTemplate reactiveMongoTemplate;

    private Task task;
    private Task subTask;

    @BeforeEach
    void setUp()
    {
        subTask = new Task( "Sub Test Task", "Sub Test Task Description", new ArrayList<>() );
        task = new Task( "Main Test Task", "Main Test Task Description", new ArrayList<>( List.of( subTask ) ) );
    }

    @Test
    void createTask()
    {
        when( reactiveMongoTemplate.save( any( Task.class ) ) ).thenReturn( Mono.just( task ) );

        webTestClient.post().uri( "/tasks" )
                .contentType( MediaType.APPLICATION_JSON )
                .bodyValue( task )
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath( "$.title" ).isEqualTo( task.getTitle() )
                .jsonPath( "$.description" ).isEqualTo( task.getDescription() );

        verify( reactiveMongoTemplate, times( 1 ) ).save( any( Task.class ) );
    }

    @Test
    void getAllTasks()
    {
        when( reactiveMongoTemplate.findAll( Task.class ) ).thenReturn( Flux.just( task ) );

        webTestClient.get().uri( "/tasks" )
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath( "$[0].title" ).isEqualTo( task.getTitle() )
                .jsonPath( "$[0].description" ).isEqualTo( task.getDescription() );

        verify( reactiveMongoTemplate, times( 1 ) ).findAll( Task.class );
    }

    @Test
    void getTaskById()
    {
        when( reactiveMongoTemplate.findById( task.getId(), Task.class ) ).thenReturn( Mono.just( task ) );

        webTestClient.get().uri( "/tasks/{id}", task.getId() )
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath( "$.title" ).isEqualTo( task.getTitle() )
                .jsonPath( "$.description" ).isEqualTo( task.getDescription() );

        verify( reactiveMongoTemplate, times( 1 ) ).findById( task.getId(), Task.class );
    }

    @Test
    void updateTask()
    {
        when( reactiveMongoTemplate.findById( task.getId(), Task.class ) ).thenReturn( Mono.just( task ) );
        when( reactiveMongoTemplate.save( any( Task.class ) ) ).thenReturn( Mono.just( task ) );

        Task updatedTask = new Task( "Updated Task", "Updated Description", new ArrayList<>() );

        webTestClient.put().uri( "/tasks/{id}", task.getId() )
                .contentType( MediaType.APPLICATION_JSON )
                .bodyValue( updatedTask )
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath( "$.title" ).isEqualTo( updatedTask.getTitle() )
                .jsonPath( "$.description" ).isEqualTo( updatedTask.getDescription() );

        verify( reactiveMongoTemplate, times( 1 ) ).findById( task.getId(), Task.class );
        verify( reactiveMongoTemplate, times( 1 ) ).save( any( Task.class ) );
    }

    @Test
    void deleteTask()
    {
        when( reactiveMongoTemplate.remove( any( Query.class ), eq( Task.class ) ) ).thenReturn( Mono.empty() );

        webTestClient.delete().uri( "/tasks/{id}", task.getId() )
                .exchange()
                .expectStatus().isOk();

        verify( reactiveMongoTemplate, times( 1 ) ).remove( any( Query.class ), eq( Task.class ) );
    }

    @Test
    void deleteSubTask()
    {
        when( reactiveMongoTemplate.findById( task.getId(), Task.class ) ).thenReturn( Mono.just( task ) );
        when( reactiveMongoTemplate.save( any( Task.class ) ) ).thenReturn( Mono.just( task ) );

        webTestClient.delete().uri( "/tasks/{id}/{subId}", task.getId(), subTask.getId() )
                .exchange()
                .expectStatus().isOk();

        verify( reactiveMongoTemplate, times( 1 ) ).findById( task.getId(), Task.class );
        verify( reactiveMongoTemplate, times( 1 ) ).save( any( Task.class ) );
    }
}