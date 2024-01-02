package com.example.groupproject.model;

public class Task {

    private int task_id;
    private String title;
    private String description;
    private String duedate;
    private String status;

    public Task(){

    }

    public Task(int task_id, String title, String description, String duedate, String status) {
        this.title = title;
        this.description = description;
        this.duedate = duedate;
        this.status = status;
        this.task_id = task_id;
    }

    public int getTask_id() {
        return task_id;
    }

    public void setTask_id(int task_id) {
        this.task_id = task_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDuedate() {
        return duedate;
    }

    public void setDuedate(String duedate) {
        this.duedate = duedate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task{" +
                "task_id=" + task_id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", shift='" + duedate + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
