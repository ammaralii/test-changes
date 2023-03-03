package com.test.changes.service;

import com.test.changes.domain.*; // for static metamodels
import com.test.changes.domain.Colors;
import com.test.changes.repository.ColorsRepository;
import com.test.changes.repository.search.ColorsSearchRepository;
import com.test.changes.service.criteria.ColorsCriteria;
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
 * Service for executing complex queries for {@link Colors} entities in the database.
 * The main input is a {@link ColorsCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Colors} or a {@link Page} of {@link Colors} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ColorsQueryService extends QueryService<Colors> {

    private final Logger log = LoggerFactory.getLogger(ColorsQueryService.class);

    private final ColorsRepository colorsRepository;

    private final ColorsSearchRepository colorsSearchRepository;

    public ColorsQueryService(ColorsRepository colorsRepository, ColorsSearchRepository colorsSearchRepository) {
        this.colorsRepository = colorsRepository;
        this.colorsSearchRepository = colorsSearchRepository;
    }

    /**
     * Return a {@link List} of {@link Colors} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Colors> findByCriteria(ColorsCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Colors> specification = createSpecification(criteria);
        return colorsRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Colors} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Colors> findByCriteria(ColorsCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Colors> specification = createSpecification(criteria);
        return colorsRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ColorsCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Colors> specification = createSpecification(criteria);
        return colorsRepository.count(specification);
    }

    /**
     * Function to convert {@link ColorsCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Colors> createSpecification(ColorsCriteria criteria) {
        Specification<Colors> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Colors_.id));
            }
            if (criteria.getColoruid() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getColoruid(), Colors_.coloruid));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Colors_.name));
            }
            if (criteria.getCarId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getCarId(), root -> root.join(Colors_.cars, JoinType.LEFT).get(Cars_.id))
                    );
            }
        }
        return specification;
    }
}
