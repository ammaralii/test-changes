package com.test.changes.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.test.changes.domain.Addresses;
import com.test.changes.repository.AddressesRepository;
import com.test.changes.repository.search.AddressesSearchRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Addresses}.
 */
@Service
@Transactional
public class AddressesService {

    private final Logger log = LoggerFactory.getLogger(AddressesService.class);

    private final AddressesRepository addressesRepository;

    private final AddressesSearchRepository addressesSearchRepository;

    public AddressesService(AddressesRepository addressesRepository, AddressesSearchRepository addressesSearchRepository) {
        this.addressesRepository = addressesRepository;
        this.addressesSearchRepository = addressesSearchRepository;
    }

    /**
     * Save a addresses.
     *
     * @param addresses the entity to save.
     * @return the persisted entity.
     */
    public Addresses save(Addresses addresses) {
        log.debug("Request to save Addresses : {}", addresses);
        Addresses result = addressesRepository.save(addresses);
        addressesSearchRepository.index(result);
        return result;
    }

    /**
     * Update a addresses.
     *
     * @param addresses the entity to save.
     * @return the persisted entity.
     */
    public Addresses update(Addresses addresses) {
        log.debug("Request to update Addresses : {}", addresses);
        Addresses result = addressesRepository.save(addresses);
        addressesSearchRepository.index(result);
        return result;
    }

    /**
     * Partially update a addresses.
     *
     * @param addresses the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Addresses> partialUpdate(Addresses addresses) {
        log.debug("Request to partially update Addresses : {}", addresses);

        return addressesRepository
            .findById(addresses.getId())
            .map(existingAddresses -> {
                if (addresses.getStreet() != null) {
                    existingAddresses.setStreet(addresses.getStreet());
                }
                if (addresses.getCity() != null) {
                    existingAddresses.setCity(addresses.getCity());
                }
                if (addresses.getState() != null) {
                    existingAddresses.setState(addresses.getState());
                }

                return existingAddresses;
            })
            .map(addressesRepository::save)
            .map(savedAddresses -> {
                addressesSearchRepository.save(savedAddresses);

                return savedAddresses;
            });
    }

    /**
     * Get all the addresses.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Addresses> findAll(Pageable pageable) {
        log.debug("Request to get all Addresses");
        return addressesRepository.findAll(pageable);
    }

    /**
     * Get one addresses by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Addresses> findOne(Long id) {
        log.debug("Request to get Addresses : {}", id);
        return addressesRepository.findById(id);
    }

    /**
     * Delete the addresses by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Addresses : {}", id);
        addressesRepository.deleteById(id);
        addressesSearchRepository.deleteById(id);
    }

    /**
     * Search for the addresses corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Addresses> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Addresses for query {}", query);
        return addressesSearchRepository.search(query, pageable);
    }
}
