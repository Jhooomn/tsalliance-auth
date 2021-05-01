package eu.tsalliance.auth.service;

import eu.tsalliance.auth.exception.InvalidCredentialsException;
import eu.tsalliance.auth.exception.InvalidSessionException;
import eu.tsalliance.auth.exception.NotFoundException;
import eu.tsalliance.auth.model.response.JwtTokenResponse;
import eu.tsalliance.auth.model.user.Credentials;
import eu.tsalliance.auth.model.user.User;
import eu.tsalliance.auth.repository.UserRepository;
import eu.tsalliance.auth.utils.CryptUtil;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;
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
                .claim("id", user.get().getId())
                .claim("etag", user.get().getEtag())
                .setIssuedAt(createdAt)
                .signWith(CryptUtil.getJwtSecretKey())
                .compact();

        return new JwtTokenResponse(user.get().getId(), token, null, createdAt);
    }

    public Optional<User> findUserDetailsByJwt(String jwt) {
        if(jwt == null) return Optional.empty();

        try {
            Claims claims = Jwts.parserBuilder().setSigningKey(CryptUtil.getJwtSecretKey()).build().parseClaimsJws(jwt).getBody();

            String userId = String.valueOf(claims.get("id"));
            String etag = String.valueOf(claims.get("etag"));

            // TODO: Fetch user and check if etag matches

            return this.userRepository.findUserByIdAndEtag(userId, etag);
        } catch (JwtException exception) {
            throw new InvalidSessionException();
        }
    }

}
