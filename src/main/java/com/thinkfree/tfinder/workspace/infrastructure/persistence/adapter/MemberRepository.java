package com.thinkfree.tfinder.workspace.infrastructure.persistence.adapter;

import com.thinkfree.tfinder.workspace.domain.Member;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.MemberEntity;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.iface.IMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberRepository implements IMemberRepository {

    private final MemberJpaRepository memberJpaRepository;

    @Override
    public Member save(Member member) {
        return memberJpaRepository.save(MemberEntity.fromDomain(member)).toDomain();
    }

    @Override
    public Member findById(Long id) {
        return memberJpaRepository.findById(id).orElseThrow(
                () -> new RuntimeException("") // TODO: 예외처리 수정
        ).toDomain();
    }

    @Override
    public boolean isExistByEmail(String email) {
        return memberJpaRepository.existsByEmail(email);
    }

    @Override
    public Member findByEmail(String email) {
        return memberJpaRepository.findByEmail(email).orElseThrow(
                () -> new RuntimeException("") // TODO: 예외처리 수정
        ).toDomain();
    }
}
