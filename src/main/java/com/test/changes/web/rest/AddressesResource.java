package com.test.changes.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.test.changes.domain.Addresses;
import com.test.changes.repository.AddressesRepository;
import com.test.changes.service.AddressesQueryService;
import com.test.changes.service.AddressesService;
import com.test.changes.service.criteria.AddressesCriteria;
import com.test.changes.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.test.changes.domain.Addresses}.
 */
@RestController
@RequestMapping("/api")
public class AddressesResource {

    private final Logger log = LoggerFactory.getLogger(AddressesResource.class);

    private static final String ENTITY_NAME = "testChangesAddresses";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AddressesService addressesService;

    private final AddressesRepository addressesRepository;

    private final AddressesQueryService addressesQueryService;

    public AddressesResource(
        AddressesService addressesService,
        AddressesRepository addressesRepository,
        AddressesQueryService addressesQueryService
    ) {
        this.addressesService = addressesService;
        this.addressesRepository = addressesRepository;
        this.addressesQueryService = addressesQueryService;
    }

    /**
     * {@code POST  /addresses} : Create a new addresses.
     *
     * @param addresses the addresses to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new addresses, or with status {@code 400 (Bad Request)} if the addresses has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/addresses")
    public ResponseEntity<Addresses> createAddresses(@Valid @RequestBody Addresses addresses) throws URISyntaxException {
        log.debug("REST request to save Addresses : {}", addresses);
        if (addresses.getId() != null) {
            throw new BadRequestAlertException("A new addresses cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Addresses result = addressesService.save(addresses);
        return ResponseEntity
            .created(new URI("/api/addresses/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /addresses/:id} : Updates an existing addresses.
     *
     * @param id the id of the addresses to save.
     * @param addresses the addresses to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated addresses,
     * or with status {@code 400 (Bad Request)} if the addresses is not valid,
     * or with status {@code 500 (Internal Server Error)} if the addresses couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/addresses/{id}")
    public ResponseEntity<Addresses> updateAddresses(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Addresses addresses
    ) throws URISyntaxException {
        log.debug("REST request to update Addresses : {}, {}", id, addresses);
        if (addresses.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, addresses.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!addressesRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Addresses result = addressesService.update(addresses);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, addresses.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /addresses/:id} : Partial updates given fields of an existing addresses, field will ignore if it is null
     *
     * @param id the id of the addresses to save.
     * @param addresses the addresses to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated addresses,
     * or with status {@code 400 (Bad Request)} if the addresses is not valid,
     * or with status {@code 404 (Not Found)} if the addresses is not found,
     * or with status {@code 500 (Internal Server Error)} if the addresses couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/addresses/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Addresses> partialUpdateAddresses(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Addresses addresses
    ) throws URISyntaxException {
        log.debug("REST request to partial update Addresses partially : {}, {}", id, addresses);
        if (addresses.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, addresses.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!addressesRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Addresses> result = addressesService.partialUpdate(addresses);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, addresses.getId().toString())
        );
    }

    /**
     * {@code GET  /addresses} : get all the addresses.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of addresses in body.
     */
    @GetMapping("/addresses")
    public ResponseEntity<List<Addresses>> getAllAddresses(
        AddressesCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Addresses by criteria: {}", criteria);
        Page<Addresses> page = addressesQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /addresses/count} : count all the addresses.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/addresses/count")
    public ResponseEntity<Long> countAddresses(AddressesCriteria criteria) {
        log.debug("REST request to count Addresses by criteria: {}", criteria);
        return ResponseEntity.ok().body(addressesQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /addresses/:id} : get the "id" addresses.
     *
     * @param id the id of the addresses to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the addresses, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/addresses/{id}")
    public ResponseEntity<Addresses> getAddresses(@PathVariable Long id) {
        log.debug("REST request to get Addresses : {}", id);
        Optional<Addresses> addresses = addressesService.findOne(id);
        return ResponseUtil.wrapOrNotFound(addresses);
    }

    /**
     * {@code DELETE  /addresses/:id} : delete the "id" addresses.
     *
     * @param id the id of the addresses to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/addresses/{id}")
    public ResponseEntity<Void> deleteAddresses(@PathVariable Long id) {
        log.debug("REST request to delete Addresses : {}", id);
        addressesService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/addresses?query=:query} : search for the addresses corresponding
     * to the query.
     *
     * @param query the query of the addresses search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/addresses")
    public ResponseEntity<List<Addresses>> searchAddresses(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of Addresses for query {}", query);
        Page<Addresses> page = addressesService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
