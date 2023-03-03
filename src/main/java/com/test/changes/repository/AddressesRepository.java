package com.test.changes.repository;

import com.test.changes.domain.Addresses;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Addresses entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AddressesRepository extends JpaRepository<Addresses, Long>, JpaSpecificationExecutor<Addresses> {}
