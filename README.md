# Rovex Remote — Android Studio project

Target model: **ROVEX UNITE RS-07CST5**.

## Experimental IR test
The app now has a **GREE test** button. It sends a GREE-family 38 kHz frame with a preset of ON / COOL / 23 °C / Auto fan. The GREE frame structure is documented by IRremoteESP8266, but no source found proves that this ROVEX model uses GREE. Treat this as a protocol-family experiment only.

The app checks Android's `ConsumerIrManager` capability before sending and asks for confirmation. A successful Android transmit call only means the phone attempted to emit the pattern; it does not prove the air conditioner received it.

## Build
Open this folder in Android Studio, sync Gradle, then choose **Build → Build Bundle(s) / APK(s) → Build APK(s)**. Use JDK 17 and Android SDK 35. Android Gradle Plugin is 8.7.3.

## APK status
This workspace has Java but no Android SDK, Android build-tools, Gradle executable, or Gradle wrapper. Therefore an APK cannot be compiled here. The archive is the source project, not an installable APK.
