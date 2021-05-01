package eu.tsalliance.auth.controller;

import eu.tsalliance.auth.model.Application;
import eu.tsalliance.auth.model.Invite;
import eu.tsalliance.auth.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/applications")
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    @GetMapping("{id}")
    public ResponseEntity<Application> findApp(@PathVariable("id") String id, Authentication authentication) {
        Optional<Application> application = this.applicationService.findById(id);

        if(!authentication.getAuthorities().contains("alliance.applications.read")) {
            return ResponseEntity.of(application.map(Application::censored));
        }

        return ResponseEntity.of(application);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('alliance.applications.read')")
    public Page<Application> listAll(Pageable pageable) {
        return this.applicationService.findAll(pageable);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('alliance.applications.write')")
    public Application createApp(@RequestBody Application application) throws Exception {
        return this.applicationService.createApp(application);
    }

    @PutMapping("{id}")
    @PreAuthorize("hasAuthority('alliance.applications.write')")
    public Application updateApp(@PathVariable("id") String id, @RequestBody Application application) throws Exception {
        return this.applicationService.updateApp(id, application);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAuthority('alliance.applications.write')")
    public ResponseEntity<Object> deleteApp(@PathVariable("id") String id) {
        this.applicationService.deleteById(id);
        return ResponseEntity.ok().build();
    }

}
