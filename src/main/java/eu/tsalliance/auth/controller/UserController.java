package eu.tsalliance.auth.controller;

import eu.tsalliance.auth.exception.ValidationException;
import eu.tsalliance.auth.model.user.User;
import eu.tsalliance.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(path = "{id}", method = RequestMethod.GET)
    public ResponseEntity<User> getUser(@PathVariable("id") String id) {
        return ResponseEntity.of(this.userService.findUserById(id));
    }

    @GetMapping
    public Page<User> getUsers(Pageable pageable) {
        return this.userService.findUsers(pageable);
    }

    @PostMapping
    public User createUser(@RequestBody User user) throws Exception {
        return this.userService.createUser(user, true);
    }

    @PutMapping("{id}")
    public User updateUser(@PathVariable("id") String id, @RequestBody User user) throws Exception {
        return this.userService.updateUser(id, user);
    }

    @DeleteMapping("{id}")
    public void deleteUser(@PathVariable("id") String id) {
        this.userService.deleteUser(id);
    }

}
