## 방탈출 사용자 예약

### 요구사항

- 발생할 수 있는 예외 상황에 대한 처리를 하여, 사용자에게 적절한 응답을 합니다.
    - 시간 생성 시 시작 시간에 유효하지 않은 값이 입력되었을 때
  - 예약 생성 시 예약자명, 날짜, 시간에 유효하지 않은 값이 입력 되었을 때
  - 특정 시간에 대한 예약이 존재하는데, 그 시간을 삭제하려 할 때 등등
- 아래와 같은 서비스 정책을 반영합니다.
  - 지나간 날짜와 시간에 대한 예약 생성은 불가능하다.
  - 중복 예약은 불가능하다.
    - ex. 이미 4월 1일 10시에 예약이 되어있다면, 4월 1일 10시에 대한 예약을 생성할 수 없다.
- 어드민의 시간 관리 페이지, 방탈출 예약 페이지에서 모든 기능이 정상적으로 동작하는지 확인합니다.

### 세부 요구 사항

1. 사용자 요청에 대한 예외 처리를 적용한다.

- [x] 예약 생성 시 예약자명, 날짜, 시간에 유효하지 않은 값이 입력 되었을 때
  - [x] 각 입력에 대하여 빈 입력값을 시도하였을 때
  - [x] 예약자명 예외 처리 
    - [x] 특수문자가 포함된 이름 처리 
  - [x] 날짜 예외 처리
    - [x] 과거 날짜 입력 예외 처리 

2. 관리자 요청에 대한 예외 처리를 적용한다.

- [ ] 시작 시간에 유효하지 않은 값이 입력되었을 때 예외로 처리한다.
    - [ ] 비어있는 입력값 처리 // 400 BAD_REQUEST
    - [ ] 운영 시간 외의 예약 시간 입력 처리 // 404 NOT_FOUND
    - [ ] 이미 존재하는 시간에 대한 입력 처리 // 409 CONFLICT
- [ ] 존재하는 예약에 해당하는 시간 삭제 요청 예외 처리 // 409 CONFLICT
