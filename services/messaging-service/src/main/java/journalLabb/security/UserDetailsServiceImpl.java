package journalLabb.security;

import journalLabb.model.User;
import journalLabb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        System.out.println("\n=== LOADING USER FROM DATABASE ===");
        System.out.println("USERNAME INPUT: " + username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    System.out.println("USER NOT FOUND IN DB!!!!");
                    return new UsernameNotFoundException("User not found: " + username);
                });

        System.out.println("USER FOUND: " + user.getUsername());
        System.out.println("HASH IN DATABASE: " + user.getPasswordHash());
        System.out.println("ROLE IN DATABASE: " + user.getRole());

        return new UserPrincipal(
                user.getId(),
                user.getUsername(),
                user.getPasswordHash(),
                user.getRole()
        );
    }
}