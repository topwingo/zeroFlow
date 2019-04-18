package com.zeroflow.threadpool;

import com.zeroflow.utils.EnhanceLogger;
import com.zeroflow.utils.LogEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: richard.chen
 * @version: v1.0
 * @description: JAVA线程池
 * @date:2019-04-13
 */
@Slf4j
public class FlowThreadPool {
    private static EnhanceLogger elog = EnhanceLogger.of(log);
    //默认线程池
    private static Executor threadPool = initThreadPool();
    //自定义线程池
    private static Executor customThreadPool;
    //异步线程池大小
    private static final int THREAD_NUM = 100;
    //排队队列大小
    private static final int QUEUE_SIZE = 1000;
    //关闭线程池的等待时间
    private static final long CLOSE_AWAIT_TIME = 5 * 1000;
    //注册一个关闭线程池的勾子
    static{
        closeExecutorThreadPool();
    }

    /**
     * 初始化线程池
     *
     * @return
     */
    private static Executor initThreadPool() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(THREAD_NUM, THREAD_NUM, 0L, TimeUnit.HOURS,
                new LinkedBlockingQueue<Runnable>(QUEUE_SIZE),
                new ThreadFactory() {
                    private final AtomicInteger threadNumber = new AtomicInteger(1);

                    @Override
                    public Thread newThread(Runnable r) {
                        Thread t = new Thread(Thread.currentThread().getThreadGroup(), r, "zeroFlow-Thread:" + threadNumber.getAndIncrement(), 0);
                        if (t.isDaemon()) {
                            t.setDaemon(false);
                        }
                        if (t.getPriority() != Thread.NORM_PRIORITY) {
                            t.setPriority(Thread.NORM_PRIORITY);
                        }
                        return t;
                    }
                }
                , new ThreadPoolExecutor.CallerRunsPolicy());
        executor.prestartAllCoreThreads();
        elog.info(LogEvent.of("FlowThreadPool-initThreadPool", "ZeroFlow线程池初始化成功"));
        return executor;
    }

    /**
     * 获取线程池
     *如配置自定义线程池优先获取，后才获取默认线程池
     * @return
     */
    public static Executor getThreadPool() {
        if (null != customThreadPool) {
            return customThreadPool;
        }
        if (null == threadPool) {
            synchronized (FlowThreadPool.class) {
                if (null == threadPool) {
                    threadPool = initThreadPool();
                }
            }
        }
        return threadPool;
    }

    //用手设置新线程池
    public static void setThreadPool(Executor threadPool) {
        FlowThreadPool.customThreadPool = threadPool;
    }

    /**
     * 关闭线程池
     */
    public static void closeExecutorThreadPool() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                ExecutorService executorService = (ExecutorService) getThreadPool();
                executorService.shutdown();
                if (!executorService.awaitTermination(CLOSE_AWAIT_TIME, TimeUnit.MILLISECONDS)) {
                    elog.info(LogEvent.of("FlowThreadPool-closeExecutorThreadPool", "ZeroFlow线程池awaitTermination-TimeOut:"+CLOSE_AWAIT_TIME+"毫秒"));
                    List<Runnable> droppedTasks = executorService.shutdownNow();
                    elog.info(LogEvent.of("FlowThreadPool-closeExecutorThreadPool", "ZeroFlow线程池仍有任务未结束")
                    .others("任务数量:",droppedTasks.size())
                    );
                }
                elog.info(LogEvent.of("FlowThreadPool-closeExecutorThreadPool", "ZeroFlow线程池已正常关闭"));

            } catch (InterruptedException ex) {
                elog.info(LogEvent.of("FlowThreadPool-closeExecutorThreadPool", "ZeroFlow线程池关闭时出现异常", ex));
            }
        }));
    }
}