package com.example.smartcafeteria.security;

import com.example.smartcafeteria.config.CustomLoginSuccessHandler;
import com.example.smartcafeteria.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    private final CustomLoginSuccessHandler customLoginSuccessHandler;

    public SecurityConfig(CustomUserDetailsService userDetailsService,
                          PasswordEncoder passwordEncoder,
                          CustomLoginSuccessHandler customLoginSuccessHandler) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.customLoginSuccessHandler = customLoginSuccessHandler;
    }
    // main spring security filter chain config
    // defines access rules, login, logout, and exception handling
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // disble CSRF for simplicity - not recommended for production
                .csrf(csrf -> csrf.disable())
                // authZ rules, who can access what
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/register", "/style.css").permitAll() // public pages
                        .requestMatchers("/menu/add", "/menu/edit/**", "/menu/delete/**").hasRole("ADMIN") // admin only
                        .requestMatchers("/admin/**").hasRole("ADMIN") // admin dashboard
                        .anyRequest().authenticated() // everything else requires login
                )
                // form login config
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(customLoginSuccessHandler)
                        .permitAll()
                )
                // unauthorized access handler
                .exceptionHandling(handling -> handling
                        .accessDeniedPage("/errorpage")
                )
                // logout config
                .logout(logout -> logout.permitAll());

        return http.build();
    }

    // authN provider using DAO-based authN
    // tells spring security how to load users and how passwords are encoded
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService); // load users from DB
        provider.setPasswordEncoder(passwordEncoder); // use encoded password
        return provider;
    }

    // exposes AuthenticationManager as a bean so it can be used elsewhere in the app
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
