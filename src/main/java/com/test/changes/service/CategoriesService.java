package com.test.changes.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.test.changes.domain.Categories;
import com.test.changes.repository.CategoriesRepository;
import com.test.changes.repository.search.CategoriesSearchRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Categories}.
 */
@Service
@Transactional
public class CategoriesService {

    private final Logger log = LoggerFactory.getLogger(CategoriesService.class);

    private final CategoriesRepository categoriesRepository;

    private final CategoriesSearchRepository categoriesSearchRepository;

    public CategoriesService(CategoriesRepository categoriesRepository, CategoriesSearchRepository categoriesSearchRepository) {
        this.categoriesRepository = categoriesRepository;
        this.categoriesSearchRepository = categoriesSearchRepository;
    }

    /**
     * Save a categories.
     *
     * @param categories the entity to save.
     * @return the persisted entity.
     */
    public Categories save(Categories categories) {
        log.debug("Request to save Categories : {}", categories);
        Categories result = categoriesRepository.save(categories);
        categoriesSearchRepository.index(result);
        return result;
    }

    /**
     * Update a categories.
     *
     * @param categories the entity to save.
     * @return the persisted entity.
     */
    public Categories update(Categories categories) {
        log.debug("Request to update Categories : {}", categories);
        Categories result = categoriesRepository.save(categories);
        categoriesSearchRepository.index(result);
        return result;
    }

    /**
     * Partially update a categories.
     *
     * @param categories the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Categories> partialUpdate(Categories categories) {
        log.debug("Request to partially update Categories : {}", categories);

        return categoriesRepository
            .findById(categories.getId())
            .map(existingCategories -> {
                if (categories.getName() != null) {
                    existingCategories.setName(categories.getName());
                }
                if (categories.getDetail() != null) {
                    existingCategories.setDetail(categories.getDetail());
                }

                return existingCategories;
            })
            .map(categoriesRepository::save)
            .map(savedCategories -> {
                categoriesSearchRepository.save(savedCategories);

                return savedCategories;
            });
    }

    /**
     * Get all the categories.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Categories> findAll(Pageable pageable) {
        log.debug("Request to get all Categories");
        return categoriesRepository.findAll(pageable);
    }

    /**
     * Get one categories by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Categories> findOne(Long id) {
        log.debug("Request to get Categories : {}", id);
        return categoriesRepository.findById(id);
    }

    /**
     * Delete the categories by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Categories : {}", id);
        categoriesRepository.deleteById(id);
        categoriesSearchRepository.deleteById(id);
    }

    /**
     * Search for the categories corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Categories> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Categories for query {}", query);
        return categoriesSearchRepository.search(query, pageable);
    }
}
