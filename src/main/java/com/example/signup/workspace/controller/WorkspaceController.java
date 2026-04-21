package com.example.signup.workspace.controller;

import com.example.signup.workspace.service.port.IWorkspaceUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WorkspaceController {

    private final IWorkspaceUseCase workspaceUseCase;

}
