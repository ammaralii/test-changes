package com.test.changes.repository;

import com.test.changes.domain.OrderDelivery;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the OrderDelivery entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OrderDeliveryRepository extends JpaRepository<OrderDelivery, Long>, JpaSpecificationExecutor<OrderDelivery> {}
