package eu.tsalliance.auth.repository;

import eu.tsalliance.auth.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ApplicationRepository extends JpaRepository<Application, String> {

    @Query("select app.id from Application app")
    List<Application> getAppsIdOnly();

}
