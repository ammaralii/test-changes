package com.test.changes.repository;

import com.test.changes.domain.PaymentMethods;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PaymentMethods entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PaymentMethodsRepository extends JpaRepository<PaymentMethods, Long>, JpaSpecificationExecutor<PaymentMethods> {}
