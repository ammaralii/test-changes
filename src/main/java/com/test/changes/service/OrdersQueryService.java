package com.test.changes.service;

import com.test.changes.domain.*; // for static metamodels
import com.test.changes.domain.Orders;
import com.test.changes.repository.OrdersRepository;
import com.test.changes.repository.search.OrdersSearchRepository;
import com.test.changes.service.criteria.OrdersCriteria;
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
 * Service for executing complex queries for {@link Orders} entities in the database.
 * The main input is a {@link OrdersCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Orders} or a {@link Page} of {@link Orders} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class OrdersQueryService extends QueryService<Orders> {

    private final Logger log = LoggerFactory.getLogger(OrdersQueryService.class);

    private final OrdersRepository ordersRepository;

    private final OrdersSearchRepository ordersSearchRepository;

    public OrdersQueryService(OrdersRepository ordersRepository, OrdersSearchRepository ordersSearchRepository) {
        this.ordersRepository = ordersRepository;
        this.ordersSearchRepository = ordersSearchRepository;
    }

    /**
     * Return a {@link List} of {@link Orders} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Orders> findByCriteria(OrdersCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Orders> specification = createSpecification(criteria);
        return ordersRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Orders} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Orders> findByCriteria(OrdersCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Orders> specification = createSpecification(criteria);
        return ordersRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(OrdersCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Orders> specification = createSpecification(criteria);
        return ordersRepository.count(specification);
    }

    /**
     * Function to convert {@link OrdersCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Orders> createSpecification(OrdersCriteria criteria) {
        Specification<Orders> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Orders_.id));
            }
            if (criteria.getOrderDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getOrderDate(), Orders_.orderDate));
            }
            if (criteria.getTotalAmount() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getTotalAmount(), Orders_.totalAmount));
            }
            if (criteria.getCustomerId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getCustomerId(), root -> root.join(Orders_.customer, JoinType.LEFT).get(Customers_.id))
                    );
            }
            if (criteria.getOrderdeliveryOrderId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getOrderdeliveryOrderId(),
                            root -> root.join(Orders_.orderdeliveryOrders, JoinType.LEFT).get(OrderDelivery_.id)
                        )
                    );
            }
            if (criteria.getOrderdetailsOrderId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getOrderdetailsOrderId(),
                            root -> root.join(Orders_.orderdetailsOrders, JoinType.LEFT).get(OrderDetails_.id)
                        )
                    );
            }
            if (criteria.getShippingdetailsOrderId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getShippingdetailsOrderId(),
                            root -> root.join(Orders_.shippingdetailsOrders, JoinType.LEFT).get(ShippingDetails_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
