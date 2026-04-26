package com.thinkfree.tfinder.workspace.controller;

import com.thinkfree.tfinder.auth.security.CustomUserDetails;
import com.thinkfree.tfinder.workspace.controller.request.InviteAcceptRequest;
import com.thinkfree.tfinder.workspace.controller.request.InviteRequest;
import com.thinkfree.tfinder.workspace.controller.request.WorkspaceCreateRequest;
import com.thinkfree.tfinder.workspace.controller.response.CreateWorkspaceResponse;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.WorkspaceEntity;
import com.thinkfree.tfinder.workspace.service.dto.CreateWorkspaceDto;
import com.thinkfree.tfinder.workspace.service.iface.IWorkspaceUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@Tag(name = "워크스페이스", description = "워크스페이스 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/workspace")
public class WorkspaceController {

    private final IWorkspaceUseCase workspaceUseCase;

    @Operation(
            summary = "워크스페이스 생성",
            description = "워크스페이스를 생성합니다. "
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "워크스페이스 생성 완료",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CreateWorkspaceResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "E-001, 생성자가 DB에서 확인디지 않음"),
            @ApiResponse(responseCode = "409", description = "E-002, 워크스페이스 이름 또는 URL 중복"),
    })
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

        return ResponseEntity
                .created(URI.create(workspace.getWorkspaceUrl()))
                .body(new CreateWorkspaceResponse(
                        workspace.getId()
                ));
    }

    @Operation(
            summary = "워크스페이스 초대 요청 수락",
            description = "워크스페이스 초대 요청을 수락합니다. 전달받은 토큰을 inviteToken에 담아 보내야 합니다"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "초대 수락 완료"),
            @ApiResponse(responseCode = "404", description = "E-001, 초대 받은 이메일이나 초대받은 워크스페이스가 존재하지 않음"),
            @ApiResponse(responseCode = "409", description = "E-002, 이미 속해있는 워크스페이스임"),
            @ApiResponse(responseCode = "401", description = "I-001, 초대 토큰이 만료되었거나 오류가 있습니다.")
    })
    @PostMapping("/invite/accept")
    public ResponseEntity<?> inviteAccept(@RequestBody InviteAcceptRequest request){

        workspaceUseCase.acceptMember(request.inviteToken());

        return ResponseEntity.noContent()
                .build();
    }

    @Operation(
            summary = "워크스페이스 초대 요청",
            description = "워크스페이스 초대 요청을 보냅니다. 최대 50개의 이메일에 초대를 보낼수 있습니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "SMTP에 초대 메일 발송 요청 완료"),
            @ApiResponse(responseCode = "404", description = "E-001, 초대자가 존재하지 않거나 초대할 워크스페이스가 존재하지 않음"),
            @ApiResponse(responseCode = "409", description = "A-002, 해당 워크스페이스에 초대자가 속해있지 않거나, 권한이 없음"),
    })
    @PostMapping("/invite")
    public ResponseEntity<?> inviteMember(
            @RequestBody InviteRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser
            ) {

        workspaceUseCase.inviteMember(
                request.toEmailList(),
                currentUser.getMemberId(),
                request.workspaceId()
        );

        return ResponseEntity.accepted()
                .build();
    }

}
