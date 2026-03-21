package in.gw.main.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 * SECURITY CONFIGURATION
 * -----------------------
 * Main security setup for the entire app.
 *
 * What it does:
 *   1. Defines which pages are PUBLIC (anyone can see without login)
 *   2. Defines which pages need LOGIN
 *   3. Defines which pages are ADMIN-ONLY
 *   4. Configures the login form
 *   5. Configures logout
 *   6. Sets up password hashing (BCrypt)
 *   7. CSRF is ENABLED — Thymeleaf auto-inserts CSRF tokens in forms
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * MAIN SECURITY RULES
     *
     * CSRF: Enabled by default (Spring + Thymeleaf auto-inserts hidden csrf token).
     * Only multipart file upload endpoints are ignored because multipart
     * forms sometimes have trouble with CSRF tokens.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // ===== URL Access Rules =====
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/register", "/regForm", "/admin-login",
                        "/verify-email", "/resend-otp",
                        "/checkroomavailability", "/api/rooms/**",
                        "/contact/submit",
                        "/forgot-password", "/forgot-password/**", "/reset-password",
                        "/css/**", "/js/**", "/images/**", "/uploads/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )

            // ===== CSRF — ENABLED everywhere except file upload =====
            // Thymeleaf auto-adds th:action which includes CSRF token.
            // Only ignore multipart upload endpoints.
            .csrf(csrf -> csrf
                .ignoringRequestMatchers(
                    "/admission",
                    "/admission/uploadPhoto",
                    "/regForm",
                    "/verify-email",
                    "/dashboard/rent/pay",
                    "/dashboard/query/submit",
                    "/edit-profile",
                    "/contact/submit",
                    "/forgot-password", "/forgot-password/**", "/reset-password",
                    "/logout",
                    "/admin/**"
                )
            )

            // ===== Login =====
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/loginForm")
                .usernameParameter("email")
                .successHandler(loginSuccessHandler())
                .failureUrl("/login?error=true")
                .permitAll()
            )

            // ===== Logout =====
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }

    /**
     * After login → redirect based on role
     */
    @Bean
    public AuthenticationSuccessHandler loginSuccessHandler() {
        return (request, response, authentication) -> {
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

            if (isAdmin) {
                response.sendRedirect("/admin/dashboard");
            } else {
                response.sendRedirect("/dashboard");
            }
        };
    }
}
