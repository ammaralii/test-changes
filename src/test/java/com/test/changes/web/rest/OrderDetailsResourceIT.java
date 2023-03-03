package com.test.changes.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.test.changes.IntegrationTest;
import com.test.changes.domain.OrderDetails;
import com.test.changes.domain.Orders;
import com.test.changes.domain.Products;
import com.test.changes.repository.OrderDetailsRepository;
import com.test.changes.repository.search.OrderDetailsSearchRepository;
import com.test.changes.service.criteria.OrderDetailsCriteria;
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
 * Integration tests for the {@link OrderDetailsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class OrderDetailsResourceIT {

    private static final Integer DEFAULT_QUANTITY = 1;
    private static final Integer UPDATED_QUANTITY = 2;
    private static final Integer SMALLER_QUANTITY = 1 - 1;

    private static final Double DEFAULT_AMOUNT = 1D;
    private static final Double UPDATED_AMOUNT = 2D;
    private static final Double SMALLER_AMOUNT = 1D - 1D;

    private static final String ENTITY_API_URL = "/api/order-details";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/order-details";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private OrderDetailsRepository orderDetailsRepository;

    @Autowired
    private OrderDetailsSearchRepository orderDetailsSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restOrderDetailsMockMvc;

    private OrderDetails orderDetails;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrderDetails createEntity(EntityManager em) {
        OrderDetails orderDetails = new OrderDetails().quantity(DEFAULT_QUANTITY).amount(DEFAULT_AMOUNT);
        // Add required entity
        Orders orders;
        if (TestUtil.findAll(em, Orders.class).isEmpty()) {
            orders = OrdersResourceIT.createEntity(em);
            em.persist(orders);
            em.flush();
        } else {
            orders = TestUtil.findAll(em, Orders.class).get(0);
        }
        orderDetails.setOrder(orders);
        // Add required entity
        Products products;
        if (TestUtil.findAll(em, Products.class).isEmpty()) {
            products = ProductsResourceIT.createEntity(em);
            em.persist(products);
            em.flush();
        } else {
            products = TestUtil.findAll(em, Products.class).get(0);
        }
        orderDetails.setProduct(products);
        return orderDetails;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrderDetails createUpdatedEntity(EntityManager em) {
        OrderDetails orderDetails = new OrderDetails().quantity(UPDATED_QUANTITY).amount(UPDATED_AMOUNT);
        // Add required entity
        Orders orders;
        if (TestUtil.findAll(em, Orders.class).isEmpty()) {
            orders = OrdersResourceIT.createUpdatedEntity(em);
            em.persist(orders);
            em.flush();
        } else {
            orders = TestUtil.findAll(em, Orders.class).get(0);
        }
        orderDetails.setOrder(orders);
        // Add required entity
        Products products;
        if (TestUtil.findAll(em, Products.class).isEmpty()) {
            products = ProductsResourceIT.createUpdatedEntity(em);
            em.persist(products);
            em.flush();
        } else {
            products = TestUtil.findAll(em, Products.class).get(0);
        }
        orderDetails.setProduct(products);
        return orderDetails;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        orderDetailsSearchRepository.deleteAll();
        assertThat(orderDetailsSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        orderDetails = createEntity(em);
    }

    @Test
    @Transactional
    void createOrderDetails() throws Exception {
        int databaseSizeBeforeCreate = orderDetailsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderDetailsSearchRepository.findAll());
        // Create the OrderDetails
        restOrderDetailsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(orderDetails)))
            .andExpect(status().isCreated());

        // Validate the OrderDetails in the database
        List<OrderDetails> orderDetailsList = orderDetailsRepository.findAll();
        assertThat(orderDetailsList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderDetailsSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        OrderDetails testOrderDetails = orderDetailsList.get(orderDetailsList.size() - 1);
        assertThat(testOrderDetails.getQuantity()).isEqualTo(DEFAULT_QUANTITY);
        assertThat(testOrderDetails.getAmount()).isEqualTo(DEFAULT_AMOUNT);
    }

    @Test
    @Transactional
    void createOrderDetailsWithExistingId() throws Exception {
        // Create the OrderDetails with an existing ID
        orderDetails.setId(1L);

        int databaseSizeBeforeCreate = orderDetailsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderDetailsSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restOrderDetailsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(orderDetails)))
            .andExpect(status().isBadRequest());

        // Validate the OrderDetails in the database
        List<OrderDetails> orderDetailsList = orderDetailsRepository.findAll();
        assertThat(orderDetailsList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderDetailsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllOrderDetails() throws Exception {
        // Initialize the database
        orderDetailsRepository.saveAndFlush(orderDetails);

        // Get all the orderDetailsList
        restOrderDetailsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(orderDetails.getId().intValue())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.doubleValue())));
    }

    @Test
    @Transactional
    void getOrderDetails() throws Exception {
        // Initialize the database
        orderDetailsRepository.saveAndFlush(orderDetails);

        // Get the orderDetails
        restOrderDetailsMockMvc
            .perform(get(ENTITY_API_URL_ID, orderDetails.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(orderDetails.getId().intValue()))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY))
            .andExpect(jsonPath("$.amount").value(DEFAULT_AMOUNT.doubleValue()));
    }

    @Test
    @Transactional
    void getOrderDetailsByIdFiltering() throws Exception {
        // Initialize the database
        orderDetailsRepository.saveAndFlush(orderDetails);

        Long id = orderDetails.getId();

        defaultOrderDetailsShouldBeFound("id.equals=" + id);
        defaultOrderDetailsShouldNotBeFound("id.notEquals=" + id);

        defaultOrderDetailsShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultOrderDetailsShouldNotBeFound("id.greaterThan=" + id);

        defaultOrderDetailsShouldBeFound("id.lessThanOrEqual=" + id);
        defaultOrderDetailsShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllOrderDetailsByQuantityIsEqualToSomething() throws Exception {
        // Initialize the database
        orderDetailsRepository.saveAndFlush(orderDetails);

        // Get all the orderDetailsList where quantity equals to DEFAULT_QUANTITY
        defaultOrderDetailsShouldBeFound("quantity.equals=" + DEFAULT_QUANTITY);

        // Get all the orderDetailsList where quantity equals to UPDATED_QUANTITY
        defaultOrderDetailsShouldNotBeFound("quantity.equals=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllOrderDetailsByQuantityIsInShouldWork() throws Exception {
        // Initialize the database
        orderDetailsRepository.saveAndFlush(orderDetails);

        // Get all the orderDetailsList where quantity in DEFAULT_QUANTITY or UPDATED_QUANTITY
        defaultOrderDetailsShouldBeFound("quantity.in=" + DEFAULT_QUANTITY + "," + UPDATED_QUANTITY);

        // Get all the orderDetailsList where quantity equals to UPDATED_QUANTITY
        defaultOrderDetailsShouldNotBeFound("quantity.in=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllOrderDetailsByQuantityIsNullOrNotNull() throws Exception {
        // Initialize the database
        orderDetailsRepository.saveAndFlush(orderDetails);

        // Get all the orderDetailsList where quantity is not null
        defaultOrderDetailsShouldBeFound("quantity.specified=true");

        // Get all the orderDetailsList where quantity is null
        defaultOrderDetailsShouldNotBeFound("quantity.specified=false");
    }

    @Test
    @Transactional
    void getAllOrderDetailsByQuantityIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        orderDetailsRepository.saveAndFlush(orderDetails);

        // Get all the orderDetailsList where quantity is greater than or equal to DEFAULT_QUANTITY
        defaultOrderDetailsShouldBeFound("quantity.greaterThanOrEqual=" + DEFAULT_QUANTITY);

        // Get all the orderDetailsList where quantity is greater than or equal to UPDATED_QUANTITY
        defaultOrderDetailsShouldNotBeFound("quantity.greaterThanOrEqual=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllOrderDetailsByQuantityIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        orderDetailsRepository.saveAndFlush(orderDetails);

        // Get all the orderDetailsList where quantity is less than or equal to DEFAULT_QUANTITY
        defaultOrderDetailsShouldBeFound("quantity.lessThanOrEqual=" + DEFAULT_QUANTITY);

        // Get all the orderDetailsList where quantity is less than or equal to SMALLER_QUANTITY
        defaultOrderDetailsShouldNotBeFound("quantity.lessThanOrEqual=" + SMALLER_QUANTITY);
    }

    @Test
    @Transactional
    void getAllOrderDetailsByQuantityIsLessThanSomething() throws Exception {
        // Initialize the database
        orderDetailsRepository.saveAndFlush(orderDetails);

        // Get all the orderDetailsList where quantity is less than DEFAULT_QUANTITY
        defaultOrderDetailsShouldNotBeFound("quantity.lessThan=" + DEFAULT_QUANTITY);

        // Get all the orderDetailsList where quantity is less than UPDATED_QUANTITY
        defaultOrderDetailsShouldBeFound("quantity.lessThan=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllOrderDetailsByQuantityIsGreaterThanSomething() throws Exception {
        // Initialize the database
        orderDetailsRepository.saveAndFlush(orderDetails);

        // Get all the orderDetailsList where quantity is greater than DEFAULT_QUANTITY
        defaultOrderDetailsShouldNotBeFound("quantity.greaterThan=" + DEFAULT_QUANTITY);

        // Get all the orderDetailsList where quantity is greater than SMALLER_QUANTITY
        defaultOrderDetailsShouldBeFound("quantity.greaterThan=" + SMALLER_QUANTITY);
    }

    @Test
    @Transactional
    void getAllOrderDetailsByAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        orderDetailsRepository.saveAndFlush(orderDetails);

        // Get all the orderDetailsList where amount equals to DEFAULT_AMOUNT
        defaultOrderDetailsShouldBeFound("amount.equals=" + DEFAULT_AMOUNT);

        // Get all the orderDetailsList where amount equals to UPDATED_AMOUNT
        defaultOrderDetailsShouldNotBeFound("amount.equals=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllOrderDetailsByAmountIsInShouldWork() throws Exception {
        // Initialize the database
        orderDetailsRepository.saveAndFlush(orderDetails);

        // Get all the orderDetailsList where amount in DEFAULT_AMOUNT or UPDATED_AMOUNT
        defaultOrderDetailsShouldBeFound("amount.in=" + DEFAULT_AMOUNT + "," + UPDATED_AMOUNT);

        // Get all the orderDetailsList where amount equals to UPDATED_AMOUNT
        defaultOrderDetailsShouldNotBeFound("amount.in=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllOrderDetailsByAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        orderDetailsRepository.saveAndFlush(orderDetails);

        // Get all the orderDetailsList where amount is not null
        defaultOrderDetailsShouldBeFound("amount.specified=true");

        // Get all the orderDetailsList where amount is null
        defaultOrderDetailsShouldNotBeFound("amount.specified=false");
    }

    @Test
    @Transactional
    void getAllOrderDetailsByAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        orderDetailsRepository.saveAndFlush(orderDetails);

        // Get all the orderDetailsList where amount is greater than or equal to DEFAULT_AMOUNT
        defaultOrderDetailsShouldBeFound("amount.greaterThanOrEqual=" + DEFAULT_AMOUNT);

        // Get all the orderDetailsList where amount is greater than or equal to UPDATED_AMOUNT
        defaultOrderDetailsShouldNotBeFound("amount.greaterThanOrEqual=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllOrderDetailsByAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        orderDetailsRepository.saveAndFlush(orderDetails);

        // Get all the orderDetailsList where amount is less than or equal to DEFAULT_AMOUNT
        defaultOrderDetailsShouldBeFound("amount.lessThanOrEqual=" + DEFAULT_AMOUNT);

        // Get all the orderDetailsList where amount is less than or equal to SMALLER_AMOUNT
        defaultOrderDetailsShouldNotBeFound("amount.lessThanOrEqual=" + SMALLER_AMOUNT);
    }

    @Test
    @Transactional
    void getAllOrderDetailsByAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        orderDetailsRepository.saveAndFlush(orderDetails);

        // Get all the orderDetailsList where amount is less than DEFAULT_AMOUNT
        defaultOrderDetailsShouldNotBeFound("amount.lessThan=" + DEFAULT_AMOUNT);

        // Get all the orderDetailsList where amount is less than UPDATED_AMOUNT
        defaultOrderDetailsShouldBeFound("amount.lessThan=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllOrderDetailsByAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        orderDetailsRepository.saveAndFlush(orderDetails);

        // Get all the orderDetailsList where amount is greater than DEFAULT_AMOUNT
        defaultOrderDetailsShouldNotBeFound("amount.greaterThan=" + DEFAULT_AMOUNT);

        // Get all the orderDetailsList where amount is greater than SMALLER_AMOUNT
        defaultOrderDetailsShouldBeFound("amount.greaterThan=" + SMALLER_AMOUNT);
    }

    @Test
    @Transactional
    void getAllOrderDetailsByOrderIsEqualToSomething() throws Exception {
        Orders order;
        if (TestUtil.findAll(em, Orders.class).isEmpty()) {
            orderDetailsRepository.saveAndFlush(orderDetails);
            order = OrdersResourceIT.createEntity(em);
        } else {
            order = TestUtil.findAll(em, Orders.class).get(0);
        }
        em.persist(order);
        em.flush();
        orderDetails.setOrder(order);
        orderDetailsRepository.saveAndFlush(orderDetails);
        Long orderId = order.getId();

        // Get all the orderDetailsList where order equals to orderId
        defaultOrderDetailsShouldBeFound("orderId.equals=" + orderId);

        // Get all the orderDetailsList where order equals to (orderId + 1)
        defaultOrderDetailsShouldNotBeFound("orderId.equals=" + (orderId + 1));
    }

    @Test
    @Transactional
    void getAllOrderDetailsByProductIsEqualToSomething() throws Exception {
        Products product;
        if (TestUtil.findAll(em, Products.class).isEmpty()) {
            orderDetailsRepository.saveAndFlush(orderDetails);
            product = ProductsResourceIT.createEntity(em);
        } else {
            product = TestUtil.findAll(em, Products.class).get(0);
        }
        em.persist(product);
        em.flush();
        orderDetails.setProduct(product);
        orderDetailsRepository.saveAndFlush(orderDetails);
        Long productId = product.getId();

        // Get all the orderDetailsList where product equals to productId
        defaultOrderDetailsShouldBeFound("productId.equals=" + productId);

        // Get all the orderDetailsList where product equals to (productId + 1)
        defaultOrderDetailsShouldNotBeFound("productId.equals=" + (productId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultOrderDetailsShouldBeFound(String filter) throws Exception {
        restOrderDetailsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(orderDetails.getId().intValue())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.doubleValue())));

        // Check, that the count call also returns 1
        restOrderDetailsMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultOrderDetailsShouldNotBeFound(String filter) throws Exception {
        restOrderDetailsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restOrderDetailsMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingOrderDetails() throws Exception {
        // Get the orderDetails
        restOrderDetailsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingOrderDetails() throws Exception {
        // Initialize the database
        orderDetailsRepository.saveAndFlush(orderDetails);

        int databaseSizeBeforeUpdate = orderDetailsRepository.findAll().size();
        orderDetailsSearchRepository.save(orderDetails);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderDetailsSearchRepository.findAll());

        // Update the orderDetails
        OrderDetails updatedOrderDetails = orderDetailsRepository.findById(orderDetails.getId()).get();
        // Disconnect from session so that the updates on updatedOrderDetails are not directly saved in db
        em.detach(updatedOrderDetails);
        updatedOrderDetails.quantity(UPDATED_QUANTITY).amount(UPDATED_AMOUNT);

        restOrderDetailsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedOrderDetails.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedOrderDetails))
            )
            .andExpect(status().isOk());

        // Validate the OrderDetails in the database
        List<OrderDetails> orderDetailsList = orderDetailsRepository.findAll();
        assertThat(orderDetailsList).hasSize(databaseSizeBeforeUpdate);
        OrderDetails testOrderDetails = orderDetailsList.get(orderDetailsList.size() - 1);
        assertThat(testOrderDetails.getQuantity()).isEqualTo(UPDATED_QUANTITY);
        assertThat(testOrderDetails.getAmount()).isEqualTo(UPDATED_AMOUNT);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderDetailsSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<OrderDetails> orderDetailsSearchList = IterableUtils.toList(orderDetailsSearchRepository.findAll());
                OrderDetails testOrderDetailsSearch = orderDetailsSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testOrderDetailsSearch.getQuantity()).isEqualTo(UPDATED_QUANTITY);
                assertThat(testOrderDetailsSearch.getAmount()).isEqualTo(UPDATED_AMOUNT);
            });
    }

    @Test
    @Transactional
    void putNonExistingOrderDetails() throws Exception {
        int databaseSizeBeforeUpdate = orderDetailsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderDetailsSearchRepository.findAll());
        orderDetails.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrderDetailsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, orderDetails.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(orderDetails))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderDetails in the database
        List<OrderDetails> orderDetailsList = orderDetailsRepository.findAll();
        assertThat(orderDetailsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderDetailsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchOrderDetails() throws Exception {
        int databaseSizeBeforeUpdate = orderDetailsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderDetailsSearchRepository.findAll());
        orderDetails.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderDetailsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(orderDetails))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderDetails in the database
        List<OrderDetails> orderDetailsList = orderDetailsRepository.findAll();
        assertThat(orderDetailsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderDetailsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamOrderDetails() throws Exception {
        int databaseSizeBeforeUpdate = orderDetailsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderDetailsSearchRepository.findAll());
        orderDetails.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderDetailsMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(orderDetails)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the OrderDetails in the database
        List<OrderDetails> orderDetailsList = orderDetailsRepository.findAll();
        assertThat(orderDetailsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderDetailsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateOrderDetailsWithPatch() throws Exception {
        // Initialize the database
        orderDetailsRepository.saveAndFlush(orderDetails);

        int databaseSizeBeforeUpdate = orderDetailsRepository.findAll().size();

        // Update the orderDetails using partial update
        OrderDetails partialUpdatedOrderDetails = new OrderDetails();
        partialUpdatedOrderDetails.setId(orderDetails.getId());

        partialUpdatedOrderDetails.quantity(UPDATED_QUANTITY);

        restOrderDetailsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrderDetails.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOrderDetails))
            )
            .andExpect(status().isOk());

        // Validate the OrderDetails in the database
        List<OrderDetails> orderDetailsList = orderDetailsRepository.findAll();
        assertThat(orderDetailsList).hasSize(databaseSizeBeforeUpdate);
        OrderDetails testOrderDetails = orderDetailsList.get(orderDetailsList.size() - 1);
        assertThat(testOrderDetails.getQuantity()).isEqualTo(UPDATED_QUANTITY);
        assertThat(testOrderDetails.getAmount()).isEqualTo(DEFAULT_AMOUNT);
    }

    @Test
    @Transactional
    void fullUpdateOrderDetailsWithPatch() throws Exception {
        // Initialize the database
        orderDetailsRepository.saveAndFlush(orderDetails);

        int databaseSizeBeforeUpdate = orderDetailsRepository.findAll().size();

        // Update the orderDetails using partial update
        OrderDetails partialUpdatedOrderDetails = new OrderDetails();
        partialUpdatedOrderDetails.setId(orderDetails.getId());

        partialUpdatedOrderDetails.quantity(UPDATED_QUANTITY).amount(UPDATED_AMOUNT);

        restOrderDetailsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrderDetails.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOrderDetails))
            )
            .andExpect(status().isOk());

        // Validate the OrderDetails in the database
        List<OrderDetails> orderDetailsList = orderDetailsRepository.findAll();
        assertThat(orderDetailsList).hasSize(databaseSizeBeforeUpdate);
        OrderDetails testOrderDetails = orderDetailsList.get(orderDetailsList.size() - 1);
        assertThat(testOrderDetails.getQuantity()).isEqualTo(UPDATED_QUANTITY);
        assertThat(testOrderDetails.getAmount()).isEqualTo(UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void patchNonExistingOrderDetails() throws Exception {
        int databaseSizeBeforeUpdate = orderDetailsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderDetailsSearchRepository.findAll());
        orderDetails.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrderDetailsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, orderDetails.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(orderDetails))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderDetails in the database
        List<OrderDetails> orderDetailsList = orderDetailsRepository.findAll();
        assertThat(orderDetailsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderDetailsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchOrderDetails() throws Exception {
        int databaseSizeBeforeUpdate = orderDetailsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderDetailsSearchRepository.findAll());
        orderDetails.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderDetailsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(orderDetails))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderDetails in the database
        List<OrderDetails> orderDetailsList = orderDetailsRepository.findAll();
        assertThat(orderDetailsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderDetailsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamOrderDetails() throws Exception {
        int databaseSizeBeforeUpdate = orderDetailsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderDetailsSearchRepository.findAll());
        orderDetails.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderDetailsMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(orderDetails))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the OrderDetails in the database
        List<OrderDetails> orderDetailsList = orderDetailsRepository.findAll();
        assertThat(orderDetailsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderDetailsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteOrderDetails() throws Exception {
        // Initialize the database
        orderDetailsRepository.saveAndFlush(orderDetails);
        orderDetailsRepository.save(orderDetails);
        orderDetailsSearchRepository.save(orderDetails);

        int databaseSizeBeforeDelete = orderDetailsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderDetailsSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the orderDetails
        restOrderDetailsMockMvc
            .perform(delete(ENTITY_API_URL_ID, orderDetails.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<OrderDetails> orderDetailsList = orderDetailsRepository.findAll();
        assertThat(orderDetailsList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderDetailsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchOrderDetails() throws Exception {
        // Initialize the database
        orderDetails = orderDetailsRepository.saveAndFlush(orderDetails);
        orderDetailsSearchRepository.save(orderDetails);

        // Search the orderDetails
        restOrderDetailsMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + orderDetails.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(orderDetails.getId().intValue())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.doubleValue())));
    }
}
