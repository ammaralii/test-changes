package com.test.changes.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.test.changes.IntegrationTest;
import com.test.changes.domain.Customers;
import com.test.changes.domain.OrderDelivery;
import com.test.changes.domain.OrderDetails;
import com.test.changes.domain.Orders;
import com.test.changes.domain.ShippingDetails;
import com.test.changes.repository.OrdersRepository;
import com.test.changes.repository.search.OrdersSearchRepository;
import com.test.changes.service.criteria.OrdersCriteria;
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
 * Integration tests for the {@link OrdersResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class OrdersResourceIT {

    private static final LocalDate DEFAULT_ORDER_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_ORDER_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_ORDER_DATE = LocalDate.ofEpochDay(-1L);

    private static final Integer DEFAULT_TOTAL_AMOUNT = 1;
    private static final Integer UPDATED_TOTAL_AMOUNT = 2;
    private static final Integer SMALLER_TOTAL_AMOUNT = 1 - 1;

    private static final String ENTITY_API_URL = "/api/orders";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/orders";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private OrdersSearchRepository ordersSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restOrdersMockMvc;

    private Orders orders;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Orders createEntity(EntityManager em) {
        Orders orders = new Orders().orderDate(DEFAULT_ORDER_DATE).totalAmount(DEFAULT_TOTAL_AMOUNT);
        // Add required entity
        Customers customers;
        if (TestUtil.findAll(em, Customers.class).isEmpty()) {
            customers = CustomersResourceIT.createEntity(em);
            em.persist(customers);
            em.flush();
        } else {
            customers = TestUtil.findAll(em, Customers.class).get(0);
        }
        orders.setCustomer(customers);
        return orders;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Orders createUpdatedEntity(EntityManager em) {
        Orders orders = new Orders().orderDate(UPDATED_ORDER_DATE).totalAmount(UPDATED_TOTAL_AMOUNT);
        // Add required entity
        Customers customers;
        if (TestUtil.findAll(em, Customers.class).isEmpty()) {
            customers = CustomersResourceIT.createUpdatedEntity(em);
            em.persist(customers);
            em.flush();
        } else {
            customers = TestUtil.findAll(em, Customers.class).get(0);
        }
        orders.setCustomer(customers);
        return orders;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        ordersSearchRepository.deleteAll();
        assertThat(ordersSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        orders = createEntity(em);
    }

    @Test
    @Transactional
    void createOrders() throws Exception {
        int databaseSizeBeforeCreate = ordersRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ordersSearchRepository.findAll());
        // Create the Orders
        restOrdersMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(orders)))
            .andExpect(status().isCreated());

        // Validate the Orders in the database
        List<Orders> ordersList = ordersRepository.findAll();
        assertThat(ordersList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(ordersSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Orders testOrders = ordersList.get(ordersList.size() - 1);
        assertThat(testOrders.getOrderDate()).isEqualTo(DEFAULT_ORDER_DATE);
        assertThat(testOrders.getTotalAmount()).isEqualTo(DEFAULT_TOTAL_AMOUNT);
    }

    @Test
    @Transactional
    void createOrdersWithExistingId() throws Exception {
        // Create the Orders with an existing ID
        orders.setId(1L);

        int databaseSizeBeforeCreate = ordersRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ordersSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restOrdersMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(orders)))
            .andExpect(status().isBadRequest());

        // Validate the Orders in the database
        List<Orders> ordersList = ordersRepository.findAll();
        assertThat(ordersList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ordersSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkOrderDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = ordersRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ordersSearchRepository.findAll());
        // set the field null
        orders.setOrderDate(null);

        // Create the Orders, which fails.

        restOrdersMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(orders)))
            .andExpect(status().isBadRequest());

        List<Orders> ordersList = ordersRepository.findAll();
        assertThat(ordersList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ordersSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllOrders() throws Exception {
        // Initialize the database
        ordersRepository.saveAndFlush(orders);

        // Get all the ordersList
        restOrdersMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(orders.getId().intValue())))
            .andExpect(jsonPath("$.[*].orderDate").value(hasItem(DEFAULT_ORDER_DATE.toString())))
            .andExpect(jsonPath("$.[*].totalAmount").value(hasItem(DEFAULT_TOTAL_AMOUNT)));
    }

    @Test
    @Transactional
    void getOrders() throws Exception {
        // Initialize the database
        ordersRepository.saveAndFlush(orders);

        // Get the orders
        restOrdersMockMvc
            .perform(get(ENTITY_API_URL_ID, orders.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(orders.getId().intValue()))
            .andExpect(jsonPath("$.orderDate").value(DEFAULT_ORDER_DATE.toString()))
            .andExpect(jsonPath("$.totalAmount").value(DEFAULT_TOTAL_AMOUNT));
    }

    @Test
    @Transactional
    void getOrdersByIdFiltering() throws Exception {
        // Initialize the database
        ordersRepository.saveAndFlush(orders);

        Long id = orders.getId();

        defaultOrdersShouldBeFound("id.equals=" + id);
        defaultOrdersShouldNotBeFound("id.notEquals=" + id);

        defaultOrdersShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultOrdersShouldNotBeFound("id.greaterThan=" + id);

        defaultOrdersShouldBeFound("id.lessThanOrEqual=" + id);
        defaultOrdersShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllOrdersByOrderDateIsEqualToSomething() throws Exception {
        // Initialize the database
        ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where orderDate equals to DEFAULT_ORDER_DATE
        defaultOrdersShouldBeFound("orderDate.equals=" + DEFAULT_ORDER_DATE);

        // Get all the ordersList where orderDate equals to UPDATED_ORDER_DATE
        defaultOrdersShouldNotBeFound("orderDate.equals=" + UPDATED_ORDER_DATE);
    }

    @Test
    @Transactional
    void getAllOrdersByOrderDateIsInShouldWork() throws Exception {
        // Initialize the database
        ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where orderDate in DEFAULT_ORDER_DATE or UPDATED_ORDER_DATE
        defaultOrdersShouldBeFound("orderDate.in=" + DEFAULT_ORDER_DATE + "," + UPDATED_ORDER_DATE);

        // Get all the ordersList where orderDate equals to UPDATED_ORDER_DATE
        defaultOrdersShouldNotBeFound("orderDate.in=" + UPDATED_ORDER_DATE);
    }

    @Test
    @Transactional
    void getAllOrdersByOrderDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where orderDate is not null
        defaultOrdersShouldBeFound("orderDate.specified=true");

        // Get all the ordersList where orderDate is null
        defaultOrdersShouldNotBeFound("orderDate.specified=false");
    }

    @Test
    @Transactional
    void getAllOrdersByOrderDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where orderDate is greater than or equal to DEFAULT_ORDER_DATE
        defaultOrdersShouldBeFound("orderDate.greaterThanOrEqual=" + DEFAULT_ORDER_DATE);

        // Get all the ordersList where orderDate is greater than or equal to UPDATED_ORDER_DATE
        defaultOrdersShouldNotBeFound("orderDate.greaterThanOrEqual=" + UPDATED_ORDER_DATE);
    }

    @Test
    @Transactional
    void getAllOrdersByOrderDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where orderDate is less than or equal to DEFAULT_ORDER_DATE
        defaultOrdersShouldBeFound("orderDate.lessThanOrEqual=" + DEFAULT_ORDER_DATE);

        // Get all the ordersList where orderDate is less than or equal to SMALLER_ORDER_DATE
        defaultOrdersShouldNotBeFound("orderDate.lessThanOrEqual=" + SMALLER_ORDER_DATE);
    }

    @Test
    @Transactional
    void getAllOrdersByOrderDateIsLessThanSomething() throws Exception {
        // Initialize the database
        ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where orderDate is less than DEFAULT_ORDER_DATE
        defaultOrdersShouldNotBeFound("orderDate.lessThan=" + DEFAULT_ORDER_DATE);

        // Get all the ordersList where orderDate is less than UPDATED_ORDER_DATE
        defaultOrdersShouldBeFound("orderDate.lessThan=" + UPDATED_ORDER_DATE);
    }

    @Test
    @Transactional
    void getAllOrdersByOrderDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where orderDate is greater than DEFAULT_ORDER_DATE
        defaultOrdersShouldNotBeFound("orderDate.greaterThan=" + DEFAULT_ORDER_DATE);

        // Get all the ordersList where orderDate is greater than SMALLER_ORDER_DATE
        defaultOrdersShouldBeFound("orderDate.greaterThan=" + SMALLER_ORDER_DATE);
    }

    @Test
    @Transactional
    void getAllOrdersByTotalAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where totalAmount equals to DEFAULT_TOTAL_AMOUNT
        defaultOrdersShouldBeFound("totalAmount.equals=" + DEFAULT_TOTAL_AMOUNT);

        // Get all the ordersList where totalAmount equals to UPDATED_TOTAL_AMOUNT
        defaultOrdersShouldNotBeFound("totalAmount.equals=" + UPDATED_TOTAL_AMOUNT);
    }

    @Test
    @Transactional
    void getAllOrdersByTotalAmountIsInShouldWork() throws Exception {
        // Initialize the database
        ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where totalAmount in DEFAULT_TOTAL_AMOUNT or UPDATED_TOTAL_AMOUNT
        defaultOrdersShouldBeFound("totalAmount.in=" + DEFAULT_TOTAL_AMOUNT + "," + UPDATED_TOTAL_AMOUNT);

        // Get all the ordersList where totalAmount equals to UPDATED_TOTAL_AMOUNT
        defaultOrdersShouldNotBeFound("totalAmount.in=" + UPDATED_TOTAL_AMOUNT);
    }

    @Test
    @Transactional
    void getAllOrdersByTotalAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where totalAmount is not null
        defaultOrdersShouldBeFound("totalAmount.specified=true");

        // Get all the ordersList where totalAmount is null
        defaultOrdersShouldNotBeFound("totalAmount.specified=false");
    }

    @Test
    @Transactional
    void getAllOrdersByTotalAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where totalAmount is greater than or equal to DEFAULT_TOTAL_AMOUNT
        defaultOrdersShouldBeFound("totalAmount.greaterThanOrEqual=" + DEFAULT_TOTAL_AMOUNT);

        // Get all the ordersList where totalAmount is greater than or equal to UPDATED_TOTAL_AMOUNT
        defaultOrdersShouldNotBeFound("totalAmount.greaterThanOrEqual=" + UPDATED_TOTAL_AMOUNT);
    }

    @Test
    @Transactional
    void getAllOrdersByTotalAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where totalAmount is less than or equal to DEFAULT_TOTAL_AMOUNT
        defaultOrdersShouldBeFound("totalAmount.lessThanOrEqual=" + DEFAULT_TOTAL_AMOUNT);

        // Get all the ordersList where totalAmount is less than or equal to SMALLER_TOTAL_AMOUNT
        defaultOrdersShouldNotBeFound("totalAmount.lessThanOrEqual=" + SMALLER_TOTAL_AMOUNT);
    }

    @Test
    @Transactional
    void getAllOrdersByTotalAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where totalAmount is less than DEFAULT_TOTAL_AMOUNT
        defaultOrdersShouldNotBeFound("totalAmount.lessThan=" + DEFAULT_TOTAL_AMOUNT);

        // Get all the ordersList where totalAmount is less than UPDATED_TOTAL_AMOUNT
        defaultOrdersShouldBeFound("totalAmount.lessThan=" + UPDATED_TOTAL_AMOUNT);
    }

    @Test
    @Transactional
    void getAllOrdersByTotalAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where totalAmount is greater than DEFAULT_TOTAL_AMOUNT
        defaultOrdersShouldNotBeFound("totalAmount.greaterThan=" + DEFAULT_TOTAL_AMOUNT);

        // Get all the ordersList where totalAmount is greater than SMALLER_TOTAL_AMOUNT
        defaultOrdersShouldBeFound("totalAmount.greaterThan=" + SMALLER_TOTAL_AMOUNT);
    }

    @Test
    @Transactional
    void getAllOrdersByCustomerIsEqualToSomething() throws Exception {
        Customers customer;
        if (TestUtil.findAll(em, Customers.class).isEmpty()) {
            ordersRepository.saveAndFlush(orders);
            customer = CustomersResourceIT.createEntity(em);
        } else {
            customer = TestUtil.findAll(em, Customers.class).get(0);
        }
        em.persist(customer);
        em.flush();
        orders.setCustomer(customer);
        ordersRepository.saveAndFlush(orders);
        Long customerId = customer.getId();

        // Get all the ordersList where customer equals to customerId
        defaultOrdersShouldBeFound("customerId.equals=" + customerId);

        // Get all the ordersList where customer equals to (customerId + 1)
        defaultOrdersShouldNotBeFound("customerId.equals=" + (customerId + 1));
    }

    @Test
    @Transactional
    void getAllOrdersByOrderdeliveryOrderIsEqualToSomething() throws Exception {
        OrderDelivery orderdeliveryOrder;
        if (TestUtil.findAll(em, OrderDelivery.class).isEmpty()) {
            ordersRepository.saveAndFlush(orders);
            orderdeliveryOrder = OrderDeliveryResourceIT.createEntity(em);
        } else {
            orderdeliveryOrder = TestUtil.findAll(em, OrderDelivery.class).get(0);
        }
        em.persist(orderdeliveryOrder);
        em.flush();
        orders.addOrderdeliveryOrder(orderdeliveryOrder);
        ordersRepository.saveAndFlush(orders);
        Long orderdeliveryOrderId = orderdeliveryOrder.getId();

        // Get all the ordersList where orderdeliveryOrder equals to orderdeliveryOrderId
        defaultOrdersShouldBeFound("orderdeliveryOrderId.equals=" + orderdeliveryOrderId);

        // Get all the ordersList where orderdeliveryOrder equals to (orderdeliveryOrderId + 1)
        defaultOrdersShouldNotBeFound("orderdeliveryOrderId.equals=" + (orderdeliveryOrderId + 1));
    }

    @Test
    @Transactional
    void getAllOrdersByOrderdetailsOrderIsEqualToSomething() throws Exception {
        OrderDetails orderdetailsOrder;
        if (TestUtil.findAll(em, OrderDetails.class).isEmpty()) {
            ordersRepository.saveAndFlush(orders);
            orderdetailsOrder = OrderDetailsResourceIT.createEntity(em);
        } else {
            orderdetailsOrder = TestUtil.findAll(em, OrderDetails.class).get(0);
        }
        em.persist(orderdetailsOrder);
        em.flush();
        orders.addOrderdetailsOrder(orderdetailsOrder);
        ordersRepository.saveAndFlush(orders);
        Long orderdetailsOrderId = orderdetailsOrder.getId();

        // Get all the ordersList where orderdetailsOrder equals to orderdetailsOrderId
        defaultOrdersShouldBeFound("orderdetailsOrderId.equals=" + orderdetailsOrderId);

        // Get all the ordersList where orderdetailsOrder equals to (orderdetailsOrderId + 1)
        defaultOrdersShouldNotBeFound("orderdetailsOrderId.equals=" + (orderdetailsOrderId + 1));
    }

    @Test
    @Transactional
    void getAllOrdersByShippingdetailsOrderIsEqualToSomething() throws Exception {
        ShippingDetails shippingdetailsOrder;
        if (TestUtil.findAll(em, ShippingDetails.class).isEmpty()) {
            ordersRepository.saveAndFlush(orders);
            shippingdetailsOrder = ShippingDetailsResourceIT.createEntity(em);
        } else {
            shippingdetailsOrder = TestUtil.findAll(em, ShippingDetails.class).get(0);
        }
        em.persist(shippingdetailsOrder);
        em.flush();
        orders.addShippingdetailsOrder(shippingdetailsOrder);
        ordersRepository.saveAndFlush(orders);
        Long shippingdetailsOrderId = shippingdetailsOrder.getId();

        // Get all the ordersList where shippingdetailsOrder equals to shippingdetailsOrderId
        defaultOrdersShouldBeFound("shippingdetailsOrderId.equals=" + shippingdetailsOrderId);

        // Get all the ordersList where shippingdetailsOrder equals to (shippingdetailsOrderId + 1)
        defaultOrdersShouldNotBeFound("shippingdetailsOrderId.equals=" + (shippingdetailsOrderId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultOrdersShouldBeFound(String filter) throws Exception {
        restOrdersMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(orders.getId().intValue())))
            .andExpect(jsonPath("$.[*].orderDate").value(hasItem(DEFAULT_ORDER_DATE.toString())))
            .andExpect(jsonPath("$.[*].totalAmount").value(hasItem(DEFAULT_TOTAL_AMOUNT)));

        // Check, that the count call also returns 1
        restOrdersMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultOrdersShouldNotBeFound(String filter) throws Exception {
        restOrdersMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restOrdersMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingOrders() throws Exception {
        // Get the orders
        restOrdersMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingOrders() throws Exception {
        // Initialize the database
        ordersRepository.saveAndFlush(orders);

        int databaseSizeBeforeUpdate = ordersRepository.findAll().size();
        ordersSearchRepository.save(orders);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ordersSearchRepository.findAll());

        // Update the orders
        Orders updatedOrders = ordersRepository.findById(orders.getId()).get();
        // Disconnect from session so that the updates on updatedOrders are not directly saved in db
        em.detach(updatedOrders);
        updatedOrders.orderDate(UPDATED_ORDER_DATE).totalAmount(UPDATED_TOTAL_AMOUNT);

        restOrdersMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedOrders.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedOrders))
            )
            .andExpect(status().isOk());

        // Validate the Orders in the database
        List<Orders> ordersList = ordersRepository.findAll();
        assertThat(ordersList).hasSize(databaseSizeBeforeUpdate);
        Orders testOrders = ordersList.get(ordersList.size() - 1);
        assertThat(testOrders.getOrderDate()).isEqualTo(UPDATED_ORDER_DATE);
        assertThat(testOrders.getTotalAmount()).isEqualTo(UPDATED_TOTAL_AMOUNT);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(ordersSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Orders> ordersSearchList = IterableUtils.toList(ordersSearchRepository.findAll());
                Orders testOrdersSearch = ordersSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testOrdersSearch.getOrderDate()).isEqualTo(UPDATED_ORDER_DATE);
                assertThat(testOrdersSearch.getTotalAmount()).isEqualTo(UPDATED_TOTAL_AMOUNT);
            });
    }

    @Test
    @Transactional
    void putNonExistingOrders() throws Exception {
        int databaseSizeBeforeUpdate = ordersRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ordersSearchRepository.findAll());
        orders.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrdersMockMvc
            .perform(
                put(ENTITY_API_URL_ID, orders.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(orders))
            )
            .andExpect(status().isBadRequest());

        // Validate the Orders in the database
        List<Orders> ordersList = ordersRepository.findAll();
        assertThat(ordersList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ordersSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchOrders() throws Exception {
        int databaseSizeBeforeUpdate = ordersRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ordersSearchRepository.findAll());
        orders.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrdersMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(orders))
            )
            .andExpect(status().isBadRequest());

        // Validate the Orders in the database
        List<Orders> ordersList = ordersRepository.findAll();
        assertThat(ordersList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ordersSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamOrders() throws Exception {
        int databaseSizeBeforeUpdate = ordersRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ordersSearchRepository.findAll());
        orders.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrdersMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(orders)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Orders in the database
        List<Orders> ordersList = ordersRepository.findAll();
        assertThat(ordersList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ordersSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateOrdersWithPatch() throws Exception {
        // Initialize the database
        ordersRepository.saveAndFlush(orders);

        int databaseSizeBeforeUpdate = ordersRepository.findAll().size();

        // Update the orders using partial update
        Orders partialUpdatedOrders = new Orders();
        partialUpdatedOrders.setId(orders.getId());

        partialUpdatedOrders.orderDate(UPDATED_ORDER_DATE);

        restOrdersMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrders.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOrders))
            )
            .andExpect(status().isOk());

        // Validate the Orders in the database
        List<Orders> ordersList = ordersRepository.findAll();
        assertThat(ordersList).hasSize(databaseSizeBeforeUpdate);
        Orders testOrders = ordersList.get(ordersList.size() - 1);
        assertThat(testOrders.getOrderDate()).isEqualTo(UPDATED_ORDER_DATE);
        assertThat(testOrders.getTotalAmount()).isEqualTo(DEFAULT_TOTAL_AMOUNT);
    }

    @Test
    @Transactional
    void fullUpdateOrdersWithPatch() throws Exception {
        // Initialize the database
        ordersRepository.saveAndFlush(orders);

        int databaseSizeBeforeUpdate = ordersRepository.findAll().size();

        // Update the orders using partial update
        Orders partialUpdatedOrders = new Orders();
        partialUpdatedOrders.setId(orders.getId());

        partialUpdatedOrders.orderDate(UPDATED_ORDER_DATE).totalAmount(UPDATED_TOTAL_AMOUNT);

        restOrdersMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrders.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOrders))
            )
            .andExpect(status().isOk());

        // Validate the Orders in the database
        List<Orders> ordersList = ordersRepository.findAll();
        assertThat(ordersList).hasSize(databaseSizeBeforeUpdate);
        Orders testOrders = ordersList.get(ordersList.size() - 1);
        assertThat(testOrders.getOrderDate()).isEqualTo(UPDATED_ORDER_DATE);
        assertThat(testOrders.getTotalAmount()).isEqualTo(UPDATED_TOTAL_AMOUNT);
    }

    @Test
    @Transactional
    void patchNonExistingOrders() throws Exception {
        int databaseSizeBeforeUpdate = ordersRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ordersSearchRepository.findAll());
        orders.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrdersMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, orders.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(orders))
            )
            .andExpect(status().isBadRequest());

        // Validate the Orders in the database
        List<Orders> ordersList = ordersRepository.findAll();
        assertThat(ordersList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ordersSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchOrders() throws Exception {
        int databaseSizeBeforeUpdate = ordersRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ordersSearchRepository.findAll());
        orders.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrdersMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(orders))
            )
            .andExpect(status().isBadRequest());

        // Validate the Orders in the database
        List<Orders> ordersList = ordersRepository.findAll();
        assertThat(ordersList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ordersSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamOrders() throws Exception {
        int databaseSizeBeforeUpdate = ordersRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ordersSearchRepository.findAll());
        orders.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrdersMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(orders)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Orders in the database
        List<Orders> ordersList = ordersRepository.findAll();
        assertThat(ordersList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ordersSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteOrders() throws Exception {
        // Initialize the database
        ordersRepository.saveAndFlush(orders);
        ordersRepository.save(orders);
        ordersSearchRepository.save(orders);

        int databaseSizeBeforeDelete = ordersRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ordersSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the orders
        restOrdersMockMvc
            .perform(delete(ENTITY_API_URL_ID, orders.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Orders> ordersList = ordersRepository.findAll();
        assertThat(ordersList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ordersSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchOrders() throws Exception {
        // Initialize the database
        orders = ordersRepository.saveAndFlush(orders);
        ordersSearchRepository.save(orders);

        // Search the orders
        restOrdersMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + orders.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(orders.getId().intValue())))
            .andExpect(jsonPath("$.[*].orderDate").value(hasItem(DEFAULT_ORDER_DATE.toString())))
            .andExpect(jsonPath("$.[*].totalAmount").value(hasItem(DEFAULT_TOTAL_AMOUNT)));
    }
}
