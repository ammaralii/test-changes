package com.test.changes.service;

import com.test.changes.domain.*; // for static metamodels
import com.test.changes.domain.Cars;
import com.test.changes.repository.CarsRepository;
import com.test.changes.repository.search.CarsSearchRepository;
import com.test.changes.service.criteria.CarsCriteria;
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
 * Service for executing complex queries for {@link Cars} entities in the database.
 * The main input is a {@link CarsCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Cars} or a {@link Page} of {@link Cars} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CarsQueryService extends QueryService<Cars> {

    private final Logger log = LoggerFactory.getLogger(CarsQueryService.class);

    private final CarsRepository carsRepository;

    private final CarsSearchRepository carsSearchRepository;

    public CarsQueryService(CarsRepository carsRepository, CarsSearchRepository carsSearchRepository) {
        this.carsRepository = carsRepository;
        this.carsSearchRepository = carsSearchRepository;
    }

    /**
     * Return a {@link List} of {@link Cars} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Cars> findByCriteria(CarsCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Cars> specification = createSpecification(criteria);
        return carsRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Cars} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Cars> findByCriteria(CarsCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Cars> specification = createSpecification(criteria);
        return carsRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CarsCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Cars> specification = createSpecification(criteria);
        return carsRepository.count(specification);
    }

    /**
     * Function to convert {@link CarsCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Cars> createSpecification(CarsCriteria criteria) {
        Specification<Cars> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Cars_.id));
            }
            if (criteria.getCaruid() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCaruid(), Cars_.caruid));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Cars_.name));
            }
            if (criteria.getColorId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getColorId(), root -> root.join(Cars_.colors, JoinType.LEFT).get(Colors_.id))
                    );
            }
        }
        return specification;
    }
}
