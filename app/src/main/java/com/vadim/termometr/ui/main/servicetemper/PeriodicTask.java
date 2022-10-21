package com.vadim.termometr.ui.main.servicetemper;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PeriodicTask {
    private final Runnable task;
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    public PeriodicTask(Runnable task) {
        this.task = task;
    }

    public void startPeriodic() {
        System.out.println("------");
        scheduledExecutorService.scheduleAtFixedRate(task, 0, 1, TimeUnit.SECONDS);
    }

}
