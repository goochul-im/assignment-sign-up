package com.thinkfree.tfinder.workspace.controller;

import com.thinkfree.tfinder.workspace.service.iface.IWorkspaceUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WorkspaceController {

    private final IWorkspaceUseCase workspaceUseCase;

}
