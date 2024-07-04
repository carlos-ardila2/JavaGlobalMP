package com.epam.jmp.spring.task3.configuration;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfiguration {

    @Value("${jwt.public.key}")
    RSAPublicKey publicKey;

    @Value("${jwt.private.key}")
    RSAPrivateKey privateKey;

    @Profile({"prod", "stg"})
    @Bean
    @Order(1)
    public SecurityFilterChain jwtTokenSecurityFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/oauth/token").authorizeHttpRequests(
                        // Authorize requests to the token endpoint
                        requests -> requests.anyRequest().authenticated())
                .csrf(csrf -> csrf.ignoringRequestMatchers("/oauth/token"))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // HTTP Basic login
                .httpBasic(withDefaults());
        return http.build();
    }

    @Profile({"prod", "stg"})
    @Bean
    @Order(2)
    public SecurityFilterChain authorizationSecurityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(
                        // Authorize requests to static resources and the actuator endpoints
                        requests -> requests.requestMatchers(
                                        "/css/**", "/js/**", "/img/**", "*.html").permitAll()
                                .anyRequest().authenticated())
                // CSRF token in request attribute _csrf.token, sent back in the request header X-XSRF-TOKEN
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()))
                // JWT token Authentication
                .oauth2ResourceServer((resourceServer) -> resourceServer.jwt(withDefaults()))
                // OAuth 2.0 Login
                .oauth2Login(withDefaults())
                // Form login
                .formLogin(withDefaults());

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails userDetails = User.builder()
                .username("admin")
                .password("$2a$10$XrSX1YYvr976T1xJ7F/5kOAgRuq7mgEctBvnGN9J2O7IO1/lLen0m")
                .authorities("app")
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(userDetails);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(this.publicKey).build();
    }

    @Bean
    JwtEncoder jwtEncoder() {
        JWK jwk = new RSAKey.Builder(this.publicKey).privateKey(this.privateKey).build();
        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }
}
