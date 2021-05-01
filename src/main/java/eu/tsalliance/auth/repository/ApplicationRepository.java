package eu.tsalliance.auth.repository;

import eu.tsalliance.auth.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.List;

@Service
public interface ApplicationRepository extends JpaRepository<Application, String> {

    @Query("select new eu.tsalliance.auth.model.Application(app.id) from Application app")
    List<Application> getAppsIdOnly();

    boolean existsByNameAndIdNot(String username, String id);
    boolean existsByUrlAndIdNot(URL url, String id);

}
