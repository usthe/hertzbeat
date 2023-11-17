/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dromara.hertzbeat.collector.dispatch;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.dromara.hertzbeat.collector.dispatch.entrance.internal.CollectJobService;
import org.dromara.hertzbeat.collector.dispatch.timer.Timeout;
import org.dromara.hertzbeat.collector.dispatch.timer.TimerDispatch;
import org.dromara.hertzbeat.collector.dispatch.timer.WheelTimerTask;
import org.dromara.hertzbeat.collector.dispatch.unit.UnitConvert;
import org.dromara.hertzbeat.collector.util.CollectUtil;
import org.dromara.hertzbeat.common.entity.job.Configmap;
import org.dromara.hertzbeat.common.entity.job.Job;
import org.dromara.hertzbeat.common.entity.job.Metrics;
import org.dromara.hertzbeat.common.entity.message.CollectRep;
import org.dromara.hertzbeat.common.queue.CommonDataQueue;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Indicator group collection task and response data scheduler
 * 指标组采集任务与响应数据调度器
 *
 * @author tomsun28
 */
@Component
@Slf4j
public class CommonDispatcher implements MetricsTaskDispatch, CollectDataDispatch, DisposableBean {

    /**
     * Metric group collection task timeout value
     * 指标组采集任务超时时间值
     */
    private static final long DURATION_TIME = 240_000L;
    /**
     * trigger sub task max num
     * 触发子任务最大数量
     */
    private static final int MAX_SUB_TASK_NUM = 50;
    private static final Gson GSON = new Gson();
    /**
     * Priority queue of index group collection tasks
     * 指标组采集任务优先级队列
     */
    private final MetricsCollectorQueue jobRequestQueue;
    /**
     * Time round task scheduler
     * 时间轮任务调度器
     */
    private final TimerDispatch timerDispatch;
    /**
     * collection data exporter
     * 采集数据导出器
     */
    private final CommonDataQueue commonDataQueue;
    /**
     * Metric group task and start time mapping map
     * 指标组任务与开始时间映射map
     */
    private final Map<String, MetricsTime> metricsTimeoutMonitorMap;

    private final List<UnitConvert> unitConvertList;

    private final ThreadPoolExecutor poolExecutor;

    private final WorkerPool workerPool;
    
    private final String collectorIdentity;

    public CommonDispatcher(MetricsCollectorQueue jobRequestQueue,
                            TimerDispatch timerDispatch,
                            CommonDataQueue commonDataQueue,
                            WorkerPool workerPool,
                            CollectJobService collectJobService,
                            List<UnitConvert> unitConvertList) {
        this.commonDataQueue = commonDataQueue;
        this.jobRequestQueue = jobRequestQueue;
        this.timerDispatch = timerDispatch;
        this.unitConvertList = unitConvertList;
        this.workerPool = workerPool;
        this.collectorIdentity = collectJobService.getCollectorIdentity();
        this.metricsTimeoutMonitorMap = new ConcurrentHashMap<>(16);
        poolExecutor = new ThreadPoolExecutor(2, 2, 1,
                TimeUnit.SECONDS,
                new SynchronousQueue<>(), r -> {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            return thread;
        });
        this.start();
    }

