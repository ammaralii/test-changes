package com.test.changes.service;

import com.test.changes.domain.*; // for static metamodels
import com.test.changes.domain.ShippingDetails;
import com.test.changes.repository.ShippingDetailsRepository;
import com.test.changes.repository.search.ShippingDetailsSearchRepository;
import com.test.changes.service.criteria.ShippingDetailsCriteria;
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
 * Service for executing complex queries for {@link ShippingDetails} entities in the database.
 * The main input is a {@link ShippingDetailsCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ShippingDetails} or a {@link Page} of {@link ShippingDetails} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ShippingDetailsQueryService extends QueryService<ShippingDetails> {

    private final Logger log = LoggerFactory.getLogger(ShippingDetailsQueryService.class);

    private final ShippingDetailsRepository shippingDetailsRepository;

    private final ShippingDetailsSearchRepository shippingDetailsSearchRepository;

    public ShippingDetailsQueryService(
        ShippingDetailsRepository shippingDetailsRepository,
        ShippingDetailsSearchRepository shippingDetailsSearchRepository
    ) {
        this.shippingDetailsRepository = shippingDetailsRepository;
        this.shippingDetailsSearchRepository = shippingDetailsSearchRepository;
    }

    /**
     * Return a {@link List} of {@link ShippingDetails} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ShippingDetails> findByCriteria(ShippingDetailsCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<ShippingDetails> specification = createSpecification(criteria);
        return shippingDetailsRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link ShippingDetails} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ShippingDetails> findByCriteria(ShippingDetailsCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ShippingDetails> specification = createSpecification(criteria);
        return shippingDetailsRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ShippingDetailsCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<ShippingDetails> specification = createSpecification(criteria);
        return shippingDetailsRepository.count(specification);
    }

    /**
     * Function to convert {@link ShippingDetailsCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ShippingDetails> createSpecification(ShippingDetailsCriteria criteria) {
        Specification<ShippingDetails> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), ShippingDetails_.id));
            }
            if (criteria.getShippingAddress() != null) {
                specification =
                    specification.and(buildStringSpecification(criteria.getShippingAddress(), ShippingDetails_.shippingAddress));
            }
            if (criteria.getShippingMethod() != null) {
                specification = specification.and(buildSpecification(criteria.getShippingMethod(), ShippingDetails_.shippingMethod));
            }
            if (criteria.getEstimatedDeliveryDate() != null) {
                specification =
                    specification.and(buildRangeSpecification(criteria.getEstimatedDeliveryDate(), ShippingDetails_.estimatedDeliveryDate));
            }
            if (criteria.getOrderId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getOrderId(), root -> root.join(ShippingDetails_.order, JoinType.LEFT).get(Orders_.id))
                    );
            }
            if (criteria.getOrderdeliveryShippingaddressId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getOrderdeliveryShippingaddressId(),
                            root -> root.join(ShippingDetails_.orderdeliveryShippingaddresses, JoinType.LEFT).get(OrderDelivery_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
