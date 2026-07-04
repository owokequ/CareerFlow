package career.flow.owoke.auth.service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import career.flow.owoke.auth.dto.JwtClaims;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtService {

    @Value("${jwt.secretAccess}")
    private String secretKeyAccess;

    @Value("${jwt.expirationAccess}")
    private Long timeLifeAccess;

    @Value("${jwt.secretRefresh}")
    private String secretKeyRefresh;

    @Value("${jwt.expirationRefresh}")
    private Long timeLifeRefresh;

    public String generateAccessToken(JwtClaims jwtClaims) {

        Map<String, Object> claims = new HashMap<>();
        List<String> roles = jwtClaims.roles();
        claims.put("name", jwtClaims.name());
        claims.put("email", jwtClaims.email());
        claims.put("emailVerified", jwtClaims.isEmailVerified());
        claims.put("roles", roles);

        return Jwts.builder()
                .addClaims(claims)
                .setSubject(jwtClaims.id())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + timeLifeAccess))
                .signWith(getSignInKeyAccess())
                .compact();
    }

    public String generateRefreshToken(JwtClaims jwtClaims) {
        Map<String, Object> claims = new HashMap<>();
        List<String> roles = jwtClaims.roles();
        claims.put("name", jwtClaims.name());
        claims.put("email", jwtClaims.email());
        claims.put("emailVerified", jwtClaims.isEmailVerified());
        claims.put("roles", roles);
        return Jwts.builder()
                .addClaims(claims)
                .setSubject(jwtClaims.id())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + timeLifeRefresh))
                .signWith(getSignInKeyRefresh())
                .compact();
    }

    public Claims validationRefreshToken(String refreshToken) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKeyRefresh())
                .build()
                .parseClaimsJws(refreshToken)
                .getBody();
    }

    public String getUsername(String token, String type) {
        return getAllClaims(token, type).getSubject();
    }

    @SuppressWarnings("unchecked")
    public List<String> getRoles(String token, String type) {
        return getAllClaims(token, type).get("roles", List.class);
    }

    public String getUserId(String token, String type) {
        return getAllClaims(token, type).getSubject();
    }

    public Claims getAllClaims(String token, String type) {
        if (type.equals("access")) {
            return Jwts.parserBuilder()
                    .setSigningKey(getSignInKeyAccess())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } else {
            return Jwts.parserBuilder()
                    .setSigningKey(getSignInKeyRefresh())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        }
    }

    private Key getSignInKeyAccess() {
        return Keys.hmacShaKeyFor(secretKeyAccess.getBytes(StandardCharsets.UTF_8));
    }

    private Key getSignInKeyRefresh() {
        return Keys.hmacShaKeyFor(secretKeyRefresh.getBytes(StandardCharsets.UTF_8));
    }

}
