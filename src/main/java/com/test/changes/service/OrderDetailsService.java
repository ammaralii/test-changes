package com.test.changes.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.test.changes.domain.OrderDetails;
import com.test.changes.repository.OrderDetailsRepository;
import com.test.changes.repository.search.OrderDetailsSearchRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link OrderDetails}.
 */
@Service
@Transactional
public class OrderDetailsService {

    private final Logger log = LoggerFactory.getLogger(OrderDetailsService.class);

    private final OrderDetailsRepository orderDetailsRepository;

    private final OrderDetailsSearchRepository orderDetailsSearchRepository;

    public OrderDetailsService(OrderDetailsRepository orderDetailsRepository, OrderDetailsSearchRepository orderDetailsSearchRepository) {
        this.orderDetailsRepository = orderDetailsRepository;
        this.orderDetailsSearchRepository = orderDetailsSearchRepository;
    }

    /**
     * Save a orderDetails.
     *
     * @param orderDetails the entity to save.
     * @return the persisted entity.
     */
    public OrderDetails save(OrderDetails orderDetails) {
        log.debug("Request to save OrderDetails : {}", orderDetails);
        OrderDetails result = orderDetailsRepository.save(orderDetails);
        orderDetailsSearchRepository.index(result);
        return result;
    }

    /**
     * Update a orderDetails.
     *
     * @param orderDetails the entity to save.
     * @return the persisted entity.
     */
    public OrderDetails update(OrderDetails orderDetails) {
        log.debug("Request to update OrderDetails : {}", orderDetails);
        OrderDetails result = orderDetailsRepository.save(orderDetails);
        orderDetailsSearchRepository.index(result);
        return result;
    }

    /**
     * Partially update a orderDetails.
     *
     * @param orderDetails the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<OrderDetails> partialUpdate(OrderDetails orderDetails) {
        log.debug("Request to partially update OrderDetails : {}", orderDetails);

        return orderDetailsRepository
            .findById(orderDetails.getId())
            .map(existingOrderDetails -> {
                if (orderDetails.getQuantity() != null) {
                    existingOrderDetails.setQuantity(orderDetails.getQuantity());
                }
                if (orderDetails.getAmount() != null) {
                    existingOrderDetails.setAmount(orderDetails.getAmount());
                }

                return existingOrderDetails;
            })
            .map(orderDetailsRepository::save)
            .map(savedOrderDetails -> {
                orderDetailsSearchRepository.save(savedOrderDetails);

                return savedOrderDetails;
            });
    }

    /**
     * Get all the orderDetails.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<OrderDetails> findAll(Pageable pageable) {
        log.debug("Request to get all OrderDetails");
        return orderDetailsRepository.findAll(pageable);
    }

    /**
     * Get one orderDetails by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<OrderDetails> findOne(Long id) {
        log.debug("Request to get OrderDetails : {}", id);
        return orderDetailsRepository.findById(id);
    }

    /**
     * Delete the orderDetails by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete OrderDetails : {}", id);
        orderDetailsRepository.deleteById(id);
        orderDetailsSearchRepository.deleteById(id);
    }

    /**
     * Search for the orderDetails corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<OrderDetails> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of OrderDetails for query {}", query);
        return orderDetailsSearchRepository.search(query, pageable);
    }
}
