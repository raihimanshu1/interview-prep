package com.lldtop16.taskmanagement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class TaskManagementSystem {
    /*
    ========================================================
    ENUMS
    ========================================================
    */

    enum TaskStatus {
        TODO,
        IN_PROGRESS,
        DONE
    }
    enum Priority {
        LOW,
        MEDIUM,
        HIGH
    }
    /*
    ========================================================
    USER
    Represents person working on tasks
    ========================================================
    */
    static class User {
        private final int id;
        private final String name;
        User(int id, String name) {
            this.id = id;
            this.name = name;
        }
        public String getName() {
            return name;
        }
    }
    /*
    ========================================================
    COMMENT
    Stores task discussion
    ========================================================
    */
    static class Comment {
        private final User user;
        private final String message;
        Comment(User user, String message) {
            this.user = user;
            this.message = message;
        }
        public void display() {
            System.out.println(
                    user.getName() + " : " + message
            );
        }
    }
    /*
    ========================================================
    TASK
    Main business object
    ========================================================
    */
    static class Task {
        private final int id;
        private final String title;
        private String description;
        private TaskStatus status;
        private Priority priority;
        private User assignedUser;
        private final List<Comment> comments;
        Task(int id, String title, String description, Priority priority) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.priority = priority;
            this.status = TaskStatus.TODO;
            comments = new ArrayList<>();
        }
        public void assignUser(User user) {
            this.assignedUser = user;
        }
        public void updateStatus(TaskStatus status) {
            this.status = status;
        }
        public void addComment(Comment comment) {
            comments.add(comment);
        }
        public void showDetails() {
            System.out.println("Task ID : " + id);
            System.out.println("Title : " + title);
            System.out.println("Status : " + status);
            System.out.println("Priority : " + priority);
            if (assignedUser != null) {
                System.out.println("Assigned To : " + assignedUser.getName());
            }
            System.out.println("Comments:");
            for (Comment c : comments) {
                c.display();
            }
            System.out.println();
        }
    }
    /*
    ========================================================
    TASK SERVICE
    Handles operations on tasks
    ========================================================
    */
    static class TaskService {
        private final Map<Integer, Task> tasks;
        TaskService() {
            tasks = new HashMap<>();
        }
        public void createTask(Task task) {
            tasks.put(task.id, task);
        }
        public Task getTask(int id) {
            return tasks.get(id);
        }
        public void updateTaskStatus(int taskId, TaskStatus status) {
            Task task = tasks.get(taskId);
            if (task != null) {
                task.updateStatus(status);
            }
        }
    }
    /*
    ========================================================
    MAIN
    ========================================================
    */
    public static void main(String[] args) {
        User developer = new User(1, "John");
        User manager = new User(2, "Mike");
        Task task = new Task(101, "Implement Login API", "Create authentication API", Priority.HIGH);
        TaskService service = new TaskService();
        // Create task
        service.createTask(task);
        // Assign developer
        task.assignUser(developer);
        // Add comments
        task.addComment(new Comment(manager, "Please finish before Friday"));
        // Update status
        service.updateTaskStatus(101, TaskStatus.IN_PROGRESS);
        // Display
        service.getTask(101).showDetails();
    }
}
/*
============================================================
TASK MANAGEMENT SYSTEM
============================================================
PROBLEM STATEMENT
============================================================
Design a Task Management system like:
- Jira
- Trello
- Asana
System should support:
- Create tasks
- Assign tasks to users
- Update task status
- Change priority
- Add comments
- View tasks
Example:
Developer receives task:
"Implement Login API"
Task:
Title:
Implement Login API
Assigned To:
John
Status:
TODO
Priority:
HIGH
============================================================
CORE ENTITIES
============================================================
TaskManagementSystem
        |
        |
        +----------------+
        |                |
       User            Task
Task
Contains:
- task id
- title
- description
- status
- priority
- assigned user
- comments
============================================================
DESIGN DECISIONS
============================================================
1. TASK STATUS USING ENUM
Instead of string:
"TODO"
"IN_PROGRESS"
"DONE"
Use enum:
TaskStatus.TODO
Benefits:
- Type safety
- Avoid spelling mistakes
------------------------------------------------------------
2. TASK PRIORITY USING ENUM
Example:
LOW
MEDIUM
HIGH
------------------------------------------------------------
3. SINGLE RESPONSIBILITY
User:
Stores user details
Task:
Stores task details
TaskService:
Handles business operations
Comment:
Stores discussion
============================================================
FLOW
============================================================
Create Task
        |
        v
Assign User
        |
        v
Update Status
        |
        v
Complete Task
============================================================
TIME COMPLEXITY
============================================================
Create Task:
O(1)
Find Task:
O(n)
(For production use HashMap)
Update Task:
O(1)
============================================================
INTERVIEW FOLLOW UPS
============================================================
1. Search tasks
Use:
HashMap
or
Database index
------------------------------------------------------------
2. Notification
When task assigned:
TaskService
        |
        v
NotificationService
------------------------------------------------------------
3. Audit history
Store:
TaskHistory
Example:
John changed status:
TODO -> DONE
------------------------------------------------------------
4. Multiple projects
Add:
Project
Project
    |
    |
   Tasks
============================================================
*/
