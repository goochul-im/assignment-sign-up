package com.thinkfree.tfinder.workspace.service.adapter;

import com.thinkfree.tfinder.workspace.service.iface.IJwtManager;
import org.springframework.stereotype.Component;

@Component
public class JwtManager implements IJwtManager {
    @Override
    public String produce() {
        return "";
    }
}
