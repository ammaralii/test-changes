package com.test.changes.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.test.changes.domain.DarazUsers;
import com.test.changes.repository.DarazUsersRepository;
import com.test.changes.repository.search.DarazUsersSearchRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link DarazUsers}.
 */
@Service
@Transactional
public class DarazUsersService {

    private final Logger log = LoggerFactory.getLogger(DarazUsersService.class);

    private final DarazUsersRepository darazUsersRepository;

    private final DarazUsersSearchRepository darazUsersSearchRepository;

    public DarazUsersService(DarazUsersRepository darazUsersRepository, DarazUsersSearchRepository darazUsersSearchRepository) {
        this.darazUsersRepository = darazUsersRepository;
        this.darazUsersSearchRepository = darazUsersSearchRepository;
    }

    /**
     * Save a darazUsers.
     *
     * @param darazUsers the entity to save.
     * @return the persisted entity.
     */
    public DarazUsers save(DarazUsers darazUsers) {
        log.debug("Request to save DarazUsers : {}", darazUsers);
        DarazUsers result = darazUsersRepository.save(darazUsers);
        darazUsersSearchRepository.index(result);
        return result;
    }

    /**
     * Update a darazUsers.
     *
     * @param darazUsers the entity to save.
     * @return the persisted entity.
     */
    public DarazUsers update(DarazUsers darazUsers) {
        log.debug("Request to update DarazUsers : {}", darazUsers);
        DarazUsers result = darazUsersRepository.save(darazUsers);
        darazUsersSearchRepository.index(result);
        return result;
    }

    /**
     * Partially update a darazUsers.
     *
     * @param darazUsers the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<DarazUsers> partialUpdate(DarazUsers darazUsers) {
        log.debug("Request to partially update DarazUsers : {}", darazUsers);

        return darazUsersRepository
            .findById(darazUsers.getId())
            .map(existingDarazUsers -> {
                if (darazUsers.getFullName() != null) {
                    existingDarazUsers.setFullName(darazUsers.getFullName());
                }
                if (darazUsers.getEmail() != null) {
                    existingDarazUsers.setEmail(darazUsers.getEmail());
                }
                if (darazUsers.getPhone() != null) {
                    existingDarazUsers.setPhone(darazUsers.getPhone());
                }

                return existingDarazUsers;
            })
            .map(darazUsersRepository::save)
            .map(savedDarazUsers -> {
                darazUsersSearchRepository.save(savedDarazUsers);

                return savedDarazUsers;
            });
    }

    /**
     * Get all the darazUsers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<DarazUsers> findAll(Pageable pageable) {
        log.debug("Request to get all DarazUsers");
        return darazUsersRepository.findAll(pageable);
    }

    /**
     * Get all the darazUsers with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<DarazUsers> findAllWithEagerRelationships(Pageable pageable) {
        return darazUsersRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Get one darazUsers by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<DarazUsers> findOne(Long id) {
        log.debug("Request to get DarazUsers : {}", id);
        return darazUsersRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the darazUsers by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete DarazUsers : {}", id);
        darazUsersRepository.deleteById(id);
        darazUsersSearchRepository.deleteById(id);
    }

    /**
     * Search for the darazUsers corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<DarazUsers> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of DarazUsers for query {}", query);
        return darazUsersSearchRepository.search(query, pageable);
    }
}
