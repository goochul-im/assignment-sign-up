package com.thinkfree.tfinder.workspace.service.dto;

import java.util.List;

public record InviteResultDto(
        List<String> inviteSuccessEmails,
        List<String> inviteFailedEmails,
        List<String> alreadyJoinedEmails
) {
}
