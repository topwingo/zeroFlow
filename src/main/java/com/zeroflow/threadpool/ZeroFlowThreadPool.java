package com.zeroflow.threadpool;

import com.zeroflow.utils.EnhanceLogger;
import com.zeroflow.utils.LogEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: richard.chen
 * @version: v1.0
 * @description: ZeroFlow线程池
 * @date:2019-04-13
 */
@Slf4j
public class ZeroFlowThreadPool {
    private static EnhanceLogger elog = EnhanceLogger.of(log);
    //默认线程池
    private static Executor threadPool;
    //自定义线程池
    private static Executor customThreadPool;
    //线程池大小
    private static final int THREAD_NUM = 100;
    //排队队列大小
    private static final int QUEUE_SIZE = 1000;
    //关闭线程池的等待时间MILLISECONDS
    private static final long CLOSE_AWAIT_TIME = 10 * 1000;

    //注册一个关闭线程池的勾子
    static {
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
        elog.info(LogEvent.of("ZeroFlowThreadPool-initThreadPool", "ZeroFlow线程池初始化成功"));
        return executor;
    }

    /**
     * 获取线程池
     * 如配置自定义线程池优先获取，后才获取默认线程池
     *
     * @return
     */
    public static Executor getThreadPool() {
        if (null != customThreadPool) {
            return customThreadPool;
        }
        if (null == threadPool) {
            synchronized (ZeroFlowThreadPool.class) {
                if (null == threadPool) {
                    threadPool = initThreadPool();
                }
            }
        }
        return threadPool;
    }


    /**
     * 获取线程池执行的执行状况
     * ActiveCount  正在执行任务数
     *TaskCount  队列任务数
     * @return
     */
    public static Map<String, String> getThreadPoolTaskNum() {
        HashMap<String, String> result = new HashMap();
        //线程未使用，直接返回
        if (null == threadPool && null == customThreadPool) {
            result.put("ActiveCount", "0");
            result.put("TaskCount", "0");
            return result;
        }
        ThreadPoolExecutor executorService = ((ThreadPoolExecutor) getThreadPool());
        String activeCount = String.valueOf(executorService.getActiveCount());
        String taskCount = String.valueOf(executorService.getTaskCount());
        //正在执行任务数
        result.put("ActiveCount", activeCount);
        //队列任务数
        result.put("TaskCount", taskCount);
        return result;

    }


    //用手设置新线程池
    public static void setThreadPool(Executor threadPool) {
        ZeroFlowThreadPool.customThreadPool = threadPool;
    }

    /**
     * 关闭线程池
     */
    public static void closeExecutorThreadPool() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            //线程未使用，直接返回
            if (null == threadPool && null == customThreadPool) {
                return;
            }
            try {
                ExecutorService executorService = (ExecutorService) getThreadPool();
                executorService.shutdown();
                if (!executorService.awaitTermination(CLOSE_AWAIT_TIME, TimeUnit.MILLISECONDS)) {
                    elog.info(LogEvent.of("ZeroFlowThreadPool-closeExecutorThreadPool", "ZeroFlow线程池awaitTermination-TimeOut:" + CLOSE_AWAIT_TIME + "毫秒"));
                    List<Runnable> droppedTasks = executorService.shutdownNow();
                    elog.info(LogEvent.of("ZeroFlowThreadPool-closeExecutorThreadPool", "ZeroFlow线程池仍有任务未结束")
                            .others("task_num:", droppedTasks.size())
                    );
                }
                elog.info(LogEvent.of("ZeroFlowThreadPool-closeExecutorThreadPool", "ZeroFlow线程池已正常关闭"));

            } catch (InterruptedException ex) {
                elog.info(LogEvent.of("ZeroFlowThreadPool-closeExecutorThreadPool", "ZeroFlow线程池关闭时出现异常", ex));
            }
        }));
    }
}