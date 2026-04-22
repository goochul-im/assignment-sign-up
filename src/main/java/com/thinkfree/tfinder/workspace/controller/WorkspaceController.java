package com.thinkfree.tfinder.workspace.controller;

import com.thinkfree.tfinder.workspace.controller.dto.InviteRequest;
import com.thinkfree.tfinder.workspace.service.iface.IWorkspaceUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class WorkspaceController {

    private final IWorkspaceUseCase workspaceUseCase;

    @GetMapping("/invite/accept")
    public ResponseEntity<?> inviteAccept(@RequestParam String token){

        workspaceUseCase.acceptMember(token);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/invite")
    public ResponseEntity<?> inviteMember(
            @RequestBody InviteRequest request
    ) {

        workspaceUseCase.inviteMember(request.toEmail(), 999L, request.workspaceId());

        return ResponseEntity.status(HttpStatus.OK)
                .build();
    }

}
