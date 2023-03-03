package com.test.changes.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.test.changes.domain.Cars;
import com.test.changes.repository.CarsRepository;
import com.test.changes.repository.search.CarsSearchRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Cars}.
 */
@Service
@Transactional
public class CarsService {

    private final Logger log = LoggerFactory.getLogger(CarsService.class);

    private final CarsRepository carsRepository;

    private final CarsSearchRepository carsSearchRepository;

    public CarsService(CarsRepository carsRepository, CarsSearchRepository carsSearchRepository) {
        this.carsRepository = carsRepository;
        this.carsSearchRepository = carsSearchRepository;
    }

    /**
     * Save a cars.
     *
     * @param cars the entity to save.
     * @return the persisted entity.
     */
    public Cars save(Cars cars) {
        log.debug("Request to save Cars : {}", cars);
        Cars result = carsRepository.save(cars);
        carsSearchRepository.index(result);
        return result;
    }

    /**
     * Update a cars.
     *
     * @param cars the entity to save.
     * @return the persisted entity.
     */
    public Cars update(Cars cars) {
        log.debug("Request to update Cars : {}", cars);
        Cars result = carsRepository.save(cars);
        carsSearchRepository.index(result);
        return result;
    }

    /**
     * Partially update a cars.
     *
     * @param cars the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Cars> partialUpdate(Cars cars) {
        log.debug("Request to partially update Cars : {}", cars);

        return carsRepository
            .findById(cars.getId())
            .map(existingCars -> {
                if (cars.getCaruid() != null) {
                    existingCars.setCaruid(cars.getCaruid());
                }
                if (cars.getName() != null) {
                    existingCars.setName(cars.getName());
                }

                return existingCars;
            })
            .map(carsRepository::save)
            .map(savedCars -> {
                carsSearchRepository.save(savedCars);

                return savedCars;
            });
    }

    /**
     * Get all the cars.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Cars> findAll(Pageable pageable) {
        log.debug("Request to get all Cars");
        return carsRepository.findAll(pageable);
    }

    /**
     * Get all the cars with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<Cars> findAllWithEagerRelationships(Pageable pageable) {
        return carsRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Get one cars by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Cars> findOne(Long id) {
        log.debug("Request to get Cars : {}", id);
        return carsRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the cars by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Cars : {}", id);
        carsRepository.deleteById(id);
        carsSearchRepository.deleteById(id);
    }

    /**
     * Search for the cars corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Cars> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Cars for query {}", query);
        return carsSearchRepository.search(query, pageable);
    }
}
