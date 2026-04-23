package com.thinkfree.tfinder.auth.controller;

import com.thinkfree.tfinder.auth.controller.dto.LoginRequest;
import com.thinkfree.tfinder.auth.controller.dto.SignupRequest;
import com.thinkfree.tfinder.auth.service.dto.LoginDto;
import com.thinkfree.tfinder.auth.service.dto.LoginResult;
import com.thinkfree.tfinder.auth.service.dto.SignupDto;
import com.thinkfree.tfinder.auth.service.iface.IAuthUseCase;
import com.thinkfree.tfinder.workspace.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final IAuthUseCase authUseCase;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request){

        LoginResult result = authUseCase.login(new LoginDto(
                request.email(),
                request.password()
        ));

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request){

        authUseCase.signUp(new SignupDto(
                request.email(),
                request.name(),
                request.password()
        ));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/test")
    public ResponseEntity<?> test(){

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
