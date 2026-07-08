package career.flow.owoke.auth.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import career.flow.owoke.auth.entity.AuthUser;
import career.flow.owoke.auth.repository.AuthRepository;
import career.flow.owoke.common.util.CustomUserDetails;
import career.flow.owoke.common.util.EmailUtils;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AuthRepository authRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        AuthUser user = authRepository.findByEmail(EmailUtils.normalize(email))
                .orElseThrow(() -> new UsernameNotFoundException(email));

        return new CustomUserDetails(user);
    }
}
