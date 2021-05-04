package eu.tsalliance.auth.controller;

import eu.tsalliance.auth.model.user.User;
import eu.tsalliance.auth.service.account.UserService;
import eu.tsalliance.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(path = "@me", method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<User> getCurrent(Authentication authentication) throws NotFoundException {
        return ResponseEntity.of(this.userService.findCurrentUser(authentication));
    }

    @RequestMapping(path = "{id}", method = RequestMethod.GET)
    @PreAuthorize("hasPermission('alliance.users.read')")
    public ResponseEntity<User> getUser(@PathVariable("id") String id) {
        return ResponseEntity.of(this.userService.findUserById(id));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('alliance.users.read')")
    public Page<User> getUsers(Pageable pageable) {
        return this.userService.findUsers(pageable);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('alliance.users.write')")
    public User createUser(@RequestBody User user) throws Exception {
        return this.userService.createUser(user, true);
    }

    @PutMapping("{id}")
    @PreAuthorize("hasAuthority('alliance.users.write') and canModifyUser(#user.role)")
    public User updateUser(@PathVariable("id") String id, @RequestBody User user) throws Exception {
        return this.userService.updateUser(id, user);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAuthority('alliance.users.write')")
    public void deleteUser(@PathVariable("id") String id) throws Exception {
        this.userService.deleteUser(id);
    }

}
