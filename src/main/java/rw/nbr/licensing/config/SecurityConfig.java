package rw.nbr.licensing.config;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import rw.nbr.licensing.user.token.JwtAuthenticationFilter;
import rw.nbr.licensing.util.CustomAuthenticationEntryPoint;


import static org.springframework.http.HttpMethod.GET;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
import static rw.nbr.licensing.type.Role.ADMIN;
import static rw.nbr.licensing.type.Role.SUPER_ADMIN;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private static final String[] WHITE_LIST_URL =
            {
                    "/",                // Allow root URL
                    "/index.html",      // Allow index page
                    "/umucyo.png",
                    "/api-docs.yml",
                    "/static/**",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/api/v1/account-access/**",
                    "/v2/api-docs",
                    "/v3/api-docs",
                    "/v3/api-docs/**",
                    "/swagger-resources",
                    "/swagger-resources/**",
                    "/configuration/ui",
                    "/configuration/security",
                    "/swagger-ui/**",
                    "/webjars/**",
                    "/swagger-ui.html"
            };
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final LogoutHandler logoutHandler;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter, AuthenticationProvider authenticationProvider, LogoutHandler logoutHandler) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.authenticationProvider = authenticationProvider;
        this.logoutHandler = logoutHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults()) // Enables CORS
                .csrf(AbstractHttpConfigurer::disable) // Optional: Disable CSRF if not needed
                .authorizeHttpRequests(req -> req
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(WHITE_LIST_URL).permitAll()// Allow all OPTIONS requests
                        .requestMatchers("/api/v1/management/**").hasAnyRole(ADMIN.name(), SUPER_ADMIN.name())
                        .requestMatchers(GET, "/api/v1/management/**").hasAnyAuthority(ADMIN.name(), SUPER_ADMIN.name())
                        //.requestMatchers(POST, "/api/v1/management/**").hasAnyAuthority(ADMIN_WRITE.name(), SUPER_ADMIN_WRITE.name())
                        // .requestMatchers(PUT, "/api/v1/management/**").hasAnyAuthority(ADMIN_UPDATE.name(), SUPER_ADMIN_UPDATE.name())
                        // .requestMatchers(DELETE, "/api/v1/management/**").hasAnyAuthority(SUPER_ADMIN_DELETE.name())
                        // .requestMatchers(GET, "/api/resources/contracts/**").hasAnyAuthority(ENTITY_CONTRACT_READ.name())
                        // .requestMatchers("/api/resources/**").hasAnyRole(ENTITY_REQ.name())
                        .anyRequest()
                        .authenticated())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint()) // Custom Error Handling
                )
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout -> logout.logoutUrl(("/api/v1/auth/logout"))
                        .addLogoutHandler(logoutHandler)
                        .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext()));
        return http.build();
    }
}
