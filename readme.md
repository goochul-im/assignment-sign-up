# tfinder

## 현재까지 구현된 기능
1. 스프링 시큐리티를 이용한 인증/인가
2. 워크스페이스 생성/초대/수락

## 시작하기
이 리포지토리를 clone받고,가용한 postgresql과 redis가 있어야 합니다.
.env 파일을 먼저 만들어야 합니다.

```text
POSTGRESQL_USERNAME= 
POSTGRESQL_PASSWORD= 
POSTGRESQL_URL= 
JWT_SECRET= 
GOOGLE_SMTP_PASSWORD= 
GOOGLE_SMTP_USERNAME=
JWT_ACCESS_EXPIRATION_SECOND= {액세스 토큰 만료 시간 (초 단위)}
JWT_REFRESH_EXPIRATION_SECOND= {리프레쉬 토큰 만료 시간 (초 단위)}
FRONTEND_URL=
REFRESH_COOKIE_SECURE= {리프레쉬 토큰이 담길 쿠키의 secure 속성}
REFRESH_COOKIE_SAME_SITE= {리프레쉬 토큰이 담길 쿠키의 samesite 속성}
REDIS_HOST=
REDIS_PORT=
JWT_INVITE_EXPIRATION_SECOND= {초대 토큰 만료 시간 (초 단위)}
```

