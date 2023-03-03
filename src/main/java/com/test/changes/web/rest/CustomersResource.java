package com.test.changes.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.test.changes.domain.Customers;
import com.test.changes.repository.CustomersRepository;
import com.test.changes.service.CustomersQueryService;
import com.test.changes.service.CustomersService;
import com.test.changes.service.criteria.CustomersCriteria;
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
 * REST controller for managing {@link com.test.changes.domain.Customers}.
 */
@RestController
@RequestMapping("/api")
public class CustomersResource {

    private final Logger log = LoggerFactory.getLogger(CustomersResource.class);

    private static final String ENTITY_NAME = "testChangesCustomers";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CustomersService customersService;

    private final CustomersRepository customersRepository;

    private final CustomersQueryService customersQueryService;

    public CustomersResource(
        CustomersService customersService,
        CustomersRepository customersRepository,
        CustomersQueryService customersQueryService
    ) {
        this.customersService = customersService;
        this.customersRepository = customersRepository;
        this.customersQueryService = customersQueryService;
    }

    /**
     * {@code POST  /customers} : Create a new customers.
     *
     * @param customers the customers to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new customers, or with status {@code 400 (Bad Request)} if the customers has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/customers")
    public ResponseEntity<Customers> createCustomers(@Valid @RequestBody Customers customers) throws URISyntaxException {
        log.debug("REST request to save Customers : {}", customers);
        if (customers.getId() != null) {
            throw new BadRequestAlertException("A new customers cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Customers result = customersService.save(customers);
        return ResponseEntity
            .created(new URI("/api/customers/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /customers/:id} : Updates an existing customers.
     *
     * @param id the id of the customers to save.
     * @param customers the customers to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated customers,
     * or with status {@code 400 (Bad Request)} if the customers is not valid,
     * or with status {@code 500 (Internal Server Error)} if the customers couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/customers/{id}")
    public ResponseEntity<Customers> updateCustomers(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Customers customers
    ) throws URISyntaxException {
        log.debug("REST request to update Customers : {}, {}", id, customers);
        if (customers.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, customers.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!customersRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Customers result = customersService.update(customers);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, customers.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /customers/:id} : Partial updates given fields of an existing customers, field will ignore if it is null
     *
     * @param id the id of the customers to save.
     * @param customers the customers to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated customers,
     * or with status {@code 400 (Bad Request)} if the customers is not valid,
     * or with status {@code 404 (Not Found)} if the customers is not found,
     * or with status {@code 500 (Internal Server Error)} if the customers couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/customers/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Customers> partialUpdateCustomers(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Customers customers
    ) throws URISyntaxException {
        log.debug("REST request to partial update Customers partially : {}, {}", id, customers);
        if (customers.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, customers.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!customersRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Customers> result = customersService.partialUpdate(customers);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, customers.getId().toString())
        );
    }

    /**
     * {@code GET  /customers} : get all the customers.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of customers in body.
     */
    @GetMapping("/customers")
    public ResponseEntity<List<Customers>> getAllCustomers(
        CustomersCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Customers by criteria: {}", criteria);
        Page<Customers> page = customersQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /customers/count} : count all the customers.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/customers/count")
    public ResponseEntity<Long> countCustomers(CustomersCriteria criteria) {
        log.debug("REST request to count Customers by criteria: {}", criteria);
        return ResponseEntity.ok().body(customersQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /customers/:id} : get the "id" customers.
     *
     * @param id the id of the customers to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the customers, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/customers/{id}")
    public ResponseEntity<Customers> getCustomers(@PathVariable Long id) {
        log.debug("REST request to get Customers : {}", id);
        Optional<Customers> customers = customersService.findOne(id);
        return ResponseUtil.wrapOrNotFound(customers);
    }

    /**
     * {@code DELETE  /customers/:id} : delete the "id" customers.
     *
     * @param id the id of the customers to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/customers/{id}")
    public ResponseEntity<Void> deleteCustomers(@PathVariable Long id) {
        log.debug("REST request to delete Customers : {}", id);
        customersService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/customers?query=:query} : search for the customers corresponding
     * to the query.
     *
     * @param query the query of the customers search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/customers")
    public ResponseEntity<List<Customers>> searchCustomers(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of Customers for query {}", query);
        Page<Customers> page = customersService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
