package org.dromara.hertzbeat.push.dao;

import org.dromara.hertzbeat.common.entity.push.PushMetrics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * push metrics dao
 *
 * @author vinci
 */
public interface PushMetricsDao extends JpaRepository<PushMetrics, Long> {
    PushMetrics findFirstByMonitorIdOrderByTimeDesc(Long monitorId);

    void deleteAllByTimeBefore(Long time);
}
