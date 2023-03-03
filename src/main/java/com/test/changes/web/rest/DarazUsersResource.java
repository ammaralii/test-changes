package com.test.changes.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.test.changes.domain.DarazUsers;
import com.test.changes.repository.DarazUsersRepository;
import com.test.changes.service.DarazUsersQueryService;
import com.test.changes.service.DarazUsersService;
import com.test.changes.service.criteria.DarazUsersCriteria;
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
 * REST controller for managing {@link com.test.changes.domain.DarazUsers}.
 */
@RestController
@RequestMapping("/api")
public class DarazUsersResource {

    private final Logger log = LoggerFactory.getLogger(DarazUsersResource.class);

    private static final String ENTITY_NAME = "testChangesDarazUsers";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DarazUsersService darazUsersService;

    private final DarazUsersRepository darazUsersRepository;

    private final DarazUsersQueryService darazUsersQueryService;

    public DarazUsersResource(
        DarazUsersService darazUsersService,
        DarazUsersRepository darazUsersRepository,
        DarazUsersQueryService darazUsersQueryService
    ) {
        this.darazUsersService = darazUsersService;
        this.darazUsersRepository = darazUsersRepository;
        this.darazUsersQueryService = darazUsersQueryService;
    }

    /**
     * {@code POST  /daraz-users} : Create a new darazUsers.
     *
     * @param darazUsers the darazUsers to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new darazUsers, or with status {@code 400 (Bad Request)} if the darazUsers has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/daraz-users")
    public ResponseEntity<DarazUsers> createDarazUsers(@Valid @RequestBody DarazUsers darazUsers) throws URISyntaxException {
        log.debug("REST request to save DarazUsers : {}", darazUsers);
        if (darazUsers.getId() != null) {
            throw new BadRequestAlertException("A new darazUsers cannot already have an ID", ENTITY_NAME, "idexists");
        }
        DarazUsers result = darazUsersService.save(darazUsers);
        return ResponseEntity
            .created(new URI("/api/daraz-users/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /daraz-users/:id} : Updates an existing darazUsers.
     *
     * @param id the id of the darazUsers to save.
     * @param darazUsers the darazUsers to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated darazUsers,
     * or with status {@code 400 (Bad Request)} if the darazUsers is not valid,
     * or with status {@code 500 (Internal Server Error)} if the darazUsers couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/daraz-users/{id}")
    public ResponseEntity<DarazUsers> updateDarazUsers(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody DarazUsers darazUsers
    ) throws URISyntaxException {
        log.debug("REST request to update DarazUsers : {}, {}", id, darazUsers);
        if (darazUsers.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, darazUsers.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!darazUsersRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        DarazUsers result = darazUsersService.update(darazUsers);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, darazUsers.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /daraz-users/:id} : Partial updates given fields of an existing darazUsers, field will ignore if it is null
     *
     * @param id the id of the darazUsers to save.
     * @param darazUsers the darazUsers to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated darazUsers,
     * or with status {@code 400 (Bad Request)} if the darazUsers is not valid,
     * or with status {@code 404 (Not Found)} if the darazUsers is not found,
     * or with status {@code 500 (Internal Server Error)} if the darazUsers couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/daraz-users/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<DarazUsers> partialUpdateDarazUsers(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody DarazUsers darazUsers
    ) throws URISyntaxException {
        log.debug("REST request to partial update DarazUsers partially : {}, {}", id, darazUsers);
        if (darazUsers.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, darazUsers.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!darazUsersRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<DarazUsers> result = darazUsersService.partialUpdate(darazUsers);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, darazUsers.getId().toString())
        );
    }

    /**
     * {@code GET  /daraz-users} : get all the darazUsers.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of darazUsers in body.
     */
    @GetMapping("/daraz-users")
    public ResponseEntity<List<DarazUsers>> getAllDarazUsers(
        DarazUsersCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get DarazUsers by criteria: {}", criteria);
        Page<DarazUsers> page = darazUsersQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /daraz-users/count} : count all the darazUsers.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/daraz-users/count")
    public ResponseEntity<Long> countDarazUsers(DarazUsersCriteria criteria) {
        log.debug("REST request to count DarazUsers by criteria: {}", criteria);
        return ResponseEntity.ok().body(darazUsersQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /daraz-users/:id} : get the "id" darazUsers.
     *
     * @param id the id of the darazUsers to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the darazUsers, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/daraz-users/{id}")
    public ResponseEntity<DarazUsers> getDarazUsers(@PathVariable Long id) {
        log.debug("REST request to get DarazUsers : {}", id);
        Optional<DarazUsers> darazUsers = darazUsersService.findOne(id);
        return ResponseUtil.wrapOrNotFound(darazUsers);
    }

    /**
     * {@code DELETE  /daraz-users/:id} : delete the "id" darazUsers.
     *
     * @param id the id of the darazUsers to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/daraz-users/{id}")
    public ResponseEntity<Void> deleteDarazUsers(@PathVariable Long id) {
        log.debug("REST request to delete DarazUsers : {}", id);
        darazUsersService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/daraz-users?query=:query} : search for the darazUsers corresponding
     * to the query.
     *
     * @param query the query of the darazUsers search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/daraz-users")
    public ResponseEntity<List<DarazUsers>> searchDarazUsers(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of DarazUsers for query {}", query);
        Page<DarazUsers> page = darazUsersService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