    public void start() {
        try {
            // Pull the indicator group collection task from the task queue and put it into the thread pool for execution
            // 从任务队列拉取指标组采集任务放入线程池执行
            poolExecutor.execute(() -> {
                Thread.currentThread().setName("metrics-task-dispatcher");
                while (!Thread.currentThread().isInterrupted()) {
                    MetricsCollect metricsCollect = null;
                    try {
                        metricsCollect = jobRequestQueue.getJob();
                        if (metricsCollect != null) {
                            workerPool.executeJob(metricsCollect);
                        }
                    } catch (RejectedExecutionException rejected) {
                        log.info("[Dispatcher]-the worker pool is full, reject this metrics task, " +
                                "sleep and put in queue again.");
                        try {
                            Thread.sleep(1000);
                            if (metricsCollect != null) {
                                // 在队列里的优先级增大
                                metricsCollect.setRunPriority((byte) (metricsCollect.getRunPriority() + 1));
                                jobRequestQueue.addJob(metricsCollect);
                            }
                        } catch (InterruptedException ignored) {
                            log.info("[Dispatcher]-metrics-task-dispatcher has been interrupt when sleep to close.");
                            Thread.currentThread().interrupt();
                        }
                    } catch (InterruptedException interruptedException) {
                        log.info("[Dispatcher]-metrics-task-dispatcher has been interrupt to close.");
                        Thread.currentThread().interrupt();
                    } catch (Exception e) {
                        log.error("[Dispatcher]-{}.", e.getMessage(), e);
                    }
                }
            });
            // Monitoring indicator group collection task execution t
            // 监控指标组采集任务执行时间
            poolExecutor.execute(() -> {
                Thread.currentThread().setName("metrics-task-monitor");
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        // Detect whether the collection unit of each indicator group has timed out for 4 minutes, and if it times out, it will be discarded and an exception will be returned.
                        // 检测每个指标组采集单元是否超时4分钟,超时则丢弃并返回异常
                        long deadline = System.currentTimeMillis() - DURATION_TIME;
                        for (Map.Entry<String, MetricsTime> entry : metricsTimeoutMonitorMap.entrySet()) {
                            MetricsTime metricsTime = entry.getValue();
                            if (metricsTime.getStartTime() < deadline) {
                                // Metric group collection timeout      指标组采集超时
                                WheelTimerTask timerJob = (WheelTimerTask) metricsTime.getTimeout().task();
                                CollectRep.MetricsData metricsData = CollectRep.MetricsData.newBuilder()
                                        .setId(timerJob.getJob().getMonitorId())
                                        .setTenantId(timerJob.getJob().getTenantId())
                                        .setApp(timerJob.getJob().getApp())
                                        .setMetrics(metricsTime.getMetrics().getName())
                                        .setPriority(metricsTime.getMetrics().getPriority())
                                        .setTime(System.currentTimeMillis())
                                        .setCode(CollectRep.Code.TIMEOUT).setMsg("collect timeout").build();
                                log.error("[Collect Timeout]: \n{}", metricsData);
                                if (metricsData.getPriority() == 0) {
                                    dispatchCollectData(metricsTime.timeout, metricsTime.getMetrics(), metricsData);
                                }
                                metricsTimeoutMonitorMap.remove(entry.getKey());
                            }
                        }
                        Thread.sleep(20000);
                    } catch (InterruptedException interruptedException) {
                        log.info("[Dispatcher]-metrics-task-monitor has been interrupt to close.");
                        Thread.currentThread().interrupt();
                    } catch (Exception e) {
                        log.error("[Task Monitor]-{}.", e.getMessage(), e);
                    }
                }
            });
        } catch (Exception e) {
            log.error("Common Dispatcher error: {}.", e.getMessage(), e);
        }
    }

    @Override
    public void dispatchMetricsTask(Timeout timeout) {
        // Divide the collection task of a single application into corresponding collection tasks of the indicator group according to the indicator group under it. AbstractCollect
        //Put each indicator group into the thread pool for scheduling
        // 将单个应用的采集任务根据其下的指标组拆分为对应的指标组采集任务 AbstractCollect
        // 将每个指标组放入线程池进行调度
        WheelTimerTask timerTask = (WheelTimerTask) timeout.task();
        Job job = timerTask.getJob();
        job.constructPriorMetrics();
        Set<Metrics> metricsSet = job.getNextCollectMetrics(null, true);
        metricsSet.forEach(metrics -> {
            MetricsCollect metricsCollect = new MetricsCollect(metrics, timeout, this, 
                    collectorIdentity, unitConvertList);
            jobRequestQueue.addJob(metricsCollect);
            if (metrics.getPrometheus() != null) {
                metricsTimeoutMonitorMap.put(String.valueOf(job.getId()),
                        new MetricsTime(System.currentTimeMillis(), metrics, timeout));
            } else {
                metricsTimeoutMonitorMap.put(job.getId() + "-" + metrics.getName(),
                        new MetricsTime(System.currentTimeMillis(), metrics, timeout));   
            }
        });
    }

    @Override
    public void dispatchCollectData(Timeout timeout, Metrics metrics, CollectRep.MetricsData metricsData) {
        WheelTimerTask timerJob = (WheelTimerTask) timeout.task();
        Job job = timerJob.getJob();
        if (metrics.isHasSubTask()) {
            metricsTimeoutMonitorMap.remove(job.getId() + "-" + metrics.getName() + "-sub-" + metrics.getSubTaskId());
            boolean isLastTask = metrics.consumeSubTaskResponse(metricsData);
            if (isLastTask) {
                metricsData = metrics.getSubTaskDataRef().get();
            } else {
                return;
            }
        } else {
            metricsTimeoutMonitorMap.remove(job.getId() + "-" + metrics.getName());
        }
        Set<Metrics> metricsSet = job.getNextCollectMetrics(metrics, false);
        if (job.isCyclic()) {
            // If it is an asynchronous periodic cyclic task, directly send the collected data of the indicator group to the message middleware
            // 若是异步的周期性循环任务,直接发送指标组的采集数据到消息中间件
            commonDataQueue.sendMetricsData(metricsData);
            if (log.isDebugEnabled()) {
                log.debug("Cyclic Job: {} - {} - {}", job.getMonitorId(), job.getApp(), metricsData.getMetrics());
                for (CollectRep.ValueRow valueRow : metricsData.getValuesList()) {
                    for (CollectRep.Field field : metricsData.getFieldsList()) {
                        log.debug("Field-->{},Value-->{}", field.getName(), valueRow.getColumns(metricsData.getFieldsList().indexOf(field)));
                    }
                }
            }
            //If metricsSet is null, it means that the execution is completed or whether the priority of the collection indicator group is 0, that is, the availability collection indicator group.
            // If the availability collection fails, the next indicator group scheduling will be cancelled and the next round of scheduling will be entered directly.
            // 若metricsSet为null表示执行完成
            // 或判断采集指标组是否优先级为0，即为可用性采集指标组 若可用性采集失败 则取消后面的指标组调度直接进入下一轮调度
            boolean isAvailableCollectFailed = metricsSet != null && !metricsSet.isEmpty()
                    && metrics.getPriority() == (byte) 0 && metricsData.getCode() != CollectRep.Code.SUCCESS;
            if (metricsSet == null || isAvailableCollectFailed) {
                // The collection and execution of all index groups of this job are completed.
                // The periodic task pushes the task to the time wheel again.
                // First, determine the execution time of the task and the task collection interval.
                // 此Job所有指标组采集执行完成
                // 周期性任务再次将任务push到时间轮
                // 先判断此次任务执行时间与任务采集间隔时间
                if (timeout.isCancelled()) {
                    return;
                }
                long spendTime = System.currentTimeMillis() - job.getDispatchTime();
                long interval = job.getInterval() - spendTime / 1000;
                interval = interval <= 0 ? 0 : interval;
                // Reset Construction Execution Metrics Group View  重置构造执行指标组视图
                job.constructPriorMetrics();
                timerDispatch.cyclicJob(timerJob, interval, TimeUnit.SECONDS);
            } else if (!metricsSet.isEmpty()) {
                // The execution of the current level indicator group is completed, and the execution of the next level indicator group starts
                // 当前级别指标组执行完成，开始执行下一级别的指标组
                // use pre collect metrics data to replace next metrics config params
                List<Map<String, Configmap>> configmapList = getConfigmapFromPreCollectData(metricsData);
                for (Metrics metricItem : metricsSet) {
                    if (CollectionUtils.isEmpty(configmapList) || CollectUtil.notContainCryPlaceholder(GSON.toJsonTree(metricItem))) {
                        MetricsCollect metricsCollect = new MetricsCollect(metricItem, timeout, this, 
                                collectorIdentity, unitConvertList);
                        jobRequestQueue.addJob(metricsCollect);
                        metricsTimeoutMonitorMap.put(job.getId() + "-" + metricItem.getName(),
                                new MetricsTime(System.currentTimeMillis(), metricItem, timeout));
                        continue;
                    }

                    int subTaskNum = Math.min(configmapList.size(), MAX_SUB_TASK_NUM);
                    AtomicInteger subTaskNumAtomic = new AtomicInteger(subTaskNum);
                    AtomicReference<CollectRep.MetricsData> metricsDataReference = new AtomicReference<>();
                    for (int index = 0; index < subTaskNum; index++) {
                        Map<String, Configmap> configmap = configmapList.get(index);
                        JsonElement metricJson = GSON.toJsonTree(metricItem);
                        CollectUtil.replaceCryPlaceholder(metricJson, configmap);
                        Metrics metric = GSON.fromJson(metricJson, Metrics.class);
                        metric.setSubTaskNum(subTaskNumAtomic);
                        metric.setSubTaskId(index);
                        metric.setSubTaskDataRef(metricsDataReference);
                        MetricsCollect metricsCollect = new MetricsCollect(metric, timeout, this,
                                collectorIdentity, unitConvertList);
                        jobRequestQueue.addJob(metricsCollect);
                        metricsTimeoutMonitorMap.put(job.getId() + "-" + metric.getName() + "-sub-" + index,
                                new MetricsTime(System.currentTimeMillis(), metric, timeout));
                    }

                }
            } else {
                // The list of indicator groups at the current execution level has not been fully executed.
                // It needs to wait for the execution of other indicator groups of the same level to complete the execution and enter the next level for execution.
                // 当前执行级别的指标组列表未全执行完成,
                // 需等待其它同级别指标组执行完成后进入下一级别执行
            }
        } else {
            // If it is a temporary one-time task, you need to wait for the collected data of all indicator groups to be packaged and returned.
            // Insert the current indicator group data into the job for unified assembly
            // 若是临时性一次任务,需等待所有指标组的采集数据统一包装返回
            // 将当前指标组数据插入job里统一组装
            job.addCollectMetricsData(metricsData);
            if (log.isDebugEnabled()) {
                log.debug("One-time Job: {}", metricsData.getMetrics());
                for (CollectRep.ValueRow valueRow : metricsData.getValuesList()) {
                    for (CollectRep.Field field : metricsData.getFieldsList()) {
                        log.debug("Field-->{},Value-->{}", field.getName(), valueRow.getColumns(metricsData.getFieldsList().indexOf(field)));
                    }
                }
            }
            if (metricsSet == null) {
                // The collection and execution of all indicator groups of this job are completed
                // and the result listener is notified of the combination of all indicator group data
                // 此Job所有指标组采集执行完成
                // 将所有指标组数据组合一起通知结果监听器
                timerDispatch.responseSyncJobData(job.getId(), job.getResponseDataTemp());
            } else if (!metricsSet.isEmpty()) {
                // The execution of the current level indicator group is completed, and the execution of the next level indicator group starts
                // 当前级别指标组执行完成，开始执行下一级别的指标组
                metricsSet.forEach(metricItem -> {
                    MetricsCollect metricsCollect = new MetricsCollect(metricItem, timeout, this,
                            collectorIdentity, unitConvertList);
                    jobRequestQueue.addJob(metricsCollect);
                    metricsTimeoutMonitorMap.put(job.getId() + "-" + metricItem.getName(),
                            new MetricsTime(System.currentTimeMillis(), metricItem, timeout));
                });
            } else {
                // The list of indicator groups at the current execution level has not been fully executed.
                // It needs to wait for the execution of other indicator groups of the same level to complete the execution and enter the next level for execution.
                // 当前执行级别的指标组列表未全执行完成,
                // 需等待其它同级别指标组执行完成后进入下一级别执行
            }
        }
    }

    @Override
    public void dispatchCollectData(Timeout timeout, Metrics metrics, List<CollectRep.MetricsData> metricsDataList) {
        WheelTimerTask timerJob = (WheelTimerTask) timeout.task();
        Job job = timerJob.getJob();
        metricsTimeoutMonitorMap.remove(String.valueOf(job.getId()));
        if (job.isCyclic()) {
            // If it is an asynchronous periodic cyclic task, directly send the collected data of the indicator group to the message middleware
            // 若是异步的周期性循环任务,直接发送指标组的采集数据到消息中间件
            metricsDataList.forEach(commonDataQueue::sendMetricsData);
            // The collection and execution of all index groups of this job are completed.
            // The periodic task pushes the task to the time wheel again.
            // First, determine the execution time of the task and the task collection interval.
            // 此Job所有指标组采集执行完成
            // 周期性任务再次将任务push到时间轮
            // 先判断此次任务执行时间与任务采集间隔时间
            if (timeout.isCancelled()) {
                return;
            }
            long spendTime = System.currentTimeMillis() - job.getDispatchTime();
            long interval = job.getInterval() - spendTime / 1000;
            interval = interval <= 0 ? 0 : interval;
            // Reset Construction Execution Metrics Group View  重置构造执行指标组视图
            job.constructPriorMetrics();
            timerDispatch.cyclicJob(timerJob, interval, TimeUnit.SECONDS);
        } else {
            // The collection and execution of all indicator groups of this job are completed
            // and the result listener is notified of the combination of all indicator group data
            timerDispatch.responseSyncJobData(job.getId(), metricsDataList);
        }
        
    }

    private List<Map<String, Configmap>> getConfigmapFromPreCollectData(CollectRep.MetricsData metricsData) {
        if (metricsData.getValuesCount() <= 0 || metricsData.getFieldsCount() <= 0) {
            return null;
        }
        List<Map<String, Configmap>> mapList = new LinkedList<>();
        for (CollectRep.ValueRow valueRow : metricsData.getValuesList()) {
            if (valueRow.getColumnsCount() != metricsData.getFieldsCount()) {
                continue;
            }
            Map<String, Configmap> configmapMap = new HashMap<>(valueRow.getColumnsCount());
            int index = 0;
            for (CollectRep.Field field : metricsData.getFieldsList()) {
                String value = valueRow.getColumns(index);
                index++;
                Configmap configmap = new Configmap(field.getName(), value, Integer.valueOf(field.getType()).byteValue());
                configmapMap.put(field.getName(), configmap);
            }
            mapList.add(configmapMap);
        }
        return mapList;
    }
    
    @Override
    public void destroy() throws Exception {
        if (poolExecutor != null) {
            poolExecutor.shutdownNow();
        }
    }
    
    @Data
    @AllArgsConstructor
    private static class MetricsTime {
        private long startTime;
        private Metrics metrics;
        private Timeout timeout;
    }
}
