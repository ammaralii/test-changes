package com.test.changes.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.test.changes.domain.OrderDetails;
import com.test.changes.repository.OrderDetailsRepository;
import com.test.changes.service.OrderDetailsQueryService;
import com.test.changes.service.OrderDetailsService;
import com.test.changes.service.criteria.OrderDetailsCriteria;
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
 * REST controller for managing {@link com.test.changes.domain.OrderDetails}.
 */
@RestController
@RequestMapping("/api")
public class OrderDetailsResource {

    private final Logger log = LoggerFactory.getLogger(OrderDetailsResource.class);

    private static final String ENTITY_NAME = "testChangesOrderDetails";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OrderDetailsService orderDetailsService;

    private final OrderDetailsRepository orderDetailsRepository;

    private final OrderDetailsQueryService orderDetailsQueryService;

    public OrderDetailsResource(
        OrderDetailsService orderDetailsService,
        OrderDetailsRepository orderDetailsRepository,
        OrderDetailsQueryService orderDetailsQueryService
    ) {
        this.orderDetailsService = orderDetailsService;
        this.orderDetailsRepository = orderDetailsRepository;
        this.orderDetailsQueryService = orderDetailsQueryService;
    }

    /**
     * {@code POST  /order-details} : Create a new orderDetails.
     *
     * @param orderDetails the orderDetails to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new orderDetails, or with status {@code 400 (Bad Request)} if the orderDetails has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/order-details")
    public ResponseEntity<OrderDetails> createOrderDetails(@Valid @RequestBody OrderDetails orderDetails) throws URISyntaxException {
        log.debug("REST request to save OrderDetails : {}", orderDetails);
        if (orderDetails.getId() != null) {
            throw new BadRequestAlertException("A new orderDetails cannot already have an ID", ENTITY_NAME, "idexists");
        }
        OrderDetails result = orderDetailsService.save(orderDetails);
        return ResponseEntity
            .created(new URI("/api/order-details/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /order-details/:id} : Updates an existing orderDetails.
     *
     * @param id the id of the orderDetails to save.
     * @param orderDetails the orderDetails to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated orderDetails,
     * or with status {@code 400 (Bad Request)} if the orderDetails is not valid,
     * or with status {@code 500 (Internal Server Error)} if the orderDetails couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/order-details/{id}")
    public ResponseEntity<OrderDetails> updateOrderDetails(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody OrderDetails orderDetails
    ) throws URISyntaxException {
        log.debug("REST request to update OrderDetails : {}, {}", id, orderDetails);
        if (orderDetails.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, orderDetails.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!orderDetailsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        OrderDetails result = orderDetailsService.update(orderDetails);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, orderDetails.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /order-details/:id} : Partial updates given fields of an existing orderDetails, field will ignore if it is null
     *
     * @param id the id of the orderDetails to save.
     * @param orderDetails the orderDetails to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated orderDetails,
     * or with status {@code 400 (Bad Request)} if the orderDetails is not valid,
     * or with status {@code 404 (Not Found)} if the orderDetails is not found,
     * or with status {@code 500 (Internal Server Error)} if the orderDetails couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/order-details/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<OrderDetails> partialUpdateOrderDetails(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody OrderDetails orderDetails
    ) throws URISyntaxException {
        log.debug("REST request to partial update OrderDetails partially : {}, {}", id, orderDetails);
        if (orderDetails.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, orderDetails.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!orderDetailsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<OrderDetails> result = orderDetailsService.partialUpdate(orderDetails);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, orderDetails.getId().toString())
        );
    }

    /**
     * {@code GET  /order-details} : get all the orderDetails.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of orderDetails in body.
     */
    @GetMapping("/order-details")
    public ResponseEntity<List<OrderDetails>> getAllOrderDetails(
        OrderDetailsCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get OrderDetails by criteria: {}", criteria);
        Page<OrderDetails> page = orderDetailsQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /order-details/count} : count all the orderDetails.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/order-details/count")
    public ResponseEntity<Long> countOrderDetails(OrderDetailsCriteria criteria) {
        log.debug("REST request to count OrderDetails by criteria: {}", criteria);
        return ResponseEntity.ok().body(orderDetailsQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /order-details/:id} : get the "id" orderDetails.
     *
     * @param id the id of the orderDetails to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the orderDetails, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/order-details/{id}")
    public ResponseEntity<OrderDetails> getOrderDetails(@PathVariable Long id) {
        log.debug("REST request to get OrderDetails : {}", id);
        Optional<OrderDetails> orderDetails = orderDetailsService.findOne(id);
        return ResponseUtil.wrapOrNotFound(orderDetails);
    }

    /**
     * {@code DELETE  /order-details/:id} : delete the "id" orderDetails.
     *
     * @param id the id of the orderDetails to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/order-details/{id}")
    public ResponseEntity<Void> deleteOrderDetails(@PathVariable Long id) {
        log.debug("REST request to delete OrderDetails : {}", id);
        orderDetailsService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/order-details?query=:query} : search for the orderDetails corresponding
     * to the query.
     *
     * @param query the query of the orderDetails search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/order-details")
    public ResponseEntity<List<OrderDetails>> searchOrderDetails(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of OrderDetails for query {}", query);
        Page<OrderDetails> page = orderDetailsService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
