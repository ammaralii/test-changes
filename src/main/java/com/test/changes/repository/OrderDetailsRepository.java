package com.test.changes.repository;

import com.test.changes.domain.OrderDetails;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the OrderDetails entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OrderDetailsRepository extends JpaRepository<OrderDetails, Long>, JpaSpecificationExecutor<OrderDetails> {}
