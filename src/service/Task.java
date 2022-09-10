package service;
import java.util.Map;

public class Task {
    private String name;
    private String desc;

    public Task(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    public static Task createTask(int i, Map<String, String> map) {
        return new Task(map.get("name"), map.get("desc"));

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }


}
