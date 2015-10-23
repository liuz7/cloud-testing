package com.vmware.vchs.billing.repository;

import com.vmware.vchs.billing.model.Event;
import com.vmware.vchs.condition.DataSourceCondition;
import org.springframework.context.annotation.Conditional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by georgeliu on 15/7/16.
 */
@Conditional(value = DataSourceCondition.class)
@Transactional(readOnly = false)
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByInstanceId(String instanceId);

    void deleteByInstanceId(String instanceId);

    @Query("select count(event_id) from Event")
    int getCount();
}
