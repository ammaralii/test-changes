package com.test.changes.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.test.changes.IntegrationTest;
import com.test.changes.domain.Customers;
import com.test.changes.domain.Orders;
import com.test.changes.domain.PaymentMethods;
import com.test.changes.repository.CustomersRepository;
import com.test.changes.repository.search.CustomersSearchRepository;
import com.test.changes.service.criteria.CustomersCriteria;
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
 * Integration tests for the {@link CustomersResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CustomersResourceIT {

    private static final String DEFAULT_FULL_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FULL_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_PHONE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/customers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/customers";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    private CustomersSearchRepository customersSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCustomersMockMvc;

    private Customers customers;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Customers createEntity(EntityManager em) {
        Customers customers = new Customers().fullName(DEFAULT_FULL_NAME).email(DEFAULT_EMAIL).phone(DEFAULT_PHONE);
        return customers;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Customers createUpdatedEntity(EntityManager em) {
        Customers customers = new Customers().fullName(UPDATED_FULL_NAME).email(UPDATED_EMAIL).phone(UPDATED_PHONE);
        return customers;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        customersSearchRepository.deleteAll();
        assertThat(customersSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        customers = createEntity(em);
    }

    @Test
    @Transactional
    void createCustomers() throws Exception {
        int databaseSizeBeforeCreate = customersRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(customersSearchRepository.findAll());
        // Create the Customers
        restCustomersMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(customers)))
            .andExpect(status().isCreated());

        // Validate the Customers in the database
        List<Customers> customersList = customersRepository.findAll();
        assertThat(customersList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(customersSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Customers testCustomers = customersList.get(customersList.size() - 1);
        assertThat(testCustomers.getFullName()).isEqualTo(DEFAULT_FULL_NAME);
        assertThat(testCustomers.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testCustomers.getPhone()).isEqualTo(DEFAULT_PHONE);
    }

    @Test
    @Transactional
    void createCustomersWithExistingId() throws Exception {
        // Create the Customers with an existing ID
        customers.setId(1L);

        int databaseSizeBeforeCreate = customersRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(customersSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restCustomersMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(customers)))
            .andExpect(status().isBadRequest());

        // Validate the Customers in the database
        List<Customers> customersList = customersRepository.findAll();
        assertThat(customersList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(customersSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkFullNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = customersRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(customersSearchRepository.findAll());
        // set the field null
        customers.setFullName(null);

        // Create the Customers, which fails.

        restCustomersMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(customers)))
            .andExpect(status().isBadRequest());

        List<Customers> customersList = customersRepository.findAll();
        assertThat(customersList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(customersSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkEmailIsRequired() throws Exception {
        int databaseSizeBeforeTest = customersRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(customersSearchRepository.findAll());
        // set the field null
        customers.setEmail(null);

        // Create the Customers, which fails.

        restCustomersMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(customers)))
            .andExpect(status().isBadRequest());

        List<Customers> customersList = customersRepository.findAll();
        assertThat(customersList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(customersSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkPhoneIsRequired() throws Exception {
        int databaseSizeBeforeTest = customersRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(customersSearchRepository.findAll());
        // set the field null
        customers.setPhone(null);

        // Create the Customers, which fails.

        restCustomersMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(customers)))
            .andExpect(status().isBadRequest());

        List<Customers> customersList = customersRepository.findAll();
        assertThat(customersList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(customersSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllCustomers() throws Exception {
        // Initialize the database
        customersRepository.saveAndFlush(customers);

        // Get all the customersList
        restCustomersMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(customers.getId().intValue())))
            .andExpect(jsonPath("$.[*].fullName").value(hasItem(DEFAULT_FULL_NAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)));
    }

    @Test
    @Transactional
    void getCustomers() throws Exception {
        // Initialize the database
        customersRepository.saveAndFlush(customers);

        // Get the customers
        restCustomersMockMvc
            .perform(get(ENTITY_API_URL_ID, customers.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(customers.getId().intValue()))
            .andExpect(jsonPath("$.fullName").value(DEFAULT_FULL_NAME))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE));
    }

    @Test
    @Transactional
    void getCustomersByIdFiltering() throws Exception {
        // Initialize the database
        customersRepository.saveAndFlush(customers);

        Long id = customers.getId();

        defaultCustomersShouldBeFound("id.equals=" + id);
        defaultCustomersShouldNotBeFound("id.notEquals=" + id);

        defaultCustomersShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultCustomersShouldNotBeFound("id.greaterThan=" + id);

        defaultCustomersShouldBeFound("id.lessThanOrEqual=" + id);
        defaultCustomersShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCustomersByFullNameIsEqualToSomething() throws Exception {
        // Initialize the database
        customersRepository.saveAndFlush(customers);

        // Get all the customersList where fullName equals to DEFAULT_FULL_NAME
        defaultCustomersShouldBeFound("fullName.equals=" + DEFAULT_FULL_NAME);

        // Get all the customersList where fullName equals to UPDATED_FULL_NAME
        defaultCustomersShouldNotBeFound("fullName.equals=" + UPDATED_FULL_NAME);
    }

    @Test
    @Transactional
    void getAllCustomersByFullNameIsInShouldWork() throws Exception {
        // Initialize the database
        customersRepository.saveAndFlush(customers);

        // Get all the customersList where fullName in DEFAULT_FULL_NAME or UPDATED_FULL_NAME
        defaultCustomersShouldBeFound("fullName.in=" + DEFAULT_FULL_NAME + "," + UPDATED_FULL_NAME);

        // Get all the customersList where fullName equals to UPDATED_FULL_NAME
        defaultCustomersShouldNotBeFound("fullName.in=" + UPDATED_FULL_NAME);
    }

    @Test
    @Transactional
    void getAllCustomersByFullNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        customersRepository.saveAndFlush(customers);

        // Get all the customersList where fullName is not null
        defaultCustomersShouldBeFound("fullName.specified=true");

        // Get all the customersList where fullName is null
        defaultCustomersShouldNotBeFound("fullName.specified=false");
    }

    @Test
    @Transactional
    void getAllCustomersByFullNameContainsSomething() throws Exception {
        // Initialize the database
        customersRepository.saveAndFlush(customers);

        // Get all the customersList where fullName contains DEFAULT_FULL_NAME
        defaultCustomersShouldBeFound("fullName.contains=" + DEFAULT_FULL_NAME);

        // Get all the customersList where fullName contains UPDATED_FULL_NAME
        defaultCustomersShouldNotBeFound("fullName.contains=" + UPDATED_FULL_NAME);
    }

    @Test
    @Transactional
    void getAllCustomersByFullNameNotContainsSomething() throws Exception {
        // Initialize the database
        customersRepository.saveAndFlush(customers);

        // Get all the customersList where fullName does not contain DEFAULT_FULL_NAME
        defaultCustomersShouldNotBeFound("fullName.doesNotContain=" + DEFAULT_FULL_NAME);

        // Get all the customersList where fullName does not contain UPDATED_FULL_NAME
        defaultCustomersShouldBeFound("fullName.doesNotContain=" + UPDATED_FULL_NAME);
    }

    @Test
    @Transactional
    void getAllCustomersByEmailIsEqualToSomething() throws Exception {
        // Initialize the database
        customersRepository.saveAndFlush(customers);

        // Get all the customersList where email equals to DEFAULT_EMAIL
        defaultCustomersShouldBeFound("email.equals=" + DEFAULT_EMAIL);

        // Get all the customersList where email equals to UPDATED_EMAIL
        defaultCustomersShouldNotBeFound("email.equals=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllCustomersByEmailIsInShouldWork() throws Exception {
        // Initialize the database
        customersRepository.saveAndFlush(customers);

        // Get all the customersList where email in DEFAULT_EMAIL or UPDATED_EMAIL
        defaultCustomersShouldBeFound("email.in=" + DEFAULT_EMAIL + "," + UPDATED_EMAIL);

        // Get all the customersList where email equals to UPDATED_EMAIL
        defaultCustomersShouldNotBeFound("email.in=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllCustomersByEmailIsNullOrNotNull() throws Exception {
        // Initialize the database
        customersRepository.saveAndFlush(customers);

        // Get all the customersList where email is not null
        defaultCustomersShouldBeFound("email.specified=true");

        // Get all the customersList where email is null
        defaultCustomersShouldNotBeFound("email.specified=false");
    }

    @Test
    @Transactional
    void getAllCustomersByEmailContainsSomething() throws Exception {
        // Initialize the database
        customersRepository.saveAndFlush(customers);

        // Get all the customersList where email contains DEFAULT_EMAIL
        defaultCustomersShouldBeFound("email.contains=" + DEFAULT_EMAIL);

        // Get all the customersList where email contains UPDATED_EMAIL
        defaultCustomersShouldNotBeFound("email.contains=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllCustomersByEmailNotContainsSomething() throws Exception {
        // Initialize the database
        customersRepository.saveAndFlush(customers);

        // Get all the customersList where email does not contain DEFAULT_EMAIL
        defaultCustomersShouldNotBeFound("email.doesNotContain=" + DEFAULT_EMAIL);

        // Get all the customersList where email does not contain UPDATED_EMAIL
        defaultCustomersShouldBeFound("email.doesNotContain=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllCustomersByPhoneIsEqualToSomething() throws Exception {
        // Initialize the database
        customersRepository.saveAndFlush(customers);

        // Get all the customersList where phone equals to DEFAULT_PHONE
        defaultCustomersShouldBeFound("phone.equals=" + DEFAULT_PHONE);

        // Get all the customersList where phone equals to UPDATED_PHONE
        defaultCustomersShouldNotBeFound("phone.equals=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllCustomersByPhoneIsInShouldWork() throws Exception {
        // Initialize the database
        customersRepository.saveAndFlush(customers);

        // Get all the customersList where phone in DEFAULT_PHONE or UPDATED_PHONE
        defaultCustomersShouldBeFound("phone.in=" + DEFAULT_PHONE + "," + UPDATED_PHONE);

        // Get all the customersList where phone equals to UPDATED_PHONE
        defaultCustomersShouldNotBeFound("phone.in=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllCustomersByPhoneIsNullOrNotNull() throws Exception {
        // Initialize the database
        customersRepository.saveAndFlush(customers);

        // Get all the customersList where phone is not null
        defaultCustomersShouldBeFound("phone.specified=true");

        // Get all the customersList where phone is null
        defaultCustomersShouldNotBeFound("phone.specified=false");
    }

    @Test
    @Transactional
    void getAllCustomersByPhoneContainsSomething() throws Exception {
        // Initialize the database
        customersRepository.saveAndFlush(customers);

        // Get all the customersList where phone contains DEFAULT_PHONE
        defaultCustomersShouldBeFound("phone.contains=" + DEFAULT_PHONE);

        // Get all the customersList where phone contains UPDATED_PHONE
        defaultCustomersShouldNotBeFound("phone.contains=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllCustomersByPhoneNotContainsSomething() throws Exception {
        // Initialize the database
        customersRepository.saveAndFlush(customers);

        // Get all the customersList where phone does not contain DEFAULT_PHONE
        defaultCustomersShouldNotBeFound("phone.doesNotContain=" + DEFAULT_PHONE);

        // Get all the customersList where phone does not contain UPDATED_PHONE
        defaultCustomersShouldBeFound("phone.doesNotContain=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllCustomersByOrdersCustomerIsEqualToSomething() throws Exception {
        Orders ordersCustomer;
        if (TestUtil.findAll(em, Orders.class).isEmpty()) {
            customersRepository.saveAndFlush(customers);
            ordersCustomer = OrdersResourceIT.createEntity(em);
        } else {
            ordersCustomer = TestUtil.findAll(em, Orders.class).get(0);
        }
        em.persist(ordersCustomer);
        em.flush();
        customers.addOrdersCustomer(ordersCustomer);
        customersRepository.saveAndFlush(customers);
        Long ordersCustomerId = ordersCustomer.getId();

        // Get all the customersList where ordersCustomer equals to ordersCustomerId
        defaultCustomersShouldBeFound("ordersCustomerId.equals=" + ordersCustomerId);

        // Get all the customersList where ordersCustomer equals to (ordersCustomerId + 1)
        defaultCustomersShouldNotBeFound("ordersCustomerId.equals=" + (ordersCustomerId + 1));
    }

    @Test
    @Transactional
    void getAllCustomersByPaymentmethodsCustomerIsEqualToSomething() throws Exception {
        PaymentMethods paymentmethodsCustomer;
        if (TestUtil.findAll(em, PaymentMethods.class).isEmpty()) {
            customersRepository.saveAndFlush(customers);
            paymentmethodsCustomer = PaymentMethodsResourceIT.createEntity(em);
        } else {
            paymentmethodsCustomer = TestUtil.findAll(em, PaymentMethods.class).get(0);
        }
        em.persist(paymentmethodsCustomer);
        em.flush();
        customers.addPaymentmethodsCustomer(paymentmethodsCustomer);
        customersRepository.saveAndFlush(customers);
        Long paymentmethodsCustomerId = paymentmethodsCustomer.getId();

        // Get all the customersList where paymentmethodsCustomer equals to paymentmethodsCustomerId
        defaultCustomersShouldBeFound("paymentmethodsCustomerId.equals=" + paymentmethodsCustomerId);

        // Get all the customersList where paymentmethodsCustomer equals to (paymentmethodsCustomerId + 1)
        defaultCustomersShouldNotBeFound("paymentmethodsCustomerId.equals=" + (paymentmethodsCustomerId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCustomersShouldBeFound(String filter) throws Exception {
        restCustomersMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(customers.getId().intValue())))
            .andExpect(jsonPath("$.[*].fullName").value(hasItem(DEFAULT_FULL_NAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)));

        // Check, that the count call also returns 1
        restCustomersMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCustomersShouldNotBeFound(String filter) throws Exception {
        restCustomersMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCustomersMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCustomers() throws Exception {
        // Get the customers
        restCustomersMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCustomers() throws Exception {
        // Initialize the database
        customersRepository.saveAndFlush(customers);

        int databaseSizeBeforeUpdate = customersRepository.findAll().size();
        customersSearchRepository.save(customers);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(customersSearchRepository.findAll());

        // Update the customers
        Customers updatedCustomers = customersRepository.findById(customers.getId()).get();
        // Disconnect from session so that the updates on updatedCustomers are not directly saved in db
        em.detach(updatedCustomers);
        updatedCustomers.fullName(UPDATED_FULL_NAME).email(UPDATED_EMAIL).phone(UPDATED_PHONE);

        restCustomersMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCustomers.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedCustomers))
            )
            .andExpect(status().isOk());

        // Validate the Customers in the database
        List<Customers> customersList = customersRepository.findAll();
        assertThat(customersList).hasSize(databaseSizeBeforeUpdate);
        Customers testCustomers = customersList.get(customersList.size() - 1);
        assertThat(testCustomers.getFullName()).isEqualTo(UPDATED_FULL_NAME);
        assertThat(testCustomers.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testCustomers.getPhone()).isEqualTo(UPDATED_PHONE);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(customersSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Customers> customersSearchList = IterableUtils.toList(customersSearchRepository.findAll());
                Customers testCustomersSearch = customersSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testCustomersSearch.getFullName()).isEqualTo(UPDATED_FULL_NAME);
                assertThat(testCustomersSearch.getEmail()).isEqualTo(UPDATED_EMAIL);
                assertThat(testCustomersSearch.getPhone()).isEqualTo(UPDATED_PHONE);
            });
    }

    @Test
    @Transactional
    void putNonExistingCustomers() throws Exception {
        int databaseSizeBeforeUpdate = customersRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(customersSearchRepository.findAll());
        customers.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCustomersMockMvc
            .perform(
                put(ENTITY_API_URL_ID, customers.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(customers))
            )
            .andExpect(status().isBadRequest());

        // Validate the Customers in the database
        List<Customers> customersList = customersRepository.findAll();
        assertThat(customersList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(customersSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchCustomers() throws Exception {
        int databaseSizeBeforeUpdate = customersRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(customersSearchRepository.findAll());
        customers.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCustomersMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(customers))
            )
            .andExpect(status().isBadRequest());

        // Validate the Customers in the database
        List<Customers> customersList = customersRepository.findAll();
        assertThat(customersList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(customersSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCustomers() throws Exception {
        int databaseSizeBeforeUpdate = customersRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(customersSearchRepository.findAll());
        customers.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCustomersMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(customers)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Customers in the database
        List<Customers> customersList = customersRepository.findAll();
        assertThat(customersList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(customersSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateCustomersWithPatch() throws Exception {
        // Initialize the database
        customersRepository.saveAndFlush(customers);

        int databaseSizeBeforeUpdate = customersRepository.findAll().size();

        // Update the customers using partial update
        Customers partialUpdatedCustomers = new Customers();
        partialUpdatedCustomers.setId(customers.getId());

        partialUpdatedCustomers.fullName(UPDATED_FULL_NAME).phone(UPDATED_PHONE);

        restCustomersMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCustomers.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCustomers))
            )
            .andExpect(status().isOk());

        // Validate the Customers in the database
        List<Customers> customersList = customersRepository.findAll();
        assertThat(customersList).hasSize(databaseSizeBeforeUpdate);
        Customers testCustomers = customersList.get(customersList.size() - 1);
        assertThat(testCustomers.getFullName()).isEqualTo(UPDATED_FULL_NAME);
        assertThat(testCustomers.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testCustomers.getPhone()).isEqualTo(UPDATED_PHONE);
    }

    @Test
    @Transactional
    void fullUpdateCustomersWithPatch() throws Exception {
        // Initialize the database
        customersRepository.saveAndFlush(customers);

        int databaseSizeBeforeUpdate = customersRepository.findAll().size();

        // Update the customers using partial update
        Customers partialUpdatedCustomers = new Customers();
        partialUpdatedCustomers.setId(customers.getId());

        partialUpdatedCustomers.fullName(UPDATED_FULL_NAME).email(UPDATED_EMAIL).phone(UPDATED_PHONE);

        restCustomersMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCustomers.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCustomers))
            )
            .andExpect(status().isOk());

        // Validate the Customers in the database
        List<Customers> customersList = customersRepository.findAll();
        assertThat(customersList).hasSize(databaseSizeBeforeUpdate);
        Customers testCustomers = customersList.get(customersList.size() - 1);
        assertThat(testCustomers.getFullName()).isEqualTo(UPDATED_FULL_NAME);
        assertThat(testCustomers.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testCustomers.getPhone()).isEqualTo(UPDATED_PHONE);
    }

    @Test
    @Transactional
    void patchNonExistingCustomers() throws Exception {
        int databaseSizeBeforeUpdate = customersRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(customersSearchRepository.findAll());
        customers.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCustomersMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, customers.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(customers))
            )
            .andExpect(status().isBadRequest());

        // Validate the Customers in the database
        List<Customers> customersList = customersRepository.findAll();
        assertThat(customersList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(customersSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCustomers() throws Exception {
        int databaseSizeBeforeUpdate = customersRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(customersSearchRepository.findAll());
        customers.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCustomersMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(customers))
            )
            .andExpect(status().isBadRequest());

        // Validate the Customers in the database
        List<Customers> customersList = customersRepository.findAll();
        assertThat(customersList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(customersSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCustomers() throws Exception {
        int databaseSizeBeforeUpdate = customersRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(customersSearchRepository.findAll());
        customers.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCustomersMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(customers))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Customers in the database
        List<Customers> customersList = customersRepository.findAll();
        assertThat(customersList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(customersSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteCustomers() throws Exception {
        // Initialize the database
        customersRepository.saveAndFlush(customers);
        customersRepository.save(customers);
        customersSearchRepository.save(customers);

        int databaseSizeBeforeDelete = customersRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(customersSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the customers
        restCustomersMockMvc
            .perform(delete(ENTITY_API_URL_ID, customers.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Customers> customersList = customersRepository.findAll();
        assertThat(customersList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(customersSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchCustomers() throws Exception {
        // Initialize the database
        customers = customersRepository.saveAndFlush(customers);
        customersSearchRepository.save(customers);

        // Search the customers
        restCustomersMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + customers.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(customers.getId().intValue())))
            .andExpect(jsonPath("$.[*].fullName").value(hasItem(DEFAULT_FULL_NAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)));
    }
}
