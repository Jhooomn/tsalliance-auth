package eu.tsalliance.auth.controller;

import eu.tsalliance.auth.model.Application;
import eu.tsalliance.auth.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/applications")
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    @PostMapping
    public Application createApp(@RequestBody Application application) throws Exception {
        return this.applicationService.createApp(application);
    }

}
