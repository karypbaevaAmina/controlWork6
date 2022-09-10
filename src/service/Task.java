package server;

public class Task {
    private Integer id;
    private String taskName;

    public Task(Integer id, String taskName) {
        this.id = id;
        this.taskName = taskName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
}
