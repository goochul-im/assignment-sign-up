package com.thinkfree.tfinder.auth.security;

import com.thinkfree.tfinder.workspace.infrastructure.persistence.adapter.MemberJpaRepository;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.MemberEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberJpaRepository memberJpaRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        MemberEntity member = memberJpaRepository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("email [" + email + "] not found")
        );

        return new CustomUserDetails(new CustomUserInfo(
                member.getId(),
                member.getEmail(),
                member.getPassword()
        ));
    }
}
