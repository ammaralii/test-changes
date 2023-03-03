package com.test.changes.repository;

import com.test.changes.domain.Categories;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Categories entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CategoriesRepository extends JpaRepository<Categories, Long>, JpaSpecificationExecutor<Categories> {}
