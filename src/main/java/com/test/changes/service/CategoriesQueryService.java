package com.test.changes.service;

import com.test.changes.domain.*; // for static metamodels
import com.test.changes.domain.Categories;
import com.test.changes.repository.CategoriesRepository;
import com.test.changes.repository.search.CategoriesSearchRepository;
import com.test.changes.service.criteria.CategoriesCriteria;
import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Categories} entities in the database.
 * The main input is a {@link CategoriesCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Categories} or a {@link Page} of {@link Categories} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CategoriesQueryService extends QueryService<Categories> {

    private final Logger log = LoggerFactory.getLogger(CategoriesQueryService.class);

    private final CategoriesRepository categoriesRepository;

    private final CategoriesSearchRepository categoriesSearchRepository;

    public CategoriesQueryService(CategoriesRepository categoriesRepository, CategoriesSearchRepository categoriesSearchRepository) {
        this.categoriesRepository = categoriesRepository;
        this.categoriesSearchRepository = categoriesSearchRepository;
    }

    /**
     * Return a {@link List} of {@link Categories} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Categories> findByCriteria(CategoriesCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Categories> specification = createSpecification(criteria);
        return categoriesRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Categories} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Categories> findByCriteria(CategoriesCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Categories> specification = createSpecification(criteria);
        return categoriesRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CategoriesCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Categories> specification = createSpecification(criteria);
        return categoriesRepository.count(specification);
    }

    /**
     * Function to convert {@link CategoriesCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Categories> createSpecification(CategoriesCriteria criteria) {
        Specification<Categories> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Categories_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Categories_.name));
            }
            if (criteria.getDetail() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDetail(), Categories_.detail));
            }
            if (criteria.getProductsCategoryId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getProductsCategoryId(),
                            root -> root.join(Categories_.productsCategories, JoinType.LEFT).get(Products_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
