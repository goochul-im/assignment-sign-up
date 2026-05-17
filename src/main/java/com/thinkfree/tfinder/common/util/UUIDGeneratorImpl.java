package com.thinkfree.tfinder.common.util;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UUIDGeneratorImpl implements IUUIDGenerator {

    @Override
    public String generate() {
        return UUID.randomUUID().toString(); // 버전 4 UUID
    }
}
