package com.example.signup.workspace.infrastructure.persistence.adapter;

import com.example.signup.workspace.domain.Member;
import com.example.signup.workspace.infrastructure.persistence.iface.IMemberRepository;
import org.springframework.stereotype.Repository;

@Repository
public class MemberRepository implements IMemberRepository {
    @Override
    public Member save(Member member) {
        return null;
    }

    @Override
    public Member findById(Long id) {
        return null;
    }

    @Override
    public boolean isExistByEmail(String email) {
        return false;
    }
}
