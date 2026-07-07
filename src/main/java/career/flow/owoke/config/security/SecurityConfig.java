package career.flow.owoke.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import career.flow.owoke.auth.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final PasswordHash passwordHash;
        private final CustomUserDetailsService userService;
        private final JwtAuthFilter jwtAuthFilter;

        @Bean
        public SecurityFilterChain getSecurityFilterChain(HttpSecurity http) throws Exception {
                return http
                                // CSRF is disabled because access tokens are sent via Authorization header;
                                // refresh cookies are HttpOnly, SameSite=Strict, and Secure in production.
                                .csrf(csrf -> csrf.disable())

                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(HttpMethod.POST,
                                                                "/api/auth/register",
                                                                "/api/auth/login",
                                                                "/api/auth/password/forgot",
                                                                "/api/auth/password/reset",
                                                                "/api/auth/refresh")
                                                .permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/auth/register/verify")
                                                .permitAll()
                                                .requestMatchers(
                                                                "/swagger-ui/**",
                                                                "/v3/api-docs/**",
                                                                "/api-docs/**",
                                                                "/actuator/health")
                                                .permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/users/me").authenticated()
                                                .requestMatchers(HttpMethod.PUT, "/api/users/me").authenticated()
                                                .requestMatchers("/api/users/**").hasRole("ADMIN")
                                                .anyRequest()
                                                .authenticated())
                                .authenticationProvider(authenticationProvider())
                                .exceptionHandling(exception -> exception
                                                .authenticationEntryPoint(
                                                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                                                .accessDeniedHandler((request, response, accessDeniedException) -> {
                                                        response.setStatus(HttpStatus.FORBIDDEN.value());
                                                }))
                                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

                                .build();
        }

        @Bean
        public AuthenticationProvider authenticationProvider() {
                DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userService);
                authProvider.setPasswordEncoder(passwordHash.passwordEncoder());
                return authProvider;
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
                        throws Exception {
                return authenticationConfiguration.getAuthenticationManager();
        }
}
