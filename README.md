# FreshCabin

FreshCabin은 저장된 악취 구간에 접근하면 내기순환으로 자동 전환하고, 통과 후 외기 상태가 안정되면 기존 설정으로 복귀하는 차량용 앱 프로토타입입니다.

## 설치 및 시연

1. [서명된 release APK](apk/FreshCabin_FreshCabin_release.apk)를 내려받아 Android 9(API 28) 이상 기기나 Android 에뮬레이터에 설치합니다. Android System WebView가 필요합니다.
2. FreshCabin을 열고 **재생**을 누르거나 화면 하단 시점 버튼을 순서대로 누릅니다. ADB를 사용하는 경우 `adb install apk/FreshCabin_FreshCabin_release.apk`로 설치할 수 있습니다.
3. **출발 → 자동 전환 → 복귀 보류 → 환기 권고 → 외기 복귀**를 확인합니다. 냄새 민감·어린이·반려견 모드는 전환 거리와 권고 시점을 변경합니다.

[모바일 웹 앱](https://ncelinelee.github.io/FreshCabin/)을 iPhone의 Safari 또는 Android의 Chrome에서 열 수도 있습니다. iPhone에서는 Safari 메뉴의 공유 → 홈 화면에 추가를 사용하고, 옵션이 보이면 웹 앱으로 열기를 켭니다. 웹 앱은 처음 온라인으로 연 뒤 오프라인에서도 실행할 수 있습니다. [공통 HTML 소스](docs/index.html)는 브라우저와 APK에서 사용합니다.

## 구현 범위 및 검증 환경

- 울산 아산로에서 남구 방향으로 이동하는 가상 주행입니다. GPS 좌표, 외부 PM2.5, HVAC 상태 및 변경은 모두 **Mock**입니다.
- APK는 Android Activity에서 로컬 HTML을 WebView로 실행합니다. 인터넷·위치 권한을 요청하지 않습니다. Pleos SDK 및 실제 차량 HVAC 연동은 후속 개발 범위입니다.
- UI 설계 기준은 가로 1280×720입니다. HTML 흐름은 macOS Chrome에서 확인했습니다. APK는 Android SDK Platform 35, Build Tools 35.0.0으로 빌드하고 서명을 검증했습니다. Android 기기 설치·실행 및 Pleos Connect 에뮬레이터는 미검증입니다. 사용한 Pleos Connect 에뮬레이터 버전은 해당 없음입니다.

## Pleos Connect 공조 조회 시연

`pleos-demo`는 기존 1차 제출용 APK와 별도로, Pleos Vehicle SDK 2.3.2를 사용하는 빌드입니다. Pleos Connect Emulator 2.1.0(API 34)에서 **FreshCabin Pleos → Pleos 공조 조회**를 누릅니다. 앱이 HVAC 순환을 지원하는 영역을 확인하고, 수신한 내기·외기 상태를 차량 화면 상단에 표시합니다. 하단의 기존 가상 주행은 SDK 조회 결과와 독립적입니다.

Pleos Connect Emulator 2.1.0(API 34, 2560×1440), Vehicle Service 2.3.22에서 서명된 APK 설치와 실행을 확인했습니다. 조회 시 SDK가 반환한 내기순환 상태가 화면 상단에 표시됐고, 같은 화면에서 가상 주행의 자동 전환 단계도 열었습니다.

일반 Android 환경이나 권한 및 서비스가 없는 환경에서는 안내를 표시합니다. Pleos 빌드의 매니페스트는 상태 조회용 `CAR_CLIMATE`만 선언합니다. 위치·외부 대기질은 시뮬레이션이며 앱에서 공조를 변경하지 않습니다.

### Pleos 빌드 및 설치

배포된 시연 APK가 있으면 빌드 명령 없이 AVD에 설치할 수 있습니다. 소스에서 빌드할 때는 Android SDK Platform 35와 Build Tools 35.0.0, JDK 17 이상을 준비합니다. Android Studio에서 이 프로젝트를 열거나 명령줄에서 아래와 같이 빌드하고 Pleos Connect AVD에 설치합니다.

```sh
./gradlew :app:assemblePleosDebug
adb install -r app/build/outputs/apk/pleos/debug/app-pleos-debug.apk
adb shell am start -n io.freshcabin.demo.pleos/io.freshcabin.demo.PleosActivity
```

[공식 Pleos 에뮬레이터 가이드](https://document.pleos.ai/docs/connect/guide/getting-started/application-development/setup-connect-sdk-emulator) · [Vehicle SDK 시작 가이드](https://document.pleos.ai/docs/pleos-only/getting-started)

## 1차 독립 시연 APK 빌드

JDK 17 이상, Android Platform 35, Android Build Tools 35.0.0이 필요합니다. `ANDROID_JAR`에 해당 플랫폼의 `android.jar` 절대 경로, `BUILD_TOOLS`에 빌드 도구 디렉터리 절대 경로를 설정합니다. `RELEASE_KEYSTORE`에는 별칭이 `freshcabin`인 서명용 키스토어 절대 경로를 설정하고, `FRESHCABIN_KEYSTORE_PASSWORD`는 환경 변수로 제공한 뒤 `./build-release.sh`를 실행합니다. APK는 `apk/`에 생성됩니다.
