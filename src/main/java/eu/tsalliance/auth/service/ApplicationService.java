package eu.tsalliance.auth.service;

import eu.tsalliance.auth.exception.ValidationException;
import eu.tsalliance.auth.model.Application;
import eu.tsalliance.auth.repository.ApplicationRepository;
import eu.tsalliance.auth.utils.RandomUtil;
import eu.tsalliance.auth.validator.Validator;
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
