package com.test.changes.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.test.changes.IntegrationTest;
import com.test.changes.domain.ProductDetails;
import com.test.changes.domain.Products;
import com.test.changes.repository.ProductDetailsRepository;
import com.test.changes.repository.search.ProductDetailsSearchRepository;
import com.test.changes.service.criteria.ProductDetailsCriteria;
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
 * Integration tests for the {@link ProductDetailsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ProductDetailsResourceIT {

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_IMAGE_URL = "AAAAAAAAAA";
    private static final String UPDATED_IMAGE_URL = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ISAVAILABLE = false;
    private static final Boolean UPDATED_ISAVAILABLE = true;

    private static final String ENTITY_API_URL = "/api/product-details";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/product-details";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ProductDetailsRepository productDetailsRepository;

    @Autowired
    private ProductDetailsSearchRepository productDetailsSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProductDetailsMockMvc;

    private ProductDetails productDetails;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductDetails createEntity(EntityManager em) {
        ProductDetails productDetails = new ProductDetails()
            .description(DEFAULT_DESCRIPTION)
            .imageUrl(DEFAULT_IMAGE_URL)
            .isavailable(DEFAULT_ISAVAILABLE);
        // Add required entity
        Products products;
        if (TestUtil.findAll(em, Products.class).isEmpty()) {
            products = ProductsResourceIT.createEntity(em);
            em.persist(products);
            em.flush();
        } else {
            products = TestUtil.findAll(em, Products.class).get(0);
        }
        productDetails.setProduct(products);
        return productDetails;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductDetails createUpdatedEntity(EntityManager em) {
        ProductDetails productDetails = new ProductDetails()
            .description(UPDATED_DESCRIPTION)
            .imageUrl(UPDATED_IMAGE_URL)
            .isavailable(UPDATED_ISAVAILABLE);
        // Add required entity
        Products products;
        if (TestUtil.findAll(em, Products.class).isEmpty()) {
            products = ProductsResourceIT.createUpdatedEntity(em);
            em.persist(products);
            em.flush();
        } else {
            products = TestUtil.findAll(em, Products.class).get(0);
        }
        productDetails.setProduct(products);
        return productDetails;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        productDetailsSearchRepository.deleteAll();
        assertThat(productDetailsSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        productDetails = createEntity(em);
    }

    @Test
    @Transactional
    void createProductDetails() throws Exception {
        int databaseSizeBeforeCreate = productDetailsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productDetailsSearchRepository.findAll());
        // Create the ProductDetails
        restProductDetailsMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(productDetails))
            )
            .andExpect(status().isCreated());

        // Validate the ProductDetails in the database
        List<ProductDetails> productDetailsList = productDetailsRepository.findAll();
        assertThat(productDetailsList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(productDetailsSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        ProductDetails testProductDetails = productDetailsList.get(productDetailsList.size() - 1);
        assertThat(testProductDetails.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testProductDetails.getImageUrl()).isEqualTo(DEFAULT_IMAGE_URL);
        assertThat(testProductDetails.getIsavailable()).isEqualTo(DEFAULT_ISAVAILABLE);
    }

    @Test
    @Transactional
    void createProductDetailsWithExistingId() throws Exception {
        // Create the ProductDetails with an existing ID
        productDetails.setId(1L);

        int databaseSizeBeforeCreate = productDetailsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productDetailsSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restProductDetailsMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(productDetails))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductDetails in the database
        List<ProductDetails> productDetailsList = productDetailsRepository.findAll();
        assertThat(productDetailsList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productDetailsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = productDetailsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productDetailsSearchRepository.findAll());
        // set the field null
        productDetails.setDescription(null);

        // Create the ProductDetails, which fails.

        restProductDetailsMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(productDetails))
            )
            .andExpect(status().isBadRequest());

        List<ProductDetails> productDetailsList = productDetailsRepository.findAll();
        assertThat(productDetailsList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productDetailsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkImageUrlIsRequired() throws Exception {
        int databaseSizeBeforeTest = productDetailsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productDetailsSearchRepository.findAll());
        // set the field null
        productDetails.setImageUrl(null);

        // Create the ProductDetails, which fails.

        restProductDetailsMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(productDetails))
            )
            .andExpect(status().isBadRequest());

        List<ProductDetails> productDetailsList = productDetailsRepository.findAll();
        assertThat(productDetailsList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productDetailsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkIsavailableIsRequired() throws Exception {
        int databaseSizeBeforeTest = productDetailsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productDetailsSearchRepository.findAll());
        // set the field null
        productDetails.setIsavailable(null);

        // Create the ProductDetails, which fails.

        restProductDetailsMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(productDetails))
            )
            .andExpect(status().isBadRequest());

        List<ProductDetails> productDetailsList = productDetailsRepository.findAll();
        assertThat(productDetailsList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productDetailsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllProductDetails() throws Exception {
        // Initialize the database
        productDetailsRepository.saveAndFlush(productDetails);

        // Get all the productDetailsList
        restProductDetailsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(productDetails.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].imageUrl").value(hasItem(DEFAULT_IMAGE_URL)))
            .andExpect(jsonPath("$.[*].isavailable").value(hasItem(DEFAULT_ISAVAILABLE.booleanValue())));
    }

    @Test
    @Transactional
    void getProductDetails() throws Exception {
        // Initialize the database
        productDetailsRepository.saveAndFlush(productDetails);

        // Get the productDetails
        restProductDetailsMockMvc
            .perform(get(ENTITY_API_URL_ID, productDetails.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(productDetails.getId().intValue()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.imageUrl").value(DEFAULT_IMAGE_URL))
            .andExpect(jsonPath("$.isavailable").value(DEFAULT_ISAVAILABLE.booleanValue()));
    }

    @Test
    @Transactional
    void getProductDetailsByIdFiltering() throws Exception {
        // Initialize the database
        productDetailsRepository.saveAndFlush(productDetails);

        Long id = productDetails.getId();

        defaultProductDetailsShouldBeFound("id.equals=" + id);
        defaultProductDetailsShouldNotBeFound("id.notEquals=" + id);

        defaultProductDetailsShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultProductDetailsShouldNotBeFound("id.greaterThan=" + id);

        defaultProductDetailsShouldBeFound("id.lessThanOrEqual=" + id);
        defaultProductDetailsShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllProductDetailsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        productDetailsRepository.saveAndFlush(productDetails);

        // Get all the productDetailsList where description equals to DEFAULT_DESCRIPTION
        defaultProductDetailsShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the productDetailsList where description equals to UPDATED_DESCRIPTION
        defaultProductDetailsShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllProductDetailsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        productDetailsRepository.saveAndFlush(productDetails);

        // Get all the productDetailsList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultProductDetailsShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the productDetailsList where description equals to UPDATED_DESCRIPTION
        defaultProductDetailsShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllProductDetailsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        productDetailsRepository.saveAndFlush(productDetails);

        // Get all the productDetailsList where description is not null
        defaultProductDetailsShouldBeFound("description.specified=true");

        // Get all the productDetailsList where description is null
        defaultProductDetailsShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    void getAllProductDetailsByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        productDetailsRepository.saveAndFlush(productDetails);

        // Get all the productDetailsList where description contains DEFAULT_DESCRIPTION
        defaultProductDetailsShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the productDetailsList where description contains UPDATED_DESCRIPTION
        defaultProductDetailsShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllProductDetailsByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        productDetailsRepository.saveAndFlush(productDetails);

        // Get all the productDetailsList where description does not contain DEFAULT_DESCRIPTION
        defaultProductDetailsShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the productDetailsList where description does not contain UPDATED_DESCRIPTION
        defaultProductDetailsShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllProductDetailsByImageUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        productDetailsRepository.saveAndFlush(productDetails);

        // Get all the productDetailsList where imageUrl equals to DEFAULT_IMAGE_URL
        defaultProductDetailsShouldBeFound("imageUrl.equals=" + DEFAULT_IMAGE_URL);

        // Get all the productDetailsList where imageUrl equals to UPDATED_IMAGE_URL
        defaultProductDetailsShouldNotBeFound("imageUrl.equals=" + UPDATED_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllProductDetailsByImageUrlIsInShouldWork() throws Exception {
        // Initialize the database
        productDetailsRepository.saveAndFlush(productDetails);

        // Get all the productDetailsList where imageUrl in DEFAULT_IMAGE_URL or UPDATED_IMAGE_URL
        defaultProductDetailsShouldBeFound("imageUrl.in=" + DEFAULT_IMAGE_URL + "," + UPDATED_IMAGE_URL);

        // Get all the productDetailsList where imageUrl equals to UPDATED_IMAGE_URL
        defaultProductDetailsShouldNotBeFound("imageUrl.in=" + UPDATED_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllProductDetailsByImageUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        productDetailsRepository.saveAndFlush(productDetails);

        // Get all the productDetailsList where imageUrl is not null
        defaultProductDetailsShouldBeFound("imageUrl.specified=true");

        // Get all the productDetailsList where imageUrl is null
        defaultProductDetailsShouldNotBeFound("imageUrl.specified=false");
    }

    @Test
    @Transactional
    void getAllProductDetailsByImageUrlContainsSomething() throws Exception {
        // Initialize the database
        productDetailsRepository.saveAndFlush(productDetails);

        // Get all the productDetailsList where imageUrl contains DEFAULT_IMAGE_URL
        defaultProductDetailsShouldBeFound("imageUrl.contains=" + DEFAULT_IMAGE_URL);

        // Get all the productDetailsList where imageUrl contains UPDATED_IMAGE_URL
        defaultProductDetailsShouldNotBeFound("imageUrl.contains=" + UPDATED_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllProductDetailsByImageUrlNotContainsSomething() throws Exception {
        // Initialize the database
        productDetailsRepository.saveAndFlush(productDetails);

        // Get all the productDetailsList where imageUrl does not contain DEFAULT_IMAGE_URL
        defaultProductDetailsShouldNotBeFound("imageUrl.doesNotContain=" + DEFAULT_IMAGE_URL);

        // Get all the productDetailsList where imageUrl does not contain UPDATED_IMAGE_URL
        defaultProductDetailsShouldBeFound("imageUrl.doesNotContain=" + UPDATED_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllProductDetailsByIsavailableIsEqualToSomething() throws Exception {
        // Initialize the database
        productDetailsRepository.saveAndFlush(productDetails);

        // Get all the productDetailsList where isavailable equals to DEFAULT_ISAVAILABLE
        defaultProductDetailsShouldBeFound("isavailable.equals=" + DEFAULT_ISAVAILABLE);

        // Get all the productDetailsList where isavailable equals to UPDATED_ISAVAILABLE
        defaultProductDetailsShouldNotBeFound("isavailable.equals=" + UPDATED_ISAVAILABLE);
    }

    @Test
    @Transactional
    void getAllProductDetailsByIsavailableIsInShouldWork() throws Exception {
        // Initialize the database
        productDetailsRepository.saveAndFlush(productDetails);

        // Get all the productDetailsList where isavailable in DEFAULT_ISAVAILABLE or UPDATED_ISAVAILABLE
        defaultProductDetailsShouldBeFound("isavailable.in=" + DEFAULT_ISAVAILABLE + "," + UPDATED_ISAVAILABLE);

        // Get all the productDetailsList where isavailable equals to UPDATED_ISAVAILABLE
        defaultProductDetailsShouldNotBeFound("isavailable.in=" + UPDATED_ISAVAILABLE);
    }

    @Test
    @Transactional
    void getAllProductDetailsByIsavailableIsNullOrNotNull() throws Exception {
        // Initialize the database
        productDetailsRepository.saveAndFlush(productDetails);

        // Get all the productDetailsList where isavailable is not null
        defaultProductDetailsShouldBeFound("isavailable.specified=true");

        // Get all the productDetailsList where isavailable is null
        defaultProductDetailsShouldNotBeFound("isavailable.specified=false");
    }

    @Test
    @Transactional
    void getAllProductDetailsByProductIsEqualToSomething() throws Exception {
        Products product;
        if (TestUtil.findAll(em, Products.class).isEmpty()) {
            productDetailsRepository.saveAndFlush(productDetails);
            product = ProductsResourceIT.createEntity(em);
        } else {
            product = TestUtil.findAll(em, Products.class).get(0);
        }
        em.persist(product);
        em.flush();
        productDetails.setProduct(product);
        productDetailsRepository.saveAndFlush(productDetails);
        Long productId = product.getId();

        // Get all the productDetailsList where product equals to productId
        defaultProductDetailsShouldBeFound("productId.equals=" + productId);

        // Get all the productDetailsList where product equals to (productId + 1)
        defaultProductDetailsShouldNotBeFound("productId.equals=" + (productId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultProductDetailsShouldBeFound(String filter) throws Exception {
        restProductDetailsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(productDetails.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].imageUrl").value(hasItem(DEFAULT_IMAGE_URL)))
            .andExpect(jsonPath("$.[*].isavailable").value(hasItem(DEFAULT_ISAVAILABLE.booleanValue())));

        // Check, that the count call also returns 1
        restProductDetailsMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultProductDetailsShouldNotBeFound(String filter) throws Exception {
        restProductDetailsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restProductDetailsMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingProductDetails() throws Exception {
        // Get the productDetails
        restProductDetailsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingProductDetails() throws Exception {
        // Initialize the database
        productDetailsRepository.saveAndFlush(productDetails);

        int databaseSizeBeforeUpdate = productDetailsRepository.findAll().size();
        productDetailsSearchRepository.save(productDetails);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productDetailsSearchRepository.findAll());

        // Update the productDetails
        ProductDetails updatedProductDetails = productDetailsRepository.findById(productDetails.getId()).get();
        // Disconnect from session so that the updates on updatedProductDetails are not directly saved in db
        em.detach(updatedProductDetails);
        updatedProductDetails.description(UPDATED_DESCRIPTION).imageUrl(UPDATED_IMAGE_URL).isavailable(UPDATED_ISAVAILABLE);

        restProductDetailsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedProductDetails.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedProductDetails))
            )
            .andExpect(status().isOk());

        // Validate the ProductDetails in the database
        List<ProductDetails> productDetailsList = productDetailsRepository.findAll();
        assertThat(productDetailsList).hasSize(databaseSizeBeforeUpdate);
        ProductDetails testProductDetails = productDetailsList.get(productDetailsList.size() - 1);
        assertThat(testProductDetails.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testProductDetails.getImageUrl()).isEqualTo(UPDATED_IMAGE_URL);
        assertThat(testProductDetails.getIsavailable()).isEqualTo(UPDATED_ISAVAILABLE);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(productDetailsSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<ProductDetails> productDetailsSearchList = IterableUtils.toList(productDetailsSearchRepository.findAll());
                ProductDetails testProductDetailsSearch = productDetailsSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testProductDetailsSearch.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
                assertThat(testProductDetailsSearch.getImageUrl()).isEqualTo(UPDATED_IMAGE_URL);
                assertThat(testProductDetailsSearch.getIsavailable()).isEqualTo(UPDATED_ISAVAILABLE);
            });
    }

    @Test
    @Transactional
    void putNonExistingProductDetails() throws Exception {
        int databaseSizeBeforeUpdate = productDetailsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productDetailsSearchRepository.findAll());
        productDetails.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductDetailsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, productDetails.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(productDetails))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductDetails in the database
        List<ProductDetails> productDetailsList = productDetailsRepository.findAll();
        assertThat(productDetailsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productDetailsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchProductDetails() throws Exception {
        int databaseSizeBeforeUpdate = productDetailsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productDetailsSearchRepository.findAll());
        productDetails.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductDetailsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(productDetails))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductDetails in the database
        List<ProductDetails> productDetailsList = productDetailsRepository.findAll();
        assertThat(productDetailsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productDetailsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProductDetails() throws Exception {
        int databaseSizeBeforeUpdate = productDetailsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productDetailsSearchRepository.findAll());
        productDetails.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductDetailsMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(productDetails)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProductDetails in the database
        List<ProductDetails> productDetailsList = productDetailsRepository.findAll();
        assertThat(productDetailsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productDetailsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateProductDetailsWithPatch() throws Exception {
        // Initialize the database
        productDetailsRepository.saveAndFlush(productDetails);

        int databaseSizeBeforeUpdate = productDetailsRepository.findAll().size();

        // Update the productDetails using partial update
        ProductDetails partialUpdatedProductDetails = new ProductDetails();
        partialUpdatedProductDetails.setId(productDetails.getId());

        partialUpdatedProductDetails.description(UPDATED_DESCRIPTION);

        restProductDetailsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProductDetails.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProductDetails))
            )
            .andExpect(status().isOk());

        // Validate the ProductDetails in the database
        List<ProductDetails> productDetailsList = productDetailsRepository.findAll();
        assertThat(productDetailsList).hasSize(databaseSizeBeforeUpdate);
        ProductDetails testProductDetails = productDetailsList.get(productDetailsList.size() - 1);
        assertThat(testProductDetails.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testProductDetails.getImageUrl()).isEqualTo(DEFAULT_IMAGE_URL);
        assertThat(testProductDetails.getIsavailable()).isEqualTo(DEFAULT_ISAVAILABLE);
    }

    @Test
    @Transactional
    void fullUpdateProductDetailsWithPatch() throws Exception {
        // Initialize the database
        productDetailsRepository.saveAndFlush(productDetails);

        int databaseSizeBeforeUpdate = productDetailsRepository.findAll().size();

        // Update the productDetails using partial update
        ProductDetails partialUpdatedProductDetails = new ProductDetails();
        partialUpdatedProductDetails.setId(productDetails.getId());

        partialUpdatedProductDetails.description(UPDATED_DESCRIPTION).imageUrl(UPDATED_IMAGE_URL).isavailable(UPDATED_ISAVAILABLE);

        restProductDetailsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProductDetails.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProductDetails))
            )
            .andExpect(status().isOk());

        // Validate the ProductDetails in the database
        List<ProductDetails> productDetailsList = productDetailsRepository.findAll();
        assertThat(productDetailsList).hasSize(databaseSizeBeforeUpdate);
        ProductDetails testProductDetails = productDetailsList.get(productDetailsList.size() - 1);
        assertThat(testProductDetails.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testProductDetails.getImageUrl()).isEqualTo(UPDATED_IMAGE_URL);
        assertThat(testProductDetails.getIsavailable()).isEqualTo(UPDATED_ISAVAILABLE);
    }

    @Test
    @Transactional
    void patchNonExistingProductDetails() throws Exception {
        int databaseSizeBeforeUpdate = productDetailsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productDetailsSearchRepository.findAll());
        productDetails.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductDetailsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, productDetails.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(productDetails))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductDetails in the database
        List<ProductDetails> productDetailsList = productDetailsRepository.findAll();
        assertThat(productDetailsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productDetailsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProductDetails() throws Exception {
        int databaseSizeBeforeUpdate = productDetailsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productDetailsSearchRepository.findAll());
        productDetails.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductDetailsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(productDetails))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductDetails in the database
        List<ProductDetails> productDetailsList = productDetailsRepository.findAll();
        assertThat(productDetailsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productDetailsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProductDetails() throws Exception {
        int databaseSizeBeforeUpdate = productDetailsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productDetailsSearchRepository.findAll());
        productDetails.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductDetailsMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(productDetails))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProductDetails in the database
        List<ProductDetails> productDetailsList = productDetailsRepository.findAll();
        assertThat(productDetailsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productDetailsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteProductDetails() throws Exception {
        // Initialize the database
        productDetailsRepository.saveAndFlush(productDetails);
        productDetailsRepository.save(productDetails);
        productDetailsSearchRepository.save(productDetails);

        int databaseSizeBeforeDelete = productDetailsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productDetailsSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the productDetails
        restProductDetailsMockMvc
            .perform(delete(ENTITY_API_URL_ID, productDetails.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ProductDetails> productDetailsList = productDetailsRepository.findAll();
        assertThat(productDetailsList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productDetailsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchProductDetails() throws Exception {
        // Initialize the database
        productDetails = productDetailsRepository.saveAndFlush(productDetails);
        productDetailsSearchRepository.save(productDetails);

        // Search the productDetails
        restProductDetailsMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + productDetails.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(productDetails.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].imageUrl").value(hasItem(DEFAULT_IMAGE_URL)))
            .andExpect(jsonPath("$.[*].isavailable").value(hasItem(DEFAULT_ISAVAILABLE.booleanValue())));
    }
}
