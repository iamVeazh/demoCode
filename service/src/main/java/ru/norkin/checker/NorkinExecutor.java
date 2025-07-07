package ru.norkin.checker;

import java.util.concurrent.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class creates executors which always log exceptions.
 */
public class NorkinExecutor {

    public static final Logger log = LoggerFactory.getLogger(NorkinExecutor.class);

    public static ScheduledExecutorService newSingleThreadScheduledExecutor() {
        return new ScheduledThreadPoolExecutor(1);
    }

    public static ScheduledExecutorService newThreadScheduledExecutor(int corePoolSize) {
        return new ScheduledThreadPoolExecutor(corePoolSize) {
            @Override
            public void execute(Runnable command) {
                super.execute(wrap(command));
            }

            @Override
            public Future<?> submit(Runnable task) {
                return super.submit(wrap(task));
            }

            @Override
            public <T> Future<T> submit(Runnable task, T result) {
                return super.submit(wrap(task), result);
            }

            @Override
            public <T> Future<T> submit(Callable<T> task) {
                return super.submit(wrap(task));
            }

            @Override
            public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
                return super.schedule(wrap(command), delay, unit);
            }

            @Override
            public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
                return super.schedule(wrap(callable), delay, unit);
            }

            @Override
            public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
                return super.scheduleAtFixedRate(wrap(command), initialDelay, period, unit);
            }

            @Override
            public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
                return super.scheduleWithFixedDelay(wrap(command), initialDelay, delay, unit);
            }
        };
    }

    public static ExecutorService newCachedThreadPool() {
        return new ThreadPoolExecutor(0, 2147483647, 60L, TimeUnit.SECONDS, new SynchronousQueue()) {
            @Override
            public void execute(Runnable command) {
                super.execute(wrap(command));
            }

            @Override
            public Future<?> submit(Runnable task) {
                return super.submit(wrap(task));
            }

            @Override
            public <T> Future<T> submit(Runnable task, T result) {
                return super.submit(wrap(task), result);
            }

            @Override
            public <T> Future<T> submit(Callable<T> task) {
                return super.submit(wrap(task));
            }
        };
    }

    private static Runnable wrap(Runnable command) {
        return () -> {
            try {
                command.run();
            } catch (Throwable e) {
                log.error("Exception in executor: " + e.getMessage(), e);
            }
        };
    }

    private static <V> Callable<V> wrap(Callable<V> command) {
        return () -> {
            try {
                return command.call();
            } catch (Throwable e) {
                log.error("Exception in executor: " + e.getMessage(), e);
                throw e;
            }
        };
    }
}
