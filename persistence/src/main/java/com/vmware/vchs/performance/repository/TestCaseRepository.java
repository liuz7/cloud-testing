package com.vmware.vchs.performance.repository;

import com.vmware.vchs.performance.model.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by georgeliu on 14/11/29.
 */
@Transactional(readOnly = false)
public interface TestCaseRepository extends JpaRepository<TestCase, Long> {

    List<TestCase> findByTestName(String testName);
}
