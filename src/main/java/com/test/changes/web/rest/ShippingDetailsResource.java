package com.test.changes.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.test.changes.domain.ShippingDetails;
import com.test.changes.repository.ShippingDetailsRepository;
import com.test.changes.service.ShippingDetailsQueryService;
import com.test.changes.service.ShippingDetailsService;
import com.test.changes.service.criteria.ShippingDetailsCriteria;
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
 * REST controller for managing {@link com.test.changes.domain.ShippingDetails}.
 */
@RestController
@RequestMapping("/api")
public class ShippingDetailsResource {

    private final Logger log = LoggerFactory.getLogger(ShippingDetailsResource.class);

    private static final String ENTITY_NAME = "testChangesShippingDetails";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ShippingDetailsService shippingDetailsService;

    private final ShippingDetailsRepository shippingDetailsRepository;

    private final ShippingDetailsQueryService shippingDetailsQueryService;

    public ShippingDetailsResource(
        ShippingDetailsService shippingDetailsService,
        ShippingDetailsRepository shippingDetailsRepository,
        ShippingDetailsQueryService shippingDetailsQueryService
    ) {
        this.shippingDetailsService = shippingDetailsService;
        this.shippingDetailsRepository = shippingDetailsRepository;
        this.shippingDetailsQueryService = shippingDetailsQueryService;
    }

    /**
     * {@code POST  /shipping-details} : Create a new shippingDetails.
     *
     * @param shippingDetails the shippingDetails to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new shippingDetails, or with status {@code 400 (Bad Request)} if the shippingDetails has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/shipping-details")
    public ResponseEntity<ShippingDetails> createShippingDetails(@Valid @RequestBody ShippingDetails shippingDetails)
        throws URISyntaxException {
        log.debug("REST request to save ShippingDetails : {}", shippingDetails);
        if (shippingDetails.getId() != null) {
            throw new BadRequestAlertException("A new shippingDetails cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ShippingDetails result = shippingDetailsService.save(shippingDetails);
        return ResponseEntity
            .created(new URI("/api/shipping-details/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /shipping-details/:id} : Updates an existing shippingDetails.
     *
     * @param id the id of the shippingDetails to save.
     * @param shippingDetails the shippingDetails to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated shippingDetails,
     * or with status {@code 400 (Bad Request)} if the shippingDetails is not valid,
     * or with status {@code 500 (Internal Server Error)} if the shippingDetails couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/shipping-details/{id}")
    public ResponseEntity<ShippingDetails> updateShippingDetails(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ShippingDetails shippingDetails
    ) throws URISyntaxException {
        log.debug("REST request to update ShippingDetails : {}, {}", id, shippingDetails);
        if (shippingDetails.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, shippingDetails.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!shippingDetailsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ShippingDetails result = shippingDetailsService.update(shippingDetails);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, shippingDetails.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /shipping-details/:id} : Partial updates given fields of an existing shippingDetails, field will ignore if it is null
     *
     * @param id the id of the shippingDetails to save.
     * @param shippingDetails the shippingDetails to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated shippingDetails,
     * or with status {@code 400 (Bad Request)} if the shippingDetails is not valid,
     * or with status {@code 404 (Not Found)} if the shippingDetails is not found,
     * or with status {@code 500 (Internal Server Error)} if the shippingDetails couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/shipping-details/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ShippingDetails> partialUpdateShippingDetails(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ShippingDetails shippingDetails
    ) throws URISyntaxException {
        log.debug("REST request to partial update ShippingDetails partially : {}, {}", id, shippingDetails);
        if (shippingDetails.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, shippingDetails.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!shippingDetailsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ShippingDetails> result = shippingDetailsService.partialUpdate(shippingDetails);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, shippingDetails.getId().toString())
        );
    }

    /**
     * {@code GET  /shipping-details} : get all the shippingDetails.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of shippingDetails in body.
     */
    @GetMapping("/shipping-details")
    public ResponseEntity<List<ShippingDetails>> getAllShippingDetails(
        ShippingDetailsCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get ShippingDetails by criteria: {}", criteria);
        Page<ShippingDetails> page = shippingDetailsQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /shipping-details/count} : count all the shippingDetails.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/shipping-details/count")
    public ResponseEntity<Long> countShippingDetails(ShippingDetailsCriteria criteria) {
        log.debug("REST request to count ShippingDetails by criteria: {}", criteria);
        return ResponseEntity.ok().body(shippingDetailsQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /shipping-details/:id} : get the "id" shippingDetails.
     *
     * @param id the id of the shippingDetails to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the shippingDetails, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/shipping-details/{id}")
    public ResponseEntity<ShippingDetails> getShippingDetails(@PathVariable Long id) {
        log.debug("REST request to get ShippingDetails : {}", id);
        Optional<ShippingDetails> shippingDetails = shippingDetailsService.findOne(id);
        return ResponseUtil.wrapOrNotFound(shippingDetails);
    }

    /**
     * {@code DELETE  /shipping-details/:id} : delete the "id" shippingDetails.
     *
     * @param id the id of the shippingDetails to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/shipping-details/{id}")
    public ResponseEntity<Void> deleteShippingDetails(@PathVariable Long id) {
        log.debug("REST request to delete ShippingDetails : {}", id);
        shippingDetailsService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/shipping-details?query=:query} : search for the shippingDetails corresponding
     * to the query.
     *
     * @param query the query of the shippingDetails search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/shipping-details")
    public ResponseEntity<List<ShippingDetails>> searchShippingDetails(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of ShippingDetails for query {}", query);
        Page<ShippingDetails> page = shippingDetailsService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
