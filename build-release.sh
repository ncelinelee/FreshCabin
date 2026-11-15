#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
: "${ANDROID_JAR:?Android 35 platform android.jar 경로를 지정하세요}"
: "${BUILD_TOOLS:?Android Build Tools 35.x 디렉터리를 지정하세요}"
: "${RELEASE_KEYSTORE:?release 키스토어 경로를 지정하세요}"
: "${FRESHCABIN_KEYSTORE_PASSWORD:?키스토어 암호를 환경 변수로 제공하세요}"

src=app/src/main
build=build/release
mkdir -p "$build/classes" "$build/dex" "$build/assets" apk
cp docs/index.html "$build/assets/index.html"
"$BUILD_TOOLS/aapt2" link -I "$ANDROID_JAR" --manifest "$src/AndroidManifest.xml" \
  -A "$build/assets" -o "$build/resources.apk"
javac --release 8 -classpath "$ANDROID_JAR" -d "$build/classes" \
  "$src/java/io/freshcabin/demo/MainActivity.java"
jar cf "$build/classes.jar" -C "$build/classes" .
"$BUILD_TOOLS/d8" --release --min-api 28 --lib "$ANDROID_JAR" \
  --output "$build/dex" "$build/classes.jar"
cp "$build/resources.apk" "$build/unsigned.apk"
(
  cd "$build/dex"
  zip -q -j ../unsigned.apk classes.dex
)
"$BUILD_TOOLS/zipalign" -f 4 "$build/unsigned.apk" "$build/aligned.apk"
"$BUILD_TOOLS/apksigner" sign --ks "$RELEASE_KEYSTORE" --ks-key-alias freshcabin \
  --ks-pass env:FRESHCABIN_KEYSTORE_PASSWORD --out apk/FreshCabin_FreshCabin_release.apk "$build/aligned.apk"
rm -f apk/FreshCabin_FreshCabin_release.apk.idsig
"$BUILD_TOOLS/apksigner" verify --verbose apk/FreshCabin_FreshCabin_release.apk
