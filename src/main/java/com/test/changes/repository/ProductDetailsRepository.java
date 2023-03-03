package com.test.changes.repository;

import com.test.changes.domain.ProductDetails;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ProductDetails entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProductDetailsRepository extends JpaRepository<ProductDetails, Long>, JpaSpecificationExecutor<ProductDetails> {}
