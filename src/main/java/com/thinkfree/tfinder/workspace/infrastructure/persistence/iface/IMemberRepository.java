package com.thinkfree.tfinder.workspace.infrastructure.persistence.iface;

import com.thinkfree.tfinder.workspace.domain.Member;

public interface IMemberRepository {

    Member save(Member member);

    Member findById(Long id);

    boolean isExistByEmail(String email);

}
