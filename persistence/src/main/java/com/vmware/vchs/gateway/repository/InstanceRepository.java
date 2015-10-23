package com.vmware.vchs.gateway.repository;

import com.vmware.vchs.condition.DataSourceCondition;
import com.vmware.vchs.gateway.model.InstanceResource;
import org.springframework.context.annotation.Conditional;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by georgeliu on 15/7/16.
 */
@Conditional(value = DataSourceCondition.class)
@Transactional(readOnly = false)
public interface InstanceRepository extends PagingAndSortingRepository<InstanceResource, Integer> {

    InstanceResource findByGuid(String guid);

//    @Query("select snap from InstanceResource b where b.instanceId = :instanceId")
//    List<SnapshotResource> listSnapshots(@Param("instanceId") String instanceId);
}
