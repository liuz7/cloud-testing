package com.vmware.vchs.performance.repository;

import com.vmware.vchs.performance.model.Request;
import com.vmware.vchs.performance.model.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by georgeliu on 14/11/27.
 */
@Transactional(readOnly = false)
public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findByTestCase(TestCase testCase);
}
