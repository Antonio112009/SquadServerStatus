package runnable;

import database.Database;
import net.dv8tion.jda.core.JDA;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Threads {

    private JDA api;
    private Database database;
    private Task task;

    public Threads(JDA api) {
        this.api = api;
        this.database = new Database();
    }

    public void start(){
        task = new Task(api, database);
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(6);
        executorService.scheduleAtFixedRate(task::UpdateMessages, 4, 60, TimeUnit.SECONDS);
        executorService.scheduleAtFixedRate(task::StatusUpdate, 0, 30, TimeUnit.SECONDS);


    }
}
