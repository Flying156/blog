package com.fly.config;

import com.fly.property.AsyncScheduleProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import javax.annotation.Resource;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 异步和定时任务配置类
 * <p>
 *     EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true):
 *     用于启用AspectJ面向切面编程的支持。proxyTargetClass = true
 *     表示使用CGLIB代理来创建切面的代理，exposeProxy = true表示暴露代理对象，
 *     以便在切面内部访问代理对象。
 *     ---
 *     EnableAsync(proxyTargetClass  = true): 用于启用异步方法的支持。
 *     当您在方法上添加@Async注解时，这些方法将在一个独立的线程中执行，以提高应用程序的性能和并发性。
 *     proxyTargetClass = true表示使用CGLIB代理来创建异步方法的代理。
 * </p>
 * @author Milk
 */
@EnableScheduling
@Configuration
@EnableAsync(proxyTargetClass = true)
@EnableConfigurationProperties(AsyncScheduleProperty.class)
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
public class AsyncScheduleConfig extends AsyncConfigurerSupport implements SchedulingConfigurer {


    @Resource
    private AsyncScheduleProperty asyncScheduleProperty;

    /**
     * 异步调用的线程池
     * 当使用了 {@link @Async} 注解时，如果程序没有 TaskExecutor 的 bean 时，
     * SpringBoot 会创建一个 SimpleAsyncTaskExecutor 指定任务
     * 可以通过注解指定线程池，但是早不到会出异常
     * <P>
     *     {@link @Primary} 允许创建两个同类型的 Bean，且使用该注解的优先注入
     * </P>
     *
     */
    @Bean
    @Primary
    public TaskExecutor taskExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setThreadNamePrefix(asyncScheduleProperty.getExecutorThreadNamePrefix());
        executor.setCorePoolSize(asyncScheduleProperty.getCorePoolSize());
        executor.setAllowCoreThreadTimeOut(asyncScheduleProperty.getAwaitForTasksToCompleteOnShutdown());
        executor.setKeepAliveSeconds(asyncScheduleProperty.getKeepAliveSeconds());
        executor.setQueueCapacity(asyncScheduleProperty.getQueueCapacity());
        executor.setMaxPoolSize(asyncScheduleProperty.getMaxPoolSize());
        executor.setAwaitTerminationSeconds(asyncScheduleProperty.getAwaitTerminationSeconds());
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        return executor;
    }


    /**
     * 定时任务线程池
     */
    @Bean
    public TaskScheduler taskScheduler(){
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();

        scheduler.setWaitForTasksToCompleteOnShutdown(asyncScheduleProperty.getAwaitForTasksToCompleteOnShutdown());
        scheduler.setAwaitTerminationSeconds(asyncScheduleProperty.getAwaitTerminationSeconds());
        scheduler.setPoolSize(asyncScheduleProperty.getCorePoolSize());
        scheduler.setThreadNamePrefix(asyncScheduleProperty.getSchedulerThreadNamePrefix());
        scheduler.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        return scheduler;
    }


    /**
     * 设置异步调用的线程池
     */
    @Override
    public Executor getAsyncExecutor() {
        return taskExecutor();
    }

    /**
     * 设置定时任务的线程池
     */
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setTaskScheduler(taskScheduler());
    }
}
