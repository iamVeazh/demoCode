package ru.norkin.checker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheckerHandler {

    public static class CheckerInfo {
        private final Checker checker;
        private final int delaySec;
        private final TimeUnit timeUnit;

        public CheckerInfo(Checker checker, int delaySec, TimeUnit timeUnit) {
            this.checker = checker;
            this.delaySec = delaySec;
            this.timeUnit = timeUnit;
        }
    }

    public static final Logger log = LoggerFactory.getLogger(CheckerHandler.class);

    private ScheduledExecutorService executor;

    private final List<CheckerInfo> checkersInfo = new ArrayList<>();


    public void addChecker(Checker checker, int delay, TimeUnit timeUnit) {
        if (delay < 0) {
            throw new IllegalArgumentException("delaySec must be greater than 0");
        }

        checkersInfo.add(new CheckerInfo(checker, delay, timeUnit));
    }

    public void start() {
        if (checkersInfo.isEmpty()) {
            throw new IllegalStateException("There are no checkers");
        }

        //at the moment the number of threads is equal to the number of checkers, but this can be changed here
        executor = NorkinExecutor.newThreadScheduledExecutor(checkersInfo.size());

        log.info("Starting checkers..");
        for (CheckerInfo checkerInfo : checkersInfo) {
            log.info("Starting {}", checkerInfo.checker.getClass().getName());
            executor.scheduleWithFixedDelay(checkerInfo.checker::checkState, 0, checkerInfo.delaySec, checkerInfo.timeUnit);
        }
    }

}
