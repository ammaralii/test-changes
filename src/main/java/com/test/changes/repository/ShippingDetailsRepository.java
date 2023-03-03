package com.test.changes.repository;

import com.test.changes.domain.ShippingDetails;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ShippingDetails entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ShippingDetailsRepository extends JpaRepository<ShippingDetails, Long>, JpaSpecificationExecutor<ShippingDetails> {}
