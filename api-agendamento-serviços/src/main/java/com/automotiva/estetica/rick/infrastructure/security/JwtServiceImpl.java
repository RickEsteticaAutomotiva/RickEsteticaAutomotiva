package com.automotiva.estetica.rick.infrastructure.security;

import com.automotiva.estetica.rick.application.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.validity}")
    private long jwtTokenValidity;

    @Override
    public String gerarToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        return Jwts.builder().setSubject(authentication.getName()).claim("roles", authorities).setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtTokenValidity * 1_000))
                .signWith(parseSecret(), SignatureAlgorithm.HS512).compact();
    }

    @Override
    public String obterUsernameDoToken(String token) {
        return getClaimsForToken(token, Claims::getSubject);
    }

    @Override
    public boolean tokenValido(String token, UserDetails userDetails) {
        String username = obterUsernameDoToken(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public List<String> getRolesFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        String roles = claims.get("roles", String.class);
        if (roles == null || roles.isBlank())
            return List.of();
        return List.of(roles.split(","));
    }

    private boolean isTokenExpired(String token) {
        return getClaimsForToken(token, Claims::getExpiration).before(new Date());
    }

    private <T> T getClaimsForToken(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(getAllClaimsFromToken(token));
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(parseSecret()).build().parseClaimsJws(token).getBody();
    }

    private SecretKey parseSecret() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
