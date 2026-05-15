package com.thinkfree.tfinder.auth.infrastructure.persistence.iface;

public interface IEmailSendLimitRepository {

    /**
     * 남은 메일 전송 제한량을 가져옵니다.
     * 제한량이 초기화되었다면 mailLimit 만큼을 반환합니다.
     * @param mailLimit 워크스페이스의 메일 제한량
     * @return 남은 제한량
     */
    int getRemainLimit(int mailLimit, long workspaceId);

}
