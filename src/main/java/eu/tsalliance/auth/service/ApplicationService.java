package eu.tsalliance.auth.service;

import eu.tsalliance.auth.model.Application;
import eu.tsalliance.auth.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepository;

    protected List<Application> findAllIdOnly() {
        return this.applicationRepository.getAppsIdOnly();
    }

}
