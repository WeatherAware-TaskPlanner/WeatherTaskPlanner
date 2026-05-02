package taskmanager.api;
import java.time.LocalDateTime;

public class weatherRecord {
    private LocalDateTime time;
    private double temp;
    private String condition;
    private double pop;


    public weatherRecord(LocalDateTime time, double temp, String condition, double pop) {
        this.time = time;
        this.temp = temp;
        this.condition = condition;
        this.pop = pop;
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

    public double getPop() {
        return pop;
    }

}
