# FreshCabin

FreshCabin은 Pleos Connect 차량 화면에서 저장된 악취 구간과 공조 상태를 함께 보여주는 공기 유입 관리 앱입니다. 대시보드는 **플레오스 스플릿** 배치와 애시 그레이·오렌지 색상으로 구성했습니다. 앱 영역에서 360° 가상 주행과 경로 지도를 전환할 수 있습니다.

## Pleos Connect 시연

1. [Pleos용 서명 APK](apk/FreshCabin_Pleos_release.apk)를 Pleos Connect 에뮬레이터에 설치하고 **FreshCabin Pleos**를 엽니다.
2. 앱을 열면 현재 순환 상태를 조회하며, **Pleos 공조 조회**로 다시 읽을 수 있습니다. Vehicle SDK 2.3.2가 순환 지원 영역을 확인한 뒤 응답으로 받은 내기·외기 상태를 상단에 표시합니다. 차량 시스템의 주행 정보와 하단 바는 Pleos Connect가 제공합니다.
3. **360° 주행** 화면에서 재생을 시작하면 도로와 주변 차량이 움직입니다. 화면을 드래그하거나 좌우 버튼으로 시점을 360° 돌릴 수 있고, **기본 시점**과 **경로 지도**로 돌아갈 수 있습니다.
4. **출발 → 자동 전환 → 복귀 보류 → 환기 권고 → 외기 복귀**를 확인합니다. 3D 도로와 차량은 가상 장면이며 GPS 경로, PM2.5, 자동 공조 변경도 Mock입니다. SDK 연동은 공조 상태 조회 범위입니다.

검증 환경은 Pleos Connect Emulator 2.1.0(Android 14/API 34), Vehicle Service 2.3.22, 가로 2560×1440입니다. [에뮬레이터 캡처](demo/FreshCabin-Pleos-emulator.png) 상단에는 SDK의 내기순환 응답, 본문에는 Mock 자동 전환이 표시됩니다. 공조 상태 조회용 `CAR_CLIMATE` 권한이 필요하며, 권한이나 Vehicle Service가 없으면 앱에 안내가 표시됩니다.

```sh
adb install -r apk/FreshCabin_Pleos_release.apk
adb shell am start -n io.freshcabin.demo.pleos/io.freshcabin.demo.PleosActivity
```

[공식 Pleos 에뮬레이터 가이드](https://document.pleos.ai/docs/connect/guide/getting-started/application-development/setup-connect-sdk-emulator) · [Vehicle SDK 시작 가이드](https://document.pleos.ai/docs/pleos-only/getting-started)

## 독립 시뮬레이션

- [독립 Android 서명 APK](apk/FreshCabin_FreshCabin_release.apk)는 Android 9(API 28) 이상에서 WebView로 실행됩니다. Pleos 서비스 없이 가상 주행을 시연할 수 있습니다.
- [웹 시연](https://ncelinelee.github.io/FreshCabin/)은 차량 화면을 브라우저에서 체험하기 위한 링크입니다. 넓은 화면에서는 차량 시스템 영역을 함께 그리며, 좁은 화면에서는 지도와 공조가 아래로 이어집니다.
- 웹과 두 APK는 [공통 HTML 소스](docs/index.html)를 사용합니다.

## 소스에서 빌드

JDK 17 이상, Android SDK Platform 35 및 Build Tools 35.0.0을 준비합니다. Gradle 프로젝트에서 `./gradlew :app:assemblePleosDebug :app:assembleStandaloneDebug`로 두 버전을 빌드할 수 있습니다.

독립 시뮬레이션 release APK를 직접 서명할 때는 `ANDROID_JAR`에 플랫폼의 `android.jar` 절대 경로, `BUILD_TOOLS`에 빌드 도구 디렉터리 절대 경로, `RELEASE_KEYSTORE`에 별칭 `freshcabin`이 들어 있는 키스토어 절대 경로를 설정합니다. 암호는 `FRESHCABIN_KEYSTORE_PASSWORD` 환경 변수로 제공하고 `./build-release.sh`를 실행합니다.
