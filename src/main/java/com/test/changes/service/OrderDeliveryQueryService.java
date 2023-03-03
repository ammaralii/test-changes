package com.test.changes.service;

import com.test.changes.domain.*; // for static metamodels
import com.test.changes.domain.OrderDelivery;
import com.test.changes.repository.OrderDeliveryRepository;
import com.test.changes.repository.search.OrderDeliverySearchRepository;
import com.test.changes.service.criteria.OrderDeliveryCriteria;
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
 * Service for executing complex queries for {@link OrderDelivery} entities in the database.
 * The main input is a {@link OrderDeliveryCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link OrderDelivery} or a {@link Page} of {@link OrderDelivery} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class OrderDeliveryQueryService extends QueryService<OrderDelivery> {

    private final Logger log = LoggerFactory.getLogger(OrderDeliveryQueryService.class);

    private final OrderDeliveryRepository orderDeliveryRepository;

    private final OrderDeliverySearchRepository orderDeliverySearchRepository;

    public OrderDeliveryQueryService(
        OrderDeliveryRepository orderDeliveryRepository,
        OrderDeliverySearchRepository orderDeliverySearchRepository
    ) {
        this.orderDeliveryRepository = orderDeliveryRepository;
        this.orderDeliverySearchRepository = orderDeliverySearchRepository;
    }

    /**
     * Return a {@link List} of {@link OrderDelivery} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<OrderDelivery> findByCriteria(OrderDeliveryCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<OrderDelivery> specification = createSpecification(criteria);
        return orderDeliveryRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link OrderDelivery} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<OrderDelivery> findByCriteria(OrderDeliveryCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<OrderDelivery> specification = createSpecification(criteria);
        return orderDeliveryRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(OrderDeliveryCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<OrderDelivery> specification = createSpecification(criteria);
        return orderDeliveryRepository.count(specification);
    }

    /**
     * Function to convert {@link OrderDeliveryCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<OrderDelivery> createSpecification(OrderDeliveryCriteria criteria) {
        Specification<OrderDelivery> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), OrderDelivery_.id));
            }
            if (criteria.getDeliveryDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDeliveryDate(), OrderDelivery_.deliveryDate));
            }
            if (criteria.getDeliveryCharge() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDeliveryCharge(), OrderDelivery_.deliveryCharge));
            }
            if (criteria.getShippingStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getShippingStatus(), OrderDelivery_.shippingStatus));
            }
            if (criteria.getOrderId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getOrderId(), root -> root.join(OrderDelivery_.order, JoinType.LEFT).get(Orders_.id))
                    );
            }
            if (criteria.getShippingAddressId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getShippingAddressId(),
                            root -> root.join(OrderDelivery_.shippingAddress, JoinType.LEFT).get(ShippingDetails_.id)
                        )
                    );
            }
            if (criteria.getDeliveryManagerId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getDeliveryManagerId(),
                            root -> root.join(OrderDelivery_.deliveryManager, JoinType.LEFT).get(DarazUsers_.id)
                        )
                    );
            }
            if (criteria.getDeliveryBoyId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getDeliveryBoyId(),
                            root -> root.join(OrderDelivery_.deliveryBoy, JoinType.LEFT).get(DarazUsers_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
