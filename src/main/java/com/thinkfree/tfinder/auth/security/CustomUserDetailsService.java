package com.thinkfree.tfinder.auth.security;

import com.thinkfree.tfinder.workspace.infrastructure.persistence.IMemberRepository;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.MemberEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class   CustomUserDetailsService implements UserDetailsService {

    private final IMemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        MemberEntity member = memberRepository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("email [" + email + "] not found")
        );

        return new CustomUserDetails(
                member.getId(),
                member.getEmail()
        );
    }
}
