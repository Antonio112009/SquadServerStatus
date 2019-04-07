package runnable;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Threads {

    private Task task;

    public void start(){
        task = new Task();
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(6);

    }
}
