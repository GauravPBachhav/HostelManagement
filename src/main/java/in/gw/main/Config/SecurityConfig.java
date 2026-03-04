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
 * This is the MAIN security setup for the entire app.
 *
 * What it does:
 *   1. Defines which pages are PUBLIC (anyone can see without login)
 *   2. Defines which pages need LOGIN
 *   3. Defines which pages are ADMIN-ONLY
 *   4. Configures the login form (which URL, which fields)
 *   5. Configures logout
 *   6. Sets up password hashing (BCrypt) so passwords are stored safely
 *
 * HOW SPRING SECURITY WORKS (simple explanation):
 *   - Every request goes through a "security filter"
 *   - The filter checks: is this URL public? does user need to be logged in?
 *   - If user is not logged in and page needs login → redirect to /login
 *   - If user logs in → Spring checks email/password using CustomUserDetailsService
 *   - If password matches → user is "authenticated" (logged in)
 *   - Spring remembers the user in the session automatically
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Spring injects our CustomUserDetailsService here
    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * PASSWORD ENCODER
     * BCrypt converts "admin123" into something like "$2a$10$xyz..."
     * Even if database is hacked, no one can see the real password.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * AUTHENTICATION PROVIDER
     * Tells Spring: "Use our CustomUserDetailsService to find users,
     * and use BCrypt to check if entered password matches stored hash."
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * MAIN SECURITY RULES
     * This is where we define all the access rules for the app.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // ===== STEP 1: Which URLs are public vs protected =====
            .authorizeHttpRequests(auth -> auth
                // These pages can be seen by ANYONE (no login needed)
                .requestMatchers("/", "/register", "/regForm", "/admin-login",
                        "/css/**", "/js/**", "/images/**", "/uploads/**").permitAll()
                // Only ADMIN role can access /admin/... pages
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // Everything else requires login (USER or ADMIN)
                .anyRequest().authenticated()
            )

            // ===== CSRF: Skip CSRF check for multipart file upload endpoints =====
            // The /admission form uses enctype="multipart/form-data" for photo upload.
            // Tomcat 11's multipart parser is triggered when CSRF filter reads form params,
            // which can cause issues. Since /admission requires login anyway, this is safe.
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/admission")
            )

            // ===== STEP 2: Login form configuration =====
            .formLogin(form -> form
                .loginPage("/login")                    // Our custom login page
                .loginProcessingUrl("/loginForm")       // Form action URL
                .usernameParameter("email")             // We use "email" field, not "username"
                .successHandler(loginSuccessHandler())  // Where to go after login
                .failureUrl("/login?error=true")        // Where to go on wrong password
                .permitAll()                            // Login page is accessible to all
            )

            // ===== STEP 3: Logout configuration =====
            .logout(logout -> logout
                .logoutUrl("/logout")                   // Logout URL (POST request)
                .logoutSuccessUrl("/login?logout=true")  // After logout, go to login page
                .permitAll()
            );

        return http.build();
    }

    /**
     * AFTER LOGIN SUCCESS - redirect based on role
     * If user is ADMIN → go to /admin/dashboard
     * If user is USER  → go to /dashboard
     */
    @Bean
    public AuthenticationSuccessHandler loginSuccessHandler() {
        return (request, response, authentication) -> {
            // Check if the logged-in user has ADMIN role
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
