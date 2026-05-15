package com.thinkfree.tfinder.workspace.event;

import org.springframework.context.ApplicationEvent;

public record SignupFirstEvent(
    String toEmail,
    String workspaceUrl
){
}
