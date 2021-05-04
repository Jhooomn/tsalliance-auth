package eu.tsalliance.auth.controller;

import eu.tsalliance.auth.model.user.Role;
import eu.tsalliance.auth.service.account.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping
    @PreAuthorize("hasPermission('alliance.roles.read')")
    public Page<Role> listAll(Pageable pageable) {
        return this.roleService.listAll(pageable);
    }

    @GetMapping("{id}")
    @PreAuthorize("hasPermission('alliance.roles.read')")
    public ResponseEntity<Role> findRole(@PathVariable("id") String id) {
        return ResponseEntity.of(this.roleService.findRoleById(id));
    }

    @PostMapping
    @PreAuthorize("hasPermission('alliance.roles.write')")
    public Role createRole(Role role) throws Exception {
        return this.roleService.createRole(role);
    }

    @PutMapping("{id}")
    @PreAuthorize("hasPermission('alliance.roles.write')")
    public Role updateRole(@PathVariable("id") String id, Role role) throws Exception {
        return this.roleService.updateRole(id, role);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasPermission('alliance.roles.write')")
    public ResponseEntity<Object> deleteRole(@PathVariable("id") String id) throws Exception {
        this.roleService.deleteRoleById(id);
        return ResponseEntity.ok().build();
    }

}
