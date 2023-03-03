package com.test.changes.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.test.changes.IntegrationTest;
import com.test.changes.domain.DarazUsers;
import com.test.changes.domain.OrderDelivery;
import com.test.changes.domain.Orders;
import com.test.changes.domain.ShippingDetails;
import com.test.changes.domain.enumeration.ShippingStatus;
import com.test.changes.repository.OrderDeliveryRepository;
import com.test.changes.repository.search.OrderDeliverySearchRepository;
import com.test.changes.service.criteria.OrderDeliveryCriteria;
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
 * Integration tests for the {@link OrderDeliveryResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class OrderDeliveryResourceIT {

    private static final LocalDate DEFAULT_DELIVERY_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DELIVERY_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_DELIVERY_DATE = LocalDate.ofEpochDay(-1L);

    private static final Double DEFAULT_DELIVERY_CHARGE = 1D;
    private static final Double UPDATED_DELIVERY_CHARGE = 2D;
    private static final Double SMALLER_DELIVERY_CHARGE = 1D - 1D;

    private static final ShippingStatus DEFAULT_SHIPPING_STATUS = ShippingStatus.Pending;
    private static final ShippingStatus UPDATED_SHIPPING_STATUS = ShippingStatus.Delivered;

    private static final String ENTITY_API_URL = "/api/order-deliveries";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/order-deliveries";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private OrderDeliveryRepository orderDeliveryRepository;

    @Autowired
    private OrderDeliverySearchRepository orderDeliverySearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restOrderDeliveryMockMvc;

    private OrderDelivery orderDelivery;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrderDelivery createEntity(EntityManager em) {
        OrderDelivery orderDelivery = new OrderDelivery()
            .deliveryDate(DEFAULT_DELIVERY_DATE)
            .deliveryCharge(DEFAULT_DELIVERY_CHARGE)
            .shippingStatus(DEFAULT_SHIPPING_STATUS);
        // Add required entity
        Orders orders;
        if (TestUtil.findAll(em, Orders.class).isEmpty()) {
            orders = OrdersResourceIT.createEntity(em);
            em.persist(orders);
            em.flush();
        } else {
            orders = TestUtil.findAll(em, Orders.class).get(0);
        }
        orderDelivery.setOrder(orders);
        // Add required entity
        ShippingDetails shippingDetails;
        if (TestUtil.findAll(em, ShippingDetails.class).isEmpty()) {
            shippingDetails = ShippingDetailsResourceIT.createEntity(em);
            em.persist(shippingDetails);
            em.flush();
        } else {
            shippingDetails = TestUtil.findAll(em, ShippingDetails.class).get(0);
        }
        orderDelivery.setShippingAddress(shippingDetails);
        // Add required entity
        DarazUsers darazUsers;
        if (TestUtil.findAll(em, DarazUsers.class).isEmpty()) {
            darazUsers = DarazUsersResourceIT.createEntity(em);
            em.persist(darazUsers);
            em.flush();
        } else {
            darazUsers = TestUtil.findAll(em, DarazUsers.class).get(0);
        }
        orderDelivery.setDeliveryManager(darazUsers);
        // Add required entity
        orderDelivery.setDeliveryBoy(darazUsers);
        return orderDelivery;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrderDelivery createUpdatedEntity(EntityManager em) {
        OrderDelivery orderDelivery = new OrderDelivery()
            .deliveryDate(UPDATED_DELIVERY_DATE)
            .deliveryCharge(UPDATED_DELIVERY_CHARGE)
            .shippingStatus(UPDATED_SHIPPING_STATUS);
        // Add required entity
        Orders orders;
        if (TestUtil.findAll(em, Orders.class).isEmpty()) {
            orders = OrdersResourceIT.createUpdatedEntity(em);
            em.persist(orders);
            em.flush();
        } else {
            orders = TestUtil.findAll(em, Orders.class).get(0);
        }
        orderDelivery.setOrder(orders);
        // Add required entity
        ShippingDetails shippingDetails;
        if (TestUtil.findAll(em, ShippingDetails.class).isEmpty()) {
            shippingDetails = ShippingDetailsResourceIT.createUpdatedEntity(em);
            em.persist(shippingDetails);
            em.flush();
        } else {
            shippingDetails = TestUtil.findAll(em, ShippingDetails.class).get(0);
        }
        orderDelivery.setShippingAddress(shippingDetails);
        // Add required entity
        DarazUsers darazUsers;
        if (TestUtil.findAll(em, DarazUsers.class).isEmpty()) {
            darazUsers = DarazUsersResourceIT.createUpdatedEntity(em);
            em.persist(darazUsers);
            em.flush();
        } else {
            darazUsers = TestUtil.findAll(em, DarazUsers.class).get(0);
        }
        orderDelivery.setDeliveryManager(darazUsers);
        // Add required entity
        orderDelivery.setDeliveryBoy(darazUsers);
        return orderDelivery;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        orderDeliverySearchRepository.deleteAll();
        assertThat(orderDeliverySearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        orderDelivery = createEntity(em);
    }

    @Test
    @Transactional
    void createOrderDelivery() throws Exception {
        int databaseSizeBeforeCreate = orderDeliveryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderDeliverySearchRepository.findAll());
        // Create the OrderDelivery
        restOrderDeliveryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(orderDelivery)))
            .andExpect(status().isCreated());

        // Validate the OrderDelivery in the database
        List<OrderDelivery> orderDeliveryList = orderDeliveryRepository.findAll();
        assertThat(orderDeliveryList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderDeliverySearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        OrderDelivery testOrderDelivery = orderDeliveryList.get(orderDeliveryList.size() - 1);
        assertThat(testOrderDelivery.getDeliveryDate()).isEqualTo(DEFAULT_DELIVERY_DATE);
        assertThat(testOrderDelivery.getDeliveryCharge()).isEqualTo(DEFAULT_DELIVERY_CHARGE);
        assertThat(testOrderDelivery.getShippingStatus()).isEqualTo(DEFAULT_SHIPPING_STATUS);
    }

    @Test
    @Transactional
    void createOrderDeliveryWithExistingId() throws Exception {
        // Create the OrderDelivery with an existing ID
        orderDelivery.setId(1L);

        int databaseSizeBeforeCreate = orderDeliveryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderDeliverySearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restOrderDeliveryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(orderDelivery)))
            .andExpect(status().isBadRequest());

        // Validate the OrderDelivery in the database
        List<OrderDelivery> orderDeliveryList = orderDeliveryRepository.findAll();
        assertThat(orderDeliveryList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderDeliverySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkShippingStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = orderDeliveryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderDeliverySearchRepository.findAll());
        // set the field null
        orderDelivery.setShippingStatus(null);

        // Create the OrderDelivery, which fails.

        restOrderDeliveryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(orderDelivery)))
            .andExpect(status().isBadRequest());

        List<OrderDelivery> orderDeliveryList = orderDeliveryRepository.findAll();
        assertThat(orderDeliveryList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderDeliverySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllOrderDeliveries() throws Exception {
        // Initialize the database
        orderDeliveryRepository.saveAndFlush(orderDelivery);

        // Get all the orderDeliveryList
        restOrderDeliveryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(orderDelivery.getId().intValue())))
            .andExpect(jsonPath("$.[*].deliveryDate").value(hasItem(DEFAULT_DELIVERY_DATE.toString())))
            .andExpect(jsonPath("$.[*].deliveryCharge").value(hasItem(DEFAULT_DELIVERY_CHARGE.doubleValue())))
            .andExpect(jsonPath("$.[*].shippingStatus").value(hasItem(DEFAULT_SHIPPING_STATUS.toString())));
    }

    @Test
    @Transactional
    void getOrderDelivery() throws Exception {
        // Initialize the database
        orderDeliveryRepository.saveAndFlush(orderDelivery);

        // Get the orderDelivery
        restOrderDeliveryMockMvc
            .perform(get(ENTITY_API_URL_ID, orderDelivery.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(orderDelivery.getId().intValue()))
            .andExpect(jsonPath("$.deliveryDate").value(DEFAULT_DELIVERY_DATE.toString()))
            .andExpect(jsonPath("$.deliveryCharge").value(DEFAULT_DELIVERY_CHARGE.doubleValue()))
            .andExpect(jsonPath("$.shippingStatus").value(DEFAULT_SHIPPING_STATUS.toString()));
    }

    @Test
    @Transactional
    void getOrderDeliveriesByIdFiltering() throws Exception {
        // Initialize the database
        orderDeliveryRepository.saveAndFlush(orderDelivery);

        Long id = orderDelivery.getId();

        defaultOrderDeliveryShouldBeFound("id.equals=" + id);
        defaultOrderDeliveryShouldNotBeFound("id.notEquals=" + id);

        defaultOrderDeliveryShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultOrderDeliveryShouldNotBeFound("id.greaterThan=" + id);

        defaultOrderDeliveryShouldBeFound("id.lessThanOrEqual=" + id);
        defaultOrderDeliveryShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllOrderDeliveriesByDeliveryDateIsEqualToSomething() throws Exception {
        // Initialize the database
        orderDeliveryRepository.saveAndFlush(orderDelivery);

        // Get all the orderDeliveryList where deliveryDate equals to DEFAULT_DELIVERY_DATE
        defaultOrderDeliveryShouldBeFound("deliveryDate.equals=" + DEFAULT_DELIVERY_DATE);

        // Get all the orderDeliveryList where deliveryDate equals to UPDATED_DELIVERY_DATE
        defaultOrderDeliveryShouldNotBeFound("deliveryDate.equals=" + UPDATED_DELIVERY_DATE);
    }

    @Test
    @Transactional
    void getAllOrderDeliveriesByDeliveryDateIsInShouldWork() throws Exception {
        // Initialize the database
        orderDeliveryRepository.saveAndFlush(orderDelivery);

        // Get all the orderDeliveryList where deliveryDate in DEFAULT_DELIVERY_DATE or UPDATED_DELIVERY_DATE
        defaultOrderDeliveryShouldBeFound("deliveryDate.in=" + DEFAULT_DELIVERY_DATE + "," + UPDATED_DELIVERY_DATE);

        // Get all the orderDeliveryList where deliveryDate equals to UPDATED_DELIVERY_DATE
        defaultOrderDeliveryShouldNotBeFound("deliveryDate.in=" + UPDATED_DELIVERY_DATE);
    }

    @Test
    @Transactional
    void getAllOrderDeliveriesByDeliveryDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        orderDeliveryRepository.saveAndFlush(orderDelivery);

        // Get all the orderDeliveryList where deliveryDate is not null
        defaultOrderDeliveryShouldBeFound("deliveryDate.specified=true");

        // Get all the orderDeliveryList where deliveryDate is null
        defaultOrderDeliveryShouldNotBeFound("deliveryDate.specified=false");
    }

    @Test
    @Transactional
    void getAllOrderDeliveriesByDeliveryDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        orderDeliveryRepository.saveAndFlush(orderDelivery);

        // Get all the orderDeliveryList where deliveryDate is greater than or equal to DEFAULT_DELIVERY_DATE
        defaultOrderDeliveryShouldBeFound("deliveryDate.greaterThanOrEqual=" + DEFAULT_DELIVERY_DATE);

        // Get all the orderDeliveryList where deliveryDate is greater than or equal to UPDATED_DELIVERY_DATE
        defaultOrderDeliveryShouldNotBeFound("deliveryDate.greaterThanOrEqual=" + UPDATED_DELIVERY_DATE);
    }

    @Test
    @Transactional
    void getAllOrderDeliveriesByDeliveryDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        orderDeliveryRepository.saveAndFlush(orderDelivery);

        // Get all the orderDeliveryList where deliveryDate is less than or equal to DEFAULT_DELIVERY_DATE
        defaultOrderDeliveryShouldBeFound("deliveryDate.lessThanOrEqual=" + DEFAULT_DELIVERY_DATE);

        // Get all the orderDeliveryList where deliveryDate is less than or equal to SMALLER_DELIVERY_DATE
        defaultOrderDeliveryShouldNotBeFound("deliveryDate.lessThanOrEqual=" + SMALLER_DELIVERY_DATE);
    }

    @Test
    @Transactional
    void getAllOrderDeliveriesByDeliveryDateIsLessThanSomething() throws Exception {
        // Initialize the database
        orderDeliveryRepository.saveAndFlush(orderDelivery);

        // Get all the orderDeliveryList where deliveryDate is less than DEFAULT_DELIVERY_DATE
        defaultOrderDeliveryShouldNotBeFound("deliveryDate.lessThan=" + DEFAULT_DELIVERY_DATE);

        // Get all the orderDeliveryList where deliveryDate is less than UPDATED_DELIVERY_DATE
        defaultOrderDeliveryShouldBeFound("deliveryDate.lessThan=" + UPDATED_DELIVERY_DATE);
    }

    @Test
    @Transactional
    void getAllOrderDeliveriesByDeliveryDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        orderDeliveryRepository.saveAndFlush(orderDelivery);

        // Get all the orderDeliveryList where deliveryDate is greater than DEFAULT_DELIVERY_DATE
        defaultOrderDeliveryShouldNotBeFound("deliveryDate.greaterThan=" + DEFAULT_DELIVERY_DATE);

        // Get all the orderDeliveryList where deliveryDate is greater than SMALLER_DELIVERY_DATE
        defaultOrderDeliveryShouldBeFound("deliveryDate.greaterThan=" + SMALLER_DELIVERY_DATE);
    }

    @Test
    @Transactional
    void getAllOrderDeliveriesByDeliveryChargeIsEqualToSomething() throws Exception {
        // Initialize the database
        orderDeliveryRepository.saveAndFlush(orderDelivery);

        // Get all the orderDeliveryList where deliveryCharge equals to DEFAULT_DELIVERY_CHARGE
        defaultOrderDeliveryShouldBeFound("deliveryCharge.equals=" + DEFAULT_DELIVERY_CHARGE);

        // Get all the orderDeliveryList where deliveryCharge equals to UPDATED_DELIVERY_CHARGE
        defaultOrderDeliveryShouldNotBeFound("deliveryCharge.equals=" + UPDATED_DELIVERY_CHARGE);
    }

    @Test
    @Transactional
    void getAllOrderDeliveriesByDeliveryChargeIsInShouldWork() throws Exception {
        // Initialize the database
        orderDeliveryRepository.saveAndFlush(orderDelivery);

        // Get all the orderDeliveryList where deliveryCharge in DEFAULT_DELIVERY_CHARGE or UPDATED_DELIVERY_CHARGE
        defaultOrderDeliveryShouldBeFound("deliveryCharge.in=" + DEFAULT_DELIVERY_CHARGE + "," + UPDATED_DELIVERY_CHARGE);

        // Get all the orderDeliveryList where deliveryCharge equals to UPDATED_DELIVERY_CHARGE
        defaultOrderDeliveryShouldNotBeFound("deliveryCharge.in=" + UPDATED_DELIVERY_CHARGE);
    }

    @Test
    @Transactional
    void getAllOrderDeliveriesByDeliveryChargeIsNullOrNotNull() throws Exception {
        // Initialize the database
        orderDeliveryRepository.saveAndFlush(orderDelivery);

        // Get all the orderDeliveryList where deliveryCharge is not null
        defaultOrderDeliveryShouldBeFound("deliveryCharge.specified=true");

        // Get all the orderDeliveryList where deliveryCharge is null
        defaultOrderDeliveryShouldNotBeFound("deliveryCharge.specified=false");
    }

    @Test
    @Transactional
    void getAllOrderDeliveriesByDeliveryChargeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        orderDeliveryRepository.saveAndFlush(orderDelivery);

        // Get all the orderDeliveryList where deliveryCharge is greater than or equal to DEFAULT_DELIVERY_CHARGE
        defaultOrderDeliveryShouldBeFound("deliveryCharge.greaterThanOrEqual=" + DEFAULT_DELIVERY_CHARGE);

        // Get all the orderDeliveryList where deliveryCharge is greater than or equal to UPDATED_DELIVERY_CHARGE
        defaultOrderDeliveryShouldNotBeFound("deliveryCharge.greaterThanOrEqual=" + UPDATED_DELIVERY_CHARGE);
    }

    @Test
    @Transactional
    void getAllOrderDeliveriesByDeliveryChargeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        orderDeliveryRepository.saveAndFlush(orderDelivery);

        // Get all the orderDeliveryList where deliveryCharge is less than or equal to DEFAULT_DELIVERY_CHARGE
        defaultOrderDeliveryShouldBeFound("deliveryCharge.lessThanOrEqual=" + DEFAULT_DELIVERY_CHARGE);

        // Get all the orderDeliveryList where deliveryCharge is less than or equal to SMALLER_DELIVERY_CHARGE
        defaultOrderDeliveryShouldNotBeFound("deliveryCharge.lessThanOrEqual=" + SMALLER_DELIVERY_CHARGE);
    }

    @Test
    @Transactional
    void getAllOrderDeliveriesByDeliveryChargeIsLessThanSomething() throws Exception {
        // Initialize the database
        orderDeliveryRepository.saveAndFlush(orderDelivery);

        // Get all the orderDeliveryList where deliveryCharge is less than DEFAULT_DELIVERY_CHARGE
        defaultOrderDeliveryShouldNotBeFound("deliveryCharge.lessThan=" + DEFAULT_DELIVERY_CHARGE);

        // Get all the orderDeliveryList where deliveryCharge is less than UPDATED_DELIVERY_CHARGE
        defaultOrderDeliveryShouldBeFound("deliveryCharge.lessThan=" + UPDATED_DELIVERY_CHARGE);
    }

    @Test
    @Transactional
    void getAllOrderDeliveriesByDeliveryChargeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        orderDeliveryRepository.saveAndFlush(orderDelivery);

        // Get all the orderDeliveryList where deliveryCharge is greater than DEFAULT_DELIVERY_CHARGE
        defaultOrderDeliveryShouldNotBeFound("deliveryCharge.greaterThan=" + DEFAULT_DELIVERY_CHARGE);

        // Get all the orderDeliveryList where deliveryCharge is greater than SMALLER_DELIVERY_CHARGE
        defaultOrderDeliveryShouldBeFound("deliveryCharge.greaterThan=" + SMALLER_DELIVERY_CHARGE);
    }

    @Test
    @Transactional
    void getAllOrderDeliveriesByShippingStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        orderDeliveryRepository.saveAndFlush(orderDelivery);

        // Get all the orderDeliveryList where shippingStatus equals to DEFAULT_SHIPPING_STATUS
        defaultOrderDeliveryShouldBeFound("shippingStatus.equals=" + DEFAULT_SHIPPING_STATUS);

        // Get all the orderDeliveryList where shippingStatus equals to UPDATED_SHIPPING_STATUS
        defaultOrderDeliveryShouldNotBeFound("shippingStatus.equals=" + UPDATED_SHIPPING_STATUS);
    }

    @Test
    @Transactional
    void getAllOrderDeliveriesByShippingStatusIsInShouldWork() throws Exception {
        // Initialize the database
        orderDeliveryRepository.saveAndFlush(orderDelivery);

        // Get all the orderDeliveryList where shippingStatus in DEFAULT_SHIPPING_STATUS or UPDATED_SHIPPING_STATUS
        defaultOrderDeliveryShouldBeFound("shippingStatus.in=" + DEFAULT_SHIPPING_STATUS + "," + UPDATED_SHIPPING_STATUS);

        // Get all the orderDeliveryList where shippingStatus equals to UPDATED_SHIPPING_STATUS
        defaultOrderDeliveryShouldNotBeFound("shippingStatus.in=" + UPDATED_SHIPPING_STATUS);
    }

    @Test
    @Transactional
    void getAllOrderDeliveriesByShippingStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        orderDeliveryRepository.saveAndFlush(orderDelivery);

        // Get all the orderDeliveryList where shippingStatus is not null
        defaultOrderDeliveryShouldBeFound("shippingStatus.specified=true");

        // Get all the orderDeliveryList where shippingStatus is null
        defaultOrderDeliveryShouldNotBeFound("shippingStatus.specified=false");
    }

    @Test
    @Transactional
    void getAllOrderDeliveriesByOrderIsEqualToSomething() throws Exception {
        Orders order;
        if (TestUtil.findAll(em, Orders.class).isEmpty()) {
            orderDeliveryRepository.saveAndFlush(orderDelivery);
            order = OrdersResourceIT.createEntity(em);
        } else {
            order = TestUtil.findAll(em, Orders.class).get(0);
        }
        em.persist(order);
        em.flush();
        orderDelivery.setOrder(order);
        orderDeliveryRepository.saveAndFlush(orderDelivery);
        Long orderId = order.getId();

        // Get all the orderDeliveryList where order equals to orderId
        defaultOrderDeliveryShouldBeFound("orderId.equals=" + orderId);

        // Get all the orderDeliveryList where order equals to (orderId + 1)
        defaultOrderDeliveryShouldNotBeFound("orderId.equals=" + (orderId + 1));
    }

    @Test
    @Transactional
    void getAllOrderDeliveriesByShippingAddressIsEqualToSomething() throws Exception {
        ShippingDetails shippingAddress;
        if (TestUtil.findAll(em, ShippingDetails.class).isEmpty()) {
            orderDeliveryRepository.saveAndFlush(orderDelivery);
            shippingAddress = ShippingDetailsResourceIT.createEntity(em);
        } else {
            shippingAddress = TestUtil.findAll(em, ShippingDetails.class).get(0);
        }
        em.persist(shippingAddress);
        em.flush();
        orderDelivery.setShippingAddress(shippingAddress);
        orderDeliveryRepository.saveAndFlush(orderDelivery);
        Long shippingAddressId = shippingAddress.getId();

        // Get all the orderDeliveryList where shippingAddress equals to shippingAddressId
        defaultOrderDeliveryShouldBeFound("shippingAddressId.equals=" + shippingAddressId);

        // Get all the orderDeliveryList where shippingAddress equals to (shippingAddressId + 1)
        defaultOrderDeliveryShouldNotBeFound("shippingAddressId.equals=" + (shippingAddressId + 1));
    }

    @Test
    @Transactional
    void getAllOrderDeliveriesByDeliveryManagerIsEqualToSomething() throws Exception {
        DarazUsers deliveryManager;
        if (TestUtil.findAll(em, DarazUsers.class).isEmpty()) {
            orderDeliveryRepository.saveAndFlush(orderDelivery);
            deliveryManager = DarazUsersResourceIT.createEntity(em);
        } else {
            deliveryManager = TestUtil.findAll(em, DarazUsers.class).get(0);
        }
        em.persist(deliveryManager);
        em.flush();
        orderDelivery.setDeliveryManager(deliveryManager);
        orderDeliveryRepository.saveAndFlush(orderDelivery);
        Long deliveryManagerId = deliveryManager.getId();

        // Get all the orderDeliveryList where deliveryManager equals to deliveryManagerId
        defaultOrderDeliveryShouldBeFound("deliveryManagerId.equals=" + deliveryManagerId);

        // Get all the orderDeliveryList where deliveryManager equals to (deliveryManagerId + 1)
        defaultOrderDeliveryShouldNotBeFound("deliveryManagerId.equals=" + (deliveryManagerId + 1));
    }

    @Test
    @Transactional
    void getAllOrderDeliveriesByDeliveryBoyIsEqualToSomething() throws Exception {
        DarazUsers deliveryBoy;
        if (TestUtil.findAll(em, DarazUsers.class).isEmpty()) {
            orderDeliveryRepository.saveAndFlush(orderDelivery);
            deliveryBoy = DarazUsersResourceIT.createEntity(em);
        } else {
            deliveryBoy = TestUtil.findAll(em, DarazUsers.class).get(0);
        }
        em.persist(deliveryBoy);
        em.flush();
        orderDelivery.setDeliveryBoy(deliveryBoy);
        orderDeliveryRepository.saveAndFlush(orderDelivery);
        Long deliveryBoyId = deliveryBoy.getId();

        // Get all the orderDeliveryList where deliveryBoy equals to deliveryBoyId
        defaultOrderDeliveryShouldBeFound("deliveryBoyId.equals=" + deliveryBoyId);

        // Get all the orderDeliveryList where deliveryBoy equals to (deliveryBoyId + 1)
        defaultOrderDeliveryShouldNotBeFound("deliveryBoyId.equals=" + (deliveryBoyId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultOrderDeliveryShouldBeFound(String filter) throws Exception {
        restOrderDeliveryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(orderDelivery.getId().intValue())))
            .andExpect(jsonPath("$.[*].deliveryDate").value(hasItem(DEFAULT_DELIVERY_DATE.toString())))
            .andExpect(jsonPath("$.[*].deliveryCharge").value(hasItem(DEFAULT_DELIVERY_CHARGE.doubleValue())))
            .andExpect(jsonPath("$.[*].shippingStatus").value(hasItem(DEFAULT_SHIPPING_STATUS.toString())));

        // Check, that the count call also returns 1
        restOrderDeliveryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultOrderDeliveryShouldNotBeFound(String filter) throws Exception {
        restOrderDeliveryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restOrderDeliveryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingOrderDelivery() throws Exception {
        // Get the orderDelivery
        restOrderDeliveryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingOrderDelivery() throws Exception {
        // Initialize the database
        orderDeliveryRepository.saveAndFlush(orderDelivery);

        int databaseSizeBeforeUpdate = orderDeliveryRepository.findAll().size();
        orderDeliverySearchRepository.save(orderDelivery);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderDeliverySearchRepository.findAll());

        // Update the orderDelivery
        OrderDelivery updatedOrderDelivery = orderDeliveryRepository.findById(orderDelivery.getId()).get();
        // Disconnect from session so that the updates on updatedOrderDelivery are not directly saved in db
        em.detach(updatedOrderDelivery);
        updatedOrderDelivery
            .deliveryDate(UPDATED_DELIVERY_DATE)
            .deliveryCharge(UPDATED_DELIVERY_CHARGE)
            .shippingStatus(UPDATED_SHIPPING_STATUS);

        restOrderDeliveryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedOrderDelivery.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedOrderDelivery))
            )
            .andExpect(status().isOk());

        // Validate the OrderDelivery in the database
        List<OrderDelivery> orderDeliveryList = orderDeliveryRepository.findAll();
        assertThat(orderDeliveryList).hasSize(databaseSizeBeforeUpdate);
        OrderDelivery testOrderDelivery = orderDeliveryList.get(orderDeliveryList.size() - 1);
        assertThat(testOrderDelivery.getDeliveryDate()).isEqualTo(UPDATED_DELIVERY_DATE);
        assertThat(testOrderDelivery.getDeliveryCharge()).isEqualTo(UPDATED_DELIVERY_CHARGE);
        assertThat(testOrderDelivery.getShippingStatus()).isEqualTo(UPDATED_SHIPPING_STATUS);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderDeliverySearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<OrderDelivery> orderDeliverySearchList = IterableUtils.toList(orderDeliverySearchRepository.findAll());
                OrderDelivery testOrderDeliverySearch = orderDeliverySearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testOrderDeliverySearch.getDeliveryDate()).isEqualTo(UPDATED_DELIVERY_DATE);
                assertThat(testOrderDeliverySearch.getDeliveryCharge()).isEqualTo(UPDATED_DELIVERY_CHARGE);
                assertThat(testOrderDeliverySearch.getShippingStatus()).isEqualTo(UPDATED_SHIPPING_STATUS);
            });
    }

    @Test
    @Transactional
    void putNonExistingOrderDelivery() throws Exception {
        int databaseSizeBeforeUpdate = orderDeliveryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderDeliverySearchRepository.findAll());
        orderDelivery.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrderDeliveryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, orderDelivery.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(orderDelivery))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderDelivery in the database
        List<OrderDelivery> orderDeliveryList = orderDeliveryRepository.findAll();
        assertThat(orderDeliveryList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderDeliverySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchOrderDelivery() throws Exception {
        int databaseSizeBeforeUpdate = orderDeliveryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderDeliverySearchRepository.findAll());
        orderDelivery.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderDeliveryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(orderDelivery))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderDelivery in the database
        List<OrderDelivery> orderDeliveryList = orderDeliveryRepository.findAll();
        assertThat(orderDeliveryList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderDeliverySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamOrderDelivery() throws Exception {
        int databaseSizeBeforeUpdate = orderDeliveryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderDeliverySearchRepository.findAll());
        orderDelivery.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderDeliveryMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(orderDelivery)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the OrderDelivery in the database
        List<OrderDelivery> orderDeliveryList = orderDeliveryRepository.findAll();
        assertThat(orderDeliveryList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderDeliverySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateOrderDeliveryWithPatch() throws Exception {
        // Initialize the database
        orderDeliveryRepository.saveAndFlush(orderDelivery);

        int databaseSizeBeforeUpdate = orderDeliveryRepository.findAll().size();

        // Update the orderDelivery using partial update
        OrderDelivery partialUpdatedOrderDelivery = new OrderDelivery();
        partialUpdatedOrderDelivery.setId(orderDelivery.getId());

        partialUpdatedOrderDelivery.deliveryCharge(UPDATED_DELIVERY_CHARGE);

        restOrderDeliveryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrderDelivery.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOrderDelivery))
            )
            .andExpect(status().isOk());

        // Validate the OrderDelivery in the database
        List<OrderDelivery> orderDeliveryList = orderDeliveryRepository.findAll();
        assertThat(orderDeliveryList).hasSize(databaseSizeBeforeUpdate);
        OrderDelivery testOrderDelivery = orderDeliveryList.get(orderDeliveryList.size() - 1);
        assertThat(testOrderDelivery.getDeliveryDate()).isEqualTo(DEFAULT_DELIVERY_DATE);
        assertThat(testOrderDelivery.getDeliveryCharge()).isEqualTo(UPDATED_DELIVERY_CHARGE);
        assertThat(testOrderDelivery.getShippingStatus()).isEqualTo(DEFAULT_SHIPPING_STATUS);
    }

    @Test
    @Transactional
    void fullUpdateOrderDeliveryWithPatch() throws Exception {
        // Initialize the database
        orderDeliveryRepository.saveAndFlush(orderDelivery);

        int databaseSizeBeforeUpdate = orderDeliveryRepository.findAll().size();

        // Update the orderDelivery using partial update
        OrderDelivery partialUpdatedOrderDelivery = new OrderDelivery();
        partialUpdatedOrderDelivery.setId(orderDelivery.getId());

        partialUpdatedOrderDelivery
            .deliveryDate(UPDATED_DELIVERY_DATE)
            .deliveryCharge(UPDATED_DELIVERY_CHARGE)
            .shippingStatus(UPDATED_SHIPPING_STATUS);

        restOrderDeliveryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrderDelivery.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOrderDelivery))
            )
            .andExpect(status().isOk());

        // Validate the OrderDelivery in the database
        List<OrderDelivery> orderDeliveryList = orderDeliveryRepository.findAll();
        assertThat(orderDeliveryList).hasSize(databaseSizeBeforeUpdate);
        OrderDelivery testOrderDelivery = orderDeliveryList.get(orderDeliveryList.size() - 1);
        assertThat(testOrderDelivery.getDeliveryDate()).isEqualTo(UPDATED_DELIVERY_DATE);
        assertThat(testOrderDelivery.getDeliveryCharge()).isEqualTo(UPDATED_DELIVERY_CHARGE);
        assertThat(testOrderDelivery.getShippingStatus()).isEqualTo(UPDATED_SHIPPING_STATUS);
    }

    @Test
    @Transactional
    void patchNonExistingOrderDelivery() throws Exception {
        int databaseSizeBeforeUpdate = orderDeliveryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderDeliverySearchRepository.findAll());
        orderDelivery.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrderDeliveryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, orderDelivery.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(orderDelivery))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderDelivery in the database
        List<OrderDelivery> orderDeliveryList = orderDeliveryRepository.findAll();
        assertThat(orderDeliveryList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderDeliverySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchOrderDelivery() throws Exception {
        int databaseSizeBeforeUpdate = orderDeliveryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderDeliverySearchRepository.findAll());
        orderDelivery.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderDeliveryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(orderDelivery))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderDelivery in the database
        List<OrderDelivery> orderDeliveryList = orderDeliveryRepository.findAll();
        assertThat(orderDeliveryList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderDeliverySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamOrderDelivery() throws Exception {
        int databaseSizeBeforeUpdate = orderDeliveryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderDeliverySearchRepository.findAll());
        orderDelivery.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderDeliveryMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(orderDelivery))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the OrderDelivery in the database
        List<OrderDelivery> orderDeliveryList = orderDeliveryRepository.findAll();
        assertThat(orderDeliveryList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderDeliverySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteOrderDelivery() throws Exception {
        // Initialize the database
        orderDeliveryRepository.saveAndFlush(orderDelivery);
        orderDeliveryRepository.save(orderDelivery);
        orderDeliverySearchRepository.save(orderDelivery);

        int databaseSizeBeforeDelete = orderDeliveryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderDeliverySearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the orderDelivery
        restOrderDeliveryMockMvc
            .perform(delete(ENTITY_API_URL_ID, orderDelivery.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<OrderDelivery> orderDeliveryList = orderDeliveryRepository.findAll();
        assertThat(orderDeliveryList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderDeliverySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchOrderDelivery() throws Exception {
        // Initialize the database
        orderDelivery = orderDeliveryRepository.saveAndFlush(orderDelivery);
        orderDeliverySearchRepository.save(orderDelivery);

        // Search the orderDelivery
        restOrderDeliveryMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + orderDelivery.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(orderDelivery.getId().intValue())))
            .andExpect(jsonPath("$.[*].deliveryDate").value(hasItem(DEFAULT_DELIVERY_DATE.toString())))
            .andExpect(jsonPath("$.[*].deliveryCharge").value(hasItem(DEFAULT_DELIVERY_CHARGE.doubleValue())))
            .andExpect(jsonPath("$.[*].shippingStatus").value(hasItem(DEFAULT_SHIPPING_STATUS.toString())));
    }
}
