package journalLabb.security;

import journalLabb.model.Patient;
import journalLabb.model.Role;
import journalLabb.model.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class UserPrincipal implements UserDetails {

    private final Long userId;
    private final Long patientId;
    private final String username;
    private final String password;
    private final Role role;                // <-- IMPORTANT: ENUM again
    private final String roleString;        // optional string
    private final Long practitionerId;

    public UserPrincipal(User user) {

        this.userId = user.getId();

        // Patient ID if applicable
        Patient patient = user.getPatient();
        this.patientId = (patient != null ? patient.getId() : null);

        this.username = user.getUsername();
        this.password = user.getPasswordHash();
        this.role = user.getRole();               // <-- ENUM stored here
        this.roleString = user.getRole().name();  // also keep string
        this.practitionerId = (user.getPractitioner() != null ? user.getPractitioner().getId() : null);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
