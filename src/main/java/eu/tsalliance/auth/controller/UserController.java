package eu.tsalliance.auth.controller;

import eu.tsalliance.auth.exception.ValidationException;
import eu.tsalliance.auth.model.user.User;
import eu.tsalliance.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("{id}")
    public User getUser(@PathVariable("id") String id) {
        return this.userService.findUserById(id);
    }

    @GetMapping()
    public Page<User> getUsers(Pageable pageable) {
        return this.userService.findUsers(pageable);
    }

    @PostMapping
    public User createUser(@RequestBody User user) throws ValidationException {
        return this.userService.createUser(user);
    }

    @PutMapping("{id}")
    public User updateUser(@PathVariable("id") String id, @RequestBody User user) throws ValidationException {
        return this.userService.updateUser(id, user);
    }

    @DeleteMapping("{id}")
    public void deleteUser(@PathVariable("id") String id) {
        this.userService.deleteUser(id);
    }

}
