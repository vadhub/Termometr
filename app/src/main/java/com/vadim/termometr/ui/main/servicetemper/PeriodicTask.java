package com.vadim.termometr.ui.main.servicetemper;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

public class PeriodicTask {
    private final Runnable task;
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    public PeriodicTask(Runnable task) {
        this.task = task;
    }

    public void startPeriodic() {
        scheduledExecutorService.scheduleAtFixedRate(task, 0, 10, SECONDS);
    }

}
