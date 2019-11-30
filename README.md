# CnA
## Development Flow
1. Splash Activity(기존과 동일하게)
2. Introduce Activity(아래 그림과 같이 변경)
![설명](https://raw.githubusercontent.com/Mineru98/Usage_Collection/master/img/intro.jpg)
3. Login & SingUp Activity(아래 그림과 같이 변경)<br><img src="https://raw.githubusercontent.com/Mineru98/Usage_Collection/master/img/signin.gif" width="300px"  alt="로그인"></img> <img src="https://raw.githubusercontent.com/Mineru98/Usage_Collection/master/img/signup.gif" width="300px"  alt="회원가입"></img>
4. Http Connection(GraphQL, Http(HttpURLConnection or retrofit2)두가지 혼합)
5. DB(SQL -> Realm) 
6. Activity
  - Toolbar(UI 개선)
7. Fragment
  - HomeFragment(Chip 제거, 나머지 UI는 유지)
  - GraphFragment(기존과 동일하게)
  - ExamFragment
    - DoExamFragment
      - ExapedListView & ListView(Endpoint에서 Srcollview의 스크롤이 안되는 버그)
      - Spinner의 총문항 수 최소값 4로 바꿀 수 있는 방법 고안할 것
    - ListExamFragment(재시험 버튼)
  - SettingFragment
    - ProfileImage 추가<br><img src="https://raw.githubusercontent.com/Mineru98/Usage_Collection/master/img/Fragment_Setting.png" width="210px"  alt="Off"></img> <img src="https://raw.githubusercontent.com/Mineru98/Usage_Collection/master/img/Fragment_Setting_On.png" width="200px" alt="On"></img>
8. Dialog(아래 둘중 하나의 라이브러리 사용해서 UI 개선)<br><img src="https://raw.githubusercontent.com/wasabeef/awesome-android-ui/master/art/DialogPlus2.gif" width="300px"  alt="Dialog1"></img> <img src="https://raw.githubusercontent.com/wasabeef/awesome-android-ui/master/art/swalert_change_type.gif" width="330px"  alt="Dialog2"></img>

## 참고자료
https://blog.mindorks.com/exploring-android-view-pager2-in-android