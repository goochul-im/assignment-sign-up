package com.thinkfree.tfinder.workspace.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record InviteResponse(
        @Schema(description = "발송에 성공한 이메일 수", example = "10")
        int inviteSuccessSize,
        @Schema(description = "발송에 성공한 이메일 리스트", example = "[\"test1@email.com\", \"test2@email.com\"....]")
        List<String> inviteSuccessEmails,
        @Schema(description = "발송에 실패한 이메일 리스트", example = "[\"failed1@email.com\", \"failed2@email.com\"....]")
        List<String> inviteFailedEmails,
        @Schema(description = "이미 워크스페이스에 참여중인 이메일 리스트", example = "[\"already1@email.com\", \"already2@email.com\"....]")
        List<String> alreadyJoinedEmails
) {

    public InviteResponse(List<String> success, List<String> failed, List<String> alreadyJoined) {
        this(success.size(), success, failed, alreadyJoined);
    }
}
