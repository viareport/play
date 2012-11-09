package play.test.behaviour;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import play.jobs.Job;
import play.jobs.JobsPlugin;

public class JBehaveAdapterExecutorService implements ExecutorService {
    private ScheduledThreadPoolExecutor playExecutorService = JobsPlugin.executor;

    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return playExecutorService.awaitTermination(timeout, unit);
    }

    public boolean equals(Object obj) {
        return playExecutorService.equals(obj);
    }

    public void execute(Runnable command) {
        playExecutorService.execute(command);
    }

    public int hashCode() {
        return playExecutorService.hashCode();
    }

    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
        throws InterruptedException {
        return playExecutorService.invokeAll(tasks, timeout, unit);
    }

    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return playExecutorService.invokeAll(tasks);
    }

    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
        throws InterruptedException, ExecutionException, TimeoutException {
        return playExecutorService.invokeAny(tasks, timeout, unit);
    }

    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException,
        ExecutionException {
        return playExecutorService.invokeAny(tasks);
    }

    public boolean isShutdown() {
        return playExecutorService.isShutdown();
    }

    public boolean isTerminated() {
        return playExecutorService.isTerminated();
    }

    public void shutdown() {
        playExecutorService.shutdown();
    }

    public List<Runnable> shutdownNow() {
        return playExecutorService.shutdownNow();
    }

    public <T> Future<T> submit(final Callable<T> task) {
        Job job = new Job<T>() {

            @Override
            public void doJob() throws Exception {
                task.call();
            }

            @Override
            public T doJobWithResult() throws Exception {
                return task.call();
            }
        };

        return job.now();
    }

    public <T> Future<T> submit(Runnable task, T result) {
        return playExecutorService.submit(task, result);
    }

    public Future<?> submit(Runnable task) {
        return playExecutorService.submit(task);
    }

    public String toString() {
        return playExecutorService.toString();
    }
}
