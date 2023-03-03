package com.test.changes.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.test.changes.domain.Colors;
import com.test.changes.repository.ColorsRepository;
import com.test.changes.repository.search.ColorsSearchRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Colors}.
 */
@Service
@Transactional
public class ColorsService {

    private final Logger log = LoggerFactory.getLogger(ColorsService.class);

    private final ColorsRepository colorsRepository;

    private final ColorsSearchRepository colorsSearchRepository;

    public ColorsService(ColorsRepository colorsRepository, ColorsSearchRepository colorsSearchRepository) {
        this.colorsRepository = colorsRepository;
        this.colorsSearchRepository = colorsSearchRepository;
    }

    /**
     * Save a colors.
     *
     * @param colors the entity to save.
     * @return the persisted entity.
     */
    public Colors save(Colors colors) {
        log.debug("Request to save Colors : {}", colors);
        Colors result = colorsRepository.save(colors);
        colorsSearchRepository.index(result);
        return result;
    }

    /**
     * Update a colors.
     *
     * @param colors the entity to save.
     * @return the persisted entity.
     */
    public Colors update(Colors colors) {
        log.debug("Request to update Colors : {}", colors);
        Colors result = colorsRepository.save(colors);
        colorsSearchRepository.index(result);
        return result;
    }

    /**
     * Partially update a colors.
     *
     * @param colors the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Colors> partialUpdate(Colors colors) {
        log.debug("Request to partially update Colors : {}", colors);

        return colorsRepository
            .findById(colors.getId())
            .map(existingColors -> {
                if (colors.getColoruid() != null) {
                    existingColors.setColoruid(colors.getColoruid());
                }
                if (colors.getName() != null) {
                    existingColors.setName(colors.getName());
                }

                return existingColors;
            })
            .map(colorsRepository::save)
            .map(savedColors -> {
                colorsSearchRepository.save(savedColors);

                return savedColors;
            });
    }

    /**
     * Get all the colors.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Colors> findAll(Pageable pageable) {
        log.debug("Request to get all Colors");
        return colorsRepository.findAll(pageable);
    }

    /**
     * Get one colors by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Colors> findOne(Long id) {
        log.debug("Request to get Colors : {}", id);
        return colorsRepository.findById(id);
    }

    /**
     * Delete the colors by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Colors : {}", id);
        colorsRepository.deleteById(id);
        colorsSearchRepository.deleteById(id);
    }

    /**
     * Search for the colors corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Colors> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Colors for query {}", query);
        return colorsSearchRepository.search(query, pageable);
    }
}
