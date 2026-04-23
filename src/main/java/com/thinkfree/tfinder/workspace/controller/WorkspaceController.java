package com.thinkfree.tfinder.workspace.controller;

import com.thinkfree.tfinder.auth.security.CustomUserDetails;
import com.thinkfree.tfinder.auth.security.CustomUserInfo;
import com.thinkfree.tfinder.workspace.controller.request.InviteRequest;
import com.thinkfree.tfinder.workspace.service.iface.IWorkspaceUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/invite")
public class WorkspaceController {

    private final IWorkspaceUseCase workspaceUseCase;

    @GetMapping("/accept")
    public ResponseEntity<?> inviteAccept(@RequestParam String token){

        workspaceUseCase.acceptMember(token);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<?> inviteMember(
            @RequestBody InviteRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser
            ) {

        workspaceUseCase.inviteMember(
                request.toEmail(),
                currentUser.getMemberId(),
                request.workspaceId()
        );

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
