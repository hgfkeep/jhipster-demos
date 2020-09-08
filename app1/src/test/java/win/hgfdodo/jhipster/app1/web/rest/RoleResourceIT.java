package win.hgfdodo.jhipster.app1.web.rest;

import win.hgfdodo.jhipster.app1.App1App;
import win.hgfdodo.jhipster.app1.domain.Role;
import win.hgfdodo.jhipster.app1.repository.RoleRepository;
import win.hgfdodo.jhipster.app1.service.RoleService;
import win.hgfdodo.jhipster.app1.service.dto.RoleDTO;
import win.hgfdodo.jhipster.app1.service.mapper.RoleMapper;
import win.hgfdodo.jhipster.app1.service.dto.RoleCriteria;
import win.hgfdodo.jhipster.app1.service.RoleQueryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link RoleResource} REST controller.
 */
@SpringBootTest(classes = App1App.class)
@AutoConfigureMockMvc
@WithMockUser
public class RoleResourceIT {

    private static final String DEFAULT_ROLE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_ROLE_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_ADDITIONAL = "AAAAAAAAAA";
    private static final String UPDATED_ADDITIONAL = "BBBBBBBBBB";

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RoleService roleService;

    @Autowired
    private RoleQueryService roleQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRoleMockMvc;

    private Role role;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Role createEntity(EntityManager em) {
        Role role = new Role()
            .roleName(DEFAULT_ROLE_NAME)
            .additional(DEFAULT_ADDITIONAL);
        return role;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Role createUpdatedEntity(EntityManager em) {
        Role role = new Role()
            .roleName(UPDATED_ROLE_NAME)
            .additional(UPDATED_ADDITIONAL);
        return role;
    }

    @BeforeEach
    public void initTest() {
        role = createEntity(em);
    }

    @Test
    @Transactional
    public void createRole() throws Exception {
        int databaseSizeBeforeCreate = roleRepository.findAll().size();
        // Create the Role
        RoleDTO roleDTO = roleMapper.toDto(role);
        restRoleMockMvc.perform(post("/api/roles").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(roleDTO)))
            .andExpect(status().isCreated());

        // Validate the Role in the database
        List<Role> roleList = roleRepository.findAll();
        assertThat(roleList).hasSize(databaseSizeBeforeCreate + 1);
        Role testRole = roleList.get(roleList.size() - 1);
        assertThat(testRole.getRoleName()).isEqualTo(DEFAULT_ROLE_NAME);
        assertThat(testRole.getAdditional()).isEqualTo(DEFAULT_ADDITIONAL);
    }

    @Test
    @Transactional
    public void createRoleWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = roleRepository.findAll().size();

        // Create the Role with an existing ID
        role.setId(1L);
        RoleDTO roleDTO = roleMapper.toDto(role);

