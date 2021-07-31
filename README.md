# 앱 개발: 언제 한 번
```
- 1학년 1학기: '창의코딩스마트앱' 수업에서 MIT App Inventor를 사용하여 애플리케이션의 prototype 완성
- 1학년 2학기: Android Studio, firebase 사용하여 최종적인 애플리케이션 구현
```

## 애플리케이션 목적
'언제 한 번' 말뿐인 바쁜 삶에서 약속 한 번 잡기 힘든 요즈음. 서로의 스케줄을 파악하여 만날 수 있는 시간대를 대신 정해주어 약속시간 선정에 대한 불편함이 해소시킬 수 있는 애플리케이션입니다.

신호등 콘셉트의 색깔로 빨강, 주황, 초록색을 사용해서 시간표를 만듭니다. 그 후 친구들의 시간표를 중첩하여 서로 만날 수 있는 시간대를 확인할 수 있습니다.

## 애플리케이션 기능

#### intro
<img src="https://user-images.githubusercontent.com/46768752/127725499-dcb1baa3-ec35-47a5-af7f-9c9759f669be.png" width='200'>

#### weekly page
<img src="https://user-images.githubusercontent.com/46768752/127725405-eab9f90e-bbe9-48cf-a884-75fb42430750.png" width='700'>

- 위 사진에서 소개된 각 버튼을 선택하여 사용자 개개인의 시간표를 작성할 수 있습니다.


#### share page
<img src="https://user-images.githubusercontent.com/46768752/127725458-579ce912-b134-4840-8a99-c250828a6a42.png" width='200'>

- 친구부르기 버튼: 시간표를 중학하고 싶은 친구 선택
- 합치기 버튼: 시간표 중첩하기
- 중첩 원리: 색이 칠해지는 우선순위는 다음과 같습니다.
	- 빨간색 + (3가지 색) --> 빨간색
	- 주황색 + (주황, 초록) -> 주황색
	- 초록색 + 초록색 --> 초록색
