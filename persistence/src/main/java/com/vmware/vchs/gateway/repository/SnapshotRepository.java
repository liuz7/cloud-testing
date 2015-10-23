package com.vmware.vchs.gateway.repository;

import com.vmware.vchs.condition.DataSourceCondition;
import com.vmware.vchs.gateway.model.SnapshotResource;
import org.springframework.context.annotation.Conditional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by georgeliu on 15/7/16.
 */
@Conditional(value = DataSourceCondition.class)
@Transactional(readOnly = false)
public interface SnapshotRepository extends PagingAndSortingRepository<SnapshotResource, Integer> {
    SnapshotResource findByGuid(String guid);

    @Query("select count(id) from SnapshotResource where isCompact = 'Y' and instanceId = :instanceId")
    int getCompactCount(@Param("instanceId") String instanceId);

    @Query("select count(id) from SnapshotResource where instanceId = :instanceId")
    int getCount(@Param("instanceId") String instanceId);

    @Query("select b from SnapshotResource b where b.instanceId = :instanceId")
    List<SnapshotResource> listSnapshots(@Param("instanceId") String instanceId);

//    @Query("select b from SnapshotResource b where b.guid = :guid")
//    List<BackupResource> listBackups(@Param("guid") String guid);

}
