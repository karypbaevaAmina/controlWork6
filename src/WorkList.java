import com.sun.net.httpserver.HttpExchange;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import server.BasicServer;
import server.ContentType;
import server.ResponseCodes;
import server.Utils;
import service.FileServiceTask;
import service.Task;
import models.TaskDataModel;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static java.util.stream.Collectors.joining;

public class WorkList extends BasicServer {


    private final static Configuration freemarker = initFreeMarker();

    protected WorkList(String host, int port) throws IOException {
        super(host, port);

        registerGet("/", this::calendarHandler);

        registerGet("/task", this::taskHandler);

        registerGet("/add", this::addHandler);

        registerPost("/add", this::registerPost);
        
        registerPost("/delete", this::deleteHandler);


    }

    private void deleteHandler(HttpExchange exchange) {
        List<Task> tasks = FileServiceTask.readFile();
        tasks.remove(1);
        redirect303(exchange, "delete.html");

    }

    private void registerPost (HttpExchange exchange) {
        Map<String, Object> map = new HashMap<>();
        String cType = getContentType(exchange);
        String raw = getBody(exchange);
        Map<String, String> parsed = Utils.parseUrlEncoded(raw, "&");
        List<Task> tasks = FileServiceTask.readFile();
        Task task = Task.createTask(tasks.size()+2, parsed);
        tasks.add(task);
        FileServiceTask.writeFile(tasks);
          redirect303(exchange, "/task");
        renderTemplate(exchange, "task.html", map);


    }


    private void addHandler(HttpExchange exchange) {
        renderTemplate(exchange, "add.html",null);
    }

    private void calendarHandler(HttpExchange exchange) {
        renderTemplate(exchange, "calendar.html", getTaskDataModel());
    }

    private void taskHandler(HttpExchange exchange) {
        renderTemplate(exchange, "task.html",getTaskDataModel());
    }


    private TaskDataModel getTaskDataModel(){
        return new TaskDataModel();
    }


    private static Configuration initFreeMarker() {
        try {
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_29);
            cfg.setDirectoryForTemplateLoading(new File("data"));

            cfg.setDefaultEncoding("UTF-8");
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            cfg.setLogTemplateExceptions(false);
            cfg.setWrapUncheckedExceptions(true);
            cfg.setFallbackOnNullLoopVariable(false);
            return cfg;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void renderTemplate(HttpExchange exchange, String templateFile, Object dataModel) {
        try {
            Template temp = freemarker.getTemplate(templateFile);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            try (OutputStreamWriter writer = new OutputStreamWriter(stream)) {

                temp.process(dataModel, writer);
                writer.flush();

                var data = stream.toByteArray();

                sendByteData(exchange, ResponseCodes.OK, ContentType.TEXT_HTML, data);
            }
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }
    }

    public String getContentType(HttpExchange exchange) {
        return exchange.getRequestHeaders()
                .getOrDefault("Content-Type", List.of(""))
                .get(0);
    }

    protected String getBody(HttpExchange exchange) {
        InputStream input = exchange.getRequestBody();
        Charset utf8 = StandardCharsets.UTF_8;
        InputStreamReader isr = new InputStreamReader(input, utf8);
        try (BufferedReader reader = new BufferedReader(isr)) {
            return reader.lines().collect(joining(""));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    protected final void redirect303(HttpExchange exchange, String path) {
        try {
            exchange.getResponseHeaders().add("Location", path);
            exchange.sendResponseHeaders(303, 0);
            exchange.getResponseBody().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
