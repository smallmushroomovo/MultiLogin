package moe.caa.multilogin.core.impl;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 代表插件线程调度器对象
 */
public abstract class AbstractScheduler {
    private final AtomicInteger asyncThreadId = new AtomicInteger(0);
    private final ScheduledExecutorService asyncExecutor = Executors.newScheduledThreadPool(5,
            r -> new Thread(r, "MultiLogin Async #" + asyncThreadId.incrementAndGet()));

    /**
     * 执行一个异步任务
     *
     * @param runnable 任务对象
     */
    public void runTaskAsync(Runnable runnable) {
        asyncExecutor.execute(runnable);
    }

    /**
     * 执行一个异步任务
     *
     * @param runnable 任务对象
     * @param delay    延时
     */
    public void runTaskAsync(Runnable runnable, long delay) {
        asyncExecutor.schedule(runnable, delay, TimeUnit.MILLISECONDS);
    }

    /**
     * 执行一个周期异步任务
     *
     * @param run    任务
     * @param delay  延迟
     * @param period 周期
     */
    public void runTaskAsyncTimer(Runnable run, long delay, long period) {
        asyncExecutor.scheduleAtFixedRate(run, delay, period, TimeUnit.MILLISECONDS);
    }

    /**
     * 关闭所有线程池内的线程
     */
    public void shutdown() {
        asyncExecutor.shutdown();
    }

    /**
     * 执行一个同步任务
     *
     * @param run   任务
     * @param delay 延迟
     */
    public abstract void runTask(Runnable run, long delay);

    /**
     * 执行一个同步任务
     *
     * @param run 任务
     */
    public abstract void runTask(Runnable run);
}
