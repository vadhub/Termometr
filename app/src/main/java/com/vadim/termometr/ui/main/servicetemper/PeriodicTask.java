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
        ScheduledFuture<?> periodicFuture = scheduledExecutorService.scheduleAtFixedRate(task, 5, 5, SECONDS);
    }

    private final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(1);

    public void beepForAnHour() {
        final Runnable beeper = () -> System.out.println("beep");
        scheduler.scheduleAtFixedRate(task, 0, 10, SECONDS);
    }

}
