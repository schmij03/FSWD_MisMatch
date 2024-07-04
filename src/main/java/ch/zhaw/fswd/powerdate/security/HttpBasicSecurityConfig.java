package ch.zhaw.fswd.powerdate.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class HttpBasicSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/api/interest/**")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/api/profile/register")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/api/**")).authenticated()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/websocket/**")).permitAll()

                        .anyRequest().permitAll())
                .formLogin(formLogin -> formLogin
                        .loginProcessingUrl("/auth/login")
                        .successHandler(customAuthenticationSuccessHandler())
                )
                .logout(logout -> logout.logoutUrl("/auth/logout")
                        .logoutSuccessHandler(customLogoutSuccessHandler())
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID"))
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(customAuthExceptionHandler()))
                .sessionManagement(sessionManagement -> sessionManagement.sessionFixation().newSession())
                .csrf(csrf -> csrf.ignoringRequestMatchers(
                        AntPathRequestMatcher.antMatcher("/h2-console/**"),
                        AntPathRequestMatcher.antMatcher("/api/**"),
                        AntPathRequestMatcher.antMatcher("/auth/**"),
                        AntPathRequestMatcher.antMatcher("/websocket/**")
                ))
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .build();
    }

    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return (request, response, authentication) -> {
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("{\"message\": \"Login successful\"}");
            response.getWriter().flush();
        };
    }

    @Bean
    public LogoutSuccessHandler customLogoutSuccessHandler() {
        return (request, response, authentication) -> {
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("{\"message\": \"Logout successful\"}");
            response.getWriter().flush();
        };
    }

    @Bean
    public AuthenticationEntryPoint customAuthExceptionHandler() {
        return (request, response, authentication) -> {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"message\": \"Unauthorized\"}");
            response.getWriter().flush();
        };
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
}
