package taskmanager.api;
import java.time.LocalDateTime;

public class weatherRecord {
    private LocalDateTime time;
    private double temp;
    private String condition;


    public weatherRecord(LocalDateTime time, double temp, String condition) {
        this.time = time;
        this.temp = temp;
        this.condition = condition;
    }


    public LocalDateTime getTime() {
        return time;
    }

    public double getTemp() {
        return temp;
    }

    public String getCondition() {
        return condition;
    }

}
