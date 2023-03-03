package com.test.changes.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.test.changes.domain.OrderDelivery;
import com.test.changes.repository.OrderDeliveryRepository;
import com.test.changes.repository.search.OrderDeliverySearchRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link OrderDelivery}.
 */
@Service
@Transactional
public class OrderDeliveryService {

    private final Logger log = LoggerFactory.getLogger(OrderDeliveryService.class);

    private final OrderDeliveryRepository orderDeliveryRepository;

    private final OrderDeliverySearchRepository orderDeliverySearchRepository;

    public OrderDeliveryService(
        OrderDeliveryRepository orderDeliveryRepository,
        OrderDeliverySearchRepository orderDeliverySearchRepository
    ) {
        this.orderDeliveryRepository = orderDeliveryRepository;
        this.orderDeliverySearchRepository = orderDeliverySearchRepository;
    }

    /**
     * Save a orderDelivery.
     *
     * @param orderDelivery the entity to save.
     * @return the persisted entity.
     */
    public OrderDelivery save(OrderDelivery orderDelivery) {
        log.debug("Request to save OrderDelivery : {}", orderDelivery);
        OrderDelivery result = orderDeliveryRepository.save(orderDelivery);
        orderDeliverySearchRepository.index(result);
        return result;
    }

    /**
     * Update a orderDelivery.
     *
     * @param orderDelivery the entity to save.
     * @return the persisted entity.
     */
    public OrderDelivery update(OrderDelivery orderDelivery) {
        log.debug("Request to update OrderDelivery : {}", orderDelivery);
        OrderDelivery result = orderDeliveryRepository.save(orderDelivery);
        orderDeliverySearchRepository.index(result);
        return result;
    }

    /**
     * Partially update a orderDelivery.
     *
     * @param orderDelivery the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<OrderDelivery> partialUpdate(OrderDelivery orderDelivery) {
        log.debug("Request to partially update OrderDelivery : {}", orderDelivery);

        return orderDeliveryRepository
            .findById(orderDelivery.getId())
            .map(existingOrderDelivery -> {
                if (orderDelivery.getDeliveryDate() != null) {
                    existingOrderDelivery.setDeliveryDate(orderDelivery.getDeliveryDate());
                }
                if (orderDelivery.getDeliveryCharge() != null) {
                    existingOrderDelivery.setDeliveryCharge(orderDelivery.getDeliveryCharge());
                }
                if (orderDelivery.getShippingStatus() != null) {
                    existingOrderDelivery.setShippingStatus(orderDelivery.getShippingStatus());
                }

                return existingOrderDelivery;
            })
            .map(orderDeliveryRepository::save)
            .map(savedOrderDelivery -> {
                orderDeliverySearchRepository.save(savedOrderDelivery);

                return savedOrderDelivery;
            });
    }

    /**
     * Get all the orderDeliveries.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<OrderDelivery> findAll(Pageable pageable) {
        log.debug("Request to get all OrderDeliveries");
        return orderDeliveryRepository.findAll(pageable);
    }

    /**
     * Get one orderDelivery by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<OrderDelivery> findOne(Long id) {
        log.debug("Request to get OrderDelivery : {}", id);
        return orderDeliveryRepository.findById(id);
    }

    /**
     * Delete the orderDelivery by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete OrderDelivery : {}", id);
        orderDeliveryRepository.deleteById(id);
        orderDeliverySearchRepository.deleteById(id);
    }

    /**
     * Search for the orderDelivery corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<OrderDelivery> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of OrderDeliveries for query {}", query);
        return orderDeliverySearchRepository.search(query, pageable);
    }
}
