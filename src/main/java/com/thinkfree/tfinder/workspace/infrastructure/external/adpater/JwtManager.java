package com.thinkfree.tfinder.workspace.infrastructure.external.adpater;

import com.thinkfree.tfinder.workspace.infrastructure.external.iface.IJwtManager;
import org.springframework.stereotype.Component;

@Component
public class JwtManager implements IJwtManager {
    @Override
    public String produce() {
        return "";
    }
}
