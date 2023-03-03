package com.test.changes.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.test.changes.IntegrationTest;
import com.test.changes.domain.Addresses;
import com.test.changes.domain.DarazUsers;
import com.test.changes.domain.DarazUsers;
import com.test.changes.domain.OrderDelivery;
import com.test.changes.domain.Roles;
import com.test.changes.repository.DarazUsersRepository;
import com.test.changes.repository.search.DarazUsersSearchRepository;
import com.test.changes.service.DarazUsersService;
import com.test.changes.service.criteria.DarazUsersCriteria;
import java.util.ArrayList;
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
 * Integration tests for the {@link DarazUsersResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class DarazUsersResourceIT {

    private static final String DEFAULT_FULL_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FULL_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_PHONE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/daraz-users";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/daraz-users";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private DarazUsersRepository darazUsersRepository;

    @Mock
    private DarazUsersRepository darazUsersRepositoryMock;

    @Mock
    private DarazUsersService darazUsersServiceMock;

    @Autowired
    private DarazUsersSearchRepository darazUsersSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDarazUsersMockMvc;

    private DarazUsers darazUsers;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DarazUsers createEntity(EntityManager em) {
        DarazUsers darazUsers = new DarazUsers().fullName(DEFAULT_FULL_NAME).email(DEFAULT_EMAIL).phone(DEFAULT_PHONE);
        return darazUsers;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DarazUsers createUpdatedEntity(EntityManager em) {
        DarazUsers darazUsers = new DarazUsers().fullName(UPDATED_FULL_NAME).email(UPDATED_EMAIL).phone(UPDATED_PHONE);
        return darazUsers;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        darazUsersSearchRepository.deleteAll();
        assertThat(darazUsersSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        darazUsers = createEntity(em);
    }

    @Test
    @Transactional
    void createDarazUsers() throws Exception {
        int databaseSizeBeforeCreate = darazUsersRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(darazUsersSearchRepository.findAll());
        // Create the DarazUsers
        restDarazUsersMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(darazUsers)))
            .andExpect(status().isCreated());

        // Validate the DarazUsers in the database
        List<DarazUsers> darazUsersList = darazUsersRepository.findAll();
        assertThat(darazUsersList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(darazUsersSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        DarazUsers testDarazUsers = darazUsersList.get(darazUsersList.size() - 1);
        assertThat(testDarazUsers.getFullName()).isEqualTo(DEFAULT_FULL_NAME);
        assertThat(testDarazUsers.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testDarazUsers.getPhone()).isEqualTo(DEFAULT_PHONE);
    }

    @Test
    @Transactional
    void createDarazUsersWithExistingId() throws Exception {
        // Create the DarazUsers with an existing ID
        darazUsers.setId(1L);

        int databaseSizeBeforeCreate = darazUsersRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(darazUsersSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restDarazUsersMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(darazUsers)))
            .andExpect(status().isBadRequest());

        // Validate the DarazUsers in the database
        List<DarazUsers> darazUsersList = darazUsersRepository.findAll();
        assertThat(darazUsersList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(darazUsersSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkFullNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = darazUsersRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(darazUsersSearchRepository.findAll());
        // set the field null
        darazUsers.setFullName(null);

        // Create the DarazUsers, which fails.

        restDarazUsersMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(darazUsers)))
            .andExpect(status().isBadRequest());

        List<DarazUsers> darazUsersList = darazUsersRepository.findAll();
        assertThat(darazUsersList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(darazUsersSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkEmailIsRequired() throws Exception {
        int databaseSizeBeforeTest = darazUsersRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(darazUsersSearchRepository.findAll());
        // set the field null
        darazUsers.setEmail(null);

        // Create the DarazUsers, which fails.

        restDarazUsersMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(darazUsers)))
            .andExpect(status().isBadRequest());

        List<DarazUsers> darazUsersList = darazUsersRepository.findAll();
        assertThat(darazUsersList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(darazUsersSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkPhoneIsRequired() throws Exception {
        int databaseSizeBeforeTest = darazUsersRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(darazUsersSearchRepository.findAll());
        // set the field null
        darazUsers.setPhone(null);

        // Create the DarazUsers, which fails.

        restDarazUsersMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(darazUsers)))
            .andExpect(status().isBadRequest());

        List<DarazUsers> darazUsersList = darazUsersRepository.findAll();
        assertThat(darazUsersList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(darazUsersSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllDarazUsers() throws Exception {
        // Initialize the database
        darazUsersRepository.saveAndFlush(darazUsers);

        // Get all the darazUsersList
        restDarazUsersMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(darazUsers.getId().intValue())))
            .andExpect(jsonPath("$.[*].fullName").value(hasItem(DEFAULT_FULL_NAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDarazUsersWithEagerRelationshipsIsEnabled() throws Exception {
        when(darazUsersServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restDarazUsersMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(darazUsersServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDarazUsersWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(darazUsersServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restDarazUsersMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(darazUsersRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getDarazUsers() throws Exception {
        // Initialize the database
        darazUsersRepository.saveAndFlush(darazUsers);

        // Get the darazUsers
        restDarazUsersMockMvc
            .perform(get(ENTITY_API_URL_ID, darazUsers.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(darazUsers.getId().intValue()))
            .andExpect(jsonPath("$.fullName").value(DEFAULT_FULL_NAME))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE));
    }

    @Test
    @Transactional
    void getDarazUsersByIdFiltering() throws Exception {
        // Initialize the database
        darazUsersRepository.saveAndFlush(darazUsers);

        Long id = darazUsers.getId();

        defaultDarazUsersShouldBeFound("id.equals=" + id);
        defaultDarazUsersShouldNotBeFound("id.notEquals=" + id);

        defaultDarazUsersShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultDarazUsersShouldNotBeFound("id.greaterThan=" + id);

        defaultDarazUsersShouldBeFound("id.lessThanOrEqual=" + id);
        defaultDarazUsersShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllDarazUsersByFullNameIsEqualToSomething() throws Exception {
        // Initialize the database
        darazUsersRepository.saveAndFlush(darazUsers);

        // Get all the darazUsersList where fullName equals to DEFAULT_FULL_NAME
        defaultDarazUsersShouldBeFound("fullName.equals=" + DEFAULT_FULL_NAME);

        // Get all the darazUsersList where fullName equals to UPDATED_FULL_NAME
        defaultDarazUsersShouldNotBeFound("fullName.equals=" + UPDATED_FULL_NAME);
    }

    @Test
    @Transactional
    void getAllDarazUsersByFullNameIsInShouldWork() throws Exception {
        // Initialize the database
        darazUsersRepository.saveAndFlush(darazUsers);

        // Get all the darazUsersList where fullName in DEFAULT_FULL_NAME or UPDATED_FULL_NAME
        defaultDarazUsersShouldBeFound("fullName.in=" + DEFAULT_FULL_NAME + "," + UPDATED_FULL_NAME);

        // Get all the darazUsersList where fullName equals to UPDATED_FULL_NAME
        defaultDarazUsersShouldNotBeFound("fullName.in=" + UPDATED_FULL_NAME);
    }

    @Test
    @Transactional
    void getAllDarazUsersByFullNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        darazUsersRepository.saveAndFlush(darazUsers);

        // Get all the darazUsersList where fullName is not null
        defaultDarazUsersShouldBeFound("fullName.specified=true");

        // Get all the darazUsersList where fullName is null
        defaultDarazUsersShouldNotBeFound("fullName.specified=false");
    }

    @Test
    @Transactional
    void getAllDarazUsersByFullNameContainsSomething() throws Exception {
        // Initialize the database
        darazUsersRepository.saveAndFlush(darazUsers);

        // Get all the darazUsersList where fullName contains DEFAULT_FULL_NAME
        defaultDarazUsersShouldBeFound("fullName.contains=" + DEFAULT_FULL_NAME);

        // Get all the darazUsersList where fullName contains UPDATED_FULL_NAME
        defaultDarazUsersShouldNotBeFound("fullName.contains=" + UPDATED_FULL_NAME);
    }

    @Test
    @Transactional
    void getAllDarazUsersByFullNameNotContainsSomething() throws Exception {
        // Initialize the database
        darazUsersRepository.saveAndFlush(darazUsers);

        // Get all the darazUsersList where fullName does not contain DEFAULT_FULL_NAME
        defaultDarazUsersShouldNotBeFound("fullName.doesNotContain=" + DEFAULT_FULL_NAME);

        // Get all the darazUsersList where fullName does not contain UPDATED_FULL_NAME
        defaultDarazUsersShouldBeFound("fullName.doesNotContain=" + UPDATED_FULL_NAME);
    }

    @Test
    @Transactional
    void getAllDarazUsersByEmailIsEqualToSomething() throws Exception {
        // Initialize the database
        darazUsersRepository.saveAndFlush(darazUsers);

        // Get all the darazUsersList where email equals to DEFAULT_EMAIL
        defaultDarazUsersShouldBeFound("email.equals=" + DEFAULT_EMAIL);

        // Get all the darazUsersList where email equals to UPDATED_EMAIL
        defaultDarazUsersShouldNotBeFound("email.equals=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllDarazUsersByEmailIsInShouldWork() throws Exception {
        // Initialize the database
        darazUsersRepository.saveAndFlush(darazUsers);

        // Get all the darazUsersList where email in DEFAULT_EMAIL or UPDATED_EMAIL
        defaultDarazUsersShouldBeFound("email.in=" + DEFAULT_EMAIL + "," + UPDATED_EMAIL);

        // Get all the darazUsersList where email equals to UPDATED_EMAIL
        defaultDarazUsersShouldNotBeFound("email.in=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllDarazUsersByEmailIsNullOrNotNull() throws Exception {
        // Initialize the database
        darazUsersRepository.saveAndFlush(darazUsers);

        // Get all the darazUsersList where email is not null
        defaultDarazUsersShouldBeFound("email.specified=true");

        // Get all the darazUsersList where email is null
        defaultDarazUsersShouldNotBeFound("email.specified=false");
    }

    @Test
    @Transactional
    void getAllDarazUsersByEmailContainsSomething() throws Exception {
        // Initialize the database
        darazUsersRepository.saveAndFlush(darazUsers);

        // Get all the darazUsersList where email contains DEFAULT_EMAIL
        defaultDarazUsersShouldBeFound("email.contains=" + DEFAULT_EMAIL);

        // Get all the darazUsersList where email contains UPDATED_EMAIL
        defaultDarazUsersShouldNotBeFound("email.contains=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllDarazUsersByEmailNotContainsSomething() throws Exception {
        // Initialize the database
        darazUsersRepository.saveAndFlush(darazUsers);

        // Get all the darazUsersList where email does not contain DEFAULT_EMAIL
        defaultDarazUsersShouldNotBeFound("email.doesNotContain=" + DEFAULT_EMAIL);

        // Get all the darazUsersList where email does not contain UPDATED_EMAIL
        defaultDarazUsersShouldBeFound("email.doesNotContain=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllDarazUsersByPhoneIsEqualToSomething() throws Exception {
        // Initialize the database
        darazUsersRepository.saveAndFlush(darazUsers);

        // Get all the darazUsersList where phone equals to DEFAULT_PHONE
        defaultDarazUsersShouldBeFound("phone.equals=" + DEFAULT_PHONE);

        // Get all the darazUsersList where phone equals to UPDATED_PHONE
        defaultDarazUsersShouldNotBeFound("phone.equals=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllDarazUsersByPhoneIsInShouldWork() throws Exception {
        // Initialize the database
        darazUsersRepository.saveAndFlush(darazUsers);

        // Get all the darazUsersList where phone in DEFAULT_PHONE or UPDATED_PHONE
        defaultDarazUsersShouldBeFound("phone.in=" + DEFAULT_PHONE + "," + UPDATED_PHONE);

        // Get all the darazUsersList where phone equals to UPDATED_PHONE
        defaultDarazUsersShouldNotBeFound("phone.in=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllDarazUsersByPhoneIsNullOrNotNull() throws Exception {
        // Initialize the database
        darazUsersRepository.saveAndFlush(darazUsers);

        // Get all the darazUsersList where phone is not null
        defaultDarazUsersShouldBeFound("phone.specified=true");

        // Get all the darazUsersList where phone is null
        defaultDarazUsersShouldNotBeFound("phone.specified=false");
    }

    @Test
    @Transactional
    void getAllDarazUsersByPhoneContainsSomething() throws Exception {
        // Initialize the database
        darazUsersRepository.saveAndFlush(darazUsers);

        // Get all the darazUsersList where phone contains DEFAULT_PHONE
        defaultDarazUsersShouldBeFound("phone.contains=" + DEFAULT_PHONE);

        // Get all the darazUsersList where phone contains UPDATED_PHONE
        defaultDarazUsersShouldNotBeFound("phone.contains=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllDarazUsersByPhoneNotContainsSomething() throws Exception {
        // Initialize the database
        darazUsersRepository.saveAndFlush(darazUsers);

        // Get all the darazUsersList where phone does not contain DEFAULT_PHONE
        defaultDarazUsersShouldNotBeFound("phone.doesNotContain=" + DEFAULT_PHONE);

        // Get all the darazUsersList where phone does not contain UPDATED_PHONE
        defaultDarazUsersShouldBeFound("phone.doesNotContain=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllDarazUsersByManagerIsEqualToSomething() throws Exception {
        DarazUsers manager;
        if (TestUtil.findAll(em, DarazUsers.class).isEmpty()) {
            darazUsersRepository.saveAndFlush(darazUsers);
            manager = DarazUsersResourceIT.createEntity(em);
        } else {
            manager = TestUtil.findAll(em, DarazUsers.class).get(0);
        }
        em.persist(manager);
        em.flush();
        darazUsers.setManager(manager);
        darazUsersRepository.saveAndFlush(darazUsers);
        Long managerId = manager.getId();

        // Get all the darazUsersList where manager equals to managerId
        defaultDarazUsersShouldBeFound("managerId.equals=" + managerId);

        // Get all the darazUsersList where manager equals to (managerId + 1)
        defaultDarazUsersShouldNotBeFound("managerId.equals=" + (managerId + 1));
    }

    @Test
    @Transactional
    void getAllDarazUsersByRoleIsEqualToSomething() throws Exception {
        Roles role;
        if (TestUtil.findAll(em, Roles.class).isEmpty()) {
            darazUsersRepository.saveAndFlush(darazUsers);
            role = RolesResourceIT.createEntity(em);
        } else {
            role = TestUtil.findAll(em, Roles.class).get(0);
        }
        em.persist(role);
        em.flush();
        darazUsers.addRole(role);
        darazUsersRepository.saveAndFlush(darazUsers);
        Long roleId = role.getId();

        // Get all the darazUsersList where role equals to roleId
        defaultDarazUsersShouldBeFound("roleId.equals=" + roleId);

        // Get all the darazUsersList where role equals to (roleId + 1)
        defaultDarazUsersShouldNotBeFound("roleId.equals=" + (roleId + 1));
    }

    @Test
    @Transactional
    void getAllDarazUsersByAddressesUserIsEqualToSomething() throws Exception {
        Addresses addressesUser;
        if (TestUtil.findAll(em, Addresses.class).isEmpty()) {
            darazUsersRepository.saveAndFlush(darazUsers);
            addressesUser = AddressesResourceIT.createEntity(em);
        } else {
            addressesUser = TestUtil.findAll(em, Addresses.class).get(0);
        }
        em.persist(addressesUser);
        em.flush();
        darazUsers.addAddressesUser(addressesUser);
        darazUsersRepository.saveAndFlush(darazUsers);
        Long addressesUserId = addressesUser.getId();

        // Get all the darazUsersList where addressesUser equals to addressesUserId
        defaultDarazUsersShouldBeFound("addressesUserId.equals=" + addressesUserId);

        // Get all the darazUsersList where addressesUser equals to (addressesUserId + 1)
        defaultDarazUsersShouldNotBeFound("addressesUserId.equals=" + (addressesUserId + 1));
    }

    @Test
    @Transactional
    void getAllDarazUsersByDarazusersManagerIsEqualToSomething() throws Exception {
        DarazUsers darazusersManager;
        if (TestUtil.findAll(em, DarazUsers.class).isEmpty()) {
            darazUsersRepository.saveAndFlush(darazUsers);
            darazusersManager = DarazUsersResourceIT.createEntity(em);
        } else {
            darazusersManager = TestUtil.findAll(em, DarazUsers.class).get(0);
        }
        em.persist(darazusersManager);
        em.flush();
        darazUsers.addDarazusersManager(darazusersManager);
        darazUsersRepository.saveAndFlush(darazUsers);
        Long darazusersManagerId = darazusersManager.getId();

        // Get all the darazUsersList where darazusersManager equals to darazusersManagerId
        defaultDarazUsersShouldBeFound("darazusersManagerId.equals=" + darazusersManagerId);

        // Get all the darazUsersList where darazusersManager equals to (darazusersManagerId + 1)
        defaultDarazUsersShouldNotBeFound("darazusersManagerId.equals=" + (darazusersManagerId + 1));
    }

    @Test
    @Transactional
    void getAllDarazUsersByOrderdeliveryDeliverymanagerIsEqualToSomething() throws Exception {
        OrderDelivery orderdeliveryDeliverymanager;
        if (TestUtil.findAll(em, OrderDelivery.class).isEmpty()) {
            darazUsersRepository.saveAndFlush(darazUsers);
            orderdeliveryDeliverymanager = OrderDeliveryResourceIT.createEntity(em);
        } else {
            orderdeliveryDeliverymanager = TestUtil.findAll(em, OrderDelivery.class).get(0);
        }
        em.persist(orderdeliveryDeliverymanager);
        em.flush();
        darazUsers.addOrderdeliveryDeliverymanager(orderdeliveryDeliverymanager);
        darazUsersRepository.saveAndFlush(darazUsers);
        Long orderdeliveryDeliverymanagerId = orderdeliveryDeliverymanager.getId();

        // Get all the darazUsersList where orderdeliveryDeliverymanager equals to orderdeliveryDeliverymanagerId
        defaultDarazUsersShouldBeFound("orderdeliveryDeliverymanagerId.equals=" + orderdeliveryDeliverymanagerId);

        // Get all the darazUsersList where orderdeliveryDeliverymanager equals to (orderdeliveryDeliverymanagerId + 1)
        defaultDarazUsersShouldNotBeFound("orderdeliveryDeliverymanagerId.equals=" + (orderdeliveryDeliverymanagerId + 1));
    }

    @Test
    @Transactional
    void getAllDarazUsersByOrderdeliveryDeliveryboyIsEqualToSomething() throws Exception {
        OrderDelivery orderdeliveryDeliveryboy;
        if (TestUtil.findAll(em, OrderDelivery.class).isEmpty()) {
            darazUsersRepository.saveAndFlush(darazUsers);
            orderdeliveryDeliveryboy = OrderDeliveryResourceIT.createEntity(em);
        } else {
            orderdeliveryDeliveryboy = TestUtil.findAll(em, OrderDelivery.class).get(0);
        }
        em.persist(orderdeliveryDeliveryboy);
        em.flush();
        darazUsers.addOrderdeliveryDeliveryboy(orderdeliveryDeliveryboy);
        darazUsersRepository.saveAndFlush(darazUsers);
        Long orderdeliveryDeliveryboyId = orderdeliveryDeliveryboy.getId();

        // Get all the darazUsersList where orderdeliveryDeliveryboy equals to orderdeliveryDeliveryboyId
        defaultDarazUsersShouldBeFound("orderdeliveryDeliveryboyId.equals=" + orderdeliveryDeliveryboyId);

        // Get all the darazUsersList where orderdeliveryDeliveryboy equals to (orderdeliveryDeliveryboyId + 1)
        defaultDarazUsersShouldNotBeFound("orderdeliveryDeliveryboyId.equals=" + (orderdeliveryDeliveryboyId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultDarazUsersShouldBeFound(String filter) throws Exception {
        restDarazUsersMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(darazUsers.getId().intValue())))
            .andExpect(jsonPath("$.[*].fullName").value(hasItem(DEFAULT_FULL_NAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)));

        // Check, that the count call also returns 1
        restDarazUsersMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultDarazUsersShouldNotBeFound(String filter) throws Exception {
        restDarazUsersMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restDarazUsersMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingDarazUsers() throws Exception {
        // Get the darazUsers
        restDarazUsersMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDarazUsers() throws Exception {
        // Initialize the database
        darazUsersRepository.saveAndFlush(darazUsers);

        int databaseSizeBeforeUpdate = darazUsersRepository.findAll().size();
        darazUsersSearchRepository.save(darazUsers);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(darazUsersSearchRepository.findAll());

        // Update the darazUsers
        DarazUsers updatedDarazUsers = darazUsersRepository.findById(darazUsers.getId()).get();
        // Disconnect from session so that the updates on updatedDarazUsers are not directly saved in db
        em.detach(updatedDarazUsers);
        updatedDarazUsers.fullName(UPDATED_FULL_NAME).email(UPDATED_EMAIL).phone(UPDATED_PHONE);

        restDarazUsersMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedDarazUsers.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedDarazUsers))
            )
            .andExpect(status().isOk());

        // Validate the DarazUsers in the database
        List<DarazUsers> darazUsersList = darazUsersRepository.findAll();
        assertThat(darazUsersList).hasSize(databaseSizeBeforeUpdate);
        DarazUsers testDarazUsers = darazUsersList.get(darazUsersList.size() - 1);
        assertThat(testDarazUsers.getFullName()).isEqualTo(UPDATED_FULL_NAME);
        assertThat(testDarazUsers.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testDarazUsers.getPhone()).isEqualTo(UPDATED_PHONE);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(darazUsersSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<DarazUsers> darazUsersSearchList = IterableUtils.toList(darazUsersSearchRepository.findAll());
                DarazUsers testDarazUsersSearch = darazUsersSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testDarazUsersSearch.getFullName()).isEqualTo(UPDATED_FULL_NAME);
                assertThat(testDarazUsersSearch.getEmail()).isEqualTo(UPDATED_EMAIL);
                assertThat(testDarazUsersSearch.getPhone()).isEqualTo(UPDATED_PHONE);
            });
    }

    @Test
    @Transactional
    void putNonExistingDarazUsers() throws Exception {
        int databaseSizeBeforeUpdate = darazUsersRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(darazUsersSearchRepository.findAll());
        darazUsers.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDarazUsersMockMvc
            .perform(
                put(ENTITY_API_URL_ID, darazUsers.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(darazUsers))
            )
            .andExpect(status().isBadRequest());

        // Validate the DarazUsers in the database
        List<DarazUsers> darazUsersList = darazUsersRepository.findAll();
        assertThat(darazUsersList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(darazUsersSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchDarazUsers() throws Exception {
        int databaseSizeBeforeUpdate = darazUsersRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(darazUsersSearchRepository.findAll());
        darazUsers.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDarazUsersMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(darazUsers))
            )
            .andExpect(status().isBadRequest());

        // Validate the DarazUsers in the database
        List<DarazUsers> darazUsersList = darazUsersRepository.findAll();
        assertThat(darazUsersList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(darazUsersSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDarazUsers() throws Exception {
        int databaseSizeBeforeUpdate = darazUsersRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(darazUsersSearchRepository.findAll());
        darazUsers.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDarazUsersMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(darazUsers)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the DarazUsers in the database
        List<DarazUsers> darazUsersList = darazUsersRepository.findAll();
        assertThat(darazUsersList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(darazUsersSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateDarazUsersWithPatch() throws Exception {
        // Initialize the database
        darazUsersRepository.saveAndFlush(darazUsers);

        int databaseSizeBeforeUpdate = darazUsersRepository.findAll().size();

        // Update the darazUsers using partial update
        DarazUsers partialUpdatedDarazUsers = new DarazUsers();
        partialUpdatedDarazUsers.setId(darazUsers.getId());

        restDarazUsersMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDarazUsers.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDarazUsers))
            )
            .andExpect(status().isOk());

        // Validate the DarazUsers in the database
        List<DarazUsers> darazUsersList = darazUsersRepository.findAll();
        assertThat(darazUsersList).hasSize(databaseSizeBeforeUpdate);
        DarazUsers testDarazUsers = darazUsersList.get(darazUsersList.size() - 1);
        assertThat(testDarazUsers.getFullName()).isEqualTo(DEFAULT_FULL_NAME);
        assertThat(testDarazUsers.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testDarazUsers.getPhone()).isEqualTo(DEFAULT_PHONE);
    }

    @Test
    @Transactional
    void fullUpdateDarazUsersWithPatch() throws Exception {
        // Initialize the database
        darazUsersRepository.saveAndFlush(darazUsers);

        int databaseSizeBeforeUpdate = darazUsersRepository.findAll().size();

        // Update the darazUsers using partial update
        DarazUsers partialUpdatedDarazUsers = new DarazUsers();
        partialUpdatedDarazUsers.setId(darazUsers.getId());

        partialUpdatedDarazUsers.fullName(UPDATED_FULL_NAME).email(UPDATED_EMAIL).phone(UPDATED_PHONE);

        restDarazUsersMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDarazUsers.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDarazUsers))
            )
            .andExpect(status().isOk());

        // Validate the DarazUsers in the database
        List<DarazUsers> darazUsersList = darazUsersRepository.findAll();
        assertThat(darazUsersList).hasSize(databaseSizeBeforeUpdate);
        DarazUsers testDarazUsers = darazUsersList.get(darazUsersList.size() - 1);
        assertThat(testDarazUsers.getFullName()).isEqualTo(UPDATED_FULL_NAME);
        assertThat(testDarazUsers.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testDarazUsers.getPhone()).isEqualTo(UPDATED_PHONE);
    }

    @Test
    @Transactional
    void patchNonExistingDarazUsers() throws Exception {
        int databaseSizeBeforeUpdate = darazUsersRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(darazUsersSearchRepository.findAll());
        darazUsers.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDarazUsersMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, darazUsers.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(darazUsers))
            )
            .andExpect(status().isBadRequest());

        // Validate the DarazUsers in the database
        List<DarazUsers> darazUsersList = darazUsersRepository.findAll();
        assertThat(darazUsersList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(darazUsersSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDarazUsers() throws Exception {
        int databaseSizeBeforeUpdate = darazUsersRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(darazUsersSearchRepository.findAll());
        darazUsers.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDarazUsersMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(darazUsers))
            )
            .andExpect(status().isBadRequest());

        // Validate the DarazUsers in the database
        List<DarazUsers> darazUsersList = darazUsersRepository.findAll();
        assertThat(darazUsersList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(darazUsersSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDarazUsers() throws Exception {
        int databaseSizeBeforeUpdate = darazUsersRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(darazUsersSearchRepository.findAll());
        darazUsers.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDarazUsersMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(darazUsers))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the DarazUsers in the database
        List<DarazUsers> darazUsersList = darazUsersRepository.findAll();
        assertThat(darazUsersList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(darazUsersSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteDarazUsers() throws Exception {
        // Initialize the database
        darazUsersRepository.saveAndFlush(darazUsers);
        darazUsersRepository.save(darazUsers);
        darazUsersSearchRepository.save(darazUsers);

        int databaseSizeBeforeDelete = darazUsersRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(darazUsersSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the darazUsers
        restDarazUsersMockMvc
            .perform(delete(ENTITY_API_URL_ID, darazUsers.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<DarazUsers> darazUsersList = darazUsersRepository.findAll();
        assertThat(darazUsersList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(darazUsersSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchDarazUsers() throws Exception {
        // Initialize the database
        darazUsers = darazUsersRepository.saveAndFlush(darazUsers);
        darazUsersSearchRepository.save(darazUsers);

        // Search the darazUsers
        restDarazUsersMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + darazUsers.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(darazUsers.getId().intValue())))
            .andExpect(jsonPath("$.[*].fullName").value(hasItem(DEFAULT_FULL_NAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)));
    }
}
