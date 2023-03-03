package com.test.changes.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.test.changes.IntegrationTest;
import com.test.changes.domain.Categories;
import com.test.changes.domain.Products;
import com.test.changes.repository.CategoriesRepository;
import com.test.changes.repository.search.CategoriesSearchRepository;
import com.test.changes.service.criteria.CategoriesCriteria;
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
 * Integration tests for the {@link CategoriesResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CategoriesResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DETAIL = "AAAAAAAAAA";
    private static final String UPDATED_DETAIL = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/categories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/categories";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CategoriesRepository categoriesRepository;

    @Autowired
    private CategoriesSearchRepository categoriesSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCategoriesMockMvc;

    private Categories categories;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Categories createEntity(EntityManager em) {
        Categories categories = new Categories().name(DEFAULT_NAME).detail(DEFAULT_DETAIL);
        return categories;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Categories createUpdatedEntity(EntityManager em) {
        Categories categories = new Categories().name(UPDATED_NAME).detail(UPDATED_DETAIL);
        return categories;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        categoriesSearchRepository.deleteAll();
        assertThat(categoriesSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        categories = createEntity(em);
    }

    @Test
    @Transactional
    void createCategories() throws Exception {
        int databaseSizeBeforeCreate = categoriesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(categoriesSearchRepository.findAll());
        // Create the Categories
        restCategoriesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(categories)))
            .andExpect(status().isCreated());

        // Validate the Categories in the database
        List<Categories> categoriesList = categoriesRepository.findAll();
        assertThat(categoriesList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(categoriesSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Categories testCategories = categoriesList.get(categoriesList.size() - 1);
        assertThat(testCategories.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCategories.getDetail()).isEqualTo(DEFAULT_DETAIL);
    }

    @Test
    @Transactional
    void createCategoriesWithExistingId() throws Exception {
        // Create the Categories with an existing ID
        categories.setId(1L);

        int databaseSizeBeforeCreate = categoriesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(categoriesSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restCategoriesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(categories)))
            .andExpect(status().isBadRequest());

        // Validate the Categories in the database
        List<Categories> categoriesList = categoriesRepository.findAll();
        assertThat(categoriesList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(categoriesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = categoriesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(categoriesSearchRepository.findAll());
        // set the field null
        categories.setName(null);

        // Create the Categories, which fails.

        restCategoriesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(categories)))
            .andExpect(status().isBadRequest());

        List<Categories> categoriesList = categoriesRepository.findAll();
        assertThat(categoriesList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(categoriesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkDetailIsRequired() throws Exception {
        int databaseSizeBeforeTest = categoriesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(categoriesSearchRepository.findAll());
        // set the field null
        categories.setDetail(null);

        // Create the Categories, which fails.

        restCategoriesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(categories)))
            .andExpect(status().isBadRequest());

        List<Categories> categoriesList = categoriesRepository.findAll();
        assertThat(categoriesList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(categoriesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllCategories() throws Exception {
        // Initialize the database
        categoriesRepository.saveAndFlush(categories);

        // Get all the categoriesList
        restCategoriesMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(categories.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].detail").value(hasItem(DEFAULT_DETAIL)));
    }

    @Test
    @Transactional
    void getCategories() throws Exception {
        // Initialize the database
        categoriesRepository.saveAndFlush(categories);

        // Get the categories
        restCategoriesMockMvc
            .perform(get(ENTITY_API_URL_ID, categories.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(categories.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.detail").value(DEFAULT_DETAIL));
    }

    @Test
    @Transactional
    void getCategoriesByIdFiltering() throws Exception {
        // Initialize the database
        categoriesRepository.saveAndFlush(categories);

        Long id = categories.getId();

        defaultCategoriesShouldBeFound("id.equals=" + id);
        defaultCategoriesShouldNotBeFound("id.notEquals=" + id);

        defaultCategoriesShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultCategoriesShouldNotBeFound("id.greaterThan=" + id);

        defaultCategoriesShouldBeFound("id.lessThanOrEqual=" + id);
        defaultCategoriesShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCategoriesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        categoriesRepository.saveAndFlush(categories);

        // Get all the categoriesList where name equals to DEFAULT_NAME
        defaultCategoriesShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the categoriesList where name equals to UPDATED_NAME
        defaultCategoriesShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllCategoriesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        categoriesRepository.saveAndFlush(categories);

        // Get all the categoriesList where name in DEFAULT_NAME or UPDATED_NAME
        defaultCategoriesShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the categoriesList where name equals to UPDATED_NAME
        defaultCategoriesShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllCategoriesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        categoriesRepository.saveAndFlush(categories);

        // Get all the categoriesList where name is not null
        defaultCategoriesShouldBeFound("name.specified=true");

        // Get all the categoriesList where name is null
        defaultCategoriesShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllCategoriesByNameContainsSomething() throws Exception {
        // Initialize the database
        categoriesRepository.saveAndFlush(categories);

        // Get all the categoriesList where name contains DEFAULT_NAME
        defaultCategoriesShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the categoriesList where name contains UPDATED_NAME
        defaultCategoriesShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllCategoriesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        categoriesRepository.saveAndFlush(categories);

        // Get all the categoriesList where name does not contain DEFAULT_NAME
        defaultCategoriesShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the categoriesList where name does not contain UPDATED_NAME
        defaultCategoriesShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllCategoriesByDetailIsEqualToSomething() throws Exception {
        // Initialize the database
        categoriesRepository.saveAndFlush(categories);

        // Get all the categoriesList where detail equals to DEFAULT_DETAIL
        defaultCategoriesShouldBeFound("detail.equals=" + DEFAULT_DETAIL);

        // Get all the categoriesList where detail equals to UPDATED_DETAIL
        defaultCategoriesShouldNotBeFound("detail.equals=" + UPDATED_DETAIL);
    }

    @Test
    @Transactional
    void getAllCategoriesByDetailIsInShouldWork() throws Exception {
        // Initialize the database
        categoriesRepository.saveAndFlush(categories);

        // Get all the categoriesList where detail in DEFAULT_DETAIL or UPDATED_DETAIL
        defaultCategoriesShouldBeFound("detail.in=" + DEFAULT_DETAIL + "," + UPDATED_DETAIL);

        // Get all the categoriesList where detail equals to UPDATED_DETAIL
        defaultCategoriesShouldNotBeFound("detail.in=" + UPDATED_DETAIL);
    }

    @Test
    @Transactional
    void getAllCategoriesByDetailIsNullOrNotNull() throws Exception {
        // Initialize the database
        categoriesRepository.saveAndFlush(categories);

        // Get all the categoriesList where detail is not null
        defaultCategoriesShouldBeFound("detail.specified=true");

        // Get all the categoriesList where detail is null
        defaultCategoriesShouldNotBeFound("detail.specified=false");
    }

    @Test
    @Transactional
    void getAllCategoriesByDetailContainsSomething() throws Exception {
        // Initialize the database
        categoriesRepository.saveAndFlush(categories);

        // Get all the categoriesList where detail contains DEFAULT_DETAIL
        defaultCategoriesShouldBeFound("detail.contains=" + DEFAULT_DETAIL);

        // Get all the categoriesList where detail contains UPDATED_DETAIL
        defaultCategoriesShouldNotBeFound("detail.contains=" + UPDATED_DETAIL);
    }

    @Test
    @Transactional
    void getAllCategoriesByDetailNotContainsSomething() throws Exception {
        // Initialize the database
        categoriesRepository.saveAndFlush(categories);

        // Get all the categoriesList where detail does not contain DEFAULT_DETAIL
        defaultCategoriesShouldNotBeFound("detail.doesNotContain=" + DEFAULT_DETAIL);

        // Get all the categoriesList where detail does not contain UPDATED_DETAIL
        defaultCategoriesShouldBeFound("detail.doesNotContain=" + UPDATED_DETAIL);
    }

    @Test
    @Transactional
    void getAllCategoriesByProductsCategoryIsEqualToSomething() throws Exception {
        Products productsCategory;
        if (TestUtil.findAll(em, Products.class).isEmpty()) {
            categoriesRepository.saveAndFlush(categories);
            productsCategory = ProductsResourceIT.createEntity(em);
        } else {
            productsCategory = TestUtil.findAll(em, Products.class).get(0);
        }
        em.persist(productsCategory);
        em.flush();
        categories.addProductsCategory(productsCategory);
        categoriesRepository.saveAndFlush(categories);
        Long productsCategoryId = productsCategory.getId();

        // Get all the categoriesList where productsCategory equals to productsCategoryId
        defaultCategoriesShouldBeFound("productsCategoryId.equals=" + productsCategoryId);

        // Get all the categoriesList where productsCategory equals to (productsCategoryId + 1)
        defaultCategoriesShouldNotBeFound("productsCategoryId.equals=" + (productsCategoryId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCategoriesShouldBeFound(String filter) throws Exception {
        restCategoriesMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(categories.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].detail").value(hasItem(DEFAULT_DETAIL)));

        // Check, that the count call also returns 1
        restCategoriesMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCategoriesShouldNotBeFound(String filter) throws Exception {
        restCategoriesMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCategoriesMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCategories() throws Exception {
        // Get the categories
        restCategoriesMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCategories() throws Exception {
        // Initialize the database
        categoriesRepository.saveAndFlush(categories);

        int databaseSizeBeforeUpdate = categoriesRepository.findAll().size();
        categoriesSearchRepository.save(categories);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(categoriesSearchRepository.findAll());

        // Update the categories
        Categories updatedCategories = categoriesRepository.findById(categories.getId()).get();
        // Disconnect from session so that the updates on updatedCategories are not directly saved in db
        em.detach(updatedCategories);
        updatedCategories.name(UPDATED_NAME).detail(UPDATED_DETAIL);

        restCategoriesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCategories.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedCategories))
            )
            .andExpect(status().isOk());

        // Validate the Categories in the database
        List<Categories> categoriesList = categoriesRepository.findAll();
        assertThat(categoriesList).hasSize(databaseSizeBeforeUpdate);
        Categories testCategories = categoriesList.get(categoriesList.size() - 1);
        assertThat(testCategories.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCategories.getDetail()).isEqualTo(UPDATED_DETAIL);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(categoriesSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Categories> categoriesSearchList = IterableUtils.toList(categoriesSearchRepository.findAll());
                Categories testCategoriesSearch = categoriesSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testCategoriesSearch.getName()).isEqualTo(UPDATED_NAME);
                assertThat(testCategoriesSearch.getDetail()).isEqualTo(UPDATED_DETAIL);
            });
    }

    @Test
    @Transactional
    void putNonExistingCategories() throws Exception {
        int databaseSizeBeforeUpdate = categoriesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(categoriesSearchRepository.findAll());
        categories.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCategoriesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, categories.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(categories))
            )
            .andExpect(status().isBadRequest());

        // Validate the Categories in the database
        List<Categories> categoriesList = categoriesRepository.findAll();
        assertThat(categoriesList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(categoriesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchCategories() throws Exception {
        int databaseSizeBeforeUpdate = categoriesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(categoriesSearchRepository.findAll());
        categories.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCategoriesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(categories))
            )
            .andExpect(status().isBadRequest());

        // Validate the Categories in the database
        List<Categories> categoriesList = categoriesRepository.findAll();
        assertThat(categoriesList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(categoriesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCategories() throws Exception {
        int databaseSizeBeforeUpdate = categoriesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(categoriesSearchRepository.findAll());
        categories.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCategoriesMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(categories)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Categories in the database
        List<Categories> categoriesList = categoriesRepository.findAll();
        assertThat(categoriesList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(categoriesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateCategoriesWithPatch() throws Exception {
        // Initialize the database
        categoriesRepository.saveAndFlush(categories);

        int databaseSizeBeforeUpdate = categoriesRepository.findAll().size();

        // Update the categories using partial update
        Categories partialUpdatedCategories = new Categories();
        partialUpdatedCategories.setId(categories.getId());

        partialUpdatedCategories.detail(UPDATED_DETAIL);

        restCategoriesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCategories.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCategories))
            )
            .andExpect(status().isOk());

        // Validate the Categories in the database
        List<Categories> categoriesList = categoriesRepository.findAll();
        assertThat(categoriesList).hasSize(databaseSizeBeforeUpdate);
        Categories testCategories = categoriesList.get(categoriesList.size() - 1);
        assertThat(testCategories.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCategories.getDetail()).isEqualTo(UPDATED_DETAIL);
    }

    @Test
    @Transactional
    void fullUpdateCategoriesWithPatch() throws Exception {
        // Initialize the database
        categoriesRepository.saveAndFlush(categories);

        int databaseSizeBeforeUpdate = categoriesRepository.findAll().size();

        // Update the categories using partial update
        Categories partialUpdatedCategories = new Categories();
        partialUpdatedCategories.setId(categories.getId());

        partialUpdatedCategories.name(UPDATED_NAME).detail(UPDATED_DETAIL);

        restCategoriesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCategories.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCategories))
            )
            .andExpect(status().isOk());

        // Validate the Categories in the database
        List<Categories> categoriesList = categoriesRepository.findAll();
        assertThat(categoriesList).hasSize(databaseSizeBeforeUpdate);
        Categories testCategories = categoriesList.get(categoriesList.size() - 1);
        assertThat(testCategories.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCategories.getDetail()).isEqualTo(UPDATED_DETAIL);
    }

    @Test
    @Transactional
    void patchNonExistingCategories() throws Exception {
        int databaseSizeBeforeUpdate = categoriesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(categoriesSearchRepository.findAll());
        categories.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCategoriesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, categories.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(categories))
            )
            .andExpect(status().isBadRequest());

        // Validate the Categories in the database
        List<Categories> categoriesList = categoriesRepository.findAll();
        assertThat(categoriesList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(categoriesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCategories() throws Exception {
        int databaseSizeBeforeUpdate = categoriesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(categoriesSearchRepository.findAll());
        categories.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCategoriesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(categories))
            )
            .andExpect(status().isBadRequest());

        // Validate the Categories in the database
        List<Categories> categoriesList = categoriesRepository.findAll();
        assertThat(categoriesList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(categoriesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCategories() throws Exception {
        int databaseSizeBeforeUpdate = categoriesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(categoriesSearchRepository.findAll());
        categories.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCategoriesMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(categories))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Categories in the database
        List<Categories> categoriesList = categoriesRepository.findAll();
        assertThat(categoriesList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(categoriesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteCategories() throws Exception {
        // Initialize the database
        categoriesRepository.saveAndFlush(categories);
        categoriesRepository.save(categories);
        categoriesSearchRepository.save(categories);

        int databaseSizeBeforeDelete = categoriesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(categoriesSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the categories
        restCategoriesMockMvc
            .perform(delete(ENTITY_API_URL_ID, categories.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Categories> categoriesList = categoriesRepository.findAll();
        assertThat(categoriesList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(categoriesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchCategories() throws Exception {
        // Initialize the database
        categories = categoriesRepository.saveAndFlush(categories);
        categoriesSearchRepository.save(categories);

        // Search the categories
        restCategoriesMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + categories.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(categories.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].detail").value(hasItem(DEFAULT_DETAIL)));
    }
}
