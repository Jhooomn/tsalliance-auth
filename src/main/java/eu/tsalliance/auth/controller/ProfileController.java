package eu.tsalliance.auth.controller;

import eu.tsalliance.auth.model.user.Profile;
import eu.tsalliance.auth.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/profiles")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @GetMapping("{id}")
    public ResponseEntity<Profile> getProfile(@PathVariable("id") String id) {
        return ResponseEntity.of(profileService.findProfileById(id));
    }

    @GetMapping
    public Page<Profile> listProfiles(Pageable pageable) {
        return profileService.listProfiles(pageable);
    }

}
