package com.thinkfree.tfinder.workspace.infrastructure.persistence.adapter;

import com.thinkfree.tfinder.common.exception.BusinessException;
import com.thinkfree.tfinder.common.exception.ErrorCode;
import com.thinkfree.tfinder.workspace.domain.Member;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.MemberEntity;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.iface.IMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
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
                () -> {
                    log.warn("member not found, id : {}", id);
                    throw new BusinessException(ErrorCode.ENTITY_NOT_FOUND);
                }
        ).toDomain();
    }

    @Override
    public boolean isExistByEmail(String email) {
        return memberJpaRepository.existsByEmail(email);
    }

    @Override
    public Member findByEmail(String email) {
        return memberJpaRepository.findByEmail(email).orElseThrow(
                () -> {
                    log.warn("member not found, email : {}", email);
                    throw new BusinessException(ErrorCode.ENTITY_NOT_FOUND);
                }
        ).toDomain();
    }
}
