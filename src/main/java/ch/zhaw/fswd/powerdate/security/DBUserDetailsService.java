package ch.zhaw.fswd.powerdate.security;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import ch.zhaw.fswd.powerdate.entity.UserDbo;
import ch.zhaw.fswd.powerdate.repository.UserRepository;

@Service
public class DBUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public DBUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String loginName) throws UsernameNotFoundException {
        Optional<UserDbo> user = userRepository.findById(loginName);

        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found with login name: " + loginName);
        }

        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(user.get().getRoleName()));

        return new org.springframework.security.core.userdetails.User(
                loginName,
                user.get().getPasswordHash(),
                authorities);
    }
}