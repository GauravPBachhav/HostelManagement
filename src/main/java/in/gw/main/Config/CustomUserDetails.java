package in.gw.main.Config;

import in.gw.main.Entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * CUSTOM USER DETAILS
 * --------------------
 * This class WRAPS our User entity so Spring Security can understand it.
 *
 * WHY do we need this?
 *   - Spring Security needs a "UserDetails" object to work with
 *   - Our "User" entity is a normal Java class
 *   - This wrapper converts our User into something Spring Security understands
 *
 * Think of it like a translator between our User and Spring Security.
 */
public class CustomUserDetails implements UserDetails {

    private final User user;  // Our actual User entity from database

    public CustomUserDetails(User user) {
        this.user = user;
    }

    // ===== CUSTOM METHODS (our own, not from Spring Security) =====

    /** Get the actual User entity (useful in controllers) */
    public User getUser() {
        return user;
    }

    /** Get user's display name */
    public String getName() {
        return user.getName();
    }

    /** Get user's database ID */
    public Long getUserId() {
        return user.getId();
    }

    // ===== SPRING SECURITY REQUIRED METHODS =====

    /** Spring Security uses this as "username" - we use email */
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    /** Spring Security checks this against entered password */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * User's ROLES (permissions)
     * "ROLE_USER" or "ROLE_ADMIN"
     * Spring Security needs "ROLE_" prefix for hasRole() to work
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
    }

    // All these return true = account is active and working
    @Override public boolean isAccountNonExpired()     { return true; }
    @Override public boolean isAccountNonLocked()      { return true; }
    @Override public boolean isCredentialsNonExpired()  { return true; }
    @Override public boolean isEnabled()                { return true; }
}
