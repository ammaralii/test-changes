package com.test.changes.service;

import com.test.changes.domain.*; // for static metamodels
import com.test.changes.domain.Customers;
import com.test.changes.repository.CustomersRepository;
import com.test.changes.repository.search.CustomersSearchRepository;
import com.test.changes.service.criteria.CustomersCriteria;
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
 * Service for executing complex queries for {@link Customers} entities in the database.
 * The main input is a {@link CustomersCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Customers} or a {@link Page} of {@link Customers} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CustomersQueryService extends QueryService<Customers> {

    private final Logger log = LoggerFactory.getLogger(CustomersQueryService.class);

    private final CustomersRepository customersRepository;

    private final CustomersSearchRepository customersSearchRepository;

    public CustomersQueryService(CustomersRepository customersRepository, CustomersSearchRepository customersSearchRepository) {
        this.customersRepository = customersRepository;
        this.customersSearchRepository = customersSearchRepository;
    }

    /**
     * Return a {@link List} of {@link Customers} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Customers> findByCriteria(CustomersCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Customers> specification = createSpecification(criteria);
        return customersRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Customers} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Customers> findByCriteria(CustomersCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Customers> specification = createSpecification(criteria);
        return customersRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CustomersCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Customers> specification = createSpecification(criteria);
        return customersRepository.count(specification);
    }

    /**
     * Function to convert {@link CustomersCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Customers> createSpecification(CustomersCriteria criteria) {
        Specification<Customers> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Customers_.id));
            }
            if (criteria.getFullName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getFullName(), Customers_.fullName));
            }
            if (criteria.getEmail() != null) {
                specification = specification.and(buildStringSpecification(criteria.getEmail(), Customers_.email));
            }
            if (criteria.getPhone() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPhone(), Customers_.phone));
            }
            if (criteria.getOrdersCustomerId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getOrdersCustomerId(),
                            root -> root.join(Customers_.ordersCustomers, JoinType.LEFT).get(Orders_.id)
                        )
                    );
            }
            if (criteria.getPaymentmethodsCustomerId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getPaymentmethodsCustomerId(),
                            root -> root.join(Customers_.paymentmethodsCustomers, JoinType.LEFT).get(PaymentMethods_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
