package com.test.changes.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.test.changes.domain.ProductDetails;
import com.test.changes.repository.ProductDetailsRepository;
import com.test.changes.repository.search.ProductDetailsSearchRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ProductDetails}.
 */
@Service
@Transactional
public class ProductDetailsService {

    private final Logger log = LoggerFactory.getLogger(ProductDetailsService.class);

    private final ProductDetailsRepository productDetailsRepository;

    private final ProductDetailsSearchRepository productDetailsSearchRepository;

    public ProductDetailsService(
        ProductDetailsRepository productDetailsRepository,
        ProductDetailsSearchRepository productDetailsSearchRepository
    ) {
        this.productDetailsRepository = productDetailsRepository;
        this.productDetailsSearchRepository = productDetailsSearchRepository;
    }

    /**
     * Save a productDetails.
     *
     * @param productDetails the entity to save.
     * @return the persisted entity.
     */
    public ProductDetails save(ProductDetails productDetails) {
        log.debug("Request to save ProductDetails : {}", productDetails);
        ProductDetails result = productDetailsRepository.save(productDetails);
        productDetailsSearchRepository.index(result);
        return result;
    }

    /**
     * Update a productDetails.
     *
     * @param productDetails the entity to save.
     * @return the persisted entity.
     */
    public ProductDetails update(ProductDetails productDetails) {
        log.debug("Request to update ProductDetails : {}", productDetails);
        ProductDetails result = productDetailsRepository.save(productDetails);
        productDetailsSearchRepository.index(result);
        return result;
    }

    /**
     * Partially update a productDetails.
     *
     * @param productDetails the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ProductDetails> partialUpdate(ProductDetails productDetails) {
        log.debug("Request to partially update ProductDetails : {}", productDetails);

        return productDetailsRepository
            .findById(productDetails.getId())
            .map(existingProductDetails -> {
                if (productDetails.getDescription() != null) {
                    existingProductDetails.setDescription(productDetails.getDescription());
                }
                if (productDetails.getImageUrl() != null) {
                    existingProductDetails.setImageUrl(productDetails.getImageUrl());
                }
                if (productDetails.getIsavailable() != null) {
                    existingProductDetails.setIsavailable(productDetails.getIsavailable());
                }

                return existingProductDetails;
            })
            .map(productDetailsRepository::save)
            .map(savedProductDetails -> {
                productDetailsSearchRepository.save(savedProductDetails);

                return savedProductDetails;
            });
    }

    /**
     * Get all the productDetails.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ProductDetails> findAll(Pageable pageable) {
        log.debug("Request to get all ProductDetails");
        return productDetailsRepository.findAll(pageable);
    }

    /**
     * Get one productDetails by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ProductDetails> findOne(Long id) {
        log.debug("Request to get ProductDetails : {}", id);
        return productDetailsRepository.findById(id);
    }

    /**
     * Delete the productDetails by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete ProductDetails : {}", id);
        productDetailsRepository.deleteById(id);
        productDetailsSearchRepository.deleteById(id);
    }

    /**
     * Search for the productDetails corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ProductDetails> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of ProductDetails for query {}", query);
        return productDetailsSearchRepository.search(query, pageable);
    }
}
