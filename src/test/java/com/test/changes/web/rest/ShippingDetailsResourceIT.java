package com.test.changes.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.test.changes.IntegrationTest;
import com.test.changes.domain.OrderDelivery;
import com.test.changes.domain.Orders;
import com.test.changes.domain.ShippingDetails;
import com.test.changes.domain.enumeration.ShippingMethod;
import com.test.changes.repository.ShippingDetailsRepository;
import com.test.changes.repository.search.ShippingDetailsSearchRepository;
import com.test.changes.service.criteria.ShippingDetailsCriteria;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link ShippingDetailsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ShippingDetailsResourceIT {

    private static final String DEFAULT_SHIPPING_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_SHIPPING_ADDRESS = "BBBBBBBBBB";

    private static final ShippingMethod DEFAULT_SHIPPING_METHOD = ShippingMethod.COD;
    private static final ShippingMethod UPDATED_SHIPPING_METHOD = ShippingMethod.CASH;

    private static final LocalDate DEFAULT_ESTIMATED_DELIVERY_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_ESTIMATED_DELIVERY_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_ESTIMATED_DELIVERY_DATE = LocalDate.ofEpochDay(-1L);

    private static final String ENTITY_API_URL = "/api/shipping-details";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/shipping-details";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ShippingDetailsRepository shippingDetailsRepository;

    @Autowired
    private ShippingDetailsSearchRepository shippingDetailsSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restShippingDetailsMockMvc;

    private ShippingDetails shippingDetails;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ShippingDetails createEntity(EntityManager em) {
        ShippingDetails shippingDetails = new ShippingDetails()
            .shippingAddress(DEFAULT_SHIPPING_ADDRESS)
            .shippingMethod(DEFAULT_SHIPPING_METHOD)
            .estimatedDeliveryDate(DEFAULT_ESTIMATED_DELIVERY_DATE);
        // Add required entity
        Orders orders;
        if (TestUtil.findAll(em, Orders.class).isEmpty()) {
            orders = OrdersResourceIT.createEntity(em);
            em.persist(orders);
            em.flush();
        } else {
            orders = TestUtil.findAll(em, Orders.class).get(0);
        }
        shippingDetails.setOrder(orders);
        return shippingDetails;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ShippingDetails createUpdatedEntity(EntityManager em) {
        ShippingDetails shippingDetails = new ShippingDetails()
            .shippingAddress(UPDATED_SHIPPING_ADDRESS)
            .shippingMethod(UPDATED_SHIPPING_METHOD)
            .estimatedDeliveryDate(UPDATED_ESTIMATED_DELIVERY_DATE);
        // Add required entity
        Orders orders;
        if (TestUtil.findAll(em, Orders.class).isEmpty()) {
            orders = OrdersResourceIT.createUpdatedEntity(em);
            em.persist(orders);
            em.flush();
        } else {
            orders = TestUtil.findAll(em, Orders.class).get(0);
        }
        shippingDetails.setOrder(orders);
        return shippingDetails;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        shippingDetailsSearchRepository.deleteAll();
        assertThat(shippingDetailsSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        shippingDetails = createEntity(em);
    }

    @Test
    @Transactional
    void createShippingDetails() throws Exception {
        int databaseSizeBeforeCreate = shippingDetailsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(shippingDetailsSearchRepository.findAll());
        // Create the ShippingDetails
        restShippingDetailsMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(shippingDetails))
            )
            .andExpect(status().isCreated());

        // Validate the ShippingDetails in the database
        List<ShippingDetails> shippingDetailsList = shippingDetailsRepository.findAll();
        assertThat(shippingDetailsList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(shippingDetailsSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        ShippingDetails testShippingDetails = shippingDetailsList.get(shippingDetailsList.size() - 1);
        assertThat(testShippingDetails.getShippingAddress()).isEqualTo(DEFAULT_SHIPPING_ADDRESS);
        assertThat(testShippingDetails.getShippingMethod()).isEqualTo(DEFAULT_SHIPPING_METHOD);
        assertThat(testShippingDetails.getEstimatedDeliveryDate()).isEqualTo(DEFAULT_ESTIMATED_DELIVERY_DATE);
    }

    @Test
    @Transactional
    void createShippingDetailsWithExistingId() throws Exception {
        // Create the ShippingDetails with an existing ID
        shippingDetails.setId(1L);

        int databaseSizeBeforeCreate = shippingDetailsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(shippingDetailsSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restShippingDetailsMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(shippingDetails))
            )
            .andExpect(status().isBadRequest());

        // Validate the ShippingDetails in the database
        List<ShippingDetails> shippingDetailsList = shippingDetailsRepository.findAll();
        assertThat(shippingDetailsList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(shippingDetailsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkShippingAddressIsRequired() throws Exception {
        int databaseSizeBeforeTest = shippingDetailsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(shippingDetailsSearchRepository.findAll());
        // set the field null
        shippingDetails.setShippingAddress(null);

        // Create the ShippingDetails, which fails.

        restShippingDetailsMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(shippingDetails))
            )
            .andExpect(status().isBadRequest());

        List<ShippingDetails> shippingDetailsList = shippingDetailsRepository.findAll();
        assertThat(shippingDetailsList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(shippingDetailsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkShippingMethodIsRequired() throws Exception {
        int databaseSizeBeforeTest = shippingDetailsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(shippingDetailsSearchRepository.findAll());
        // set the field null
        shippingDetails.setShippingMethod(null);

        // Create the ShippingDetails, which fails.

        restShippingDetailsMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(shippingDetails))
            )
            .andExpect(status().isBadRequest());

        List<ShippingDetails> shippingDetailsList = shippingDetailsRepository.findAll();
        assertThat(shippingDetailsList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(shippingDetailsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkEstimatedDeliveryDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = shippingDetailsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(shippingDetailsSearchRepository.findAll());
        // set the field null
        shippingDetails.setEstimatedDeliveryDate(null);

        // Create the ShippingDetails, which fails.

        restShippingDetailsMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(shippingDetails))
            )
            .andExpect(status().isBadRequest());

        List<ShippingDetails> shippingDetailsList = shippingDetailsRepository.findAll();
        assertThat(shippingDetailsList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(shippingDetailsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllShippingDetails() throws Exception {
        // Initialize the database
        shippingDetailsRepository.saveAndFlush(shippingDetails);

        // Get all the shippingDetailsList
        restShippingDetailsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(shippingDetails.getId().intValue())))
            .andExpect(jsonPath("$.[*].shippingAddress").value(hasItem(DEFAULT_SHIPPING_ADDRESS)))
            .andExpect(jsonPath("$.[*].shippingMethod").value(hasItem(DEFAULT_SHIPPING_METHOD.toString())))
            .andExpect(jsonPath("$.[*].estimatedDeliveryDate").value(hasItem(DEFAULT_ESTIMATED_DELIVERY_DATE.toString())));
    }

    @Test
    @Transactional
    void getShippingDetails() throws Exception {
        // Initialize the database
        shippingDetailsRepository.saveAndFlush(shippingDetails);

        // Get the shippingDetails
        restShippingDetailsMockMvc
            .perform(get(ENTITY_API_URL_ID, shippingDetails.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(shippingDetails.getId().intValue()))
            .andExpect(jsonPath("$.shippingAddress").value(DEFAULT_SHIPPING_ADDRESS))
            .andExpect(jsonPath("$.shippingMethod").value(DEFAULT_SHIPPING_METHOD.toString()))
            .andExpect(jsonPath("$.estimatedDeliveryDate").value(DEFAULT_ESTIMATED_DELIVERY_DATE.toString()));
    }

    @Test
    @Transactional
    void getShippingDetailsByIdFiltering() throws Exception {
        // Initialize the database
        shippingDetailsRepository.saveAndFlush(shippingDetails);

        Long id = shippingDetails.getId();

        defaultShippingDetailsShouldBeFound("id.equals=" + id);
        defaultShippingDetailsShouldNotBeFound("id.notEquals=" + id);

        defaultShippingDetailsShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultShippingDetailsShouldNotBeFound("id.greaterThan=" + id);

        defaultShippingDetailsShouldBeFound("id.lessThanOrEqual=" + id);
        defaultShippingDetailsShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllShippingDetailsByShippingAddressIsEqualToSomething() throws Exception {
        // Initialize the database
        shippingDetailsRepository.saveAndFlush(shippingDetails);

        // Get all the shippingDetailsList where shippingAddress equals to DEFAULT_SHIPPING_ADDRESS
        defaultShippingDetailsShouldBeFound("shippingAddress.equals=" + DEFAULT_SHIPPING_ADDRESS);

        // Get all the shippingDetailsList where shippingAddress equals to UPDATED_SHIPPING_ADDRESS
        defaultShippingDetailsShouldNotBeFound("shippingAddress.equals=" + UPDATED_SHIPPING_ADDRESS);
    }

    @Test
    @Transactional
    void getAllShippingDetailsByShippingAddressIsInShouldWork() throws Exception {
        // Initialize the database
        shippingDetailsRepository.saveAndFlush(shippingDetails);

        // Get all the shippingDetailsList where shippingAddress in DEFAULT_SHIPPING_ADDRESS or UPDATED_SHIPPING_ADDRESS
        defaultShippingDetailsShouldBeFound("shippingAddress.in=" + DEFAULT_SHIPPING_ADDRESS + "," + UPDATED_SHIPPING_ADDRESS);

        // Get all the shippingDetailsList where shippingAddress equals to UPDATED_SHIPPING_ADDRESS
        defaultShippingDetailsShouldNotBeFound("shippingAddress.in=" + UPDATED_SHIPPING_ADDRESS);
    }

    @Test
    @Transactional
    void getAllShippingDetailsByShippingAddressIsNullOrNotNull() throws Exception {
        // Initialize the database
        shippingDetailsRepository.saveAndFlush(shippingDetails);

        // Get all the shippingDetailsList where shippingAddress is not null
        defaultShippingDetailsShouldBeFound("shippingAddress.specified=true");

        // Get all the shippingDetailsList where shippingAddress is null
        defaultShippingDetailsShouldNotBeFound("shippingAddress.specified=false");
    }

    @Test
    @Transactional
    void getAllShippingDetailsByShippingAddressContainsSomething() throws Exception {
        // Initialize the database
        shippingDetailsRepository.saveAndFlush(shippingDetails);

        // Get all the shippingDetailsList where shippingAddress contains DEFAULT_SHIPPING_ADDRESS
        defaultShippingDetailsShouldBeFound("shippingAddress.contains=" + DEFAULT_SHIPPING_ADDRESS);

        // Get all the shippingDetailsList where shippingAddress contains UPDATED_SHIPPING_ADDRESS
        defaultShippingDetailsShouldNotBeFound("shippingAddress.contains=" + UPDATED_SHIPPING_ADDRESS);
    }

    @Test
    @Transactional
    void getAllShippingDetailsByShippingAddressNotContainsSomething() throws Exception {
        // Initialize the database
        shippingDetailsRepository.saveAndFlush(shippingDetails);

        // Get all the shippingDetailsList where shippingAddress does not contain DEFAULT_SHIPPING_ADDRESS
        defaultShippingDetailsShouldNotBeFound("shippingAddress.doesNotContain=" + DEFAULT_SHIPPING_ADDRESS);

        // Get all the shippingDetailsList where shippingAddress does not contain UPDATED_SHIPPING_ADDRESS
        defaultShippingDetailsShouldBeFound("shippingAddress.doesNotContain=" + UPDATED_SHIPPING_ADDRESS);
    }

    @Test
    @Transactional
    void getAllShippingDetailsByShippingMethodIsEqualToSomething() throws Exception {
        // Initialize the database
        shippingDetailsRepository.saveAndFlush(shippingDetails);

        // Get all the shippingDetailsList where shippingMethod equals to DEFAULT_SHIPPING_METHOD
        defaultShippingDetailsShouldBeFound("shippingMethod.equals=" + DEFAULT_SHIPPING_METHOD);

        // Get all the shippingDetailsList where shippingMethod equals to UPDATED_SHIPPING_METHOD
        defaultShippingDetailsShouldNotBeFound("shippingMethod.equals=" + UPDATED_SHIPPING_METHOD);
    }

    @Test
    @Transactional
    void getAllShippingDetailsByShippingMethodIsInShouldWork() throws Exception {
        // Initialize the database
        shippingDetailsRepository.saveAndFlush(shippingDetails);

        // Get all the shippingDetailsList where shippingMethod in DEFAULT_SHIPPING_METHOD or UPDATED_SHIPPING_METHOD
        defaultShippingDetailsShouldBeFound("shippingMethod.in=" + DEFAULT_SHIPPING_METHOD + "," + UPDATED_SHIPPING_METHOD);

        // Get all the shippingDetailsList where shippingMethod equals to UPDATED_SHIPPING_METHOD
        defaultShippingDetailsShouldNotBeFound("shippingMethod.in=" + UPDATED_SHIPPING_METHOD);
    }

    @Test
    @Transactional
    void getAllShippingDetailsByShippingMethodIsNullOrNotNull() throws Exception {
        // Initialize the database
        shippingDetailsRepository.saveAndFlush(shippingDetails);

        // Get all the shippingDetailsList where shippingMethod is not null
        defaultShippingDetailsShouldBeFound("shippingMethod.specified=true");

        // Get all the shippingDetailsList where shippingMethod is null
        defaultShippingDetailsShouldNotBeFound("shippingMethod.specified=false");
    }

    @Test
    @Transactional
    void getAllShippingDetailsByEstimatedDeliveryDateIsEqualToSomething() throws Exception {
        // Initialize the database
        shippingDetailsRepository.saveAndFlush(shippingDetails);

        // Get all the shippingDetailsList where estimatedDeliveryDate equals to DEFAULT_ESTIMATED_DELIVERY_DATE
        defaultShippingDetailsShouldBeFound("estimatedDeliveryDate.equals=" + DEFAULT_ESTIMATED_DELIVERY_DATE);

        // Get all the shippingDetailsList where estimatedDeliveryDate equals to UPDATED_ESTIMATED_DELIVERY_DATE
        defaultShippingDetailsShouldNotBeFound("estimatedDeliveryDate.equals=" + UPDATED_ESTIMATED_DELIVERY_DATE);
    }

    @Test
    @Transactional
    void getAllShippingDetailsByEstimatedDeliveryDateIsInShouldWork() throws Exception {
        // Initialize the database
        shippingDetailsRepository.saveAndFlush(shippingDetails);

        // Get all the shippingDetailsList where estimatedDeliveryDate in DEFAULT_ESTIMATED_DELIVERY_DATE or UPDATED_ESTIMATED_DELIVERY_DATE
        defaultShippingDetailsShouldBeFound(
            "estimatedDeliveryDate.in=" + DEFAULT_ESTIMATED_DELIVERY_DATE + "," + UPDATED_ESTIMATED_DELIVERY_DATE
        );

        // Get all the shippingDetailsList where estimatedDeliveryDate equals to UPDATED_ESTIMATED_DELIVERY_DATE
        defaultShippingDetailsShouldNotBeFound("estimatedDeliveryDate.in=" + UPDATED_ESTIMATED_DELIVERY_DATE);
    }

    @Test
    @Transactional
    void getAllShippingDetailsByEstimatedDeliveryDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        shippingDetailsRepository.saveAndFlush(shippingDetails);

        // Get all the shippingDetailsList where estimatedDeliveryDate is not null
        defaultShippingDetailsShouldBeFound("estimatedDeliveryDate.specified=true");

        // Get all the shippingDetailsList where estimatedDeliveryDate is null
        defaultShippingDetailsShouldNotBeFound("estimatedDeliveryDate.specified=false");
    }

    @Test
    @Transactional
    void getAllShippingDetailsByEstimatedDeliveryDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        shippingDetailsRepository.saveAndFlush(shippingDetails);

        // Get all the shippingDetailsList where estimatedDeliveryDate is greater than or equal to DEFAULT_ESTIMATED_DELIVERY_DATE
        defaultShippingDetailsShouldBeFound("estimatedDeliveryDate.greaterThanOrEqual=" + DEFAULT_ESTIMATED_DELIVERY_DATE);

        // Get all the shippingDetailsList where estimatedDeliveryDate is greater than or equal to UPDATED_ESTIMATED_DELIVERY_DATE
        defaultShippingDetailsShouldNotBeFound("estimatedDeliveryDate.greaterThanOrEqual=" + UPDATED_ESTIMATED_DELIVERY_DATE);
    }

    @Test
    @Transactional
    void getAllShippingDetailsByEstimatedDeliveryDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        shippingDetailsRepository.saveAndFlush(shippingDetails);

        // Get all the shippingDetailsList where estimatedDeliveryDate is less than or equal to DEFAULT_ESTIMATED_DELIVERY_DATE
        defaultShippingDetailsShouldBeFound("estimatedDeliveryDate.lessThanOrEqual=" + DEFAULT_ESTIMATED_DELIVERY_DATE);

        // Get all the shippingDetailsList where estimatedDeliveryDate is less than or equal to SMALLER_ESTIMATED_DELIVERY_DATE
        defaultShippingDetailsShouldNotBeFound("estimatedDeliveryDate.lessThanOrEqual=" + SMALLER_ESTIMATED_DELIVERY_DATE);
    }

    @Test
    @Transactional
    void getAllShippingDetailsByEstimatedDeliveryDateIsLessThanSomething() throws Exception {
        // Initialize the database
        shippingDetailsRepository.saveAndFlush(shippingDetails);

        // Get all the shippingDetailsList where estimatedDeliveryDate is less than DEFAULT_ESTIMATED_DELIVERY_DATE
        defaultShippingDetailsShouldNotBeFound("estimatedDeliveryDate.lessThan=" + DEFAULT_ESTIMATED_DELIVERY_DATE);

        // Get all the shippingDetailsList where estimatedDeliveryDate is less than UPDATED_ESTIMATED_DELIVERY_DATE
        defaultShippingDetailsShouldBeFound("estimatedDeliveryDate.lessThan=" + UPDATED_ESTIMATED_DELIVERY_DATE);
    }

    @Test
    @Transactional
    void getAllShippingDetailsByEstimatedDeliveryDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        shippingDetailsRepository.saveAndFlush(shippingDetails);

        // Get all the shippingDetailsList where estimatedDeliveryDate is greater than DEFAULT_ESTIMATED_DELIVERY_DATE
        defaultShippingDetailsShouldNotBeFound("estimatedDeliveryDate.greaterThan=" + DEFAULT_ESTIMATED_DELIVERY_DATE);

        // Get all the shippingDetailsList where estimatedDeliveryDate is greater than SMALLER_ESTIMATED_DELIVERY_DATE
        defaultShippingDetailsShouldBeFound("estimatedDeliveryDate.greaterThan=" + SMALLER_ESTIMATED_DELIVERY_DATE);
    }

    @Test
    @Transactional
    void getAllShippingDetailsByOrderIsEqualToSomething() throws Exception {
        Orders order;
        if (TestUtil.findAll(em, Orders.class).isEmpty()) {
            shippingDetailsRepository.saveAndFlush(shippingDetails);
            order = OrdersResourceIT.createEntity(em);
        } else {
            order = TestUtil.findAll(em, Orders.class).get(0);
        }
        em.persist(order);
        em.flush();
        shippingDetails.setOrder(order);
        shippingDetailsRepository.saveAndFlush(shippingDetails);
        Long orderId = order.getId();

        // Get all the shippingDetailsList where order equals to orderId
        defaultShippingDetailsShouldBeFound("orderId.equals=" + orderId);

        // Get all the shippingDetailsList where order equals to (orderId + 1)
        defaultShippingDetailsShouldNotBeFound("orderId.equals=" + (orderId + 1));
    }

    @Test
    @Transactional
    void getAllShippingDetailsByOrderdeliveryShippingaddressIsEqualToSomething() throws Exception {
        OrderDelivery orderdeliveryShippingaddress;
        if (TestUtil.findAll(em, OrderDelivery.class).isEmpty()) {
            shippingDetailsRepository.saveAndFlush(shippingDetails);
            orderdeliveryShippingaddress = OrderDeliveryResourceIT.createEntity(em);
        } else {
            orderdeliveryShippingaddress = TestUtil.findAll(em, OrderDelivery.class).get(0);
        }
        em.persist(orderdeliveryShippingaddress);
        em.flush();
        shippingDetails.addOrderdeliveryShippingaddress(orderdeliveryShippingaddress);
        shippingDetailsRepository.saveAndFlush(shippingDetails);
        Long orderdeliveryShippingaddressId = orderdeliveryShippingaddress.getId();

        // Get all the shippingDetailsList where orderdeliveryShippingaddress equals to orderdeliveryShippingaddressId
        defaultShippingDetailsShouldBeFound("orderdeliveryShippingaddressId.equals=" + orderdeliveryShippingaddressId);

        // Get all the shippingDetailsList where orderdeliveryShippingaddress equals to (orderdeliveryShippingaddressId + 1)
        defaultShippingDetailsShouldNotBeFound("orderdeliveryShippingaddressId.equals=" + (orderdeliveryShippingaddressId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultShippingDetailsShouldBeFound(String filter) throws Exception {
        restShippingDetailsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(shippingDetails.getId().intValue())))
            .andExpect(jsonPath("$.[*].shippingAddress").value(hasItem(DEFAULT_SHIPPING_ADDRESS)))
            .andExpect(jsonPath("$.[*].shippingMethod").value(hasItem(DEFAULT_SHIPPING_METHOD.toString())))
            .andExpect(jsonPath("$.[*].estimatedDeliveryDate").value(hasItem(DEFAULT_ESTIMATED_DELIVERY_DATE.toString())));

        // Check, that the count call also returns 1
        restShippingDetailsMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultShippingDetailsShouldNotBeFound(String filter) throws Exception {
        restShippingDetailsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restShippingDetailsMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingShippingDetails() throws Exception {
        // Get the shippingDetails
        restShippingDetailsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingShippingDetails() throws Exception {
        // Initialize the database
        shippingDetailsRepository.saveAndFlush(shippingDetails);

        int databaseSizeBeforeUpdate = shippingDetailsRepository.findAll().size();
        shippingDetailsSearchRepository.save(shippingDetails);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(shippingDetailsSearchRepository.findAll());

        // Update the shippingDetails
        ShippingDetails updatedShippingDetails = shippingDetailsRepository.findById(shippingDetails.getId()).get();
        // Disconnect from session so that the updates on updatedShippingDetails are not directly saved in db
        em.detach(updatedShippingDetails);
        updatedShippingDetails
            .shippingAddress(UPDATED_SHIPPING_ADDRESS)
            .shippingMethod(UPDATED_SHIPPING_METHOD)
            .estimatedDeliveryDate(UPDATED_ESTIMATED_DELIVERY_DATE);

        restShippingDetailsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedShippingDetails.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedShippingDetails))
            )
            .andExpect(status().isOk());

        // Validate the ShippingDetails in the database
        List<ShippingDetails> shippingDetailsList = shippingDetailsRepository.findAll();
        assertThat(shippingDetailsList).hasSize(databaseSizeBeforeUpdate);
        ShippingDetails testShippingDetails = shippingDetailsList.get(shippingDetailsList.size() - 1);
        assertThat(testShippingDetails.getShippingAddress()).isEqualTo(UPDATED_SHIPPING_ADDRESS);
        assertThat(testShippingDetails.getShippingMethod()).isEqualTo(UPDATED_SHIPPING_METHOD);
        assertThat(testShippingDetails.getEstimatedDeliveryDate()).isEqualTo(UPDATED_ESTIMATED_DELIVERY_DATE);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(shippingDetailsSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<ShippingDetails> shippingDetailsSearchList = IterableUtils.toList(shippingDetailsSearchRepository.findAll());
                ShippingDetails testShippingDetailsSearch = shippingDetailsSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testShippingDetailsSearch.getShippingAddress()).isEqualTo(UPDATED_SHIPPING_ADDRESS);
                assertThat(testShippingDetailsSearch.getShippingMethod()).isEqualTo(UPDATED_SHIPPING_METHOD);
                assertThat(testShippingDetailsSearch.getEstimatedDeliveryDate()).isEqualTo(UPDATED_ESTIMATED_DELIVERY_DATE);
            });
    }

    @Test
    @Transactional
    void putNonExistingShippingDetails() throws Exception {
        int databaseSizeBeforeUpdate = shippingDetailsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(shippingDetailsSearchRepository.findAll());
        shippingDetails.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restShippingDetailsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, shippingDetails.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(shippingDetails))
            )
            .andExpect(status().isBadRequest());

        // Validate the ShippingDetails in the database
        List<ShippingDetails> shippingDetailsList = shippingDetailsRepository.findAll();
        assertThat(shippingDetailsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(shippingDetailsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchShippingDetails() throws Exception {
        int databaseSizeBeforeUpdate = shippingDetailsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(shippingDetailsSearchRepository.findAll());
        shippingDetails.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShippingDetailsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(shippingDetails))
            )
            .andExpect(status().isBadRequest());

        // Validate the ShippingDetails in the database
        List<ShippingDetails> shippingDetailsList = shippingDetailsRepository.findAll();
        assertThat(shippingDetailsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(shippingDetailsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamShippingDetails() throws Exception {
        int databaseSizeBeforeUpdate = shippingDetailsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(shippingDetailsSearchRepository.findAll());
        shippingDetails.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShippingDetailsMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(shippingDetails))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ShippingDetails in the database
        List<ShippingDetails> shippingDetailsList = shippingDetailsRepository.findAll();
        assertThat(shippingDetailsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(shippingDetailsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateShippingDetailsWithPatch() throws Exception {
        // Initialize the database
        shippingDetailsRepository.saveAndFlush(shippingDetails);

        int databaseSizeBeforeUpdate = shippingDetailsRepository.findAll().size();

        // Update the shippingDetails using partial update
        ShippingDetails partialUpdatedShippingDetails = new ShippingDetails();
        partialUpdatedShippingDetails.setId(shippingDetails.getId());

        partialUpdatedShippingDetails.shippingMethod(UPDATED_SHIPPING_METHOD).estimatedDeliveryDate(UPDATED_ESTIMATED_DELIVERY_DATE);

        restShippingDetailsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedShippingDetails.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedShippingDetails))
            )
            .andExpect(status().isOk());

        // Validate the ShippingDetails in the database
        List<ShippingDetails> shippingDetailsList = shippingDetailsRepository.findAll();
        assertThat(shippingDetailsList).hasSize(databaseSizeBeforeUpdate);
        ShippingDetails testShippingDetails = shippingDetailsList.get(shippingDetailsList.size() - 1);
        assertThat(testShippingDetails.getShippingAddress()).isEqualTo(DEFAULT_SHIPPING_ADDRESS);
        assertThat(testShippingDetails.getShippingMethod()).isEqualTo(UPDATED_SHIPPING_METHOD);
        assertThat(testShippingDetails.getEstimatedDeliveryDate()).isEqualTo(UPDATED_ESTIMATED_DELIVERY_DATE);
    }

    @Test
    @Transactional
    void fullUpdateShippingDetailsWithPatch() throws Exception {
        // Initialize the database
        shippingDetailsRepository.saveAndFlush(shippingDetails);

        int databaseSizeBeforeUpdate = shippingDetailsRepository.findAll().size();

        // Update the shippingDetails using partial update
        ShippingDetails partialUpdatedShippingDetails = new ShippingDetails();
        partialUpdatedShippingDetails.setId(shippingDetails.getId());

        partialUpdatedShippingDetails
            .shippingAddress(UPDATED_SHIPPING_ADDRESS)
            .shippingMethod(UPDATED_SHIPPING_METHOD)
            .estimatedDeliveryDate(UPDATED_ESTIMATED_DELIVERY_DATE);

        restShippingDetailsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedShippingDetails.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedShippingDetails))
            )
            .andExpect(status().isOk());

        // Validate the ShippingDetails in the database
        List<ShippingDetails> shippingDetailsList = shippingDetailsRepository.findAll();
        assertThat(shippingDetailsList).hasSize(databaseSizeBeforeUpdate);
        ShippingDetails testShippingDetails = shippingDetailsList.get(shippingDetailsList.size() - 1);
        assertThat(testShippingDetails.getShippingAddress()).isEqualTo(UPDATED_SHIPPING_ADDRESS);
        assertThat(testShippingDetails.getShippingMethod()).isEqualTo(UPDATED_SHIPPING_METHOD);
        assertThat(testShippingDetails.getEstimatedDeliveryDate()).isEqualTo(UPDATED_ESTIMATED_DELIVERY_DATE);
    }

    @Test
    @Transactional
    void patchNonExistingShippingDetails() throws Exception {
        int databaseSizeBeforeUpdate = shippingDetailsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(shippingDetailsSearchRepository.findAll());
        shippingDetails.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restShippingDetailsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, shippingDetails.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(shippingDetails))
            )
            .andExpect(status().isBadRequest());

        // Validate the ShippingDetails in the database
        List<ShippingDetails> shippingDetailsList = shippingDetailsRepository.findAll();
        assertThat(shippingDetailsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(shippingDetailsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchShippingDetails() throws Exception {
        int databaseSizeBeforeUpdate = shippingDetailsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(shippingDetailsSearchRepository.findAll());
        shippingDetails.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShippingDetailsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(shippingDetails))
            )
            .andExpect(status().isBadRequest());

        // Validate the ShippingDetails in the database
        List<ShippingDetails> shippingDetailsList = shippingDetailsRepository.findAll();
        assertThat(shippingDetailsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(shippingDetailsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamShippingDetails() throws Exception {
        int databaseSizeBeforeUpdate = shippingDetailsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(shippingDetailsSearchRepository.findAll());
        shippingDetails.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShippingDetailsMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(shippingDetails))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ShippingDetails in the database
        List<ShippingDetails> shippingDetailsList = shippingDetailsRepository.findAll();
        assertThat(shippingDetailsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(shippingDetailsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteShippingDetails() throws Exception {
        // Initialize the database
        shippingDetailsRepository.saveAndFlush(shippingDetails);
        shippingDetailsRepository.save(shippingDetails);
        shippingDetailsSearchRepository.save(shippingDetails);

        int databaseSizeBeforeDelete = shippingDetailsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(shippingDetailsSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the shippingDetails
        restShippingDetailsMockMvc
            .perform(delete(ENTITY_API_URL_ID, shippingDetails.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ShippingDetails> shippingDetailsList = shippingDetailsRepository.findAll();
        assertThat(shippingDetailsList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(shippingDetailsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchShippingDetails() throws Exception {
        // Initialize the database
        shippingDetails = shippingDetailsRepository.saveAndFlush(shippingDetails);
        shippingDetailsSearchRepository.save(shippingDetails);

        // Search the shippingDetails
        restShippingDetailsMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + shippingDetails.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(shippingDetails.getId().intValue())))
            .andExpect(jsonPath("$.[*].shippingAddress").value(hasItem(DEFAULT_SHIPPING_ADDRESS)))
            .andExpect(jsonPath("$.[*].shippingMethod").value(hasItem(DEFAULT_SHIPPING_METHOD.toString())))
            .andExpect(jsonPath("$.[*].estimatedDeliveryDate").value(hasItem(DEFAULT_ESTIMATED_DELIVERY_DATE.toString())));
    }
}
