package eu.tsalliance.auth.service;

import eu.tsalliance.auth.exception.InvalidCredentialsException;
import eu.tsalliance.auth.exception.NotFoundException;
import eu.tsalliance.auth.model.response.JwtTokenResponse;
import eu.tsalliance.auth.model.user.Credentials;
import eu.tsalliance.auth.model.user.User;
import eu.tsalliance.auth.repository.UserRepository;
import eu.tsalliance.auth.utils.CryptUtil;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Jwts;

import java.util.Date;
import java.util.Optional;

@Service
public class AuthenticationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public JwtTokenResponse signin(Credentials credentials) throws Exception {
        Optional<User> user = this.userRepository.findUserByEmailOrUsername(credentials.getEmail(), credentials.getUsername());

        if(user.isEmpty()) {
            throw new NotFoundException();
        }

        if(!passwordEncoder.matches(credentials.getPassword(), user.get().getPassword())) {
            throw new InvalidCredentialsException();
        }

        Date createdAt = new Date();

        String token = Jwts.builder()
                .setSubject(user.get().getId())
                .claim("id", user.get().getId())
                .setIssuedAt(createdAt)
                .signWith(SignatureAlgorithm.HS512, CryptUtil.getJwtSecretKey())
                .compact();

        return new JwtTokenResponse(user.get().getId(), token, null, createdAt);
    }

}
