import java.io.IOException;
import java.util.Calendar;

public class Main {
    public static void main(String[] args) {


        try {
            new WorkList("localhost", 9869).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}