import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

public class Race {
    private ArrayList<Stage> stages;
    public ArrayList<Stage> getStages() { return stages; }
    private AtomicBoolean status = new AtomicBoolean(false);

    public Race(Stage... stages) {
        this.stages = new ArrayList<>(Arrays.asList(stages));
    }

    public AtomicBoolean getStatus() {
        return status;
    }
}