# Auth
Auth 구현 예시입니다.

JDK 11, gradle 7버전 이상의 Springboot 프로젝트입니다.
jwt(access, refresh token)을 활용하여 auth를 설계하였습니다.
로컬 빌드 및 테스트에 용이하도록 내장 redis 및 h2db를 사용하였습니다(내장 redis의 경우 스프링 프로젝트를 셧다운 해도 계속 구동되기 때문에 재빌드 시 port를 kill해야 합니다).

구현된 api는 크게 4가지 입니다

Auth(jwt 필터를 거치지 않습니다.)
회원가입(signup) -> 전화번호, 이메일, 이름, 비밀번호를 정규 표현식을 사용하여 검증합니다.
로그인(login) -> 식별 가능한 방식인 전화번호 및 이메일 + 비밀번호로 로그인할 수 있도록 하였습니다.
비밀번호 변경(changePassword) -> 등록된 전화번호일 경우 정규 표현식을 만족하는 새로운 비밀번호로 변경 가능합니다. 불가피하게 유저 객체의 불변성이 깨지므로 변경 타임스탬프를 찍는 이유이기도 합니다.

User(jwt 필터를 거칩니다. 로그인 후 획득한 accessToken으로 인증한 경우에만 api 호출이 가능합니다.)
내 정보 보기 -> 추가 예정

SMS, E-MAIL을 직접 전송하여 인증 + OAuth 추가 예정
