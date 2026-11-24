#!/bin/sh
# Сборка APK без Gradle. Нужны: JDK 17+, Android SDK (platform android-34, build-tools 35.0.0).
# Использование: SDK=/путь/к/android-sdk ./build.sh
set -e
SDK=${SDK:-$HOME/Android/Sdk}; BT=$SDK/build-tools/35.0.0; JAR=$SDK/platforms/android-34/android.jar
cp index.html android/assets/index.html
rm -rf out && mkdir -p out/classes
$BT/aapt2 compile --dir android/res -o out/res.zip
$BT/aapt2 link -o out/base.apk -I $JAR --manifest android/AndroidManifest.xml -A android/assets out/res.zip --min-sdk-version 21 --target-sdk-version 34 --java out/gen
javac --release 8 -cp $JAR -d out/classes $(find android/src out/gen -name '*.java')
(cd out/classes && jar cf ../classes.jar .)
$BT/d8 --lib $JAR --min-api 21 --output out out/classes.jar
cp out/base.apk out/unsigned.apk && (cd out && zip -q -j unsigned.apk classes.dex)
$BT/zipalign -f 4 out/unsigned.apk out/aligned.apk
$BT/apksigner sign --ks signing/key.jks --ks-key-alias td --ks-pass pass:tdpass1 --out InfDefense.apk out/aligned.apk
echo "Готово: InfDefense.apk"
