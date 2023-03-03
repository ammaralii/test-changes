package com.test.changes.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.test.changes.domain.ShippingDetails;
import com.test.changes.repository.ShippingDetailsRepository;
import com.test.changes.repository.search.ShippingDetailsSearchRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ShippingDetails}.
 */
@Service
@Transactional
public class ShippingDetailsService {

    private final Logger log = LoggerFactory.getLogger(ShippingDetailsService.class);

    private final ShippingDetailsRepository shippingDetailsRepository;

    private final ShippingDetailsSearchRepository shippingDetailsSearchRepository;

    public ShippingDetailsService(
        ShippingDetailsRepository shippingDetailsRepository,
        ShippingDetailsSearchRepository shippingDetailsSearchRepository
    ) {
        this.shippingDetailsRepository = shippingDetailsRepository;
        this.shippingDetailsSearchRepository = shippingDetailsSearchRepository;
    }

    /**
     * Save a shippingDetails.
     *
     * @param shippingDetails the entity to save.
     * @return the persisted entity.
     */
    public ShippingDetails save(ShippingDetails shippingDetails) {
        log.debug("Request to save ShippingDetails : {}", shippingDetails);
        ShippingDetails result = shippingDetailsRepository.save(shippingDetails);
        shippingDetailsSearchRepository.index(result);
        return result;
    }

    /**
     * Update a shippingDetails.
     *
     * @param shippingDetails the entity to save.
     * @return the persisted entity.
     */
    public ShippingDetails update(ShippingDetails shippingDetails) {
        log.debug("Request to update ShippingDetails : {}", shippingDetails);
        ShippingDetails result = shippingDetailsRepository.save(shippingDetails);
        shippingDetailsSearchRepository.index(result);
        return result;
    }

    /**
     * Partially update a shippingDetails.
     *
     * @param shippingDetails the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ShippingDetails> partialUpdate(ShippingDetails shippingDetails) {
        log.debug("Request to partially update ShippingDetails : {}", shippingDetails);

        return shippingDetailsRepository
            .findById(shippingDetails.getId())
            .map(existingShippingDetails -> {
                if (shippingDetails.getShippingAddress() != null) {
                    existingShippingDetails.setShippingAddress(shippingDetails.getShippingAddress());
                }
                if (shippingDetails.getShippingMethod() != null) {
                    existingShippingDetails.setShippingMethod(shippingDetails.getShippingMethod());
                }
                if (shippingDetails.getEstimatedDeliveryDate() != null) {
                    existingShippingDetails.setEstimatedDeliveryDate(shippingDetails.getEstimatedDeliveryDate());
                }

                return existingShippingDetails;
            })
            .map(shippingDetailsRepository::save)
            .map(savedShippingDetails -> {
                shippingDetailsSearchRepository.save(savedShippingDetails);

                return savedShippingDetails;
            });
    }

    /**
     * Get all the shippingDetails.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ShippingDetails> findAll(Pageable pageable) {
        log.debug("Request to get all ShippingDetails");
        return shippingDetailsRepository.findAll(pageable);
    }

    /**
     * Get one shippingDetails by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ShippingDetails> findOne(Long id) {
        log.debug("Request to get ShippingDetails : {}", id);
        return shippingDetailsRepository.findById(id);
    }

    /**
     * Delete the shippingDetails by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete ShippingDetails : {}", id);
        shippingDetailsRepository.deleteById(id);
        shippingDetailsSearchRepository.deleteById(id);
    }

    /**
     * Search for the shippingDetails corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ShippingDetails> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of ShippingDetails for query {}", query);
        return shippingDetailsSearchRepository.search(query, pageable);
    }
}
