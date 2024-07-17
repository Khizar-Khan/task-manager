package com.example.task_manager.controller;

import com.example.task_manager.model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest( webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT )
class TaskControllerIntegrationTest
{
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ReactiveMongoTemplate reactiveMongoTemplate;

    private Task task;
    private Task subTask;

    @BeforeEach
    void setUp()
    {
        subTask = new Task( "Sub Test Task", "Sub Test Task Description", new ArrayList<>() );
        task = new Task( "Main Test Task", "Main Test Task Description", new ArrayList<>( List.of( subTask ) ) );
    }

    @AfterEach
    void cleanDatabase()
    {
        Flux<String> collectionNames = reactiveMongoTemplate.getCollectionNames();
        collectionNames.flatMap( collectionName -> reactiveMongoTemplate.remove( new Query(), collectionName ) )
                .blockLast(); // Ensure the operation completes before proceeding
    }

    @Test
    void createTask()
    {
        webTestClient.post().uri( "/tasks" )
                .contentType( MediaType.APPLICATION_JSON )
                .bodyValue( task )
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath( "$.title" ).isEqualTo( task.getTitle() )
                .jsonPath( "$.description" ).isEqualTo( task.getDescription() );
    }

    @Test
    void getAllTasks()
    {
        webTestClient.post().uri( "/tasks" )
                .contentType( MediaType.APPLICATION_JSON )
                .bodyValue( task )
                .exchange()
                .expectStatus().isOk();

        webTestClient.get().uri( "/tasks" )
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath( "$[0].title" ).isEqualTo( task.getTitle() )
                .jsonPath( "$[0].description" ).isEqualTo( task.getDescription() );
    }

    @Test
    void getTaskById()
    {
        webTestClient.post().uri( "/tasks" )
                .contentType( MediaType.APPLICATION_JSON )
                .bodyValue( task )
                .exchange()
                .expectStatus().isOk();

        webTestClient.get().uri( "/tasks/{id}", task.getId() )
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath( "$.title" ).isEqualTo( task.getTitle() )
                .jsonPath( "$.description" ).isEqualTo( task.getDescription() );
    }

    @Test
    void updateTask()
    {
        webTestClient.post().uri( "/tasks" )
                .contentType( MediaType.APPLICATION_JSON )
                .bodyValue( task )
                .exchange()
                .expectStatus().isOk();

        Task updatedTask = new Task( "Updated Task", "Updated Description", new ArrayList<>() );

        webTestClient.put().uri( "/tasks/{id}", task.getId() )
                .contentType( MediaType.APPLICATION_JSON )
                .bodyValue( updatedTask )
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath( "$.title" ).isEqualTo( updatedTask.getTitle() )
                .jsonPath( "$.description" ).isEqualTo( updatedTask.getDescription() );
    }

    @Test
    void deleteTask()
    {
        webTestClient.post().uri( "/tasks" )
                .contentType( MediaType.APPLICATION_JSON )
                .bodyValue( task )
                .exchange()
                .expectStatus().isOk();

        webTestClient.delete().uri( "/tasks/{id}", task.getId() )
                .exchange()
                .expectStatus().isOk();

        webTestClient.get().uri( "/tasks/{id}", task.getId() )
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void deleteSubTask()
    {
        webTestClient.post().uri( "/tasks" )
                .contentType( MediaType.APPLICATION_JSON )
                .bodyValue( task )
                .exchange()
                .expectStatus().isOk();

        webTestClient.delete().uri( "/tasks/{id}/{subId}", task.getId(), subTask.getId() )
                .exchange()
                .expectStatus().isOk();

        webTestClient.get().uri( "/tasks/{id}", task.getId() )
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath( "$.subTasks" ).isEmpty();
    }
}