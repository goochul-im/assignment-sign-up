package com.thinkfree.tfinder.auth.infrastructure.persistence.iface;

public interface IEmailSendLimitRepository {

    /**
     * 남은 메일 전송 할당량을 가져옵니다.
     * 할당량이 초기화되었다면 mailLimit 만큼을 반환합니다.
     * @param mailLimit 워크스페이스의 메일 할당량
     * @return 남은 할당량
     */
    int getRemainLimit(int mailLimit, long workspaceId);

    /**
     * 남은 메일 전송 제한량을 감소시킵니다.
     * @param decrease 감소시킬 할당량
     * @param workspaceId 할당량을 감소시킬 워크스페이스
     * @return 남은 할당량
     */
    boolean decreaseRemainLimit(int decrease, long workspaceId);

}
