package com.test.changes.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.test.changes.domain.PaymentMethods;
import com.test.changes.repository.PaymentMethodsRepository;
import com.test.changes.service.PaymentMethodsQueryService;
import com.test.changes.service.PaymentMethodsService;
import com.test.changes.service.criteria.PaymentMethodsCriteria;
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
 * REST controller for managing {@link com.test.changes.domain.PaymentMethods}.
 */
@RestController
@RequestMapping("/api")
public class PaymentMethodsResource {

    private final Logger log = LoggerFactory.getLogger(PaymentMethodsResource.class);

    private static final String ENTITY_NAME = "testChangesPaymentMethods";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PaymentMethodsService paymentMethodsService;

    private final PaymentMethodsRepository paymentMethodsRepository;

    private final PaymentMethodsQueryService paymentMethodsQueryService;

    public PaymentMethodsResource(
        PaymentMethodsService paymentMethodsService,
        PaymentMethodsRepository paymentMethodsRepository,
        PaymentMethodsQueryService paymentMethodsQueryService
    ) {
        this.paymentMethodsService = paymentMethodsService;
        this.paymentMethodsRepository = paymentMethodsRepository;
        this.paymentMethodsQueryService = paymentMethodsQueryService;
    }

    /**
     * {@code POST  /payment-methods} : Create a new paymentMethods.
     *
     * @param paymentMethods the paymentMethods to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new paymentMethods, or with status {@code 400 (Bad Request)} if the paymentMethods has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/payment-methods")
    public ResponseEntity<PaymentMethods> createPaymentMethods(@Valid @RequestBody PaymentMethods paymentMethods)
        throws URISyntaxException {
        log.debug("REST request to save PaymentMethods : {}", paymentMethods);
        if (paymentMethods.getId() != null) {
            throw new BadRequestAlertException("A new paymentMethods cannot already have an ID", ENTITY_NAME, "idexists");
        }
        PaymentMethods result = paymentMethodsService.save(paymentMethods);
        return ResponseEntity
            .created(new URI("/api/payment-methods/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /payment-methods/:id} : Updates an existing paymentMethods.
     *
     * @param id the id of the paymentMethods to save.
     * @param paymentMethods the paymentMethods to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated paymentMethods,
     * or with status {@code 400 (Bad Request)} if the paymentMethods is not valid,
     * or with status {@code 500 (Internal Server Error)} if the paymentMethods couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/payment-methods/{id}")
    public ResponseEntity<PaymentMethods> updatePaymentMethods(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PaymentMethods paymentMethods
    ) throws URISyntaxException {
        log.debug("REST request to update PaymentMethods : {}, {}", id, paymentMethods);
        if (paymentMethods.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, paymentMethods.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!paymentMethodsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        PaymentMethods result = paymentMethodsService.update(paymentMethods);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, paymentMethods.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /payment-methods/:id} : Partial updates given fields of an existing paymentMethods, field will ignore if it is null
     *
     * @param id the id of the paymentMethods to save.
     * @param paymentMethods the paymentMethods to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated paymentMethods,
     * or with status {@code 400 (Bad Request)} if the paymentMethods is not valid,
     * or with status {@code 404 (Not Found)} if the paymentMethods is not found,
     * or with status {@code 500 (Internal Server Error)} if the paymentMethods couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/payment-methods/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PaymentMethods> partialUpdatePaymentMethods(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PaymentMethods paymentMethods
    ) throws URISyntaxException {
        log.debug("REST request to partial update PaymentMethods partially : {}, {}", id, paymentMethods);
        if (paymentMethods.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, paymentMethods.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!paymentMethodsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PaymentMethods> result = paymentMethodsService.partialUpdate(paymentMethods);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, paymentMethods.getId().toString())
        );
    }

    /**
     * {@code GET  /payment-methods} : get all the paymentMethods.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of paymentMethods in body.
     */
    @GetMapping("/payment-methods")
    public ResponseEntity<List<PaymentMethods>> getAllPaymentMethods(
        PaymentMethodsCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get PaymentMethods by criteria: {}", criteria);
        Page<PaymentMethods> page = paymentMethodsQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /payment-methods/count} : count all the paymentMethods.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/payment-methods/count")
    public ResponseEntity<Long> countPaymentMethods(PaymentMethodsCriteria criteria) {
        log.debug("REST request to count PaymentMethods by criteria: {}", criteria);
        return ResponseEntity.ok().body(paymentMethodsQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /payment-methods/:id} : get the "id" paymentMethods.
     *
     * @param id the id of the paymentMethods to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the paymentMethods, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/payment-methods/{id}")
    public ResponseEntity<PaymentMethods> getPaymentMethods(@PathVariable Long id) {
        log.debug("REST request to get PaymentMethods : {}", id);
        Optional<PaymentMethods> paymentMethods = paymentMethodsService.findOne(id);
        return ResponseUtil.wrapOrNotFound(paymentMethods);
    }

    /**
     * {@code DELETE  /payment-methods/:id} : delete the "id" paymentMethods.
     *
     * @param id the id of the paymentMethods to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/payment-methods/{id}")
    public ResponseEntity<Void> deletePaymentMethods(@PathVariable Long id) {
        log.debug("REST request to delete PaymentMethods : {}", id);
        paymentMethodsService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/payment-methods?query=:query} : search for the paymentMethods corresponding
     * to the query.
     *
     * @param query the query of the paymentMethods search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/payment-methods")
    public ResponseEntity<List<PaymentMethods>> searchPaymentMethods(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of PaymentMethods for query {}", query);
        Page<PaymentMethods> page = paymentMethodsService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
