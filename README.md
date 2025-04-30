# Asis

---

## 프로젝트 개요

- **개발 기간**: 2025.03.12 ~ 진행 중  
- **핵심 기술**: Spring Boot, MySQL, JWT, OAuth2, RestTemplate, FastAPI 연동  
- **기여 역할**: 전체 백엔드 설계 및 구현, AI 연동 로직, 인증 시스템, DB 구조 설계

---

##  관련 레포지토리

-  AI 서버: [Asis_Ai (FastAPI)](https://github.com/MnDubi/Asis_Ai)
-  Spring Boot 서버: [MnDubi_Back](https://github.com/MnDubi/MnDubi_Back)

---

##  주요 기능 (Spring Boot API)

Asis Spring Boot 서버는 **할 일 관리, 사용자 인증, 친구 관리, 그룹/공유 TDL 기능** 등을 포함하며 FastAPI 기반 AI 서버와 연동되어 **카테고리를 자동 분류**하는 기능을 제공합니다.

---

## 🧾 ERD

![ERD](https://github.com/user-attachments/assets/da784479-e5b8-4552-a499-7822e755dc6b)

---

###  인증 / 회원 관리

| 기능             | 메서드 | 경로                              | 설명 |
|------------------|--------|-----------------------------------|------|
| 회원가입         | POST   | `/auth/register`                  | 이메일, 이름, 비밀번호 기반 가입 |
| 로그인 (자체)    | POST   | `/auth/login`                     | 자체 로그인 및 JWT 발급 |
| OAuth2 로그인 시작 | GET    | `/oauth2/authorize/{provider}`    | Google/Naver/Kakao 인증 시작 |
| OAuth2 콜백       | GET    | `/oauth2/callback/{provider}`     | 인가 코드 수신 |
| OAuth2 JWT 발급   | POST   | `/auth/oauth/jwt`                 | 인가 코드로 JWT 발급 |
| 프로필 조회/수정/삭제 | GET/POST/PUT/DELETE | `/profile` (추정)               | 사용자 정보 관리 |
| 로그아웃          | DELETE | `/logout`                         | 로그아웃 기능 (예정) |

---

### 친구 기능

| 기능             | 메서드 | 경로                             | 설명 |
|------------------|--------|----------------------------------|------|
| 친구 목록 조회    | GET    | `/friends`                       | 현재 친구 리스트 |
| 친구 여부 확인    | GET    | `/friends/check/{userCode}`      | 특정 유저와 친구인지 확인 |
| 친구 요청         | POST   | `/friends/request`               | 친구 요청 보내기 |
| 친구 요청 수락    | POST   | `/friends/accept`                | 받은 요청 수락 |
| 친구 요청 거절    | POST   | `/friends/reject`                | 받은 요청 거절 |
| 친구 요청 취소    | DELETE | `/friends/cancel`                | 내가 보낸 요청 취소 |
| 친구 삭제         | DELETE | `/friends/{userCode}`            | 친구 관계 삭제 |
| 요청 목록 확인    | GET    | `/friends/requests`              | 받은 요청 전체 보기 |
| 보낸 요청 여부 확인 | GET    | `/friends/requested/{userCode}`  | 이미 요청했는지 확인 |

---

### 개인 TDL (To-Do List)

| 기능               | 메서드 | 경로                        | 설명 |
|--------------------|--------|-----------------------------|------|
| TDL 등록            | POST   | `/toDoList/insert`          | 개인 할 일 생성 |
| TDL 수정            | PUT    | `/toDoList/modify`          | 제목, 일정 등 수정 |
| TDL 삭제            | DELETE | `/toDoList/delete`          | 할 일 삭제 |
| TDL 완료 처리       | PUT    | `/toDoList/success`         | 완료 여부 변경 |
| TDL 전체 조회       | GET    | `/toDoList/get`             | 모든 개인 TDL 조회 |
| 기간제 TDL 등록     | POST   | `/toDoList/insert/until`    | 시작/종료일 포함한 할 일 |
| TDL 완수율 저장     | POST   | `/toDoList/finish`          | 평가용 통계 저장 |

---

### 그룹 TDL

| 기능               | 메서드 | 경로                             | 설명 |
|--------------------|--------|----------------------------------|------|
| 그룹 TDL 생성       | POST   | `/group/toDoList/create`        | 다수 사용자에게 할 일 생성 |
| 그룹 TDL 등록       | POST   | `/group/toDoList/insert`        | 개별 추가 |
| 그룹 TDL 수정       | PUT    | `/group/toDoList/modify`        | 제목 등 변경 |
| 그룹 TDL 삭제       | DELETE | `/group/toDoList/delete`        | 할 일 삭제 |
| 그룹 전체 삭제      | DELETE | `/group/toDoList/delete/all`    | 전체 그룹 삭제 |
| 그룹 완료 처리      | PUT    | `/group/toDoList/success`       | 완료 여부 변경 |
| 그룹 TDL 전체 조회  | GET    | `/group/toDoList/get`           | 전체 그룹 할 일 조회 |
| 그룹 완수율 저장    | POST   | `/group/toDoList/finish`        | 그룹 평가용 통계 저장 |
| 초대/응답/거절/조회 | POST/PUT/DELETE/GET | `/group/toDoList/invite`, `/refuse`, `/accept` 등 | 그룹 초대 및 관리 |

---

### 공유 TDL

| 기능               | 메서드 | 경로                            | 설명 |
|--------------------|--------|----------------------------------|------|
| 공유 생성           | POST   | `/share/toDoList/create`        | 친구에게 할 일 공유 시작 |
| 공유 할 일 추가     | POST   | `/share/toDoList/insert`        | 개별 추가 |
| 공유 수정/삭제/완료 | PUT/DELETE | `/modify`, `/delete`, `/success` | 할 일 공유 관리 |
| 공유 TDL 조회       | GET    | `/share/toDoList/get`           | 공유된 할 일 조회 |
| 공유 초대 발송      | POST   | `/share/toDoList/invite`        | 친구를 공유 그룹에 초대 |

---

### 캘린더 / 평가

| 기능             | 메서드 | 경로                        | 설명 |
|------------------|--------|-----------------------------|------|
| 개인 캘린더       | GET    | `/calendar/private`         | 날짜별 할 일 보기 |
| 월말평가(개인)     | GET    | `/calendar/private/month`   | 개인 평가 통계 |
| 그룹 캘린더       | GET    | `/calendar/group`           | 그룹 일정 조회 |
| 월말평가(그룹)     | GET    | `/calendar/group/month`     | 그룹 평가 통계 |
| 공유 캘린더       | GET    | `/calendar/share` (예정)    | 공유 일정 조회 |
| 월말평가(공유)     | GET    | `/calendar/share/month` (예정) | 공유 평가 통계 |

---

###  AI 카테고리 분류 (FastAPI 연동)

| 기능              | 메서드 | 경로                               | 설명 |
|-------------------|--------|------------------------------------|------|
| 자동 분류 요청     | POST   | `/classify-category`               | 문장을 AI에 보내 분류 요청 |
| 카테고리 수동 추가 | POST   | `/add-category`                    | AI에 수동 카테고리 등록 |
| 분류 결과 저장     | 내부 로직 | -                                | 유사도 기반 분류 결과 + 임베딩 저장 |
| 카테고리 관련 기타 | PUT/GET/DELETE | (추후 구현)                      | 카테고리 수정/삭제/조회 등 예정 |


