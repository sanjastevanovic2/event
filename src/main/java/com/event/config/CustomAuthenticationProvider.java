//package com.event.config;
//
//import com.event.security.CustomUserDetailsService;
//import org.springframework.security.authentication.AuthenticationProvider;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.authentication.CachingUserDetailsService;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//@Component
//public class CustomAuthenticationProvider implements AuthenticationProvider {
//
//    private final CustomUserDetailsService userDetailsService;
//    private final PasswordEncoder passwordEncoder;  // Inject PasswordEncoder
//
//    public CustomAuthenticationProvider(CustomUserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
//        this.userDetailsService = userDetailsService;
//        this.passwordEncoder = passwordEncoder;
//    }
//
//    @Override
//    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//        String username = authentication.getName();
//        String rawPassword = (String) authentication.getCredentials();  // The password provided by the user
//
//        try {
//            // Load the user from the database
//            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
//
//            // Verify the password using the PasswordEncoder
//            if (!passwordEncoder.matches(rawPassword, userDetails.getPassword())) {
//                throw new BadCredentialsException("Invalid credentials");
//            }
//
//            // If password matches, return an authenticated token
//            return new UsernamePasswordAuthenticationToken(
//                    userDetails.getUsername(),
//                    userDetails.getPassword(),
//                    userDetails.getAuthorities());
//
//        } catch (UsernameNotFoundException e) {
//            throw new BadCredentialsException("Invalid credentials");
//        }
//    }
//
//    @Override
//    public boolean supports(Class<?> authentication) {
//        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
//    }
//}