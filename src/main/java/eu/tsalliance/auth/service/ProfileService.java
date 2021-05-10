package eu.tsalliance.auth.service;

import eu.tsalliance.auth.model.user.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/** @author Jhon Camilo Baron Berdugo */
public interface ProfileService {
  Optional<Profile> findProfileById(String id);

  Page<Profile> listProfiles(Pageable pageable);
}
