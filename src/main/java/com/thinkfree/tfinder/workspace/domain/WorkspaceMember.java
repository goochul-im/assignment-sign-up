package com.thinkfree.tfinder.workspace.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

/**
 * 클래스에 있어도 되는 필드와 아닌 필드
 * 인터페이스로 빠질 수 있는 필드인가??
 * 필드에 있어도 되는 필드인가? -> 변경되지 않는 필드
 *
 * 도메인과 엔티티가 꼭 1:1로 필드가 다 대응될 필요는 없다
 */
@AllArgsConstructor
@Getter
public class WorkspaceMember {

    private Long id;
    private Long memberId;
    private Long workspaceId;
    private MemberRole role;
    private boolean isSaveSearchTerm;
    private Instant lastLoginTime;

}
