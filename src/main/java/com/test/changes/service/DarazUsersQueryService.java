package com.test.changes.service;

import com.test.changes.domain.*; // for static metamodels
import com.test.changes.domain.DarazUsers;
import com.test.changes.repository.DarazUsersRepository;
import com.test.changes.repository.search.DarazUsersSearchRepository;
import com.test.changes.service.criteria.DarazUsersCriteria;
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
 * Service for executing complex queries for {@link DarazUsers} entities in the database.
 * The main input is a {@link DarazUsersCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link DarazUsers} or a {@link Page} of {@link DarazUsers} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class DarazUsersQueryService extends QueryService<DarazUsers> {

    private final Logger log = LoggerFactory.getLogger(DarazUsersQueryService.class);

    private final DarazUsersRepository darazUsersRepository;

    private final DarazUsersSearchRepository darazUsersSearchRepository;

    public DarazUsersQueryService(DarazUsersRepository darazUsersRepository, DarazUsersSearchRepository darazUsersSearchRepository) {
        this.darazUsersRepository = darazUsersRepository;
        this.darazUsersSearchRepository = darazUsersSearchRepository;
    }

    /**
     * Return a {@link List} of {@link DarazUsers} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<DarazUsers> findByCriteria(DarazUsersCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<DarazUsers> specification = createSpecification(criteria);
        return darazUsersRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link DarazUsers} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<DarazUsers> findByCriteria(DarazUsersCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<DarazUsers> specification = createSpecification(criteria);
        return darazUsersRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(DarazUsersCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<DarazUsers> specification = createSpecification(criteria);
        return darazUsersRepository.count(specification);
    }

    /**
     * Function to convert {@link DarazUsersCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<DarazUsers> createSpecification(DarazUsersCriteria criteria) {
        Specification<DarazUsers> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), DarazUsers_.id));
            }
            if (criteria.getFullName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getFullName(), DarazUsers_.fullName));
            }
            if (criteria.getEmail() != null) {
                specification = specification.and(buildStringSpecification(criteria.getEmail(), DarazUsers_.email));
            }
            if (criteria.getPhone() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPhone(), DarazUsers_.phone));
            }
            if (criteria.getManagerId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getManagerId(),
                            root -> root.join(DarazUsers_.manager, JoinType.LEFT).get(DarazUsers_.id)
                        )
                    );
            }
            if (criteria.getRoleId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getRoleId(), root -> root.join(DarazUsers_.roles, JoinType.LEFT).get(Roles_.id))
                    );
            }
            if (criteria.getAddressesUserId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getAddressesUserId(),
                            root -> root.join(DarazUsers_.addressesUsers, JoinType.LEFT).get(Addresses_.id)
                        )
                    );
            }
            if (criteria.getDarazusersManagerId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getDarazusersManagerId(),
                            root -> root.join(DarazUsers_.darazusersManagers, JoinType.LEFT).get(DarazUsers_.id)
                        )
                    );
            }
            if (criteria.getOrderdeliveryDeliverymanagerId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getOrderdeliveryDeliverymanagerId(),
                            root -> root.join(DarazUsers_.orderdeliveryDeliverymanagers, JoinType.LEFT).get(OrderDelivery_.id)
                        )
                    );
            }
            if (criteria.getOrderdeliveryDeliveryboyId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getOrderdeliveryDeliveryboyId(),
                            root -> root.join(DarazUsers_.orderdeliveryDeliveryboys, JoinType.LEFT).get(OrderDelivery_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
