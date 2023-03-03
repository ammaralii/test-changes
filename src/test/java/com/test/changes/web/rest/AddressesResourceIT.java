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
import com.test.changes.repository.AddressesRepository;
import com.test.changes.repository.search.AddressesSearchRepository;
import com.test.changes.service.criteria.AddressesCriteria;
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
 * Integration tests for the {@link AddressesResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AddressesResourceIT {

    private static final String DEFAULT_STREET = "AAAAAAAAAA";
    private static final String UPDATED_STREET = "BBBBBBBBBB";

    private static final String DEFAULT_CITY = "AAAAAAAAAA";
    private static final String UPDATED_CITY = "BBBBBBBBBB";

    private static final String DEFAULT_STATE = "AAAAAAAAAA";
    private static final String UPDATED_STATE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/addresses";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/addresses";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AddressesRepository addressesRepository;

    @Autowired
    private AddressesSearchRepository addressesSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAddressesMockMvc;

    private Addresses addresses;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Addresses createEntity(EntityManager em) {
        Addresses addresses = new Addresses().street(DEFAULT_STREET).city(DEFAULT_CITY).state(DEFAULT_STATE);
        // Add required entity
        DarazUsers darazUsers;
        if (TestUtil.findAll(em, DarazUsers.class).isEmpty()) {
            darazUsers = DarazUsersResourceIT.createEntity(em);
            em.persist(darazUsers);
            em.flush();
        } else {
            darazUsers = TestUtil.findAll(em, DarazUsers.class).get(0);
        }
        addresses.setUser(darazUsers);
        return addresses;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Addresses createUpdatedEntity(EntityManager em) {
        Addresses addresses = new Addresses().street(UPDATED_STREET).city(UPDATED_CITY).state(UPDATED_STATE);
        // Add required entity
        DarazUsers darazUsers;
        if (TestUtil.findAll(em, DarazUsers.class).isEmpty()) {
            darazUsers = DarazUsersResourceIT.createUpdatedEntity(em);
            em.persist(darazUsers);
            em.flush();
        } else {
            darazUsers = TestUtil.findAll(em, DarazUsers.class).get(0);
        }
        addresses.setUser(darazUsers);
        return addresses;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        addressesSearchRepository.deleteAll();
        assertThat(addressesSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        addresses = createEntity(em);
    }

    @Test
    @Transactional
    void createAddresses() throws Exception {
        int databaseSizeBeforeCreate = addressesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(addressesSearchRepository.findAll());
        // Create the Addresses
        restAddressesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(addresses)))
            .andExpect(status().isCreated());

        // Validate the Addresses in the database
        List<Addresses> addressesList = addressesRepository.findAll();
        assertThat(addressesList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(addressesSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Addresses testAddresses = addressesList.get(addressesList.size() - 1);
        assertThat(testAddresses.getStreet()).isEqualTo(DEFAULT_STREET);
        assertThat(testAddresses.getCity()).isEqualTo(DEFAULT_CITY);
        assertThat(testAddresses.getState()).isEqualTo(DEFAULT_STATE);
    }

    @Test
    @Transactional
    void createAddressesWithExistingId() throws Exception {
        // Create the Addresses with an existing ID
        addresses.setId(1L);

        int databaseSizeBeforeCreate = addressesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(addressesSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restAddressesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(addresses)))
            .andExpect(status().isBadRequest());

        // Validate the Addresses in the database
        List<Addresses> addressesList = addressesRepository.findAll();
        assertThat(addressesList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(addressesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkStreetIsRequired() throws Exception {
        int databaseSizeBeforeTest = addressesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(addressesSearchRepository.findAll());
        // set the field null
        addresses.setStreet(null);

        // Create the Addresses, which fails.

        restAddressesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(addresses)))
            .andExpect(status().isBadRequest());

        List<Addresses> addressesList = addressesRepository.findAll();
        assertThat(addressesList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(addressesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCityIsRequired() throws Exception {
        int databaseSizeBeforeTest = addressesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(addressesSearchRepository.findAll());
        // set the field null
        addresses.setCity(null);

        // Create the Addresses, which fails.

        restAddressesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(addresses)))
            .andExpect(status().isBadRequest());

        List<Addresses> addressesList = addressesRepository.findAll();
        assertThat(addressesList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(addressesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkStateIsRequired() throws Exception {
        int databaseSizeBeforeTest = addressesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(addressesSearchRepository.findAll());
        // set the field null
        addresses.setState(null);

        // Create the Addresses, which fails.

        restAddressesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(addresses)))
            .andExpect(status().isBadRequest());

        List<Addresses> addressesList = addressesRepository.findAll();
        assertThat(addressesList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(addressesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllAddresses() throws Exception {
        // Initialize the database
        addressesRepository.saveAndFlush(addresses);

        // Get all the addressesList
        restAddressesMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(addresses.getId().intValue())))
            .andExpect(jsonPath("$.[*].street").value(hasItem(DEFAULT_STREET)))
            .andExpect(jsonPath("$.[*].city").value(hasItem(DEFAULT_CITY)))
            .andExpect(jsonPath("$.[*].state").value(hasItem(DEFAULT_STATE)));
    }

    @Test
    @Transactional
    void getAddresses() throws Exception {
        // Initialize the database
        addressesRepository.saveAndFlush(addresses);

        // Get the addresses
        restAddressesMockMvc
            .perform(get(ENTITY_API_URL_ID, addresses.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(addresses.getId().intValue()))
            .andExpect(jsonPath("$.street").value(DEFAULT_STREET))
            .andExpect(jsonPath("$.city").value(DEFAULT_CITY))
            .andExpect(jsonPath("$.state").value(DEFAULT_STATE));
    }

    @Test
    @Transactional
    void getAddressesByIdFiltering() throws Exception {
        // Initialize the database
        addressesRepository.saveAndFlush(addresses);

        Long id = addresses.getId();

        defaultAddressesShouldBeFound("id.equals=" + id);
        defaultAddressesShouldNotBeFound("id.notEquals=" + id);

        defaultAddressesShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultAddressesShouldNotBeFound("id.greaterThan=" + id);

        defaultAddressesShouldBeFound("id.lessThanOrEqual=" + id);
        defaultAddressesShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllAddressesByStreetIsEqualToSomething() throws Exception {
        // Initialize the database
        addressesRepository.saveAndFlush(addresses);

        // Get all the addressesList where street equals to DEFAULT_STREET
        defaultAddressesShouldBeFound("street.equals=" + DEFAULT_STREET);

        // Get all the addressesList where street equals to UPDATED_STREET
        defaultAddressesShouldNotBeFound("street.equals=" + UPDATED_STREET);
    }

    @Test
    @Transactional
    void getAllAddressesByStreetIsInShouldWork() throws Exception {
        // Initialize the database
        addressesRepository.saveAndFlush(addresses);

        // Get all the addressesList where street in DEFAULT_STREET or UPDATED_STREET
        defaultAddressesShouldBeFound("street.in=" + DEFAULT_STREET + "," + UPDATED_STREET);

        // Get all the addressesList where street equals to UPDATED_STREET
        defaultAddressesShouldNotBeFound("street.in=" + UPDATED_STREET);
    }

    @Test
    @Transactional
    void getAllAddressesByStreetIsNullOrNotNull() throws Exception {
        // Initialize the database
        addressesRepository.saveAndFlush(addresses);

        // Get all the addressesList where street is not null
        defaultAddressesShouldBeFound("street.specified=true");

        // Get all the addressesList where street is null
        defaultAddressesShouldNotBeFound("street.specified=false");
    }

    @Test
    @Transactional
    void getAllAddressesByStreetContainsSomething() throws Exception {
        // Initialize the database
        addressesRepository.saveAndFlush(addresses);

        // Get all the addressesList where street contains DEFAULT_STREET
        defaultAddressesShouldBeFound("street.contains=" + DEFAULT_STREET);

        // Get all the addressesList where street contains UPDATED_STREET
        defaultAddressesShouldNotBeFound("street.contains=" + UPDATED_STREET);
    }

    @Test
    @Transactional
    void getAllAddressesByStreetNotContainsSomething() throws Exception {
        // Initialize the database
        addressesRepository.saveAndFlush(addresses);

        // Get all the addressesList where street does not contain DEFAULT_STREET
        defaultAddressesShouldNotBeFound("street.doesNotContain=" + DEFAULT_STREET);

        // Get all the addressesList where street does not contain UPDATED_STREET
        defaultAddressesShouldBeFound("street.doesNotContain=" + UPDATED_STREET);
    }

    @Test
    @Transactional
    void getAllAddressesByCityIsEqualToSomething() throws Exception {
        // Initialize the database
        addressesRepository.saveAndFlush(addresses);

        // Get all the addressesList where city equals to DEFAULT_CITY
        defaultAddressesShouldBeFound("city.equals=" + DEFAULT_CITY);

        // Get all the addressesList where city equals to UPDATED_CITY
        defaultAddressesShouldNotBeFound("city.equals=" + UPDATED_CITY);
    }

    @Test
    @Transactional
    void getAllAddressesByCityIsInShouldWork() throws Exception {
        // Initialize the database
        addressesRepository.saveAndFlush(addresses);

        // Get all the addressesList where city in DEFAULT_CITY or UPDATED_CITY
        defaultAddressesShouldBeFound("city.in=" + DEFAULT_CITY + "," + UPDATED_CITY);

        // Get all the addressesList where city equals to UPDATED_CITY
        defaultAddressesShouldNotBeFound("city.in=" + UPDATED_CITY);
    }

    @Test
    @Transactional
    void getAllAddressesByCityIsNullOrNotNull() throws Exception {
        // Initialize the database
        addressesRepository.saveAndFlush(addresses);

        // Get all the addressesList where city is not null
        defaultAddressesShouldBeFound("city.specified=true");

        // Get all the addressesList where city is null
        defaultAddressesShouldNotBeFound("city.specified=false");
    }

    @Test
    @Transactional
    void getAllAddressesByCityContainsSomething() throws Exception {
        // Initialize the database
        addressesRepository.saveAndFlush(addresses);

        // Get all the addressesList where city contains DEFAULT_CITY
        defaultAddressesShouldBeFound("city.contains=" + DEFAULT_CITY);

        // Get all the addressesList where city contains UPDATED_CITY
        defaultAddressesShouldNotBeFound("city.contains=" + UPDATED_CITY);
    }

    @Test
    @Transactional
    void getAllAddressesByCityNotContainsSomething() throws Exception {
        // Initialize the database
        addressesRepository.saveAndFlush(addresses);

        // Get all the addressesList where city does not contain DEFAULT_CITY
        defaultAddressesShouldNotBeFound("city.doesNotContain=" + DEFAULT_CITY);

        // Get all the addressesList where city does not contain UPDATED_CITY
        defaultAddressesShouldBeFound("city.doesNotContain=" + UPDATED_CITY);
    }

    @Test
    @Transactional
    void getAllAddressesByStateIsEqualToSomething() throws Exception {
        // Initialize the database
        addressesRepository.saveAndFlush(addresses);

        // Get all the addressesList where state equals to DEFAULT_STATE
        defaultAddressesShouldBeFound("state.equals=" + DEFAULT_STATE);

        // Get all the addressesList where state equals to UPDATED_STATE
        defaultAddressesShouldNotBeFound("state.equals=" + UPDATED_STATE);
    }

    @Test
    @Transactional
    void getAllAddressesByStateIsInShouldWork() throws Exception {
        // Initialize the database
        addressesRepository.saveAndFlush(addresses);

        // Get all the addressesList where state in DEFAULT_STATE or UPDATED_STATE
        defaultAddressesShouldBeFound("state.in=" + DEFAULT_STATE + "," + UPDATED_STATE);

        // Get all the addressesList where state equals to UPDATED_STATE
        defaultAddressesShouldNotBeFound("state.in=" + UPDATED_STATE);
    }

    @Test
    @Transactional
    void getAllAddressesByStateIsNullOrNotNull() throws Exception {
        // Initialize the database
        addressesRepository.saveAndFlush(addresses);

        // Get all the addressesList where state is not null
        defaultAddressesShouldBeFound("state.specified=true");

        // Get all the addressesList where state is null
        defaultAddressesShouldNotBeFound("state.specified=false");
    }

    @Test
    @Transactional
    void getAllAddressesByStateContainsSomething() throws Exception {
        // Initialize the database
        addressesRepository.saveAndFlush(addresses);

        // Get all the addressesList where state contains DEFAULT_STATE
        defaultAddressesShouldBeFound("state.contains=" + DEFAULT_STATE);

        // Get all the addressesList where state contains UPDATED_STATE
        defaultAddressesShouldNotBeFound("state.contains=" + UPDATED_STATE);
    }

    @Test
    @Transactional
    void getAllAddressesByStateNotContainsSomething() throws Exception {
        // Initialize the database
        addressesRepository.saveAndFlush(addresses);

        // Get all the addressesList where state does not contain DEFAULT_STATE
        defaultAddressesShouldNotBeFound("state.doesNotContain=" + DEFAULT_STATE);

        // Get all the addressesList where state does not contain UPDATED_STATE
        defaultAddressesShouldBeFound("state.doesNotContain=" + UPDATED_STATE);
    }

    @Test
    @Transactional
    void getAllAddressesByUserIsEqualToSomething() throws Exception {
        DarazUsers user;
        if (TestUtil.findAll(em, DarazUsers.class).isEmpty()) {
            addressesRepository.saveAndFlush(addresses);
            user = DarazUsersResourceIT.createEntity(em);
        } else {
            user = TestUtil.findAll(em, DarazUsers.class).get(0);
        }
        em.persist(user);
        em.flush();
        addresses.setUser(user);
        addressesRepository.saveAndFlush(addresses);
        Long userId = user.getId();

        // Get all the addressesList where user equals to userId
        defaultAddressesShouldBeFound("userId.equals=" + userId);

        // Get all the addressesList where user equals to (userId + 1)
        defaultAddressesShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultAddressesShouldBeFound(String filter) throws Exception {
        restAddressesMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(addresses.getId().intValue())))
            .andExpect(jsonPath("$.[*].street").value(hasItem(DEFAULT_STREET)))
            .andExpect(jsonPath("$.[*].city").value(hasItem(DEFAULT_CITY)))
            .andExpect(jsonPath("$.[*].state").value(hasItem(DEFAULT_STATE)));

        // Check, that the count call also returns 1
        restAddressesMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultAddressesShouldNotBeFound(String filter) throws Exception {
        restAddressesMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restAddressesMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingAddresses() throws Exception {
        // Get the addresses
        restAddressesMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAddresses() throws Exception {
        // Initialize the database
        addressesRepository.saveAndFlush(addresses);

        int databaseSizeBeforeUpdate = addressesRepository.findAll().size();
        addressesSearchRepository.save(addresses);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(addressesSearchRepository.findAll());

        // Update the addresses
        Addresses updatedAddresses = addressesRepository.findById(addresses.getId()).get();
        // Disconnect from session so that the updates on updatedAddresses are not directly saved in db
        em.detach(updatedAddresses);
        updatedAddresses.street(UPDATED_STREET).city(UPDATED_CITY).state(UPDATED_STATE);

        restAddressesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedAddresses.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedAddresses))
            )
            .andExpect(status().isOk());

        // Validate the Addresses in the database
        List<Addresses> addressesList = addressesRepository.findAll();
        assertThat(addressesList).hasSize(databaseSizeBeforeUpdate);
        Addresses testAddresses = addressesList.get(addressesList.size() - 1);
        assertThat(testAddresses.getStreet()).isEqualTo(UPDATED_STREET);
        assertThat(testAddresses.getCity()).isEqualTo(UPDATED_CITY);
        assertThat(testAddresses.getState()).isEqualTo(UPDATED_STATE);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(addressesSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Addresses> addressesSearchList = IterableUtils.toList(addressesSearchRepository.findAll());
                Addresses testAddressesSearch = addressesSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testAddressesSearch.getStreet()).isEqualTo(UPDATED_STREET);
                assertThat(testAddressesSearch.getCity()).isEqualTo(UPDATED_CITY);
                assertThat(testAddressesSearch.getState()).isEqualTo(UPDATED_STATE);
            });
    }

    @Test
    @Transactional
    void putNonExistingAddresses() throws Exception {
        int databaseSizeBeforeUpdate = addressesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(addressesSearchRepository.findAll());
        addresses.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAddressesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, addresses.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(addresses))
            )
            .andExpect(status().isBadRequest());

        // Validate the Addresses in the database
        List<Addresses> addressesList = addressesRepository.findAll();
        assertThat(addressesList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(addressesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchAddresses() throws Exception {
        int databaseSizeBeforeUpdate = addressesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(addressesSearchRepository.findAll());
        addresses.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAddressesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(addresses))
            )
            .andExpect(status().isBadRequest());

        // Validate the Addresses in the database
        List<Addresses> addressesList = addressesRepository.findAll();
        assertThat(addressesList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(addressesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAddresses() throws Exception {
        int databaseSizeBeforeUpdate = addressesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(addressesSearchRepository.findAll());
        addresses.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAddressesMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(addresses)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Addresses in the database
        List<Addresses> addressesList = addressesRepository.findAll();
        assertThat(addressesList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(addressesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateAddressesWithPatch() throws Exception {
        // Initialize the database
        addressesRepository.saveAndFlush(addresses);

        int databaseSizeBeforeUpdate = addressesRepository.findAll().size();

        // Update the addresses using partial update
        Addresses partialUpdatedAddresses = new Addresses();
        partialUpdatedAddresses.setId(addresses.getId());

        partialUpdatedAddresses.street(UPDATED_STREET).state(UPDATED_STATE);

        restAddressesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAddresses.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAddresses))
            )
            .andExpect(status().isOk());

        // Validate the Addresses in the database
        List<Addresses> addressesList = addressesRepository.findAll();
        assertThat(addressesList).hasSize(databaseSizeBeforeUpdate);
        Addresses testAddresses = addressesList.get(addressesList.size() - 1);
        assertThat(testAddresses.getStreet()).isEqualTo(UPDATED_STREET);
        assertThat(testAddresses.getCity()).isEqualTo(DEFAULT_CITY);
        assertThat(testAddresses.getState()).isEqualTo(UPDATED_STATE);
    }

    @Test
    @Transactional
    void fullUpdateAddressesWithPatch() throws Exception {
        // Initialize the database
        addressesRepository.saveAndFlush(addresses);

        int databaseSizeBeforeUpdate = addressesRepository.findAll().size();

        // Update the addresses using partial update
        Addresses partialUpdatedAddresses = new Addresses();
        partialUpdatedAddresses.setId(addresses.getId());

        partialUpdatedAddresses.street(UPDATED_STREET).city(UPDATED_CITY).state(UPDATED_STATE);

        restAddressesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAddresses.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAddresses))
            )
            .andExpect(status().isOk());

        // Validate the Addresses in the database
        List<Addresses> addressesList = addressesRepository.findAll();
        assertThat(addressesList).hasSize(databaseSizeBeforeUpdate);
        Addresses testAddresses = addressesList.get(addressesList.size() - 1);
        assertThat(testAddresses.getStreet()).isEqualTo(UPDATED_STREET);
        assertThat(testAddresses.getCity()).isEqualTo(UPDATED_CITY);
        assertThat(testAddresses.getState()).isEqualTo(UPDATED_STATE);
    }

    @Test
    @Transactional
    void patchNonExistingAddresses() throws Exception {
        int databaseSizeBeforeUpdate = addressesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(addressesSearchRepository.findAll());
        addresses.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAddressesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, addresses.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(addresses))
            )
            .andExpect(status().isBadRequest());

        // Validate the Addresses in the database
        List<Addresses> addressesList = addressesRepository.findAll();
        assertThat(addressesList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(addressesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAddresses() throws Exception {
        int databaseSizeBeforeUpdate = addressesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(addressesSearchRepository.findAll());
        addresses.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAddressesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(addresses))
            )
            .andExpect(status().isBadRequest());

        // Validate the Addresses in the database
        List<Addresses> addressesList = addressesRepository.findAll();
        assertThat(addressesList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(addressesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAddresses() throws Exception {
        int databaseSizeBeforeUpdate = addressesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(addressesSearchRepository.findAll());
        addresses.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAddressesMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(addresses))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Addresses in the database
        List<Addresses> addressesList = addressesRepository.findAll();
        assertThat(addressesList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(addressesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteAddresses() throws Exception {
        // Initialize the database
        addressesRepository.saveAndFlush(addresses);
        addressesRepository.save(addresses);
        addressesSearchRepository.save(addresses);

        int databaseSizeBeforeDelete = addressesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(addressesSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the addresses
        restAddressesMockMvc
            .perform(delete(ENTITY_API_URL_ID, addresses.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Addresses> addressesList = addressesRepository.findAll();
        assertThat(addressesList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(addressesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchAddresses() throws Exception {
        // Initialize the database
        addresses = addressesRepository.saveAndFlush(addresses);
        addressesSearchRepository.save(addresses);

        // Search the addresses
        restAddressesMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + addresses.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(addresses.getId().intValue())))
            .andExpect(jsonPath("$.[*].street").value(hasItem(DEFAULT_STREET)))
            .andExpect(jsonPath("$.[*].city").value(hasItem(DEFAULT_CITY)))
            .andExpect(jsonPath("$.[*].state").value(hasItem(DEFAULT_STATE)));
    }
}