        // An entity with an existing ID cannot be created, so this API call must fail
        restRoleMockMvc.perform(post("/api/roles").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(roleDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Role in the database
        List<Role> roleList = roleRepository.findAll();
        assertThat(roleList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkRoleNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = roleRepository.findAll().size();
        // set the field null
        role.setRoleName(null);

        // Create the Role, which fails.
        RoleDTO roleDTO = roleMapper.toDto(role);


        restRoleMockMvc.perform(post("/api/roles").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(roleDTO)))
            .andExpect(status().isBadRequest());

        List<Role> roleList = roleRepository.findAll();
        assertThat(roleList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllRoles() throws Exception {
        // Initialize the database
        roleRepository.saveAndFlush(role);

        // Get all the roleList
        restRoleMockMvc.perform(get("/api/roles?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(role.getId().intValue())))
            .andExpect(jsonPath("$.[*].roleName").value(hasItem(DEFAULT_ROLE_NAME)))
            .andExpect(jsonPath("$.[*].additional").value(hasItem(DEFAULT_ADDITIONAL)));
    }
    
    @Test
    @Transactional
    public void getRole() throws Exception {
        // Initialize the database
        roleRepository.saveAndFlush(role);

        // Get the role
        restRoleMockMvc.perform(get("/api/roles/{id}", role.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(role.getId().intValue()))
            .andExpect(jsonPath("$.roleName").value(DEFAULT_ROLE_NAME))
            .andExpect(jsonPath("$.additional").value(DEFAULT_ADDITIONAL));
    }


    @Test
    @Transactional
    public void getRolesByIdFiltering() throws Exception {
        // Initialize the database
        roleRepository.saveAndFlush(role);

        Long id = role.getId();

        defaultRoleShouldBeFound("id.equals=" + id);
        defaultRoleShouldNotBeFound("id.notEquals=" + id);

        defaultRoleShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultRoleShouldNotBeFound("id.greaterThan=" + id);

        defaultRoleShouldBeFound("id.lessThanOrEqual=" + id);
        defaultRoleShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllRolesByRoleNameIsEqualToSomething() throws Exception {
        // Initialize the database
        roleRepository.saveAndFlush(role);

        // Get all the roleList where roleName equals to DEFAULT_ROLE_NAME
        defaultRoleShouldBeFound("roleName.equals=" + DEFAULT_ROLE_NAME);

        // Get all the roleList where roleName equals to UPDATED_ROLE_NAME
        defaultRoleShouldNotBeFound("roleName.equals=" + UPDATED_ROLE_NAME);
    }

    @Test
    @Transactional
    public void getAllRolesByRoleNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        roleRepository.saveAndFlush(role);

        // Get all the roleList where roleName not equals to DEFAULT_ROLE_NAME
        defaultRoleShouldNotBeFound("roleName.notEquals=" + DEFAULT_ROLE_NAME);

        // Get all the roleList where roleName not equals to UPDATED_ROLE_NAME
        defaultRoleShouldBeFound("roleName.notEquals=" + UPDATED_ROLE_NAME);
    }

    @Test
    @Transactional
    public void getAllRolesByRoleNameIsInShouldWork() throws Exception {
        // Initialize the database
        roleRepository.saveAndFlush(role);

        // Get all the roleList where roleName in DEFAULT_ROLE_NAME or UPDATED_ROLE_NAME
        defaultRoleShouldBeFound("roleName.in=" + DEFAULT_ROLE_NAME + "," + UPDATED_ROLE_NAME);

        // Get all the roleList where roleName equals to UPDATED_ROLE_NAME
        defaultRoleShouldNotBeFound("roleName.in=" + UPDATED_ROLE_NAME);
    }

    @Test
    @Transactional
    public void getAllRolesByRoleNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        roleRepository.saveAndFlush(role);

        // Get all the roleList where roleName is not null
        defaultRoleShouldBeFound("roleName.specified=true");

        // Get all the roleList where roleName is null
        defaultRoleShouldNotBeFound("roleName.specified=false");
    }
                @Test
    @Transactional
    public void getAllRolesByRoleNameContainsSomething() throws Exception {
        // Initialize the database
        roleRepository.saveAndFlush(role);

        // Get all the roleList where roleName contains DEFAULT_ROLE_NAME
        defaultRoleShouldBeFound("roleName.contains=" + DEFAULT_ROLE_NAME);

        // Get all the roleList where roleName contains UPDATED_ROLE_NAME
        defaultRoleShouldNotBeFound("roleName.contains=" + UPDATED_ROLE_NAME);
    }

    @Test
    @Transactional
    public void getAllRolesByRoleNameNotContainsSomething() throws Exception {
        // Initialize the database
        roleRepository.saveAndFlush(role);

        // Get all the roleList where roleName does not contain DEFAULT_ROLE_NAME
        defaultRoleShouldNotBeFound("roleName.doesNotContain=" + DEFAULT_ROLE_NAME);

        // Get all the roleList where roleName does not contain UPDATED_ROLE_NAME
        defaultRoleShouldBeFound("roleName.doesNotContain=" + UPDATED_ROLE_NAME);
    }


    @Test
    @Transactional
    public void getAllRolesByAdditionalIsEqualToSomething() throws Exception {
        // Initialize the database
        roleRepository.saveAndFlush(role);

        // Get all the roleList where additional equals to DEFAULT_ADDITIONAL
        defaultRoleShouldBeFound("additional.equals=" + DEFAULT_ADDITIONAL);

        // Get all the roleList where additional equals to UPDATED_ADDITIONAL
        defaultRoleShouldNotBeFound("additional.equals=" + UPDATED_ADDITIONAL);
    }

    @Test
    @Transactional
    public void getAllRolesByAdditionalIsNotEqualToSomething() throws Exception {
        // Initialize the database
        roleRepository.saveAndFlush(role);

        // Get all the roleList where additional not equals to DEFAULT_ADDITIONAL
        defaultRoleShouldNotBeFound("additional.notEquals=" + DEFAULT_ADDITIONAL);

        // Get all the roleList where additional not equals to UPDATED_ADDITIONAL
        defaultRoleShouldBeFound("additional.notEquals=" + UPDATED_ADDITIONAL);
    }

    @Test
    @Transactional
    public void getAllRolesByAdditionalIsInShouldWork() throws Exception {
        // Initialize the database
        roleRepository.saveAndFlush(role);

        // Get all the roleList where additional in DEFAULT_ADDITIONAL or UPDATED_ADDITIONAL
        defaultRoleShouldBeFound("additional.in=" + DEFAULT_ADDITIONAL + "," + UPDATED_ADDITIONAL);

        // Get all the roleList where additional equals to UPDATED_ADDITIONAL
        defaultRoleShouldNotBeFound("additional.in=" + UPDATED_ADDITIONAL);
    }

    @Test
    @Transactional
    public void getAllRolesByAdditionalIsNullOrNotNull() throws Exception {
        // Initialize the database
        roleRepository.saveAndFlush(role);

        // Get all the roleList where additional is not null
        defaultRoleShouldBeFound("additional.specified=true");

        // Get all the roleList where additional is null
        defaultRoleShouldNotBeFound("additional.specified=false");
    }
                @Test
    @Transactional
    public void getAllRolesByAdditionalContainsSomething() throws Exception {
        // Initialize the database
        roleRepository.saveAndFlush(role);

        // Get all the roleList where additional contains DEFAULT_ADDITIONAL
        defaultRoleShouldBeFound("additional.contains=" + DEFAULT_ADDITIONAL);

        // Get all the roleList where additional contains UPDATED_ADDITIONAL
        defaultRoleShouldNotBeFound("additional.contains=" + UPDATED_ADDITIONAL);
    }

    @Test
    @Transactional
    public void getAllRolesByAdditionalNotContainsSomething() throws Exception {
        // Initialize the database
        roleRepository.saveAndFlush(role);

        // Get all the roleList where additional does not contain DEFAULT_ADDITIONAL
        defaultRoleShouldNotBeFound("additional.doesNotContain=" + DEFAULT_ADDITIONAL);

        // Get all the roleList where additional does not contain UPDATED_ADDITIONAL
        defaultRoleShouldBeFound("additional.doesNotContain=" + UPDATED_ADDITIONAL);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultRoleShouldBeFound(String filter) throws Exception {
        restRoleMockMvc.perform(get("/api/roles?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(role.getId().intValue())))
            .andExpect(jsonPath("$.[*].roleName").value(hasItem(DEFAULT_ROLE_NAME)))
            .andExpect(jsonPath("$.[*].additional").value(hasItem(DEFAULT_ADDITIONAL)));

        // Check, that the count call also returns 1
        restRoleMockMvc.perform(get("/api/roles/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultRoleShouldNotBeFound(String filter) throws Exception {
        restRoleMockMvc.perform(get("/api/roles?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restRoleMockMvc.perform(get("/api/roles/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    public void getNonExistingRole() throws Exception {
        // Get the role
        restRoleMockMvc.perform(get("/api/roles/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateRole() throws Exception {
        // Initialize the database
        roleRepository.saveAndFlush(role);

        int databaseSizeBeforeUpdate = roleRepository.findAll().size();

        // Update the role
        Role updatedRole = roleRepository.findById(role.getId()).get();
        // Disconnect from session so that the updates on updatedRole are not directly saved in db
        em.detach(updatedRole);
        updatedRole
            .roleName(UPDATED_ROLE_NAME)
            .additional(UPDATED_ADDITIONAL);
        RoleDTO roleDTO = roleMapper.toDto(updatedRole);

        restRoleMockMvc.perform(put("/api/roles").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(roleDTO)))
            .andExpect(status().isOk());

        // Validate the Role in the database
        List<Role> roleList = roleRepository.findAll();
        assertThat(roleList).hasSize(databaseSizeBeforeUpdate);
        Role testRole = roleList.get(roleList.size() - 1);
        assertThat(testRole.getRoleName()).isEqualTo(UPDATED_ROLE_NAME);
        assertThat(testRole.getAdditional()).isEqualTo(UPDATED_ADDITIONAL);
    }

    @Test
    @Transactional
    public void updateNonExistingRole() throws Exception {
        int databaseSizeBeforeUpdate = roleRepository.findAll().size();

        // Create the Role
        RoleDTO roleDTO = roleMapper.toDto(role);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRoleMockMvc.perform(put("/api/roles").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(roleDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Role in the database
        List<Role> roleList = roleRepository.findAll();
        assertThat(roleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteRole() throws Exception {
        // Initialize the database
        roleRepository.saveAndFlush(role);

        int databaseSizeBeforeDelete = roleRepository.findAll().size();

        // Delete the role
        restRoleMockMvc.perform(delete("/api/roles/{id}", role.getId()).with(csrf())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Role> roleList = roleRepository.findAll();
        assertThat(roleList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
