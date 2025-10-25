package com.automotiva.estetica.rick.api_agendamento_servicos.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class GerenciadorTokenJwt {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.validity}")
    private long jwtTokenValidity;

    /**
     * Gera token JWT a partir de uma autenticação.
     * Inclui username e roles como claims.
     */
    public String generateToken(final Authentication authentication) {
        final String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim("roles", authorities) // roles no token
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtTokenValidity * 1_000))
                .signWith(parseSecret(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Retorna o username (subject) do token.
     */
    public String getUsernameFromToken(String token) {
        return getClaimsForToken(token, Claims::getSubject);
    }

    /**
     * Retorna a data de expiração do token.
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimsForToken(token, Claims::getExpiration);
    }

    /**
     * Retorna roles do token como lista de strings.
     */
    public List<String> getRolesFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        String roles = claims.get("roles", String.class);
        if (roles == null || roles.isBlank()) return List.of();
        return List.of(roles.split(","));
    }

    /**
     * Valida se o token é válido para o usuário.
     */
    public boolean isTokenValido(String token, UserDetails userDetails) {
        String username = getUsernameFromToken(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    /**
     * Verifica se o token está expirado.
     */
    public boolean isTokenExpired(String token) {
        Date expirationDate = getExpirationDateFromToken(token);
        return expirationDate.before(new Date());
    }

    /**
     * Extrai qualquer claim usando uma função.
     */
    public <T> T getClaimsForToken(String token, Function<Claims, T> claimsResolver) {
        Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Retorna todas as claims do token. Lança exceção se token inválido.
     */
    private Claims getAllClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(parseSecret())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException | IllegalArgumentException e) {
            throw new RuntimeException("Token JWT inválido: " + e.getMessage());
        }
    }

    /**
     * Converte a string secreta em SecretKey.
     */
    private SecretKey parseSecret() {
        return Keys.hmacShaKeyFor(this.secret.getBytes(StandardCharsets.UTF_8));
    }

}
