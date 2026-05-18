package com.thinkfree.tfinder.workspace.service.dto;

import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.WorkspaceMemberEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

import java.util.List;

public record WorkspaceMembersPageResponse(
        @Schema(description = "워크스페이스 멤버 총원", example = "3")
        int size,
        @Schema(description = "현재 페이지 번호", example = "1")
        int page,
        @Schema(description = "총 페이지 수", example = "5")
        int maxPage,
        @Schema(description = "각 페이지 크기", example = "10")
        int pageSize,
        @Schema(description = "다음 페이지 여부", example = "true")
        boolean hasNextPage,
        @Schema(description = "워크스페이스 멤버 목록")
        List<WorkspaceMemberResponse> memberList
) {

        public WorkspaceMembersPageResponse(Page<WorkspaceMemberEntity> page) {
                this((int) page.getTotalElements(),
                        page.getNumber() + 1,
                        page.getTotalPages(),
                        page.getSize(),
                        page.hasNext(),
                        page.getContent().stream()
                                .map(entity -> new WorkspaceMemberResponse(
                                        entity.getId(),
                                        entity.getMember().getNickname(),
                                        entity.getMember().getEmail(),
                                        entity.getRole()
                                )).toList()
                );
        }
}
