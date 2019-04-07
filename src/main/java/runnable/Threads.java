package runnable;

import net.dv8tion.jda.core.JDA;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Threads {

    private JDA api;

    public Threads(JDA api) {
        this.api = api;
    }

    private Task task;

    public void start(){
        task = new Task(api);
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(6);
        executorService.scheduleAtFixedRate(task::UpdateMessages, 0, 1, TimeUnit.MINUTES);


    }
}
