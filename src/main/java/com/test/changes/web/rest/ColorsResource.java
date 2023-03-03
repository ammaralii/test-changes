package com.test.changes.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.test.changes.domain.Colors;
import com.test.changes.repository.ColorsRepository;
import com.test.changes.service.ColorsQueryService;
import com.test.changes.service.ColorsService;
import com.test.changes.service.criteria.ColorsCriteria;
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
 * REST controller for managing {@link com.test.changes.domain.Colors}.
 */
@RestController
@RequestMapping("/api")
public class ColorsResource {

    private final Logger log = LoggerFactory.getLogger(ColorsResource.class);

    private static final String ENTITY_NAME = "testChangesColors";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ColorsService colorsService;

    private final ColorsRepository colorsRepository;

    private final ColorsQueryService colorsQueryService;

    public ColorsResource(ColorsService colorsService, ColorsRepository colorsRepository, ColorsQueryService colorsQueryService) {
        this.colorsService = colorsService;
        this.colorsRepository = colorsRepository;
        this.colorsQueryService = colorsQueryService;
    }

    /**
     * {@code POST  /colors} : Create a new colors.
     *
     * @param colors the colors to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new colors, or with status {@code 400 (Bad Request)} if the colors has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/colors")
    public ResponseEntity<Colors> createColors(@Valid @RequestBody Colors colors) throws URISyntaxException {
        log.debug("REST request to save Colors : {}", colors);
        if (colors.getId() != null) {
            throw new BadRequestAlertException("A new colors cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Colors result = colorsService.save(colors);
        return ResponseEntity
            .created(new URI("/api/colors/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /colors/:id} : Updates an existing colors.
     *
     * @param id the id of the colors to save.
     * @param colors the colors to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated colors,
     * or with status {@code 400 (Bad Request)} if the colors is not valid,
     * or with status {@code 500 (Internal Server Error)} if the colors couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/colors/{id}")
    public ResponseEntity<Colors> updateColors(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Colors colors
    ) throws URISyntaxException {
        log.debug("REST request to update Colors : {}, {}", id, colors);
        if (colors.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, colors.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!colorsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Colors result = colorsService.update(colors);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, colors.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /colors/:id} : Partial updates given fields of an existing colors, field will ignore if it is null
     *
     * @param id the id of the colors to save.
     * @param colors the colors to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated colors,
     * or with status {@code 400 (Bad Request)} if the colors is not valid,
     * or with status {@code 404 (Not Found)} if the colors is not found,
     * or with status {@code 500 (Internal Server Error)} if the colors couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/colors/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Colors> partialUpdateColors(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Colors colors
    ) throws URISyntaxException {
        log.debug("REST request to partial update Colors partially : {}, {}", id, colors);
        if (colors.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, colors.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!colorsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Colors> result = colorsService.partialUpdate(colors);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, colors.getId().toString())
        );
    }

    /**
     * {@code GET  /colors} : get all the colors.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of colors in body.
     */
    @GetMapping("/colors")
    public ResponseEntity<List<Colors>> getAllColors(
        ColorsCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Colors by criteria: {}", criteria);
        Page<Colors> page = colorsQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /colors/count} : count all the colors.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/colors/count")
    public ResponseEntity<Long> countColors(ColorsCriteria criteria) {
        log.debug("REST request to count Colors by criteria: {}", criteria);
        return ResponseEntity.ok().body(colorsQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /colors/:id} : get the "id" colors.
     *
     * @param id the id of the colors to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the colors, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/colors/{id}")
    public ResponseEntity<Colors> getColors(@PathVariable Long id) {
        log.debug("REST request to get Colors : {}", id);
        Optional<Colors> colors = colorsService.findOne(id);
        return ResponseUtil.wrapOrNotFound(colors);
    }

    /**
     * {@code DELETE  /colors/:id} : delete the "id" colors.
     *
     * @param id the id of the colors to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/colors/{id}")
    public ResponseEntity<Void> deleteColors(@PathVariable Long id) {
        log.debug("REST request to delete Colors : {}", id);
        colorsService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/colors?query=:query} : search for the colors corresponding
     * to the query.
     *
     * @param query the query of the colors search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/colors")
    public ResponseEntity<List<Colors>> searchColors(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of Colors for query {}", query);
        Page<Colors> page = colorsService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
