package models;

import service.FileServiceTask;
import service.Task;

import java.util.List;
import java.util.Random;

public class TaskDataModel {
    private List <Task> tasks;

    public TaskDataModel() {
        this.tasks =FileServiceTask.readFile();
    }

    public List<Task> getTasks() {
        return tasks;
    }
}
