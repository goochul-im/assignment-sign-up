package com.example.signup.workspace.infrastructure.persistence.iface;

import com.example.signup.workspace.domain.Member;

public interface IMemberRepository {

    Member save(Member member);

    Member findById(Long id);

    boolean isExistByEmail(String email);

}
