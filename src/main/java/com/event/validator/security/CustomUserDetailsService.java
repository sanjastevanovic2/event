package com.event.validator.security;

import com.event.entity.User;
import com.event.exception.BadRequestException;
import com.event.exception.ExceptionCode;
import com.event.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if(!user.isActive()) {
            throw new BadRequestException(ExceptionCode.NOT_ALLOWED.name(), "Profile is deactivated. Please contact support for re-activate");
        }
        Set<GrantedAuthority> authorities =  Set.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
        return new org.springframework.security.core.userdetails.User(
                username,
                user.getPassword(),
                authorities
        );

    }
}