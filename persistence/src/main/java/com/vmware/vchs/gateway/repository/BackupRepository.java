package com.vmware.vchs.gateway.repository;

import com.vmware.vchs.condition.DataSourceCondition;
import com.vmware.vchs.gateway.model.BackupResource;
import com.vmware.vchs.gateway.model.Resource;
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
public interface BackupRepository extends PagingAndSortingRepository<BackupResource, Integer> {

    BackupResource findByGuidAndStatusNot(String guid, Resource.State state);

    BackupResource findByResourceUri(String resourceUrl);

    @Query("select b from BackupResource b where b.instanceId = :instanceId")
    List<BackupResource> listBackups(@Param("instanceId") String instanceId);

}
