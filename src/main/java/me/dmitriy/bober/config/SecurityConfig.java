package me.dmitriy.bober.config;

import me.dmitriy.bober.data.UserRepository;
import me.dmitriy.bober.models.User;
import me.dmitriy.bober.models.UserRole;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    //Finally, Smith bot is employed after several years in New Koluton!

    private final UserRepository userRepository;

    public SecurityConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        HttpSessionRequestCache requestCache = new HttpSessionRequestCache();
        requestCache.setRequestMatcher(request -> {
            String uri = request.getRequestURI();
            return !uri.startsWith("/files/") &&
                    !uri.startsWith("/css/") &&
                    !uri.startsWith("/js/") &&
                    !uri.startsWith("/img/") &&
                    !uri.startsWith("/fonts/");
        });
        http
                .requestCache(cache -> cache.requestCache(requestCache))
//                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/", "/home", "/about",
                                "/books/**", "/authors/**",
                                "/login", "/registration",
                                "/error", "/authors/books/**",
                                "/css/**", "/img/**", "/js/**",
                                "/fonts/**", "/files/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/registration").permitAll()
                        .requestMatchers("/account/**").hasAnyRole(
                                UserRole.USER.name(),
                                UserRole.ADMIN.name(),
                                UserRole.SUDO.name(),
                                UserRole.AUTHOR.name())
                        .requestMatchers("/admin/**").hasAnyRole(UserRole.ADMIN.name(), UserRole.SUDO.name())
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .permitAll()
                        .usernameParameter("nickname")
                        .defaultSuccessUrl("/books"))
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .permitAll());
        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String nickname) throws UsernameNotFoundException {
                User user = userRepository
                        .findByNicknameIgnoreCase(nickname)
                        .orElseThrow(() -> new UsernameNotFoundException("User "+nickname+" not found"));
                Set<SimpleGrantedAuthority> roles =
                        Collections.singleton(new SimpleGrantedAuthority("ROLE_"+user.getRole()));
                return new org.springframework.security.core.userdetails.
                        User(
                                user.getNickname(),
                                user.getPassword(),
                                true,
                                true,
                                true,
                                !(user.isBlocked()),
                                roles);
            }
        };
    }
}
