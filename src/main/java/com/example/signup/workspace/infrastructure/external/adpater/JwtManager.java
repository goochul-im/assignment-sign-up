package com.example.signup.workspace.infrastructure.external.adpater;

import com.example.signup.workspace.infrastructure.external.iface.IJwtManager;
import org.springframework.stereotype.Component;

@Component
public class JwtManager implements IJwtManager {
    @Override
    public String produce() {
        return "";
    }
}
