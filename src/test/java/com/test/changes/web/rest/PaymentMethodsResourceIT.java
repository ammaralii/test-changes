package com.test.changes.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.test.changes.IntegrationTest;
import com.test.changes.domain.Customers;
import com.test.changes.domain.PaymentMethods;
import com.test.changes.repository.PaymentMethodsRepository;
import com.test.changes.repository.search.PaymentMethodsSearchRepository;
import com.test.changes.service.criteria.PaymentMethodsCriteria;
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
 * Integration tests for the {@link PaymentMethodsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PaymentMethodsResourceIT {

    private static final String DEFAULT_CARD_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_CARD_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_CARD_HOLDER_NAME = "AAAAAAAAAA";
    private static final String UPDATED_CARD_HOLDER_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_EXPIRATION_DATE = "AAAAAAAAAA";
    private static final String UPDATED_EXPIRATION_DATE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/payment-methods";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/payment-methods";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PaymentMethodsRepository paymentMethodsRepository;

    @Autowired
    private PaymentMethodsSearchRepository paymentMethodsSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPaymentMethodsMockMvc;

    private PaymentMethods paymentMethods;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PaymentMethods createEntity(EntityManager em) {
        PaymentMethods paymentMethods = new PaymentMethods()
            .cardNumber(DEFAULT_CARD_NUMBER)
            .cardHolderName(DEFAULT_CARD_HOLDER_NAME)
            .expirationDate(DEFAULT_EXPIRATION_DATE);
        // Add required entity
        Customers customers;
        if (TestUtil.findAll(em, Customers.class).isEmpty()) {
            customers = CustomersResourceIT.createEntity(em);
            em.persist(customers);
            em.flush();
        } else {
            customers = TestUtil.findAll(em, Customers.class).get(0);
        }
        paymentMethods.setCustomer(customers);
        return paymentMethods;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PaymentMethods createUpdatedEntity(EntityManager em) {
        PaymentMethods paymentMethods = new PaymentMethods()
            .cardNumber(UPDATED_CARD_NUMBER)
            .cardHolderName(UPDATED_CARD_HOLDER_NAME)
            .expirationDate(UPDATED_EXPIRATION_DATE);
        // Add required entity
        Customers customers;
        if (TestUtil.findAll(em, Customers.class).isEmpty()) {
            customers = CustomersResourceIT.createUpdatedEntity(em);
            em.persist(customers);
            em.flush();
        } else {
            customers = TestUtil.findAll(em, Customers.class).get(0);
        }
        paymentMethods.setCustomer(customers);
        return paymentMethods;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        paymentMethodsSearchRepository.deleteAll();
        assertThat(paymentMethodsSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        paymentMethods = createEntity(em);
    }

    @Test
    @Transactional
    void createPaymentMethods() throws Exception {
        int databaseSizeBeforeCreate = paymentMethodsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentMethodsSearchRepository.findAll());
        // Create the PaymentMethods
        restPaymentMethodsMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(paymentMethods))
            )
            .andExpect(status().isCreated());

        // Validate the PaymentMethods in the database
        List<PaymentMethods> paymentMethodsList = paymentMethodsRepository.findAll();
        assertThat(paymentMethodsList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentMethodsSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        PaymentMethods testPaymentMethods = paymentMethodsList.get(paymentMethodsList.size() - 1);
        assertThat(testPaymentMethods.getCardNumber()).isEqualTo(DEFAULT_CARD_NUMBER);
        assertThat(testPaymentMethods.getCardHolderName()).isEqualTo(DEFAULT_CARD_HOLDER_NAME);
        assertThat(testPaymentMethods.getExpirationDate()).isEqualTo(DEFAULT_EXPIRATION_DATE);
    }

    @Test
    @Transactional
    void createPaymentMethodsWithExistingId() throws Exception {
        // Create the PaymentMethods with an existing ID
        paymentMethods.setId(1L);

        int databaseSizeBeforeCreate = paymentMethodsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentMethodsSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restPaymentMethodsMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(paymentMethods))
            )
            .andExpect(status().isBadRequest());

        // Validate the PaymentMethods in the database
        List<PaymentMethods> paymentMethodsList = paymentMethodsRepository.findAll();
        assertThat(paymentMethodsList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentMethodsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCardNumberIsRequired() throws Exception {
        int databaseSizeBeforeTest = paymentMethodsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentMethodsSearchRepository.findAll());
        // set the field null
        paymentMethods.setCardNumber(null);

        // Create the PaymentMethods, which fails.

        restPaymentMethodsMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(paymentMethods))
            )
            .andExpect(status().isBadRequest());

        List<PaymentMethods> paymentMethodsList = paymentMethodsRepository.findAll();
        assertThat(paymentMethodsList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentMethodsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCardHolderNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = paymentMethodsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentMethodsSearchRepository.findAll());
        // set the field null
        paymentMethods.setCardHolderName(null);

        // Create the PaymentMethods, which fails.

        restPaymentMethodsMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(paymentMethods))
            )
            .andExpect(status().isBadRequest());

        List<PaymentMethods> paymentMethodsList = paymentMethodsRepository.findAll();
        assertThat(paymentMethodsList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentMethodsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkExpirationDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = paymentMethodsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentMethodsSearchRepository.findAll());
        // set the field null
        paymentMethods.setExpirationDate(null);

        // Create the PaymentMethods, which fails.

        restPaymentMethodsMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(paymentMethods))
            )
            .andExpect(status().isBadRequest());

        List<PaymentMethods> paymentMethodsList = paymentMethodsRepository.findAll();
        assertThat(paymentMethodsList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentMethodsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllPaymentMethods() throws Exception {
        // Initialize the database
        paymentMethodsRepository.saveAndFlush(paymentMethods);

        // Get all the paymentMethodsList
        restPaymentMethodsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(paymentMethods.getId().intValue())))
            .andExpect(jsonPath("$.[*].cardNumber").value(hasItem(DEFAULT_CARD_NUMBER)))
            .andExpect(jsonPath("$.[*].cardHolderName").value(hasItem(DEFAULT_CARD_HOLDER_NAME)))
            .andExpect(jsonPath("$.[*].expirationDate").value(hasItem(DEFAULT_EXPIRATION_DATE)));
    }

    @Test
    @Transactional
    void getPaymentMethods() throws Exception {
        // Initialize the database
        paymentMethodsRepository.saveAndFlush(paymentMethods);

        // Get the paymentMethods
        restPaymentMethodsMockMvc
            .perform(get(ENTITY_API_URL_ID, paymentMethods.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(paymentMethods.getId().intValue()))
            .andExpect(jsonPath("$.cardNumber").value(DEFAULT_CARD_NUMBER))
            .andExpect(jsonPath("$.cardHolderName").value(DEFAULT_CARD_HOLDER_NAME))
            .andExpect(jsonPath("$.expirationDate").value(DEFAULT_EXPIRATION_DATE));
    }

    @Test
    @Transactional
    void getPaymentMethodsByIdFiltering() throws Exception {
        // Initialize the database
        paymentMethodsRepository.saveAndFlush(paymentMethods);

        Long id = paymentMethods.getId();

        defaultPaymentMethodsShouldBeFound("id.equals=" + id);
        defaultPaymentMethodsShouldNotBeFound("id.notEquals=" + id);

        defaultPaymentMethodsShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultPaymentMethodsShouldNotBeFound("id.greaterThan=" + id);

        defaultPaymentMethodsShouldBeFound("id.lessThanOrEqual=" + id);
        defaultPaymentMethodsShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPaymentMethodsByCardNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        paymentMethodsRepository.saveAndFlush(paymentMethods);

        // Get all the paymentMethodsList where cardNumber equals to DEFAULT_CARD_NUMBER
        defaultPaymentMethodsShouldBeFound("cardNumber.equals=" + DEFAULT_CARD_NUMBER);

        // Get all the paymentMethodsList where cardNumber equals to UPDATED_CARD_NUMBER
        defaultPaymentMethodsShouldNotBeFound("cardNumber.equals=" + UPDATED_CARD_NUMBER);
    }

    @Test
    @Transactional
    void getAllPaymentMethodsByCardNumberIsInShouldWork() throws Exception {
        // Initialize the database
        paymentMethodsRepository.saveAndFlush(paymentMethods);

        // Get all the paymentMethodsList where cardNumber in DEFAULT_CARD_NUMBER or UPDATED_CARD_NUMBER
        defaultPaymentMethodsShouldBeFound("cardNumber.in=" + DEFAULT_CARD_NUMBER + "," + UPDATED_CARD_NUMBER);

        // Get all the paymentMethodsList where cardNumber equals to UPDATED_CARD_NUMBER
        defaultPaymentMethodsShouldNotBeFound("cardNumber.in=" + UPDATED_CARD_NUMBER);
    }

    @Test
    @Transactional
    void getAllPaymentMethodsByCardNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        paymentMethodsRepository.saveAndFlush(paymentMethods);

        // Get all the paymentMethodsList where cardNumber is not null
        defaultPaymentMethodsShouldBeFound("cardNumber.specified=true");

        // Get all the paymentMethodsList where cardNumber is null
        defaultPaymentMethodsShouldNotBeFound("cardNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentMethodsByCardNumberContainsSomething() throws Exception {
        // Initialize the database
        paymentMethodsRepository.saveAndFlush(paymentMethods);

        // Get all the paymentMethodsList where cardNumber contains DEFAULT_CARD_NUMBER
        defaultPaymentMethodsShouldBeFound("cardNumber.contains=" + DEFAULT_CARD_NUMBER);

        // Get all the paymentMethodsList where cardNumber contains UPDATED_CARD_NUMBER
        defaultPaymentMethodsShouldNotBeFound("cardNumber.contains=" + UPDATED_CARD_NUMBER);
    }

    @Test
    @Transactional
    void getAllPaymentMethodsByCardNumberNotContainsSomething() throws Exception {
        // Initialize the database
        paymentMethodsRepository.saveAndFlush(paymentMethods);

        // Get all the paymentMethodsList where cardNumber does not contain DEFAULT_CARD_NUMBER
        defaultPaymentMethodsShouldNotBeFound("cardNumber.doesNotContain=" + DEFAULT_CARD_NUMBER);

        // Get all the paymentMethodsList where cardNumber does not contain UPDATED_CARD_NUMBER
        defaultPaymentMethodsShouldBeFound("cardNumber.doesNotContain=" + UPDATED_CARD_NUMBER);
    }

    @Test
    @Transactional
    void getAllPaymentMethodsByCardHolderNameIsEqualToSomething() throws Exception {
        // Initialize the database
        paymentMethodsRepository.saveAndFlush(paymentMethods);

        // Get all the paymentMethodsList where cardHolderName equals to DEFAULT_CARD_HOLDER_NAME
        defaultPaymentMethodsShouldBeFound("cardHolderName.equals=" + DEFAULT_CARD_HOLDER_NAME);

        // Get all the paymentMethodsList where cardHolderName equals to UPDATED_CARD_HOLDER_NAME
        defaultPaymentMethodsShouldNotBeFound("cardHolderName.equals=" + UPDATED_CARD_HOLDER_NAME);
    }

    @Test
    @Transactional
    void getAllPaymentMethodsByCardHolderNameIsInShouldWork() throws Exception {
        // Initialize the database
        paymentMethodsRepository.saveAndFlush(paymentMethods);

        // Get all the paymentMethodsList where cardHolderName in DEFAULT_CARD_HOLDER_NAME or UPDATED_CARD_HOLDER_NAME
        defaultPaymentMethodsShouldBeFound("cardHolderName.in=" + DEFAULT_CARD_HOLDER_NAME + "," + UPDATED_CARD_HOLDER_NAME);

        // Get all the paymentMethodsList where cardHolderName equals to UPDATED_CARD_HOLDER_NAME
        defaultPaymentMethodsShouldNotBeFound("cardHolderName.in=" + UPDATED_CARD_HOLDER_NAME);
    }

    @Test
    @Transactional
    void getAllPaymentMethodsByCardHolderNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        paymentMethodsRepository.saveAndFlush(paymentMethods);

        // Get all the paymentMethodsList where cardHolderName is not null
        defaultPaymentMethodsShouldBeFound("cardHolderName.specified=true");

        // Get all the paymentMethodsList where cardHolderName is null
        defaultPaymentMethodsShouldNotBeFound("cardHolderName.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentMethodsByCardHolderNameContainsSomething() throws Exception {
        // Initialize the database
        paymentMethodsRepository.saveAndFlush(paymentMethods);

        // Get all the paymentMethodsList where cardHolderName contains DEFAULT_CARD_HOLDER_NAME
        defaultPaymentMethodsShouldBeFound("cardHolderName.contains=" + DEFAULT_CARD_HOLDER_NAME);

        // Get all the paymentMethodsList where cardHolderName contains UPDATED_CARD_HOLDER_NAME
        defaultPaymentMethodsShouldNotBeFound("cardHolderName.contains=" + UPDATED_CARD_HOLDER_NAME);
    }

    @Test
    @Transactional
    void getAllPaymentMethodsByCardHolderNameNotContainsSomething() throws Exception {
        // Initialize the database
        paymentMethodsRepository.saveAndFlush(paymentMethods);

        // Get all the paymentMethodsList where cardHolderName does not contain DEFAULT_CARD_HOLDER_NAME
        defaultPaymentMethodsShouldNotBeFound("cardHolderName.doesNotContain=" + DEFAULT_CARD_HOLDER_NAME);

        // Get all the paymentMethodsList where cardHolderName does not contain UPDATED_CARD_HOLDER_NAME
        defaultPaymentMethodsShouldBeFound("cardHolderName.doesNotContain=" + UPDATED_CARD_HOLDER_NAME);
    }

    @Test
    @Transactional
    void getAllPaymentMethodsByExpirationDateIsEqualToSomething() throws Exception {
        // Initialize the database
        paymentMethodsRepository.saveAndFlush(paymentMethods);

        // Get all the paymentMethodsList where expirationDate equals to DEFAULT_EXPIRATION_DATE
        defaultPaymentMethodsShouldBeFound("expirationDate.equals=" + DEFAULT_EXPIRATION_DATE);

        // Get all the paymentMethodsList where expirationDate equals to UPDATED_EXPIRATION_DATE
        defaultPaymentMethodsShouldNotBeFound("expirationDate.equals=" + UPDATED_EXPIRATION_DATE);
    }

    @Test
    @Transactional
    void getAllPaymentMethodsByExpirationDateIsInShouldWork() throws Exception {
        // Initialize the database
        paymentMethodsRepository.saveAndFlush(paymentMethods);

        // Get all the paymentMethodsList where expirationDate in DEFAULT_EXPIRATION_DATE or UPDATED_EXPIRATION_DATE
        defaultPaymentMethodsShouldBeFound("expirationDate.in=" + DEFAULT_EXPIRATION_DATE + "," + UPDATED_EXPIRATION_DATE);

        // Get all the paymentMethodsList where expirationDate equals to UPDATED_EXPIRATION_DATE
        defaultPaymentMethodsShouldNotBeFound("expirationDate.in=" + UPDATED_EXPIRATION_DATE);
    }

    @Test
    @Transactional
    void getAllPaymentMethodsByExpirationDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        paymentMethodsRepository.saveAndFlush(paymentMethods);

        // Get all the paymentMethodsList where expirationDate is not null
        defaultPaymentMethodsShouldBeFound("expirationDate.specified=true");

        // Get all the paymentMethodsList where expirationDate is null
        defaultPaymentMethodsShouldNotBeFound("expirationDate.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentMethodsByExpirationDateContainsSomething() throws Exception {
        // Initialize the database
        paymentMethodsRepository.saveAndFlush(paymentMethods);

        // Get all the paymentMethodsList where expirationDate contains DEFAULT_EXPIRATION_DATE
        defaultPaymentMethodsShouldBeFound("expirationDate.contains=" + DEFAULT_EXPIRATION_DATE);

        // Get all the paymentMethodsList where expirationDate contains UPDATED_EXPIRATION_DATE
        defaultPaymentMethodsShouldNotBeFound("expirationDate.contains=" + UPDATED_EXPIRATION_DATE);
    }

    @Test
    @Transactional
    void getAllPaymentMethodsByExpirationDateNotContainsSomething() throws Exception {
        // Initialize the database
        paymentMethodsRepository.saveAndFlush(paymentMethods);

        // Get all the paymentMethodsList where expirationDate does not contain DEFAULT_EXPIRATION_DATE
        defaultPaymentMethodsShouldNotBeFound("expirationDate.doesNotContain=" + DEFAULT_EXPIRATION_DATE);

        // Get all the paymentMethodsList where expirationDate does not contain UPDATED_EXPIRATION_DATE
        defaultPaymentMethodsShouldBeFound("expirationDate.doesNotContain=" + UPDATED_EXPIRATION_DATE);
    }

    @Test
    @Transactional
    void getAllPaymentMethodsByCustomerIsEqualToSomething() throws Exception {
        Customers customer;
        if (TestUtil.findAll(em, Customers.class).isEmpty()) {
            paymentMethodsRepository.saveAndFlush(paymentMethods);
            customer = CustomersResourceIT.createEntity(em);
        } else {
            customer = TestUtil.findAll(em, Customers.class).get(0);
        }
        em.persist(customer);
        em.flush();
        paymentMethods.setCustomer(customer);
        paymentMethodsRepository.saveAndFlush(paymentMethods);
        Long customerId = customer.getId();

        // Get all the paymentMethodsList where customer equals to customerId
        defaultPaymentMethodsShouldBeFound("customerId.equals=" + customerId);

        // Get all the paymentMethodsList where customer equals to (customerId + 1)
        defaultPaymentMethodsShouldNotBeFound("customerId.equals=" + (customerId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPaymentMethodsShouldBeFound(String filter) throws Exception {
        restPaymentMethodsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(paymentMethods.getId().intValue())))
            .andExpect(jsonPath("$.[*].cardNumber").value(hasItem(DEFAULT_CARD_NUMBER)))
            .andExpect(jsonPath("$.[*].cardHolderName").value(hasItem(DEFAULT_CARD_HOLDER_NAME)))
            .andExpect(jsonPath("$.[*].expirationDate").value(hasItem(DEFAULT_EXPIRATION_DATE)));

        // Check, that the count call also returns 1
        restPaymentMethodsMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPaymentMethodsShouldNotBeFound(String filter) throws Exception {
        restPaymentMethodsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPaymentMethodsMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingPaymentMethods() throws Exception {
        // Get the paymentMethods
        restPaymentMethodsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPaymentMethods() throws Exception {
        // Initialize the database
        paymentMethodsRepository.saveAndFlush(paymentMethods);

        int databaseSizeBeforeUpdate = paymentMethodsRepository.findAll().size();
        paymentMethodsSearchRepository.save(paymentMethods);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentMethodsSearchRepository.findAll());

        // Update the paymentMethods
        PaymentMethods updatedPaymentMethods = paymentMethodsRepository.findById(paymentMethods.getId()).get();
        // Disconnect from session so that the updates on updatedPaymentMethods are not directly saved in db
        em.detach(updatedPaymentMethods);
        updatedPaymentMethods
            .cardNumber(UPDATED_CARD_NUMBER)
            .cardHolderName(UPDATED_CARD_HOLDER_NAME)
            .expirationDate(UPDATED_EXPIRATION_DATE);

        restPaymentMethodsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedPaymentMethods.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedPaymentMethods))
            )
            .andExpect(status().isOk());

        // Validate the PaymentMethods in the database
        List<PaymentMethods> paymentMethodsList = paymentMethodsRepository.findAll();
        assertThat(paymentMethodsList).hasSize(databaseSizeBeforeUpdate);
        PaymentMethods testPaymentMethods = paymentMethodsList.get(paymentMethodsList.size() - 1);
        assertThat(testPaymentMethods.getCardNumber()).isEqualTo(UPDATED_CARD_NUMBER);
        assertThat(testPaymentMethods.getCardHolderName()).isEqualTo(UPDATED_CARD_HOLDER_NAME);
        assertThat(testPaymentMethods.getExpirationDate()).isEqualTo(UPDATED_EXPIRATION_DATE);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentMethodsSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<PaymentMethods> paymentMethodsSearchList = IterableUtils.toList(paymentMethodsSearchRepository.findAll());
                PaymentMethods testPaymentMethodsSearch = paymentMethodsSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testPaymentMethodsSearch.getCardNumber()).isEqualTo(UPDATED_CARD_NUMBER);
                assertThat(testPaymentMethodsSearch.getCardHolderName()).isEqualTo(UPDATED_CARD_HOLDER_NAME);
                assertThat(testPaymentMethodsSearch.getExpirationDate()).isEqualTo(UPDATED_EXPIRATION_DATE);
            });
    }

    @Test
    @Transactional
    void putNonExistingPaymentMethods() throws Exception {
        int databaseSizeBeforeUpdate = paymentMethodsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentMethodsSearchRepository.findAll());
        paymentMethods.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPaymentMethodsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, paymentMethods.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(paymentMethods))
            )
            .andExpect(status().isBadRequest());

        // Validate the PaymentMethods in the database
        List<PaymentMethods> paymentMethodsList = paymentMethodsRepository.findAll();
        assertThat(paymentMethodsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentMethodsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchPaymentMethods() throws Exception {
        int databaseSizeBeforeUpdate = paymentMethodsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentMethodsSearchRepository.findAll());
        paymentMethods.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaymentMethodsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(paymentMethods))
            )
            .andExpect(status().isBadRequest());

        // Validate the PaymentMethods in the database
        List<PaymentMethods> paymentMethodsList = paymentMethodsRepository.findAll();
        assertThat(paymentMethodsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentMethodsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPaymentMethods() throws Exception {
        int databaseSizeBeforeUpdate = paymentMethodsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentMethodsSearchRepository.findAll());
        paymentMethods.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaymentMethodsMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(paymentMethods)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PaymentMethods in the database
        List<PaymentMethods> paymentMethodsList = paymentMethodsRepository.findAll();
        assertThat(paymentMethodsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentMethodsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdatePaymentMethodsWithPatch() throws Exception {
        // Initialize the database
        paymentMethodsRepository.saveAndFlush(paymentMethods);

        int databaseSizeBeforeUpdate = paymentMethodsRepository.findAll().size();

        // Update the paymentMethods using partial update
        PaymentMethods partialUpdatedPaymentMethods = new PaymentMethods();
        partialUpdatedPaymentMethods.setId(paymentMethods.getId());

        partialUpdatedPaymentMethods.cardHolderName(UPDATED_CARD_HOLDER_NAME).expirationDate(UPDATED_EXPIRATION_DATE);

        restPaymentMethodsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPaymentMethods.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPaymentMethods))
            )
            .andExpect(status().isOk());

        // Validate the PaymentMethods in the database
        List<PaymentMethods> paymentMethodsList = paymentMethodsRepository.findAll();
        assertThat(paymentMethodsList).hasSize(databaseSizeBeforeUpdate);
        PaymentMethods testPaymentMethods = paymentMethodsList.get(paymentMethodsList.size() - 1);
        assertThat(testPaymentMethods.getCardNumber()).isEqualTo(DEFAULT_CARD_NUMBER);
        assertThat(testPaymentMethods.getCardHolderName()).isEqualTo(UPDATED_CARD_HOLDER_NAME);
        assertThat(testPaymentMethods.getExpirationDate()).isEqualTo(UPDATED_EXPIRATION_DATE);
    }

    @Test
    @Transactional
    void fullUpdatePaymentMethodsWithPatch() throws Exception {
        // Initialize the database
        paymentMethodsRepository.saveAndFlush(paymentMethods);

        int databaseSizeBeforeUpdate = paymentMethodsRepository.findAll().size();

        // Update the paymentMethods using partial update
        PaymentMethods partialUpdatedPaymentMethods = new PaymentMethods();
        partialUpdatedPaymentMethods.setId(paymentMethods.getId());

        partialUpdatedPaymentMethods
            .cardNumber(UPDATED_CARD_NUMBER)
            .cardHolderName(UPDATED_CARD_HOLDER_NAME)
            .expirationDate(UPDATED_EXPIRATION_DATE);

        restPaymentMethodsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPaymentMethods.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPaymentMethods))
            )
            .andExpect(status().isOk());

        // Validate the PaymentMethods in the database
        List<PaymentMethods> paymentMethodsList = paymentMethodsRepository.findAll();
        assertThat(paymentMethodsList).hasSize(databaseSizeBeforeUpdate);
        PaymentMethods testPaymentMethods = paymentMethodsList.get(paymentMethodsList.size() - 1);
        assertThat(testPaymentMethods.getCardNumber()).isEqualTo(UPDATED_CARD_NUMBER);
        assertThat(testPaymentMethods.getCardHolderName()).isEqualTo(UPDATED_CARD_HOLDER_NAME);
        assertThat(testPaymentMethods.getExpirationDate()).isEqualTo(UPDATED_EXPIRATION_DATE);
    }

    @Test
    @Transactional
    void patchNonExistingPaymentMethods() throws Exception {
        int databaseSizeBeforeUpdate = paymentMethodsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentMethodsSearchRepository.findAll());
        paymentMethods.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPaymentMethodsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, paymentMethods.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(paymentMethods))
            )
            .andExpect(status().isBadRequest());

        // Validate the PaymentMethods in the database
        List<PaymentMethods> paymentMethodsList = paymentMethodsRepository.findAll();
        assertThat(paymentMethodsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentMethodsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPaymentMethods() throws Exception {
        int databaseSizeBeforeUpdate = paymentMethodsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentMethodsSearchRepository.findAll());
        paymentMethods.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaymentMethodsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(paymentMethods))
            )
            .andExpect(status().isBadRequest());

        // Validate the PaymentMethods in the database
        List<PaymentMethods> paymentMethodsList = paymentMethodsRepository.findAll();
        assertThat(paymentMethodsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentMethodsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPaymentMethods() throws Exception {
        int databaseSizeBeforeUpdate = paymentMethodsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentMethodsSearchRepository.findAll());
        paymentMethods.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaymentMethodsMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(paymentMethods))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the PaymentMethods in the database
        List<PaymentMethods> paymentMethodsList = paymentMethodsRepository.findAll();
        assertThat(paymentMethodsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentMethodsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deletePaymentMethods() throws Exception {
        // Initialize the database
        paymentMethodsRepository.saveAndFlush(paymentMethods);
        paymentMethodsRepository.save(paymentMethods);
        paymentMethodsSearchRepository.save(paymentMethods);

        int databaseSizeBeforeDelete = paymentMethodsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentMethodsSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the paymentMethods
        restPaymentMethodsMockMvc
            .perform(delete(ENTITY_API_URL_ID, paymentMethods.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<PaymentMethods> paymentMethodsList = paymentMethodsRepository.findAll();
        assertThat(paymentMethodsList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentMethodsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchPaymentMethods() throws Exception {
        // Initialize the database
        paymentMethods = paymentMethodsRepository.saveAndFlush(paymentMethods);
        paymentMethodsSearchRepository.save(paymentMethods);

        // Search the paymentMethods
        restPaymentMethodsMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + paymentMethods.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(paymentMethods.getId().intValue())))
            .andExpect(jsonPath("$.[*].cardNumber").value(hasItem(DEFAULT_CARD_NUMBER)))
            .andExpect(jsonPath("$.[*].cardHolderName").value(hasItem(DEFAULT_CARD_HOLDER_NAME)))
            .andExpect(jsonPath("$.[*].expirationDate").value(hasItem(DEFAULT_EXPIRATION_DATE)));
    }
}
