package eu.tsalliance.auth.service;

import eu.tsalliance.auth.exception.auth.AccountNotFoundException;
import eu.tsalliance.auth.exception.auth.InvalidCredentialsException;
import eu.tsalliance.auth.exception.auth.InvalidSessionException;
import eu.tsalliance.auth.exception.NotFoundException;
import eu.tsalliance.auth.exception.auth.SessionExpiredException;
import eu.tsalliance.auth.model.response.JwtTokenResponse;
import eu.tsalliance.auth.model.user.Credentials;
import eu.tsalliance.auth.model.user.User;
import eu.tsalliance.auth.repository.UserRepository;
import eu.tsalliance.auth.utils.CryptUtil;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class AuthenticationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Sign in user using their credentials
     * @param credentials Credentials object
     * @return JwtTokenResponse
     * @throws Exception Exception
     */
    public JwtTokenResponse signin(Credentials credentials) throws Exception {
        Optional<User> user = this.userRepository.findUserByEmailOrUsername(credentials.getIdentifier(), credentials.getIdentifier());

        if(user.isEmpty()) {
            throw new NotFoundException();
        }

        if(!passwordEncoder.matches(credentials.getPassword(), user.get().getPassword())) {
            throw new InvalidCredentialsException();
        }

        Date createdAt = new Date();

        String token = Jwts.builder()
                .claim("id", user.get().getId())
                .claim("etag", user.get().getEtag())
                .setIssuedAt(createdAt)
                .signWith(CryptUtil.getJwtSecretKey())
                .compact();

        return new JwtTokenResponse(user.get().getId(), token, null, createdAt);
    }

    /**
     * Find user by parsing jwt and search for user with id
     * @param jwt Jwt token
     * @return Optional of type User
     */
    public Optional<User> findUserDetailsByJwt(String jwt) {
        if(jwt == null) return Optional.empty();

        try {
            Claims claims = Jwts.parserBuilder().setSigningKey(CryptUtil.getJwtSecretKey()).build().parseClaimsJws(jwt).getBody();

            String userId = String.valueOf(claims.get("id"));
            String etag = String.valueOf(claims.get("etag"));

            Optional<User> user = this.userRepository.findById(userId);
            if(user.isEmpty()) {
                throw new AccountNotFoundException();
            }

            if(!user.get().getEtag().equals(etag)) {
                throw new SessionExpiredException();
            }

            return user;
        } catch (JwtException exception) {
            throw new InvalidSessionException();
        }
    }

}
