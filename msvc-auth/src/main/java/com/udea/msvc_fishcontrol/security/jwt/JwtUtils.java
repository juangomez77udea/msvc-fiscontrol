package com.udea.msvc_fishcontrol.security.jwt;

import com.udea.msvc_fishcontrol.models.user.UserEntity;
import com.udea.msvc_fishcontrol.repositories.user.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtUtils {

    @Value("${jwt.secret.key}")
    private String secretKey;

    @Value("${jwt.time.expiration}")
    private String timeExpiration;

    @Autowired
    private UserRepository userRepository;

    public String generateAccessToken(String username) {
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        if (user != null) {
            List<String> roles = user.getRoles().stream()
                    .map(role -> "ROLE_" + role.getName().name())
                    .collect(Collectors.toList());

            Map<String, Object> claims = new HashMap<>();
            claims.put("roles", roles);

            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(username)
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(timeExpiration)))
                    .signWith(getSignatureKey(), SignatureAlgorithm.HS256)
                    .compact();
        } else {
            return Jwts.builder()
                    .setSubject(username)
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(timeExpiration)))
                    .signWith(getSignatureKey(), SignatureAlgorithm.HS256)
                    .compact();
        }
    }

    public String generateAccessToken(UserDetails userDetails) {
        // Asegúrate que los roles tengan el prefijo "ROLE_"
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        // Debug: Verifica los roles antes de incluirlos en el token
        log.info("Roles incluidos en el token para {}: {}", userDetails.getUsername(), roles);

        return Jwts.builder()
                .claim("roles", roles)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(timeExpiration)))
                .signWith(getSignatureKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return getClaim(token, Claims::getSubject);
    }

    @SuppressWarnings("unchecked")
    public List<String> getRolesFromToken(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("roles", List.class);
    }

    public <T> T getClaim(String token, Function<Claims, T> claimsTFunction) {
        Claims claims = extractAllClaims(token);
        return claimsTFunction.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignatureKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Key getSignatureKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Boolean isTokenValid(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(getSignatureKey())
                    .build()
                    .parseClaimsJws(token);

            Date expiration = claimsJws.getBody().getExpiration();
            return !expiration.before(new Date());

        } catch (ExpiredJwtException e) {
            log.error("Token expirado");
            return false;
        } catch (SignatureException e) {
            log.error("Error en la firma del token");
            return false;
        } catch (MalformedJwtException e) {
            log.error("Token mal formado");
            return false;
        } catch (UnsupportedJwtException e) {
            log.error("Token no soportado");
            return false;
        } catch (IllegalArgumentException e) {
            log.error("Token vacío o nulo");
            return false;
        } catch (Exception e) {
            log.error("Error al validar token: {}", e.getMessage());
            return false;
        }
    }

    public Boolean isTokenExpired(String token) {
        try {
            Date expiration = extractAllClaims(token).getExpiration();
            return expiration.before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        } catch (Exception e) {
            log.error("Error al verificar expiración del token");
            return true;
        }
    }
}
