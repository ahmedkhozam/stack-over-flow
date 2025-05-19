package com.example.demo.security;

import com.example.demo.entity.StackUser;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@AllArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final StackUser stackUser;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null; // مفيش أدوار دلوقتي
    }

    @Override
    public String getPassword() {
        return stackUser.getPassword();
    }

    @Override
    public String getUsername() {
        return stackUser.getEmail(); // هنا بندخل بالإيميل
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public StackUser getUser() {
        return stackUser;
    }
}
