package eu.tsalliance.auth.service;

import eu.tsalliance.auth.model.Application;
import eu.tsalliance.auth.repository.ApplicationRepository;
import eu.tsalliance.auth.utils.RandomUtil;
import eu.tsalliance.exception.NotFoundException;
import eu.tsalliance.validator.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.List;
import java.util.Optional;

@Service
public class ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private Validator validator;

    protected List<Application> findAllIdOnly() {
        return this.applicationRepository.getAppsIdOnly();
    }

    public Page<Application> findAll(Pageable pageable) {
        return this.applicationRepository.findAll(pageable);
    }

    public Optional<Application> findById(String id) {
        return this.applicationRepository.findById(id);
    }

    /**
     * Register a new application
     * @param application Application's data
     * @return Application
     * @throws Exception Exception
     */
    public Application createApp(Application application) throws Exception {
        application.setAccessToken(RandomUtil.generateRandomString(64));
        application.setClientId(RandomUtil.generateRandomString(64));
        application.setClientSecret(RandomUtil.generateRandomString(64));
        application.setCode(RandomUtil.generateRandomNumber(4));

        this.validator.text(application.getName(), "name", true).minLen(3).maxLen(32).alpha().unique(() -> !this.existsByNameAndIdNot(application.getName(), application.getId())).check();
        this.validator.text(application.getDescription(), "description", false).minLen(12).maxLen(254).check();
        this.validator.url(application.getUrl().toString(), "url", true).unique(() -> !this.existsByUrlAndIdNot(application.getUrl(), application.getId())).check();
        this.validator.throwErrors();

        return this.applicationRepository.saveAndFlush(application);
    }

    /**
     * Register a new application
     * @param id Application's id
     * @param updated Application's data
     * @return Application
     * @throws Exception Exception
     */
    public Application updateApp(String id, Application updated) throws Exception {
        Optional<Application> application = this.findById(id);

        if(application.isEmpty()) {
            throw new NotFoundException();
        }

        if(this.validator.text(application.get().getName(), "name", false).minLen(3).maxLen(32).alpha().unique(() -> !this.existsByNameAndIdNot(application.get().getName(), application.get().getId())).check()) {
            if(updated.getName() != null) {
                application.get().setName(updated.getName());
            }
        }

        if(this.validator.text(application.get().getName(), "description", false).minLen(3).maxLen(254).check()) {
            if(updated.getDescription() != null) {
                application.get().setDescription(updated.getDescription());
            }
        }

        if(this.validator.url(application.get().getUrl().toString(), "url", false).unique(() -> !this.existsByUrlAndIdNot(application.get().getUrl(), application.get().getId())).check()) {
            if(updated.getUrl() != null) {
                application.get().setUrl(updated.getUrl());
            }
        }

        this.validator.throwErrors();
        return this.applicationRepository.saveAndFlush(application.get());
    }

    /**
     * Delete application by id
     * @param id Application's id
     */
    public void deleteById(String id) {
        this.applicationRepository.deleteById(id);
    }

    /**
     * Check if an application with name already exists
     * @param name Application name to lookup
     * @param id Application's id to check if it is not the same application
     * @return True or False
     */
    public boolean existsByNameAndIdNot(String name, String id) {
        return this.applicationRepository.existsByNameAndIdNot(name, id);
    }

    /**
     * Check if an application with url already exists
     * @param url URL to lookup
     * @param id Application's id to check if it is not the same application
     * @return True or False
     */
    public boolean existsByUrlAndIdNot(URL url, String id) {
        return this.applicationRepository.existsByUrlAndIdNot(url, id);
    }

}
