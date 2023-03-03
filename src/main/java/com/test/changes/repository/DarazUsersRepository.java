package com.test.changes.repository;

import com.test.changes.domain.DarazUsers;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the DarazUsers entity.
 *
 * When extending this class, extend DarazUsersRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
@Repository
public interface DarazUsersRepository
    extends DarazUsersRepositoryWithBagRelationships, JpaRepository<DarazUsers, Long>, JpaSpecificationExecutor<DarazUsers> {
    default Optional<DarazUsers> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findById(id));
    }

    default List<DarazUsers> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAll());
    }

    default Page<DarazUsers> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAll(pageable));
    }
}
