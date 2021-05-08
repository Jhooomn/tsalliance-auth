package eu.tsalliance.auth.service.account;

import eu.tsalliance.apiutils.exception.NotFoundException;
import eu.tsalliance.apiutils.validator.Validator;
import eu.tsalliance.auth.exception.account.AccessDeniedException;
import eu.tsalliance.auth.model.user.Role;
import eu.tsalliance.auth.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.Optional;

@Service
public class RoleService {

    private final String ROOT_ROLE_NAME = "root";

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private Validator validator;

    @PostConstruct
    private void createRootRole() throws Exception {
        Role role = new Role();
        role.setName(ROOT_ROLE_NAME);
        role.setPermissions(Collections.singleton("*"));
        role.setHierarchy(1000);

        this.findOrCreateRoleByName(ROOT_ROLE_NAME, role);
    }

    /**
     * Get role's database entry
     * @param id Role's id
     * @return Optional of type Role
     */
    public Optional<Role> findRoleById(String id) {
        return this.roleRepository.findById(id);
    }

    /**
     * Get role's database entries
     * @param pageable Pagination settings
     * @return Page of type Role
     */
    public Page<Role> listAll(Pageable pageable) {
        return this.roleRepository.findAll(pageable);
    }

    /**
     * Create a new role
     * @param role Role's data
     * @return Role
     * @throws Exception Exception
     */
    public Role createRole(Role role) throws Exception {

        this.validator.text(role.getName(), "name", true).alpha().minLen(3).maxLen(32).unique(() -> !this.existsByName(role.getName())).check();
        this.validator.number(role.getHierarchy(), "hierarchy", false).min(0).max(role.getName().equals(ROOT_ROLE_NAME) ? 1000 : 999).check();
        this.validator.throwErrors();

        return this.roleRepository.saveAndFlush(role);
    }

    /**
     * Update a role
     * @param updated Role's data
     * @return Role
     * @throws Exception Exception
     */
    public Role updateRole(String id, Role updated) throws Exception {
        Optional<Role> role = this.findRoleById(id);

        if(role.isEmpty()) {
            throw new NotFoundException();
        }

        // Root role can not be updated
        if(role.get().getName().equals(ROOT_ROLE_NAME)) {
            throw new AccessDeniedException();
        }

        if(this.validator.text(role.get().getName(), "name", false).alpha().minLen(3).maxLen(32).unique(() -> !this.existsByName(updated.getName())).check()) {
            if(updated.getName() != null) {
                role.get().setName(updated.getName());
            }
        }

        if(this.validator.number(updated.getHierarchy(), "name", false).min(0).max(999).check()) {
            role.get().setHierarchy(updated.getHierarchy());
        }

        if(updated.getPermissions() != null) {
            role.get().setPermissions(updated.getPermissions());
        }

        this.validator.throwErrors();
        return this.roleRepository.saveAndFlush(role.get());
    }

    /**
     * Find a role by its name. If it does not exist, a new role is created
     * @param name Name of the role
     * @param create Role's data to be created
     * @return Role
     * @throws Exception Exception
     */
    public Role findOrCreateRoleByName(String name, Role create) throws Exception {
        Optional<Role> role = this.roleRepository.findByName(name);

        if(role.isEmpty()) {
            return this.createRole(create);
        }

        return role.get();
    }

    /**
     * Check if a role with name already exists
     * @param name Role's name
     * @return True or False
     */
    public boolean existsByName(String name) {
        return this.roleRepository.existsByName(name);
    }

    /**
     * Delete a role by id
     * @param id Role's id
     */
    public void deleteRoleById(String id) throws Exception {

        // Root role can not be deleted
        if(this.findRoleById(id).orElse(new Role()).getName().equals(ROOT_ROLE_NAME)) {
            throw new AccessDeniedException();
        }

        this.roleRepository.deleteById(id);
    }

    /**
     * Get the root role
     * @return Role
     * @throws NotFoundException Exception
     */
    public Role getRootRole() throws NotFoundException {
        return this.roleRepository.findByName(ROOT_ROLE_NAME).orElseThrow(NotFoundException::new);
    }

}
