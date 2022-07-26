# Auth
Auth 구현 예시입니다.

JDK 11, gradle 7버전 이상의 Springboot 프로젝트입니다.

MVC 패턴에 JPA orm을 사용하였습니다.

로컬 빌드 및 테스트에 용이하도록 내장 redis 및 h2db를 사용하였습니다(프로젝트 실행 시 함께 실행, 자동 설치).

내장 redis의 경우 스프링 프로젝트를 셧다운 해도 계속 구동되기 때문에 재빌드 시 port를 kill해야 합니다.

스프링 시큐리티 의존성을 추가하고 필터를 JWT(access, refresh token)을 활용하여 검증하는 방식으로 커스터마이즈했습니다.

1. access token: 로그인 시 쿠키에 발급되는 jwt로 해당 access token이 있을 경우 로그인된 사용자로 인식합니다. 탈취 위험을 줄이고 보안성을 높이기 위해 만료 시간을 30분으로 설정합니다.
2. refresh token: 마찬가지로 로그인 시 쿠키에 발급되는 jwt로 access token의 짧은 만료 시간으로 초래할 불편함을 축소해줍니다. access token이 만료되어도 refresh token이 있고 검증이 된다면 access token을 재발급해줍니다. refresh token의 만료 시간은 30일이고 redis에 key로 저장합니다(value: userPhoneNum).

구현된 api는 하기와 같습니다.



## AuthController(/auth, jwt 필터를 거치지 않습니다.)

#### 회원가입(/signup)

SignUpUserDto 객체를 요청하여 전화번호를 정규 표현식으로 검증하여 국내에서 존재할 수 있는 형식의 전화번호면 인증을 완료하는 방식으로 로직을 수행합니다. 그 외에도 이메일, 이름, 비밀번호 역시 기존의 형식에 맞도록 정규 표현식을 사용하여 검증합니다. 보안성과 개인정보를 위해 비밀번호를 saltUtil을 사용하여 인코딩하여 DB에 저장합니다. 성공 시 코드와 메세지를 담은 ResponseDto를 반환합니다.

#### 로그인(/login)

LogInUserDto 객체를 요청하여 식별 가능한 방식인 전화번호 및 이메일 + 비밀번호로 로그인할 수 있도록 하였습니다. 등록된 유저로 판단될 경우 access token과 refresh token을 쿠키로 발급해주고 refresh token : userPhoneNum 키, 밸류로 redis에 저장합니다. 성공 시 코드와 메세지를 담은 ResponseDto를 반환합니다.

#### 비밀번호 변경(/changePassword)

ChangePasswordUserDto 객체를 요청하여 등록된 전화번호일 경우 정규 표현식을 만족하는 새로운 비밀번호로 변경 가능합니다. 불가피하게 유저 객체의 불변성이 깨지므로 변경 타임스탬프를 찍는 이유이기도 합니다. 마찬가지로 보안성과 개인정보를 위해 비밀번호를 saltUtil을 사용하여 인코딩하여 DB에 저장합니다. 성공 시 코드와 메세지를 담은 ResponseDto를 반환합니다.



## UserController(/user, jwt 필터를 거칩니다.)

로그인 후 획득한 accessToken으로 인증한 경우에만 api 호출이 가능합니다.

혹은 refresh token을 검증하여 access token을 재발급해줍니다.

#### 내 정보 보기(/findUserInfoByRefreshToken)

컨트롤러에서 서블릿을 인자로 받아 쿠키에서 refresh token의 이름을 가져옵니다. 해당 변수가 key로 redis에 조회한 값이 등록된 유저의 phoneNum인지 확인합니다. 유저 객체 확인 후 UserInfoDto 객체의 정적 팩토리 메서드 from()에 유저 객체를 넣어줌으로써 비밀번호를 제외한 모든 유저의 정보를 담은 UserInfoDto를 반환합니다.



## Test


비즈니스 로직을 검증하기 위해 AuthServiceImpl, UserServiceImpl의 테스트를 진행하였고 gradle Test JacocoTestReport로 생성된 레포트(경로: /build/reports/jacoco/test/html/com.example.auth.service.impl/index.html)를 보시면 테스트 커버리지 100%를 달성한 것을 확인할 수 있습니다.



## To Do

할 수 있었지만 시간 관계상 하지못한 아쉬운 것들 + 추후 추가하고 싶은 것들

controller 테스트 코드 작성을 통한 spring rest docs 자동 생성 및 open api3 스펙 전환

SMS, E-MAIL을 직접 전송하여 인증 + OAuth 인증
