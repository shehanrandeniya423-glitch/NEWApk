# Cookie Editor App (Android)

Simple Android browser app that lets you **view, edit, add, and delete cookies** for any website.

## Features

- Built-in WebView browser
- View all cookies for the current website
- Edit cookie name / value / domain / path
- Add new cookies
- Delete cookies
- Works with most websites (uses Android CookieManager)

## How to Build & Install

### Requirements
- Android Studio (Hedgehog | 2023.1.1 or newer recommended)
- JDK 17 or 21
- Android phone/emulator with Android 7.0+ (API 24+)

### Steps

1. **Extract** this ZIP file
2. Open **Android Studio**
3. Click **Open** → select the `CookieEditorApp` folder
4. Wait for Gradle sync to finish (first time may take a few minutes)
5. Connect your phone (enable USB Debugging) **or** start an emulator
6. Click the green **Run** button (▶)

### Alternative (Command line)

```bash
./gradlew assembleDebug
```

APK will be generated at:
`app/build/outputs/apk/debug/app-debug.apk`

Then install it:
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

## How to Use

1. Open the app
2. Type any website URL and press **Go**
3. After the page loads, press the **🍪** button
4. You will see the list of cookies
5. Tap **Edit** to change a cookie value
6. Tap **Delete** to remove a cookie
7. Tap **Add Cookie** to create a new one

## Notes

- This app uses Android's `CookieManager`. Some cookies marked as `HttpOnly` may have limitations depending on the system version.
- Cookies are stored in the WebView's cookie store (not shared with Chrome/Firefox).
- For advanced needs (export in Netscape format, etc.), use Firefox + Cookie-Editor extension instead.

## Package

- Application ID: `com.cookieeditor.app`
- Min SDK: 24 (Android 7.0)
- Target SDK: 34

Made for educational / personal use.
