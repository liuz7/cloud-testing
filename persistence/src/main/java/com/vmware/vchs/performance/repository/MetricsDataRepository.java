package com.vmware.vchs.performance.repository;

import com.vmware.vchs.performance.model.LoggingData;
import com.vmware.vchs.performance.model.MetricsData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by georgeliu on 14/11/29.
 */
@Transactional(readOnly = false)
public interface MetricsDataRepository extends JpaRepository<MetricsData, Long> {

    List<LoggingData> findById(String id);
}
