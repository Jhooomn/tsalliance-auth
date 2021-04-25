package eu.tsalliance.auth.controller;

import eu.tsalliance.auth.model.user.Profile;
import eu.tsalliance.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/profiles")
public class ProfileController {

    @Autowired
    private UserService userService;

    @GetMapping("{id}")
    public Profile getProfile(@PathVariable("id") String id) {
        return this.userService.findProfileById(id);
    }

}
