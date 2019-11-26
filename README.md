# CnA

## 1. 문제 추가

1. 단원 입력
2. 문제쓰기 사진
3. 문제풀이 사진

```
if(추가 된 문제가 10문제 이상일 때){
  다음으로
}
```

## 2. 시험 시작

왼쪽 상단에 현재 문제 위치를 보여준다. (첫번째 문제인 경우 1/10 으로 표시를 해준다.)

상단 imageview에는 문제쓰기에 등록 된 사진 view

하단 imageview에는 문제풀이에 등록 된 사진 view

다음 버튼을 누르면 다음 문제로 넘어간다.

이전 버튼을 누르면 이전 문제로 넘어간다.

마지막 문제일 경우 완료 버튼을 누르게 되면 시험이 종료 되고

문제와 해설이 보이는 페이지를 보여준다.

동시에 정답과 오답을 체크하고 이에 대해서는 본인 스스로에게 맡긴다.

정답에 체크를 하면 추후 랜덤 문제를 만들어줄 때 빈도 수를 낮추게 되고,

오답을 체크를 하면 빈도 수를 높이거나 유지를 해준다.

(슬라이드 뷰로 만들면 어떨까? 뚜똬뚜똬 형식으로)

## 3. 시험 완료

문제에 기입 된 단원에 따라 그래프를 만들어 준다.

(현재는 등록이 된 문제에 대해서만 그래프를 만들어 주고 있음.)

(따라서 시험이 완료 되고 정답률과 오답률에 따른 그래프를 따로 만들어야 할듯.)

## ToDoList

App

- [x] 로그인 시 DefaultInputActivity로 이동하는 Bug 수정 바람.
- [x] Notification Icon 추가.
- [x] 로그아웃 시 SQLiteDB 초기화.
- [x] App 내에서 NoteCode 입력 Dialog 구현.(쿠폰 유효기간 보여주기, 유효기간 남은 상태에서 코드 입력 시 쿠폰의 기간만큼 연장 되고. 유효기간이 끝난 상태에서 입력 시 새로운 유효기간 보여줌.)
- [x] DB와 ExamResult 결과 Sharing.
  - [x] 회원가입 후 바로 로그인이 아닌 경우가 아닌 경우 DB와 동기화.
- [x] ~~단원명 GridView를 더 Custom 해야함.(각각이 CheckBox처럼 동작하도록)~~
- [x] ExamResultAcitivity 구현.
  - [x] Note 및 Exam 결과에 대한 Graph를 View에 그리기.
- [X] (중요!)문제 출현 빈도 [Algorith 구현](https://github.com/Mineru98/DictionaryRandom).
- [ ] Notification 종류 생성.
- [ ] if Internet Off 상태라면 DB와 동기화 하지 않지 않고 TmpTable에 쌓아뒀다가 Internet On 상태일때 일괄 Server와 동기화 처리(일부 기능 제외)
- [x] AddNote에서 단원을 입력할 때, 폴더 구조로 (대단원, 소단원으로) 클릭으로 선택 지정.
- [ ] 신규 유저일 경우 튜토리얼 진행하는 투명 Dialog 생성 필요.
- [ ] 재시험 기능
- [ ] 가상 시뮬레이션으로 테스트 가능한 다양한 크기의 디스플레이 테스트.
- [x] Time Timer.
- [ ] App Update 확인.
- [ ] Custom Memo Image 구현.
- [ ] 예습을 하는 학생들을 위해서 시험을 칠수 있는 범위가 현재 자신의 학년에 대한 내용이 아닌 학년을 추가해서 할 수 있게끔 변경 가능캐 해야함.
- [ ] OffLine Resource Processing
- [ ] if OffLine ⇒ OnLine then Query Processing(Option)
  - [ ] Off Automadicaly Process
    - [ ] show Dialog warning "if you turn off switches, you will not be able to sync with DB server"
  - [ ] On Automadicaly Process
    - [ ] if (wifi, 3G/4G/5G ) turn on network then automatically sync process
    - [ ] if only wifi network work automatically sync process
- [ ] if existing client then sync with DB server
  - [ ] it selected yes then getting data from DB server
  - [ ] it selected no then keep in push data
- [ ] controllable note EachTime
- [ ] change Emersion random exam
- [ ] apply new toolbar(reference kakao talk)
- [ ] network check (client controlled dismiss)
- [ ] prob&solv image(able to edit & other things change)
- [ ] multi delete note(니가 가능하면 다중 편집도 해봐)
- [ ] apply deeplink

Bug

- [x] DefaultInputDialog에서 초/중/고까지만 선택하고 학년은 선택하지 않고 dismiss()할 경우 마지막 선택한 값인 학년 다이얼로그에서 데기함. 예외처리 필요함.(2019.05.14)
- [ ] 시험 결과를 보는 곳에서 요소 2개가 겹쳐서 보이는 경우가 발생(발생 : 2019.6.9)
- [ ] [AddNoteActivity].(Select Last ProbClassID) // Reason: ChapterData
- [ ] [ExamFragment].(TabBar Color is not matched)
- [ ] [DoExamFragment].(Selected ProbClassId Overlap)
- [ ] [DoExamFragment].(Seekbar UI is intuitive)
- [ ] [DoExamFragment].(Can't Click Seekbar. Only Drag)
- [ ] [SettingFragment].(Update ProfileName)

---

여담

- 친구 깨우기 시스템(자랑하기 유사 시스템) : 시간 내에 친구가 일어나면 두사람 다에게 포인트(서비스 내에서 사용가능한 무언가)지급
- 아이디어 시작 : 시험 기간에 공부하다가 잠깐 졸릴 때, 자다가 일어나고 싶을 때 친구에게 깨워달라고 부탁하는데 이러한 점을 이용해서 긍정적인 시너지를 만들어낼 수 있단 생각에 떠올림.
