package career.flow.owoke.config.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import career.flow.owoke.auth.service.JwtService;
import career.flow.owoke.auth.service.RedisService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final RedisService redisService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String jwt = null;
        String userId = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            try {
                userId = jwtService.getUserId(jwt, "access");
            } catch (ExpiredJwtException e) {
                log.debug("Token expired");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            } catch (SignatureException | IllegalArgumentException e) {
                log.debug("Token invalid");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            Claims claims = jwtService.getAllClaims(jwt, "access");
            if (!Boolean.TRUE.equals(claims.get("emailVerified", Boolean.class))) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userId,
                    null,
                    jwtService.getRoles(jwt, "access").stream().map(SimpleGrantedAuthority::new).toList());
            SecurityContextHolder.getContext().setAuthentication(authToken);

        }

        if (jwt != null) {
            if (redisService.exists("ban:userId:" + jwtService.getUserId(jwt, "access"))) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }
        filterChain.doFilter(request, response);

    }

}
