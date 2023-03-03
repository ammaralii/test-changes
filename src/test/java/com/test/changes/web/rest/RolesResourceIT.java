package com.test.changes.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.test.changes.IntegrationTest;
import com.test.changes.domain.DarazUsers;
import com.test.changes.domain.Roles;
import com.test.changes.repository.RolesRepository;
import com.test.changes.repository.search.RolesSearchRepository;
import com.test.changes.service.criteria.RolesCriteria;
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
 * Integration tests for the {@link RolesResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class RolesResourceIT {

    private static final Integer DEFAULT_ROLE_PR_ID = 1;
    private static final Integer UPDATED_ROLE_PR_ID = 2;
    private static final Integer SMALLER_ROLE_PR_ID = 1 - 1;

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/roles";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/roles";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private RolesSearchRepository rolesSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRolesMockMvc;

    private Roles roles;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Roles createEntity(EntityManager em) {
        Roles roles = new Roles().rolePrId(DEFAULT_ROLE_PR_ID).name(DEFAULT_NAME);
        return roles;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Roles createUpdatedEntity(EntityManager em) {
        Roles roles = new Roles().rolePrId(UPDATED_ROLE_PR_ID).name(UPDATED_NAME);
        return roles;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        rolesSearchRepository.deleteAll();
        assertThat(rolesSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        roles = createEntity(em);
    }

    @Test
    @Transactional
    void createRoles() throws Exception {
        int databaseSizeBeforeCreate = rolesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(rolesSearchRepository.findAll());
        // Create the Roles
        restRolesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(roles)))
            .andExpect(status().isCreated());

        // Validate the Roles in the database
        List<Roles> rolesList = rolesRepository.findAll();
        assertThat(rolesList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(rolesSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Roles testRoles = rolesList.get(rolesList.size() - 1);
        assertThat(testRoles.getRolePrId()).isEqualTo(DEFAULT_ROLE_PR_ID);
        assertThat(testRoles.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    void createRolesWithExistingId() throws Exception {
        // Create the Roles with an existing ID
        roles.setId(1L);

        int databaseSizeBeforeCreate = rolesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(rolesSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restRolesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(roles)))
            .andExpect(status().isBadRequest());

        // Validate the Roles in the database
        List<Roles> rolesList = rolesRepository.findAll();
        assertThat(rolesList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(rolesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkRolePrIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = rolesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(rolesSearchRepository.findAll());
        // set the field null
        roles.setRolePrId(null);

        // Create the Roles, which fails.

        restRolesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(roles)))
            .andExpect(status().isBadRequest());

        List<Roles> rolesList = rolesRepository.findAll();
        assertThat(rolesList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(rolesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = rolesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(rolesSearchRepository.findAll());
        // set the field null
        roles.setName(null);

        // Create the Roles, which fails.

        restRolesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(roles)))
            .andExpect(status().isBadRequest());

        List<Roles> rolesList = rolesRepository.findAll();
        assertThat(rolesList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(rolesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllRoles() throws Exception {
        // Initialize the database
        rolesRepository.saveAndFlush(roles);

        // Get all the rolesList
        restRolesMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(roles.getId().intValue())))
            .andExpect(jsonPath("$.[*].rolePrId").value(hasItem(DEFAULT_ROLE_PR_ID)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }

    @Test
    @Transactional
    void getRoles() throws Exception {
        // Initialize the database
        rolesRepository.saveAndFlush(roles);

        // Get the roles
        restRolesMockMvc
            .perform(get(ENTITY_API_URL_ID, roles.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(roles.getId().intValue()))
            .andExpect(jsonPath("$.rolePrId").value(DEFAULT_ROLE_PR_ID))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }

    @Test
    @Transactional
    void getRolesByIdFiltering() throws Exception {
        // Initialize the database
        rolesRepository.saveAndFlush(roles);

        Long id = roles.getId();

        defaultRolesShouldBeFound("id.equals=" + id);
        defaultRolesShouldNotBeFound("id.notEquals=" + id);

        defaultRolesShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultRolesShouldNotBeFound("id.greaterThan=" + id);

        defaultRolesShouldBeFound("id.lessThanOrEqual=" + id);
        defaultRolesShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllRolesByRolePrIdIsEqualToSomething() throws Exception {
        // Initialize the database
        rolesRepository.saveAndFlush(roles);

        // Get all the rolesList where rolePrId equals to DEFAULT_ROLE_PR_ID
        defaultRolesShouldBeFound("rolePrId.equals=" + DEFAULT_ROLE_PR_ID);

        // Get all the rolesList where rolePrId equals to UPDATED_ROLE_PR_ID
        defaultRolesShouldNotBeFound("rolePrId.equals=" + UPDATED_ROLE_PR_ID);
    }

    @Test
    @Transactional
    void getAllRolesByRolePrIdIsInShouldWork() throws Exception {
        // Initialize the database
        rolesRepository.saveAndFlush(roles);

        // Get all the rolesList where rolePrId in DEFAULT_ROLE_PR_ID or UPDATED_ROLE_PR_ID
        defaultRolesShouldBeFound("rolePrId.in=" + DEFAULT_ROLE_PR_ID + "," + UPDATED_ROLE_PR_ID);

        // Get all the rolesList where rolePrId equals to UPDATED_ROLE_PR_ID
        defaultRolesShouldNotBeFound("rolePrId.in=" + UPDATED_ROLE_PR_ID);
    }

    @Test
    @Transactional
    void getAllRolesByRolePrIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        rolesRepository.saveAndFlush(roles);

        // Get all the rolesList where rolePrId is not null
        defaultRolesShouldBeFound("rolePrId.specified=true");

        // Get all the rolesList where rolePrId is null
        defaultRolesShouldNotBeFound("rolePrId.specified=false");
    }

    @Test
    @Transactional
    void getAllRolesByRolePrIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        rolesRepository.saveAndFlush(roles);

        // Get all the rolesList where rolePrId is greater than or equal to DEFAULT_ROLE_PR_ID
        defaultRolesShouldBeFound("rolePrId.greaterThanOrEqual=" + DEFAULT_ROLE_PR_ID);

        // Get all the rolesList where rolePrId is greater than or equal to UPDATED_ROLE_PR_ID
        defaultRolesShouldNotBeFound("rolePrId.greaterThanOrEqual=" + UPDATED_ROLE_PR_ID);
    }

    @Test
    @Transactional
    void getAllRolesByRolePrIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        rolesRepository.saveAndFlush(roles);

        // Get all the rolesList where rolePrId is less than or equal to DEFAULT_ROLE_PR_ID
        defaultRolesShouldBeFound("rolePrId.lessThanOrEqual=" + DEFAULT_ROLE_PR_ID);

        // Get all the rolesList where rolePrId is less than or equal to SMALLER_ROLE_PR_ID
        defaultRolesShouldNotBeFound("rolePrId.lessThanOrEqual=" + SMALLER_ROLE_PR_ID);
    }

    @Test
    @Transactional
    void getAllRolesByRolePrIdIsLessThanSomething() throws Exception {
        // Initialize the database
        rolesRepository.saveAndFlush(roles);

        // Get all the rolesList where rolePrId is less than DEFAULT_ROLE_PR_ID
        defaultRolesShouldNotBeFound("rolePrId.lessThan=" + DEFAULT_ROLE_PR_ID);

        // Get all the rolesList where rolePrId is less than UPDATED_ROLE_PR_ID
        defaultRolesShouldBeFound("rolePrId.lessThan=" + UPDATED_ROLE_PR_ID);
    }

    @Test
    @Transactional
    void getAllRolesByRolePrIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        rolesRepository.saveAndFlush(roles);

        // Get all the rolesList where rolePrId is greater than DEFAULT_ROLE_PR_ID
        defaultRolesShouldNotBeFound("rolePrId.greaterThan=" + DEFAULT_ROLE_PR_ID);

        // Get all the rolesList where rolePrId is greater than SMALLER_ROLE_PR_ID
        defaultRolesShouldBeFound("rolePrId.greaterThan=" + SMALLER_ROLE_PR_ID);
    }

    @Test
    @Transactional
    void getAllRolesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        rolesRepository.saveAndFlush(roles);

        // Get all the rolesList where name equals to DEFAULT_NAME
        defaultRolesShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the rolesList where name equals to UPDATED_NAME
        defaultRolesShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllRolesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        rolesRepository.saveAndFlush(roles);

        // Get all the rolesList where name in DEFAULT_NAME or UPDATED_NAME
        defaultRolesShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the rolesList where name equals to UPDATED_NAME
        defaultRolesShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllRolesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        rolesRepository.saveAndFlush(roles);

        // Get all the rolesList where name is not null
        defaultRolesShouldBeFound("name.specified=true");

        // Get all the rolesList where name is null
        defaultRolesShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllRolesByNameContainsSomething() throws Exception {
        // Initialize the database
        rolesRepository.saveAndFlush(roles);

        // Get all the rolesList where name contains DEFAULT_NAME
        defaultRolesShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the rolesList where name contains UPDATED_NAME
        defaultRolesShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllRolesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        rolesRepository.saveAndFlush(roles);

        // Get all the rolesList where name does not contain DEFAULT_NAME
        defaultRolesShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the rolesList where name does not contain UPDATED_NAME
        defaultRolesShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllRolesByUserIsEqualToSomething() throws Exception {
        DarazUsers user;
        if (TestUtil.findAll(em, DarazUsers.class).isEmpty()) {
            rolesRepository.saveAndFlush(roles);
            user = DarazUsersResourceIT.createEntity(em);
        } else {
            user = TestUtil.findAll(em, DarazUsers.class).get(0);
        }
        em.persist(user);
        em.flush();
        roles.addUser(user);
        rolesRepository.saveAndFlush(roles);
        Long userId = user.getId();

        // Get all the rolesList where user equals to userId
        defaultRolesShouldBeFound("userId.equals=" + userId);

        // Get all the rolesList where user equals to (userId + 1)
        defaultRolesShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultRolesShouldBeFound(String filter) throws Exception {
        restRolesMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(roles.getId().intValue())))
            .andExpect(jsonPath("$.[*].rolePrId").value(hasItem(DEFAULT_ROLE_PR_ID)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));

        // Check, that the count call also returns 1
        restRolesMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultRolesShouldNotBeFound(String filter) throws Exception {
        restRolesMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restRolesMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingRoles() throws Exception {
        // Get the roles
        restRolesMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingRoles() throws Exception {
        // Initialize the database
        rolesRepository.saveAndFlush(roles);

        int databaseSizeBeforeUpdate = rolesRepository.findAll().size();
        rolesSearchRepository.save(roles);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(rolesSearchRepository.findAll());

        // Update the roles
        Roles updatedRoles = rolesRepository.findById(roles.getId()).get();
        // Disconnect from session so that the updates on updatedRoles are not directly saved in db
        em.detach(updatedRoles);
        updatedRoles.rolePrId(UPDATED_ROLE_PR_ID).name(UPDATED_NAME);

        restRolesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedRoles.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedRoles))
            )
            .andExpect(status().isOk());

        // Validate the Roles in the database
        List<Roles> rolesList = rolesRepository.findAll();
        assertThat(rolesList).hasSize(databaseSizeBeforeUpdate);
        Roles testRoles = rolesList.get(rolesList.size() - 1);
        assertThat(testRoles.getRolePrId()).isEqualTo(UPDATED_ROLE_PR_ID);
        assertThat(testRoles.getName()).isEqualTo(UPDATED_NAME);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(rolesSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Roles> rolesSearchList = IterableUtils.toList(rolesSearchRepository.findAll());
                Roles testRolesSearch = rolesSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testRolesSearch.getRolePrId()).isEqualTo(UPDATED_ROLE_PR_ID);
                assertThat(testRolesSearch.getName()).isEqualTo(UPDATED_NAME);
            });
    }

    @Test
    @Transactional
    void putNonExistingRoles() throws Exception {
        int databaseSizeBeforeUpdate = rolesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(rolesSearchRepository.findAll());
        roles.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRolesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, roles.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(roles))
            )
            .andExpect(status().isBadRequest());

        // Validate the Roles in the database
        List<Roles> rolesList = rolesRepository.findAll();
        assertThat(rolesList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(rolesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchRoles() throws Exception {
        int databaseSizeBeforeUpdate = rolesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(rolesSearchRepository.findAll());
        roles.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRolesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(roles))
            )
            .andExpect(status().isBadRequest());

        // Validate the Roles in the database
        List<Roles> rolesList = rolesRepository.findAll();
        assertThat(rolesList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(rolesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRoles() throws Exception {
        int databaseSizeBeforeUpdate = rolesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(rolesSearchRepository.findAll());
        roles.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRolesMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(roles)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Roles in the database
        List<Roles> rolesList = rolesRepository.findAll();
        assertThat(rolesList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(rolesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateRolesWithPatch() throws Exception {
        // Initialize the database
        rolesRepository.saveAndFlush(roles);

        int databaseSizeBeforeUpdate = rolesRepository.findAll().size();

        // Update the roles using partial update
        Roles partialUpdatedRoles = new Roles();
        partialUpdatedRoles.setId(roles.getId());

        restRolesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRoles.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRoles))
            )
            .andExpect(status().isOk());

        // Validate the Roles in the database
        List<Roles> rolesList = rolesRepository.findAll();
        assertThat(rolesList).hasSize(databaseSizeBeforeUpdate);
        Roles testRoles = rolesList.get(rolesList.size() - 1);
        assertThat(testRoles.getRolePrId()).isEqualTo(DEFAULT_ROLE_PR_ID);
        assertThat(testRoles.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    void fullUpdateRolesWithPatch() throws Exception {
        // Initialize the database
        rolesRepository.saveAndFlush(roles);

        int databaseSizeBeforeUpdate = rolesRepository.findAll().size();

        // Update the roles using partial update
        Roles partialUpdatedRoles = new Roles();
        partialUpdatedRoles.setId(roles.getId());

        partialUpdatedRoles.rolePrId(UPDATED_ROLE_PR_ID).name(UPDATED_NAME);

        restRolesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRoles.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRoles))
            )
            .andExpect(status().isOk());

        // Validate the Roles in the database
        List<Roles> rolesList = rolesRepository.findAll();
        assertThat(rolesList).hasSize(databaseSizeBeforeUpdate);
        Roles testRoles = rolesList.get(rolesList.size() - 1);
        assertThat(testRoles.getRolePrId()).isEqualTo(UPDATED_ROLE_PR_ID);
        assertThat(testRoles.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void patchNonExistingRoles() throws Exception {
        int databaseSizeBeforeUpdate = rolesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(rolesSearchRepository.findAll());
        roles.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRolesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, roles.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(roles))
            )
            .andExpect(status().isBadRequest());

        // Validate the Roles in the database
        List<Roles> rolesList = rolesRepository.findAll();
        assertThat(rolesList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(rolesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRoles() throws Exception {
        int databaseSizeBeforeUpdate = rolesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(rolesSearchRepository.findAll());
        roles.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRolesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(roles))
            )
            .andExpect(status().isBadRequest());

        // Validate the Roles in the database
        List<Roles> rolesList = rolesRepository.findAll();
        assertThat(rolesList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(rolesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRoles() throws Exception {
        int databaseSizeBeforeUpdate = rolesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(rolesSearchRepository.findAll());
        roles.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRolesMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(roles)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Roles in the database
        List<Roles> rolesList = rolesRepository.findAll();
        assertThat(rolesList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(rolesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteRoles() throws Exception {
        // Initialize the database
        rolesRepository.saveAndFlush(roles);
        rolesRepository.save(roles);
        rolesSearchRepository.save(roles);

        int databaseSizeBeforeDelete = rolesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(rolesSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the roles
        restRolesMockMvc
            .perform(delete(ENTITY_API_URL_ID, roles.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Roles> rolesList = rolesRepository.findAll();
        assertThat(rolesList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(rolesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchRoles() throws Exception {
        // Initialize the database
        roles = rolesRepository.saveAndFlush(roles);
        rolesSearchRepository.save(roles);

        // Search the roles
        restRolesMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + roles.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(roles.getId().intValue())))
            .andExpect(jsonPath("$.[*].rolePrId").value(hasItem(DEFAULT_ROLE_PR_ID)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }
}
