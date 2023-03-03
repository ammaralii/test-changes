package com.test.changes.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.test.changes.domain.Orders;
import com.test.changes.repository.OrdersRepository;
import com.test.changes.service.OrdersQueryService;
import com.test.changes.service.OrdersService;
import com.test.changes.service.criteria.OrdersCriteria;
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
 * REST controller for managing {@link com.test.changes.domain.Orders}.
 */
@RestController
@RequestMapping("/api")
public class OrdersResource {

    private final Logger log = LoggerFactory.getLogger(OrdersResource.class);

    private static final String ENTITY_NAME = "testChangesOrders";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OrdersService ordersService;

    private final OrdersRepository ordersRepository;

    private final OrdersQueryService ordersQueryService;

    public OrdersResource(OrdersService ordersService, OrdersRepository ordersRepository, OrdersQueryService ordersQueryService) {
        this.ordersService = ordersService;
        this.ordersRepository = ordersRepository;
        this.ordersQueryService = ordersQueryService;
    }

    /**
     * {@code POST  /orders} : Create a new orders.
     *
     * @param orders the orders to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new orders, or with status {@code 400 (Bad Request)} if the orders has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/orders")
    public ResponseEntity<Orders> createOrders(@Valid @RequestBody Orders orders) throws URISyntaxException {
        log.debug("REST request to save Orders : {}", orders);
        if (orders.getId() != null) {
            throw new BadRequestAlertException("A new orders cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Orders result = ordersService.save(orders);
        return ResponseEntity
            .created(new URI("/api/orders/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /orders/:id} : Updates an existing orders.
     *
     * @param id the id of the orders to save.
     * @param orders the orders to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated orders,
     * or with status {@code 400 (Bad Request)} if the orders is not valid,
     * or with status {@code 500 (Internal Server Error)} if the orders couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/orders/{id}")
    public ResponseEntity<Orders> updateOrders(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Orders orders
    ) throws URISyntaxException {
        log.debug("REST request to update Orders : {}, {}", id, orders);
        if (orders.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, orders.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ordersRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Orders result = ordersService.update(orders);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, orders.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /orders/:id} : Partial updates given fields of an existing orders, field will ignore if it is null
     *
     * @param id the id of the orders to save.
     * @param orders the orders to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated orders,
     * or with status {@code 400 (Bad Request)} if the orders is not valid,
     * or with status {@code 404 (Not Found)} if the orders is not found,
     * or with status {@code 500 (Internal Server Error)} if the orders couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/orders/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Orders> partialUpdateOrders(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Orders orders
    ) throws URISyntaxException {
        log.debug("REST request to partial update Orders partially : {}, {}", id, orders);
        if (orders.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, orders.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ordersRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Orders> result = ordersService.partialUpdate(orders);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, orders.getId().toString())
        );
    }

    /**
     * {@code GET  /orders} : get all the orders.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of orders in body.
     */
    @GetMapping("/orders")
    public ResponseEntity<List<Orders>> getAllOrders(
        OrdersCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Orders by criteria: {}", criteria);
        Page<Orders> page = ordersQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /orders/count} : count all the orders.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/orders/count")
    public ResponseEntity<Long> countOrders(OrdersCriteria criteria) {
        log.debug("REST request to count Orders by criteria: {}", criteria);
        return ResponseEntity.ok().body(ordersQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /orders/:id} : get the "id" orders.
     *
     * @param id the id of the orders to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the orders, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/orders/{id}")
    public ResponseEntity<Orders> getOrders(@PathVariable Long id) {
        log.debug("REST request to get Orders : {}", id);
        Optional<Orders> orders = ordersService.findOne(id);
        return ResponseUtil.wrapOrNotFound(orders);
    }

    /**
     * {@code DELETE  /orders/:id} : delete the "id" orders.
     *
     * @param id the id of the orders to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/orders/{id}")
    public ResponseEntity<Void> deleteOrders(@PathVariable Long id) {
        log.debug("REST request to delete Orders : {}", id);
        ordersService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/orders?query=:query} : search for the orders corresponding
     * to the query.
     *
     * @param query the query of the orders search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/orders")
    public ResponseEntity<List<Orders>> searchOrders(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of Orders for query {}", query);
        Page<Orders> page = ordersService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
