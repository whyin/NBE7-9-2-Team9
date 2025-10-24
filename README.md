# NBE7-9-2-Team9
> 프로그래머스 데브코스 7기 9회차 9팀 2차 프로젝트

## 1. 프로젝트 개요
본 프로젝트는 여행 일정 추천 및 장소 공유 플랫폼의 **백엔드 서버**로,  
회원 인증, 일정 관리, 북마크, 관리자용 장소 관리 등 핵심 기능을 제공합니다.  
JWT 기반 인증을 통해 **Stateless한 REST API 구조**를 유지하며,  
Spring Security를 활용한 **Role 기반 접근 제어**를 구현하였습니다.

---

## 2. 팀원

| 이름 | 역할 | 담당 기능 |
|------|------|------------|
| 김영인 | Backend Developer | 회원 인증, JWT, Security 설정, 예외 처리 구조 설계 |
| 팀원 A | Backend Developer | 플랜(Plan) 도메인 API, 일정 CRUD, 쿼리 최적화 |
| 팀원 B | Backend Developer | 장소(Place) 및 관리자(Admin) API |
| 팀원 C | Frontend Developer | React 기반 UI, API 연동 |
| 팀원 D | Frontend Developer | 플랜 및 장소 관련 화면 구현, UX 개선 |

---

## 3. 주요 기능

| 구분 | 설명 |
|------|------|
| 회원 관리 | 회원가입, 로그인, 회원정보 수정, 탈퇴 처리 |
| 인증/인가 | JWT 기반 Access/Refresh Token 발급 및 검증, Role별 접근 제어 |
| 플랜 관리 | 여행 일정 생성, 수정, 조회, 삭제 |
| 장소 관리 (Admin) | 관리자 전용 장소 등록, 수정, 삭제 기능 |
| 북마크 | 사용자가 관심 장소를 저장 및 해제 |
| 예외 처리 | 전역 예외(GlobalExceptionHandler) 및 ErrorCode 기반 표준 응답 |
| 데이터 관리 | BaseEntity를 통한 생성/수정 시간 자동 관리 |

---

## 4. 기술 스택

| 구분 | 기술 |
|------|------|
| Language | Java 21 |
| Framework | Spring Boot 3.5.6 |
| ORM | Spring Data JPA, Hibernate |
| Database | H2 (개발 환경), MySQL (운영 환경) |
| Security | Spring Security, JWT |
| Build Tool | Gradle |
| Validation | Jakarta Validation |
| ETC | Lombok, dotenv, DevTools |

---

## 5. 아키텍처 구조 (Backend)

## 6. ERD
