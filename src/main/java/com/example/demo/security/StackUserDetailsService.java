package com.example.demo.security;

import com.example.demo.entity.StackUser;
import com.example.demo.repository.StackUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StackUserDetailsService implements UserDetailsService {

    private final StackUserRepository stackUserRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        StackUser stackUser = stackUserRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("No user found with email: " + email));

        return new CustomUserDetails(stackUser);
    }
}
