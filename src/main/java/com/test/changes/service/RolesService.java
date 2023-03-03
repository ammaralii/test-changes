package com.test.changes.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.test.changes.domain.Roles;
import com.test.changes.repository.RolesRepository;
import com.test.changes.repository.search.RolesSearchRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Roles}.
 */
@Service
@Transactional
public class RolesService {

    private final Logger log = LoggerFactory.getLogger(RolesService.class);

    private final RolesRepository rolesRepository;

    private final RolesSearchRepository rolesSearchRepository;

    public RolesService(RolesRepository rolesRepository, RolesSearchRepository rolesSearchRepository) {
        this.rolesRepository = rolesRepository;
        this.rolesSearchRepository = rolesSearchRepository;
    }

    /**
     * Save a roles.
     *
     * @param roles the entity to save.
     * @return the persisted entity.
     */
    public Roles save(Roles roles) {
        log.debug("Request to save Roles : {}", roles);
        Roles result = rolesRepository.save(roles);
        rolesSearchRepository.index(result);
        return result;
    }

    /**
     * Update a roles.
     *
     * @param roles the entity to save.
     * @return the persisted entity.
     */
    public Roles update(Roles roles) {
        log.debug("Request to update Roles : {}", roles);
        Roles result = rolesRepository.save(roles);
        rolesSearchRepository.index(result);
        return result;
    }

    /**
     * Partially update a roles.
     *
     * @param roles the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Roles> partialUpdate(Roles roles) {
        log.debug("Request to partially update Roles : {}", roles);

        return rolesRepository
            .findById(roles.getId())
            .map(existingRoles -> {
                if (roles.getRolePrId() != null) {
                    existingRoles.setRolePrId(roles.getRolePrId());
                }
                if (roles.getName() != null) {
                    existingRoles.setName(roles.getName());
                }

                return existingRoles;
            })
            .map(rolesRepository::save)
            .map(savedRoles -> {
                rolesSearchRepository.save(savedRoles);

                return savedRoles;
            });
    }

    /**
     * Get all the roles.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Roles> findAll(Pageable pageable) {
        log.debug("Request to get all Roles");
        return rolesRepository.findAll(pageable);
    }

    /**
     * Get one roles by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Roles> findOne(Long id) {
        log.debug("Request to get Roles : {}", id);
        return rolesRepository.findById(id);
    }

    /**
     * Delete the roles by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Roles : {}", id);
        rolesRepository.deleteById(id);
        rolesSearchRepository.deleteById(id);
    }

    /**
     * Search for the roles corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Roles> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Roles for query {}", query);
        return rolesSearchRepository.search(query, pageable);
    }
}
