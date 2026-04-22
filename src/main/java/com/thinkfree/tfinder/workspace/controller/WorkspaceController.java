package com.thinkfree.tfinder.workspace.controller;

import com.thinkfree.tfinder.workspace.service.iface.IWorkspaceUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WorkspaceController {

    private final IWorkspaceUseCase workspaceUseCase;

    @GetMapping("/invite/accept")
    public ResponseEntity<?> inviteAccept(@RequestParam String token){

        return new ResponseEntity<>();
    }

    @PostMapping("/invite")
    public ResponseEntity<?> inviteMember() {



        return new ResponseEntity<>();
    }

}
