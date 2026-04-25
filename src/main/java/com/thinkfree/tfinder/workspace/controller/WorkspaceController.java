package com.thinkfree.tfinder.workspace.controller;

import com.thinkfree.tfinder.auth.security.CustomUserDetails;
import com.thinkfree.tfinder.auth.security.CustomUserInfo;
import com.thinkfree.tfinder.workspace.controller.request.InviteAcceptRequest;
import com.thinkfree.tfinder.workspace.controller.request.InviteRequest;
import com.thinkfree.tfinder.workspace.controller.request.WorkspaceCreateRequest;
import com.thinkfree.tfinder.workspace.controller.response.CreateWorkspaceResponse;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.WorkspaceEntity;
import com.thinkfree.tfinder.workspace.service.dto.CreateWorkspaceDto;
import com.thinkfree.tfinder.workspace.service.iface.IWorkspaceUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/workspace")
public class WorkspaceController {

    private final IWorkspaceUseCase workspaceUseCase;

    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody @Valid WorkspaceCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ){

        WorkspaceEntity workspace = workspaceUseCase.create(new CreateWorkspaceDto(
                currentUser.getMemberId(),
                request.workspaceName(),
                request.workspaceUrl()
        ));

        return new ResponseEntity<>(
                new CreateWorkspaceResponse(
                        workspace.getId()
                ),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/invite/accept")
    public ResponseEntity<?> inviteAccept(@RequestBody InviteAcceptRequest request){

        workspaceUseCase.acceptMember(request.inviteToken());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/invite")
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
