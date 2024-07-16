# Task Manager Application

This is a Task Manager application built with Spring Boot, Spring WebFlux, and Spring Data Reactive MongoDB. It allows you to create, read, update, and delete tasks, as well as manage sub-tasks.

## Features

- Create a new task
- Retrieve all tasks
- Retrieve a task by ID
- Update an existing task
- Delete a task
- Delete a sub-task

## Building and Running the Application

### Prerequisites

- Java 17
- MongoDB
- Maven

### Building the Project

1. Clone the repository:

   ```sh
   git clone https://github.com/Khizar-Khan/task-manager
   cd task-manager
   ```

2. Build the project using Maven:

   ```sh
   mvn clean install
   ```

3. Setting up MongoDB

   ```sh
   1. Install MongoDB from the official website.
   2. Ensure MongoDB is running on the default port (27017).
   ```

4. Running the Application
   ```sh
   mvn spring-boot:run
   ```

## API Endpoints

### Create a Task

- **URL**: `/tasks`
- **Method**: `POST`
- **Description**: Will create and store a new task into the database.
- **Request Body**:

```json
{
  "title": "Task Title",
  "description": "Task Description",
  "subTasks": []
}
```

- **Response**: `200 OK` with the created task.

### Retrieve All Tasks

- **URL**: `/tasks`
- **Method**: `GET`
- **Description**: Will retrieve all the tasks in the database.
- **Response**: `200 OK` with a list of tasks.

### Retrieve a Task by ID

- **URL**: `/tasks/{id}`
- **Method**: `GET`
- **Description**: Will retrieve a task via ID.
- **Response**:
  - `200 OK` with the task if found.
  - `404 Not Found` if the task does not exist.

### Update a Task

- **URL**: `/tasks/{id}`
- **Method**: `PUT`
- **Description**: Will update an existing task within the database.
- **Request Body**:

```json
{
  "title": "Updated Title",
  "description": "Updated Description",
  "subTasks": []
}
```

- **Response**: `200 OK` with the updated task.

### Delete a Task

- **URL**: `/tasks/{id}`
- **Method**: `DELETE`
- **Description**: Will delete a task via ID.
- **Response**: `200 OK` once task deleted.

### Delete a Sub-Task

- **URL**: `/tasks/{id}/{subId}`
- **Method**: `DELETE`
- **Description**: Will delete a sub task via task and sub-task IDs.
- **Response**: `200 OK` once task deleted.
