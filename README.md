# FreshCabin

FreshCabin은 저장된 악취 구간에 접근하면 내기순환으로 자동 전환하고, 통과 후 외기 상태가 안정되면 기존 설정으로 복귀하는 차량용 앱 프로토타입입니다.

## 설치 및 시연

1. [서명된 release APK](apk/FreshCabin_FreshCabin_release.apk)를 내려받아 Android 9(API 28) 이상 기기나 Android 에뮬레이터에 설치합니다. Android System WebView가 필요합니다.
2. FreshCabin을 열고 **재생**을 누르거나 화면 하단 시점 버튼을 순서대로 누릅니다. ADB를 사용하는 경우 `adb install apk/FreshCabin_FreshCabin_release.apk`로 설치할 수 있습니다.
3. **출발 → 자동 전환 → 복귀 보류 → 환기 권고 → 외기 복귀**를 확인합니다. 냄새 민감·어린이·반려견 모드는 전환 거리와 권고 시점을 변경합니다.

Android 기기 없이 살펴보려면 [HTML 소스](app/src/main/assets/index.html)를 내려받아 Chrome에서 엽니다. 앱과 브라우저 시연 모두 오프라인으로 동작합니다.

## 구현 범위 및 검증 환경

- 울산 아산로에서 남구 방향으로 이동하는 가상 주행입니다. GPS 좌표, 외부 PM2.5, HVAC 상태 및 변경은 모두 **Mock**입니다.
- APK는 Android Activity에서 로컬 HTML을 WebView로 실행합니다. 인터넷·위치 권한을 요청하지 않습니다. Pleos SDK 및 실제 차량 HVAC 연동은 후속 개발 범위입니다.
- UI 설계 기준은 가로 1280×720입니다. HTML 흐름은 macOS Chrome에서 확인했습니다. APK는 Android SDK Platform 35, Build Tools 35.0.0으로 빌드하고 서명을 검증했습니다. Android 기기 설치·실행 및 Pleos Connect 에뮬레이터는 미검증입니다. 사용한 Pleos Connect 에뮬레이터 버전은 해당 없음입니다.

## 빌드

JDK 17 이상, Android Platform 35, Android Build Tools 35.0.0이 필요합니다. `ANDROID_JAR`에 해당 플랫폼의 `android.jar` 절대 경로, `BUILD_TOOLS`에 빌드 도구 디렉터리 절대 경로를 설정합니다. `RELEASE_KEYSTORE`에는 별칭이 `freshcabin`인 서명용 키스토어 절대 경로를 설정하고, `FRESHCABIN_KEYSTORE_PASSWORD`는 환경 변수로 제공한 뒤 `./build-release.sh`를 실행합니다. APK는 `apk/`에 생성됩니다.
