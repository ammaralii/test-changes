package com.test.changes.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.test.changes.IntegrationTest;
import com.test.changes.domain.Cars;
import com.test.changes.domain.Colors;
import com.test.changes.repository.ColorsRepository;
import com.test.changes.repository.search.ColorsSearchRepository;
import com.test.changes.service.criteria.ColorsCriteria;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.apache.commons.collections4.IterableUtils;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ColorsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ColorsResourceIT {

    private static final Integer DEFAULT_COLORUID = 1;
    private static final Integer UPDATED_COLORUID = 2;
    private static final Integer SMALLER_COLORUID = 1 - 1;

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/colors";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/colors";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ColorsRepository colorsRepository;

    @Autowired
    private ColorsSearchRepository colorsSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restColorsMockMvc;

    private Colors colors;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Colors createEntity(EntityManager em) {
        Colors colors = new Colors().coloruid(DEFAULT_COLORUID).name(DEFAULT_NAME);
        return colors;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Colors createUpdatedEntity(EntityManager em) {
        Colors colors = new Colors().coloruid(UPDATED_COLORUID).name(UPDATED_NAME);
        return colors;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        colorsSearchRepository.deleteAll();
        assertThat(colorsSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        colors = createEntity(em);
    }

    @Test
    @Transactional
    void createColors() throws Exception {
        int databaseSizeBeforeCreate = colorsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(colorsSearchRepository.findAll());
        // Create the Colors
        restColorsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(colors)))
            .andExpect(status().isCreated());

        // Validate the Colors in the database
        List<Colors> colorsList = colorsRepository.findAll();
        assertThat(colorsList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(colorsSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Colors testColors = colorsList.get(colorsList.size() - 1);
        assertThat(testColors.getColoruid()).isEqualTo(DEFAULT_COLORUID);
        assertThat(testColors.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    void createColorsWithExistingId() throws Exception {
        // Create the Colors with an existing ID
        colors.setId(1L);

        int databaseSizeBeforeCreate = colorsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(colorsSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restColorsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(colors)))
            .andExpect(status().isBadRequest());

        // Validate the Colors in the database
        List<Colors> colorsList = colorsRepository.findAll();
        assertThat(colorsList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(colorsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkColoruidIsRequired() throws Exception {
        int databaseSizeBeforeTest = colorsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(colorsSearchRepository.findAll());
        // set the field null
        colors.setColoruid(null);

        // Create the Colors, which fails.

        restColorsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(colors)))
            .andExpect(status().isBadRequest());

        List<Colors> colorsList = colorsRepository.findAll();
        assertThat(colorsList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(colorsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = colorsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(colorsSearchRepository.findAll());
        // set the field null
        colors.setName(null);

        // Create the Colors, which fails.

        restColorsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(colors)))
            .andExpect(status().isBadRequest());

        List<Colors> colorsList = colorsRepository.findAll();
        assertThat(colorsList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(colorsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllColors() throws Exception {
        // Initialize the database
        colorsRepository.saveAndFlush(colors);

        // Get all the colorsList
        restColorsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(colors.getId().intValue())))
            .andExpect(jsonPath("$.[*].coloruid").value(hasItem(DEFAULT_COLORUID)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }

    @Test
    @Transactional
    void getColors() throws Exception {
        // Initialize the database
        colorsRepository.saveAndFlush(colors);

        // Get the colors
        restColorsMockMvc
            .perform(get(ENTITY_API_URL_ID, colors.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(colors.getId().intValue()))
            .andExpect(jsonPath("$.coloruid").value(DEFAULT_COLORUID))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }

    @Test
    @Transactional
    void getColorsByIdFiltering() throws Exception {
        // Initialize the database
        colorsRepository.saveAndFlush(colors);

        Long id = colors.getId();

        defaultColorsShouldBeFound("id.equals=" + id);
        defaultColorsShouldNotBeFound("id.notEquals=" + id);

        defaultColorsShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultColorsShouldNotBeFound("id.greaterThan=" + id);

        defaultColorsShouldBeFound("id.lessThanOrEqual=" + id);
        defaultColorsShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllColorsByColoruidIsEqualToSomething() throws Exception {
        // Initialize the database
        colorsRepository.saveAndFlush(colors);

        // Get all the colorsList where coloruid equals to DEFAULT_COLORUID
        defaultColorsShouldBeFound("coloruid.equals=" + DEFAULT_COLORUID);

        // Get all the colorsList where coloruid equals to UPDATED_COLORUID
        defaultColorsShouldNotBeFound("coloruid.equals=" + UPDATED_COLORUID);
    }

    @Test
    @Transactional
    void getAllColorsByColoruidIsInShouldWork() throws Exception {
        // Initialize the database
        colorsRepository.saveAndFlush(colors);

        // Get all the colorsList where coloruid in DEFAULT_COLORUID or UPDATED_COLORUID
        defaultColorsShouldBeFound("coloruid.in=" + DEFAULT_COLORUID + "," + UPDATED_COLORUID);

        // Get all the colorsList where coloruid equals to UPDATED_COLORUID
        defaultColorsShouldNotBeFound("coloruid.in=" + UPDATED_COLORUID);
    }

    @Test
    @Transactional
    void getAllColorsByColoruidIsNullOrNotNull() throws Exception {
        // Initialize the database
        colorsRepository.saveAndFlush(colors);

        // Get all the colorsList where coloruid is not null
        defaultColorsShouldBeFound("coloruid.specified=true");

        // Get all the colorsList where coloruid is null
        defaultColorsShouldNotBeFound("coloruid.specified=false");
    }

    @Test
    @Transactional
    void getAllColorsByColoruidIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        colorsRepository.saveAndFlush(colors);

        // Get all the colorsList where coloruid is greater than or equal to DEFAULT_COLORUID
        defaultColorsShouldBeFound("coloruid.greaterThanOrEqual=" + DEFAULT_COLORUID);

        // Get all the colorsList where coloruid is greater than or equal to UPDATED_COLORUID
        defaultColorsShouldNotBeFound("coloruid.greaterThanOrEqual=" + UPDATED_COLORUID);
    }

    @Test
    @Transactional
    void getAllColorsByColoruidIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        colorsRepository.saveAndFlush(colors);

        // Get all the colorsList where coloruid is less than or equal to DEFAULT_COLORUID
        defaultColorsShouldBeFound("coloruid.lessThanOrEqual=" + DEFAULT_COLORUID);

        // Get all the colorsList where coloruid is less than or equal to SMALLER_COLORUID
        defaultColorsShouldNotBeFound("coloruid.lessThanOrEqual=" + SMALLER_COLORUID);
    }

    @Test
    @Transactional
    void getAllColorsByColoruidIsLessThanSomething() throws Exception {
        // Initialize the database
        colorsRepository.saveAndFlush(colors);

        // Get all the colorsList where coloruid is less than DEFAULT_COLORUID
        defaultColorsShouldNotBeFound("coloruid.lessThan=" + DEFAULT_COLORUID);

        // Get all the colorsList where coloruid is less than UPDATED_COLORUID
        defaultColorsShouldBeFound("coloruid.lessThan=" + UPDATED_COLORUID);
    }

    @Test
    @Transactional
    void getAllColorsByColoruidIsGreaterThanSomething() throws Exception {
        // Initialize the database
        colorsRepository.saveAndFlush(colors);

        // Get all the colorsList where coloruid is greater than DEFAULT_COLORUID
        defaultColorsShouldNotBeFound("coloruid.greaterThan=" + DEFAULT_COLORUID);

        // Get all the colorsList where coloruid is greater than SMALLER_COLORUID
        defaultColorsShouldBeFound("coloruid.greaterThan=" + SMALLER_COLORUID);
    }

    @Test
    @Transactional
    void getAllColorsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        colorsRepository.saveAndFlush(colors);

        // Get all the colorsList where name equals to DEFAULT_NAME
        defaultColorsShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the colorsList where name equals to UPDATED_NAME
        defaultColorsShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllColorsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        colorsRepository.saveAndFlush(colors);

        // Get all the colorsList where name in DEFAULT_NAME or UPDATED_NAME
        defaultColorsShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the colorsList where name equals to UPDATED_NAME
        defaultColorsShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllColorsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        colorsRepository.saveAndFlush(colors);

        // Get all the colorsList where name is not null
        defaultColorsShouldBeFound("name.specified=true");

        // Get all the colorsList where name is null
        defaultColorsShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllColorsByNameContainsSomething() throws Exception {
        // Initialize the database
        colorsRepository.saveAndFlush(colors);

        // Get all the colorsList where name contains DEFAULT_NAME
        defaultColorsShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the colorsList where name contains UPDATED_NAME
        defaultColorsShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllColorsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        colorsRepository.saveAndFlush(colors);

        // Get all the colorsList where name does not contain DEFAULT_NAME
        defaultColorsShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the colorsList where name does not contain UPDATED_NAME
        defaultColorsShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllColorsByCarIsEqualToSomething() throws Exception {
        Cars car;
        if (TestUtil.findAll(em, Cars.class).isEmpty()) {
            colorsRepository.saveAndFlush(colors);
            car = CarsResourceIT.createEntity(em);
        } else {
            car = TestUtil.findAll(em, Cars.class).get(0);
        }
        em.persist(car);
        em.flush();
        colors.addCar(car);
        colorsRepository.saveAndFlush(colors);
        Long carId = car.getId();

        // Get all the colorsList where car equals to carId
        defaultColorsShouldBeFound("carId.equals=" + carId);

        // Get all the colorsList where car equals to (carId + 1)
        defaultColorsShouldNotBeFound("carId.equals=" + (carId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultColorsShouldBeFound(String filter) throws Exception {
        restColorsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(colors.getId().intValue())))
            .andExpect(jsonPath("$.[*].coloruid").value(hasItem(DEFAULT_COLORUID)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));

        // Check, that the count call also returns 1
        restColorsMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultColorsShouldNotBeFound(String filter) throws Exception {
        restColorsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restColorsMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingColors() throws Exception {
        // Get the colors
        restColorsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingColors() throws Exception {
        // Initialize the database
        colorsRepository.saveAndFlush(colors);

        int databaseSizeBeforeUpdate = colorsRepository.findAll().size();
        colorsSearchRepository.save(colors);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(colorsSearchRepository.findAll());

        // Update the colors
        Colors updatedColors = colorsRepository.findById(colors.getId()).get();
        // Disconnect from session so that the updates on updatedColors are not directly saved in db
        em.detach(updatedColors);
        updatedColors.coloruid(UPDATED_COLORUID).name(UPDATED_NAME);

        restColorsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedColors.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedColors))
            )
            .andExpect(status().isOk());

        // Validate the Colors in the database
        List<Colors> colorsList = colorsRepository.findAll();
        assertThat(colorsList).hasSize(databaseSizeBeforeUpdate);
        Colors testColors = colorsList.get(colorsList.size() - 1);
        assertThat(testColors.getColoruid()).isEqualTo(UPDATED_COLORUID);
        assertThat(testColors.getName()).isEqualTo(UPDATED_NAME);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(colorsSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Colors> colorsSearchList = IterableUtils.toList(colorsSearchRepository.findAll());
                Colors testColorsSearch = colorsSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testColorsSearch.getColoruid()).isEqualTo(UPDATED_COLORUID);
                assertThat(testColorsSearch.getName()).isEqualTo(UPDATED_NAME);
            });
    }

    @Test
    @Transactional
    void putNonExistingColors() throws Exception {
        int databaseSizeBeforeUpdate = colorsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(colorsSearchRepository.findAll());
        colors.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restColorsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, colors.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(colors))
            )
            .andExpect(status().isBadRequest());

        // Validate the Colors in the database
        List<Colors> colorsList = colorsRepository.findAll();
        assertThat(colorsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(colorsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchColors() throws Exception {
        int databaseSizeBeforeUpdate = colorsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(colorsSearchRepository.findAll());
        colors.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restColorsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(colors))
            )
            .andExpect(status().isBadRequest());

        // Validate the Colors in the database
        List<Colors> colorsList = colorsRepository.findAll();
        assertThat(colorsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(colorsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamColors() throws Exception {
        int databaseSizeBeforeUpdate = colorsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(colorsSearchRepository.findAll());
        colors.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restColorsMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(colors)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Colors in the database
        List<Colors> colorsList = colorsRepository.findAll();
        assertThat(colorsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(colorsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateColorsWithPatch() throws Exception {
        // Initialize the database
        colorsRepository.saveAndFlush(colors);

        int databaseSizeBeforeUpdate = colorsRepository.findAll().size();

        // Update the colors using partial update
        Colors partialUpdatedColors = new Colors();
        partialUpdatedColors.setId(colors.getId());

        partialUpdatedColors.coloruid(UPDATED_COLORUID).name(UPDATED_NAME);

        restColorsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedColors.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedColors))
            )
            .andExpect(status().isOk());

        // Validate the Colors in the database
        List<Colors> colorsList = colorsRepository.findAll();
        assertThat(colorsList).hasSize(databaseSizeBeforeUpdate);
        Colors testColors = colorsList.get(colorsList.size() - 1);
        assertThat(testColors.getColoruid()).isEqualTo(UPDATED_COLORUID);
        assertThat(testColors.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void fullUpdateColorsWithPatch() throws Exception {
        // Initialize the database
        colorsRepository.saveAndFlush(colors);

        int databaseSizeBeforeUpdate = colorsRepository.findAll().size();

        // Update the colors using partial update
        Colors partialUpdatedColors = new Colors();
        partialUpdatedColors.setId(colors.getId());

        partialUpdatedColors.coloruid(UPDATED_COLORUID).name(UPDATED_NAME);

        restColorsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedColors.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedColors))
            )
            .andExpect(status().isOk());

        // Validate the Colors in the database
        List<Colors> colorsList = colorsRepository.findAll();
        assertThat(colorsList).hasSize(databaseSizeBeforeUpdate);
        Colors testColors = colorsList.get(colorsList.size() - 1);
        assertThat(testColors.getColoruid()).isEqualTo(UPDATED_COLORUID);
        assertThat(testColors.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void patchNonExistingColors() throws Exception {
        int databaseSizeBeforeUpdate = colorsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(colorsSearchRepository.findAll());
        colors.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restColorsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, colors.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(colors))
            )
            .andExpect(status().isBadRequest());

        // Validate the Colors in the database
        List<Colors> colorsList = colorsRepository.findAll();
        assertThat(colorsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(colorsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchColors() throws Exception {
        int databaseSizeBeforeUpdate = colorsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(colorsSearchRepository.findAll());
        colors.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restColorsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(colors))
            )
            .andExpect(status().isBadRequest());

        // Validate the Colors in the database
        List<Colors> colorsList = colorsRepository.findAll();
        assertThat(colorsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(colorsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamColors() throws Exception {
        int databaseSizeBeforeUpdate = colorsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(colorsSearchRepository.findAll());
        colors.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restColorsMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(colors)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Colors in the database
        List<Colors> colorsList = colorsRepository.findAll();
        assertThat(colorsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(colorsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteColors() throws Exception {
        // Initialize the database
        colorsRepository.saveAndFlush(colors);
        colorsRepository.save(colors);
        colorsSearchRepository.save(colors);

        int databaseSizeBeforeDelete = colorsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(colorsSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the colors
        restColorsMockMvc
            .perform(delete(ENTITY_API_URL_ID, colors.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Colors> colorsList = colorsRepository.findAll();
        assertThat(colorsList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(colorsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchColors() throws Exception {
        // Initialize the database
        colors = colorsRepository.saveAndFlush(colors);
        colorsSearchRepository.save(colors);

        // Search the colors
        restColorsMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + colors.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(colors.getId().intValue())))
            .andExpect(jsonPath("$.[*].coloruid").value(hasItem(DEFAULT_COLORUID)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }
}
