package com.test.changes.repository;

import com.test.changes.domain.Customers;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Customers entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CustomersRepository extends JpaRepository<Customers, Long>, JpaSpecificationExecutor<Customers> {}
