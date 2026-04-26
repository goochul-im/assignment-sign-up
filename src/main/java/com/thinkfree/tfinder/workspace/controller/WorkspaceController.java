package com.thinkfree.tfinder.workspace.controller;

import com.thinkfree.tfinder.auth.security.CustomUserDetails;
import com.thinkfree.tfinder.workspace.controller.request.InviteAcceptRequest;
import com.thinkfree.tfinder.workspace.controller.request.InviteRequest;
import com.thinkfree.tfinder.workspace.controller.request.WorkspaceCreateRequest;
import com.thinkfree.tfinder.workspace.controller.response.CreateWorkspaceResponse;
import com.thinkfree.tfinder.workspace.controller.response.MyWorkspaceResponse;
import com.thinkfree.tfinder.workspace.controller.response.WorkspaceMembersResponse;
import com.thinkfree.tfinder.workspace.service.dto.MyWorkspacesResultDto;
import com.thinkfree.tfinder.workspace.service.dto.WorkspaceMemberResultDto;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.WorkspaceEntity;
import com.thinkfree.tfinder.workspace.service.dto.CreateWorkspaceDto;
import com.thinkfree.tfinder.workspace.service.iface.IWorkspaceUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@Tag(name = "워크스페이스", description = "워크스페이스 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/workspace")
public class WorkspaceController {

    private final IWorkspaceUseCase workspaceUseCase;

    @SecurityRequirement(name = "Auth")
    @Operation(
            summary = "내 워크스페이스 목록 조회",
            description = "인증된 멤버가 속해 있는 모든 워크스페이스를 조회하고 멤버의 역할도 같이 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "내 워크스페이스 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MyWorkspaceResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "E-001, API를 요청한 멤버가 존재하지 않음"),
    })
    @GetMapping("/my")
    public ResponseEntity<?> findMyWorkspaces(
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {

        List<MyWorkspacesResultDto> myWorkspaces = workspaceUseCase.findMyWorkspaces(currentUser.getMemberId());
        MyWorkspaceResponse response = new MyWorkspaceResponse(
                myWorkspaces.size(),
                myWorkspaces
        );
        return ResponseEntity.ok(
                response
        );
    }

    @SecurityRequirement(name = "Auth")
    @Operation(
            summary = "워크스페이스 멤버 목록 조회",
            description = "인증된 멤버가 속한 워크스페이스의 모든 멤버와 역할을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "워크스페이스 멤버 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = WorkspaceMembersResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "403", description = "A-002, 해당 워크스페이스에 속해있지 않음"),
            @ApiResponse(responseCode = "404", description = "E-001, 멤버 또는 워크스페이스가 존재하지 않음"),
    })
    @GetMapping("/{workspaceId}/members")
    public ResponseEntity<?> findWorkspaceMembers(
            @PathVariable long workspaceId,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {

        List<WorkspaceMemberResultDto> members = workspaceUseCase.findWorkspaceMembers(
                currentUser.getMemberId(),
                workspaceId
        );
        WorkspaceMembersResponse response = new WorkspaceMembersResponse(
                members.size(),
                members
        );

        return ResponseEntity.ok(
                response
        );
    }

    @SecurityRequirement(name = "Auth")
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
            @ApiResponse(responseCode = "404", description = "E-001, 생성 요청자가 존재하지 않습니다."),
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
            @ApiResponse(responseCode = "401", description = "I-001, 초대 토큰이 만료되었거나 오류가 있습니다."),
            @ApiResponse(responseCode = "401", description = "I-002, 사용자가 아직 가입하지 않았기 때문에 가입을 먼저 안내해야함."),
    })
    @PostMapping("/invite/accept")
    public ResponseEntity<?> inviteAccept(@RequestBody InviteAcceptRequest request){

        workspaceUseCase.acceptMember(request.inviteToken());

        return ResponseEntity.noContent()
                .build();
    }

    @SecurityRequirement(name = "Auth")
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
