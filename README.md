# VF Recomp Online Lobby Tracker (Android Widget)

5분 주기로 `https://vf-online.pages.dev/online`의 접속자 현황(Searching, Waiting, Playing, KR 인원)을 모니터링하여 홈 화면에 표시하는 안드로이드 위젯 프로젝트입니다.

## APK 빌드 방법
1. **Android Studio에서 열기**:
   - `File` -> `Open...` -> 압축 해제한 폴더 선택.
2. **APK 즉시 생성**:
   - 상단 메뉴 `Build` -> `Build Bundle(s) / APK(s)` -> `Build APK(s)`.
   - 빌드가 완료되면 우측 하단 팝업의 `locate`를 클릭하여 생성된 `app-debug.apk` 파일을 휴대폰으로 전송해 설치합니다.
3. **위젯 등록**:
   - 휴대폰 홈 화면 빈 곳을 길게 터치 -> `위젯` -> `VF Recomp Tracker` 위젯을 끌어다 놓으면 5분 간격 자동 갱신이 시작됩니다.
