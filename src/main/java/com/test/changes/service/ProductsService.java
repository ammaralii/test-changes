package com.test.changes.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.test.changes.domain.Products;
import com.test.changes.repository.ProductsRepository;
import com.test.changes.repository.search.ProductsSearchRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Products}.
 */
@Service
@Transactional
public class ProductsService {

    private final Logger log = LoggerFactory.getLogger(ProductsService.class);

    private final ProductsRepository productsRepository;

    private final ProductsSearchRepository productsSearchRepository;

    public ProductsService(ProductsRepository productsRepository, ProductsSearchRepository productsSearchRepository) {
        this.productsRepository = productsRepository;
        this.productsSearchRepository = productsSearchRepository;
    }

    /**
     * Save a products.
     *
     * @param products the entity to save.
     * @return the persisted entity.
     */
    public Products save(Products products) {
        log.debug("Request to save Products : {}", products);
        Products result = productsRepository.save(products);
        productsSearchRepository.index(result);
        return result;
    }

    /**
     * Update a products.
     *
     * @param products the entity to save.
     * @return the persisted entity.
     */
    public Products update(Products products) {
        log.debug("Request to update Products : {}", products);
        Products result = productsRepository.save(products);
        productsSearchRepository.index(result);
        return result;
    }

    /**
     * Partially update a products.
     *
     * @param products the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Products> partialUpdate(Products products) {
        log.debug("Request to partially update Products : {}", products);

        return productsRepository
            .findById(products.getId())
            .map(existingProducts -> {
                if (products.getName() != null) {
                    existingProducts.setName(products.getName());
                }

                return existingProducts;
            })
            .map(productsRepository::save)
            .map(savedProducts -> {
                productsSearchRepository.save(savedProducts);

                return savedProducts;
            });
    }

    /**
     * Get all the products.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Products> findAll(Pageable pageable) {
        log.debug("Request to get all Products");
        return productsRepository.findAll(pageable);
    }

    /**
     * Get one products by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Products> findOne(Long id) {
        log.debug("Request to get Products : {}", id);
        return productsRepository.findById(id);
    }

    /**
     * Delete the products by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Products : {}", id);
        productsRepository.deleteById(id);
        productsSearchRepository.deleteById(id);
    }

    /**
     * Search for the products corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Products> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Products for query {}", query);
        return productsSearchRepository.search(query, pageable);
    }
}
